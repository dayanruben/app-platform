@file:OptIn(ExperimentalCompilerApi::class)

package software.amazon.app.platform.inject.processor

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.COMPILATION_ERROR
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import software.amazon.app.platform.inject.OPEN_SOURCE_LOOKUP_PACKAGE
import software.amazon.app.platform.inject.compile
import software.amazon.app.platform.inject.generatedComponent
import software.amazon.app.platform.inject.inner
import software.amazon.app.platform.inject.isAnnotatedWith
import software.amazon.app.platform.inject.isNotAnnotatedWith
import software.amazon.app.platform.inject.origin

class ContributesBindingProcessorTest {

  @Test
  fun `a component interface is generated in the lookup package for a contributed binding`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base

            @Inject
            @ContributesBinding(Unit::class)
            class Impl : Base 
            """
    ) {
      val generatedComponent = impl.generatedComponent

      assertThat(generatedComponent.packageName).isEqualTo(OPEN_SOURCE_LOOKUP_PACKAGE)
      assertThat(generatedComponent.origin).isEqualTo(impl)

      val method = generatedComponent.declaredMethods.single()
      assertThat(method.name).isEqualTo("provideImplBase")
      assertThat(method.parameters.single().type).isEqualTo(impl)
      assertThat(method.returnType).isEqualTo(base)
      assertThat(method).isAnnotatedWith(Provides::class)
    }
  }

  @Test
  fun `a component interface is generated in the lookup package for an inner contributed binding`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base

            interface Impl {
                @Inject
                @ContributesBinding(Unit::class)
                class Inner : Base
            } 
            """
    ) {
      val generatedComponent = impl.inner.generatedComponent

      assertThat(generatedComponent.packageName).isEqualTo(OPEN_SOURCE_LOOKUP_PACKAGE)
      assertThat(generatedComponent.origin).isEqualTo(impl.inner)

      val method = generatedComponent.declaredMethods.single()
      assertThat(method.name).isEqualTo("provideImplInnerBase")
      assertThat(method.parameters.single().type).isEqualTo(impl.inner)
      assertThat(method.returnType).isEqualTo(base)
      assertThat(method).isAnnotatedWith(Provides::class)
    }
  }

  @Test
  fun `the explicit bound type has a higher priority`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base
            interface Base2 : Base

            @Inject
            @ContributesBinding(Unit::class, boundType = Base::class)
            class Impl : Base2 

            @Inject
            @ContributesBinding(Unit::class)
            class Impl2 : Base2 
            """
    ) {
      assertThat(impl.generatedComponent.declaredMethods.single().returnType).isEqualTo(base)
      assertThat(impl2.generatedComponent.declaredMethods.single().returnType).isEqualTo(base2)
    }
  }

  @Test
  fun `it's an error when there's no super type`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            @Inject
            @ContributesBinding(Unit::class)
            class Impl 
            """,
      exitCode = COMPILATION_ERROR,
    ) {
      assertThat(messages)
        .contains("The bound type could not be determined for Impl. There are no super types.")
    }
  }

  @Test
  fun `it's an error when there are multiple super types`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base
            interface Base2

            @Inject
            @ContributesBinding(Unit::class)
            class Impl : Base, Base2
            """,
      exitCode = COMPILATION_ERROR,
    ) {
      assertThat(messages)
        .contains(
          "The bound type could not be determined for Impl. " +
            "There are multiple super types: Base, Base2."
        )
    }
  }

  @Test
  fun `bindings are repeatable`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base
            interface Base2

            @Inject
            @ContributesBinding(Unit::class, boundType = Base::class)
            @ContributesBinding(Unit::class, boundType = Base2::class)
            class Impl : Base, Base2 
            """
    ) {
      val generatedComponent = impl.generatedComponent

      assertThat(generatedComponent.packageName).isEqualTo(OPEN_SOURCE_LOOKUP_PACKAGE)
      assertThat(generatedComponent.origin).isEqualTo(impl)

      with(generatedComponent.declaredMethods.single { it.name == "provideImplBase" }) {
        assertThat(parameters.single().type).isEqualTo(impl)
        assertThat(returnType).isEqualTo(base)
        assertThat(this).isAnnotatedWith(Provides::class)
      }

      with(generatedComponent.declaredMethods.single { it.name == "provideImplBase2" }) {
        assertThat(parameters.single().type).isEqualTo(impl)
        assertThat(returnType).isEqualTo(base2)
        assertThat(this).isAnnotatedWith(Provides::class)
      }
    }
  }

  @Test
  fun `it's an error to use different scopes for multiple bindings`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base
            interface Base2

            @Inject
            @ContributesBinding(scope = String::class, boundType = Base::class)
            @ContributesBinding(scope = Unit::class, boundType = Base2::class)
            class Impl : Base, Base2 
            """,
      exitCode = COMPILATION_ERROR,
    ) {
      assertThat(messages).contains("All scopes on annotations must be the same.")
    }
  }

  @Test
  fun `it's an error to duplicate the same binding`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base

            @Inject
            @ContributesBinding(Unit::class, boundType = Base::class)
            @ContributesBinding(Unit::class, boundType = Base::class)
            class Impl : Base, Base2 
            """,
      exitCode = COMPILATION_ERROR,
    ) {
      assertThat(messages)
        .contains("The same type should not be contributed twice: software.amazon.test.Base.")
    }
  }

  @Test
  fun `a component interface is generated in the lookup package for a contributed multibinding`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base

            @Inject
            @ContributesBinding(Unit::class, multibinding = true)
            class Impl : Base 
            """
    ) {
      val generatedComponent = impl.generatedComponent

      assertThat(generatedComponent.packageName).isEqualTo(OPEN_SOURCE_LOOKUP_PACKAGE)
      assertThat(generatedComponent.origin).isEqualTo(impl)

      val method = generatedComponent.declaredMethods.single()
      assertThat(method.name).isEqualTo("provideImplBaseMultibinding")
      assertThat(method.parameters.single().type).isEqualTo(impl)
      assertThat(method.returnType).isEqualTo(base)
      assertThat(method).isAnnotatedWith(Provides::class)
      assertThat(method).isAnnotatedWith(IntoSet::class)
    }
  }

  @Test
  fun `both binding and multibinding component interfaces can be generated in the lookup package for a contributed multibinding`() {
    compile(
      """
            package software.amazon.test
    
            import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
            import me.tatarka.inject.annotations.Inject

            interface Base

            @Inject
            @ContributesBinding(Unit::class, multibinding = false)
            @ContributesBinding(Unit::class, multibinding = true)
            class Impl : Base 
            """
    ) {
      val generatedComponent = impl.generatedComponent

      assertThat(generatedComponent.packageName).isEqualTo(OPEN_SOURCE_LOOKUP_PACKAGE)
      assertThat(generatedComponent.origin).isEqualTo(impl)

      assertThat(generatedComponent.declaredMethods).hasSize(2)

      val bindingMethod = generatedComponent.declaredMethods.first { it.name == "provideImplBase" }
      assertThat(bindingMethod.parameters.single().type).isEqualTo(impl)
      assertThat(bindingMethod.returnType).isEqualTo(base)
      assertThat(bindingMethod).isAnnotatedWith(Provides::class)
      assertThat(bindingMethod).isNotAnnotatedWith(IntoSet::class)

      val multibindingBindingMethod =
        generatedComponent.declaredMethods.first { it.name == "provideImplBaseMultibinding" }
      assertThat(multibindingBindingMethod.parameters.single().type).isEqualTo(impl)
      assertThat(multibindingBindingMethod.returnType).isEqualTo(base)
      assertThat(multibindingBindingMethod).isAnnotatedWith(Provides::class)
      assertThat(multibindingBindingMethod).isAnnotatedWith(IntoSet::class)
    }
  }

  private val JvmCompilationResult.base: Class<*>
    get() = classLoader.loadClass("software.amazon.test.Base")

  private val JvmCompilationResult.base2: Class<*>
    get() = classLoader.loadClass("software.amazon.test.Base2")

  private val JvmCompilationResult.impl: Class<*>
    get() = classLoader.loadClass("software.amazon.test.Impl")

  private val JvmCompilationResult.impl2: Class<*>
    get() = classLoader.loadClass("software.amazon.test.Impl2")
}

package software.amazon.app.platform.metro

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Scope
import software.amazon.app.platform.ksp.ContextAware

internal interface MetroContextAware : ContextAware {
  val injectFqName
    get() = Inject::class.requireQualifiedName()

  private val scopeFqName
    get() = Scope::class.requireQualifiedName()
}

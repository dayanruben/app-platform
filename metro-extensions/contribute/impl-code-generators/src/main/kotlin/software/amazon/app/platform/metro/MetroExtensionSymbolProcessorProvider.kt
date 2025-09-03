package software.amazon.app.platform.metro

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import software.amazon.app.platform.ksp.CompositeSymbolProcessor
import software.amazon.app.platform.metro.processor.ContributesRendererProcessor
import software.amazon.app.platform.metro.processor.ContributesRobotProcessor
import software.amazon.app.platform.metro.processor.ContributesScopedProcessor

/** Entry point for KSP to pick up our [SymbolProcessor]. */
@AutoService(SymbolProcessorProvider::class)
@Suppress("unused")
public class MetroExtensionSymbolProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    return CompositeSymbolProcessor(
      ContributesRendererProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
      ),
      ContributesRobotProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
      ),
      ContributesScopedProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
      ),
    )
  }
}

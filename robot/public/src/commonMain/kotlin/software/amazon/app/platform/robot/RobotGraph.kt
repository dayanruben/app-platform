package software.amazon.app.platform.robot

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

/** Graph that provides all contributed [Robot] instances from the Metro dependency graph. */
@ContributesTo(AppScope::class)
public interface RobotGraph {
  /** All [Robot]s provided in the Metro dependency graph. */
  public val robots: Map<KClass<*>, Provider<Robot>>
}

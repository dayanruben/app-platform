package software.amazon.test

import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass
import software.amazon.app.platform.robot.Robot

interface TestRobotGraph {
  val robots: Map<KClass<*>, Provider<Robot>>
}

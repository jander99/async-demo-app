package sequential

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class SequentialAsyncSimulation extends BasicSimulation {

  val sequentialScenario: ScenarioBuilder = generateScenario("Sequential", "Async")
  setUp(sequentialScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

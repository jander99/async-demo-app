package pooled

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class AsyncPooledParallelSimulation extends BasicSimulation {

  val asyncScenario: ScenarioBuilder = generateScenario("AsyncPooled", "Parallel")
  setUp(asyncScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

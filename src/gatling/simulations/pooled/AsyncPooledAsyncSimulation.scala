package pooled

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class AsyncPooledAsyncSimulation extends BasicSimulation {

  val asyncScenario: ScenarioBuilder = generateScenario("AsyncPooled", "Async")
  setUp(asyncScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

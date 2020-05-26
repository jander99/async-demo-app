package async

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class AsyncAsyncSimulation extends BasicSimulation {

  val asyncScenario: ScenarioBuilder = generateScenario("Async", "Async")
  setUp(asyncScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

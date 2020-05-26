package async

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class AsyncSequentialSimulation extends BasicSimulation {

  val asyncScenario: ScenarioBuilder = generateScenario("Async", "Sequential")
  setUp(asyncScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

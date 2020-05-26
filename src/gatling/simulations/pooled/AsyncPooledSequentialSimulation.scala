package pooled

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class AsyncPooledSequentialSimulation extends BasicSimulation {

  val asyncScenario: ScenarioBuilder = generateScenario("AsyncPooled", "Sequential")
  setUp(asyncScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

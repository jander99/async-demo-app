package parallel

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ParallelAsyncSimulation extends BasicSimulation {

  val sequentialScenario: ScenarioBuilder = generateScenario("Parallel", "Async")
  setUp(sequentialScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

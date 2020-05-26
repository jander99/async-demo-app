package parallel

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ParallelAsyncPooledSimulation extends BasicSimulation {

  val sequentialScenario: ScenarioBuilder = generateScenario("Parallel", "Sequential")
  setUp(sequentialScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

package parallel

import core.BasicSimulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ParallelParallelSimulation extends BasicSimulation {

  val sequentialScenario: ScenarioBuilder = generateScenario("Parallel", "Parallel")
  setUp(sequentialScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

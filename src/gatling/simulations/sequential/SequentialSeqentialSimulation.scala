package sequential

import core.BasicSimulation
import io.gatling.core.Predef._

import scala.concurrent.duration._

class SequentialSeqentialSimulation extends BasicSimulation {

  val sequentialScenario = generateScenario("Sequential", "Sequential")
  setUp(sequentialScenario.inject(constantUsersPerSec(numUsers) during (rampUpSeconds seconds)))
}

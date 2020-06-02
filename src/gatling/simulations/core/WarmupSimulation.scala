package core

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class WarmupSimulation extends BasicSimulation {

  var one: ScenarioBuilder = generateScenario("Async", "AsyncPooled")
  var two: ScenarioBuilder = generateScenario("Parallel", "Sequential")
  var three: ScenarioBuilder = generateScenario("AsyncPooled", "Parallel")

  setUp(one.inject(constantUsersPerSec(1) during (1 seconds)),
    two.inject(constantConcurrentUsers(1) during(1 seconds)),
    three.inject(constantConcurrentUsers(1) during(1 seconds)))

}

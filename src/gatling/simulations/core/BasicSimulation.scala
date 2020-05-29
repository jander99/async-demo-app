package core

import io.gatling.core.Predef._
import io.gatling.core.body.StringBody
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  val r = scala.util.Random
  val requestHeaders = Map(
    HttpHeaderNames.ContentType -> HttpHeaderValues.ApplicationJson,
    HttpHeaderNames.Accept -> HttpHeaderValues.ApplicationJson
  )
  val testingUrl = "http://localhost:8080/"
  var numUsers: Int = 50
  var rampUpSeconds: Int = 90

  def generateScenario(fs1type: String, fs2type: String): ScenarioBuilder = {
    scenario(s"${fs1type}/${fs2type} Scenario")
      .exec(http(s"${fs1type}/${fs2type}")
        .post(testingUrl)
        .headers(requestHeaders)
        .body(StringBody(session => s"""{\"numIterations\": ${generateRandomIterations()}, \"fastService1Type\": \"${fs1type}\", \"fastService2Type\": \"${fs2type}\"}""")).asJson
        .check(status.is(200)))
  }

  def generateRandomIterations(): Int = {

    val percentile: Int = r.nextInt(99) + 1
    var rando: Int = 1

    if (percentile <= 25) {
      rando += r.nextInt(1)
    }
    if (percentile > 25 && percentile <= 50) {
      rando += r.nextInt(6)
    }
    if (percentile > 50 && percentile <= 75) {
      rando += r.nextInt(9)
    }
    if (percentile > 75 && percentile <= 90) {
      rando += r.nextInt(20)
    }
    if (percentile > 90 && percentile <= 99) {
      rando += r.nextInt(35)
    }
    if (percentile > 99) {
      rando += r.nextInt(70)
    }
    rando
  }
}

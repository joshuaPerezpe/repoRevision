package simulations.msUsers

import com.intuit.karate.gatling.PreDef.{karateFeature, karateProtocol, pauseFor}
import io.gatling.core.Predef.{Simulation, configuration, csv, openInjectionProfileFactory, rampUsers, scenario}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class usersConfig extends Simulation{

  val apiBaas = scenario("MS_USERS")
.exec(karateFeature("classpath:integration/feature/bulkPayments/msUsers/v1/usersConfig.feature@performance=msUsers"));

val protocol = karateProtocol(
"/feature" -> pauseFor( "get" -> 0)
)

protocol.runner.karateEnv("perf")
// 1 tps
setUp(
apiBaas.inject(

  rampUsers(5) during (1 seconds),
  rampUsers(50) during (10 seconds),
  rampUsers(100) during (20 seconds),
  rampUsers(200) during (40 seconds),
  rampUsers(1000) during (200 seconds),
  rampUsers(750) during (150 seconds),
  rampUsers(350) during (70 seconds)

).protocols(protocol))

}


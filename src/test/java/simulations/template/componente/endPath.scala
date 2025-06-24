package simulations.template.componente

import com.intuit.karate.gatling.PreDef.{karateFeature, karateProtocol, pauseFor}
import io.gatling.core.Predef.{Simulation, configuration, csv, openInjectionProfileFactory, rampUsers, scenario}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class endPath extends Simulation{

  val feed = csv("data/apiBaasLbtr/data.csv").circular();


  val apiBaas = scenario("TEF_LBTR/REGISTRAR")
    .feed(feed)
    .exec(karateFeature("classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/endPath.feature@performance=api_baas_lbtr"));

  val protocol = karateProtocol(
    "/feature" -> pauseFor( "post" -> 0)
  )

 protocol.runner.karateEnv("perf")
// 1 tps
  setUp(
    apiBaas.inject(

      rampUsers(1) during (1 seconds),
      rampUsers(10) during (10 seconds),
      rampUsers(50) during (50 seconds),
      rampUsers(30) during (30 seconds),
      rampUsers(100) during (100 seconds),
      rampUsers(200) during (200 seconds),
      rampUsers(100) during (100 seconds),

    ).protocols(protocol))

}


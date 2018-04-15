package leveragesim

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import leveragesim.DemandType.NoiseDemand
import leveragesim.Messages.{Demand, Price}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}


class NoiseTraderSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("NoiseTraderSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A noise trader actor" should "reply to price message with demand" in {
    val testSim = new Simulation(initialWealth=1, totalValue=1)
    val testProbe = TestProbe()
    val message = Price(1, testProbe.ref)
    val testNoiseTrader = system.actorOf(Props(new NoiseTrader(rho=1, sigma=1, sim=testSim, seed=1)))
    testNoiseTrader ! message
    testProbe.expectMsgPF() { //
       case Demand(x: Double, NoiseDemand, `testNoiseTrader`) => Math.abs(x - 4.76635) should be <= 5e-6
    }
  }
}

package leveragesim

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import leveragesim.DemandType.NoiseDemand
import leveragesim.Messages.{Demand, Price, RequestForQuote}
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
    val testSim = new Simulation(fundamentalValue=1, totalValue=1)
    val testProbe = TestProbe()
    val message = Price(1, testProbe.ref)
    val testNoiseTrader = system.actorOf(Props(new NoiseTrader(rho=1, sigma=1, sim=testSim, seed=1)))
    testNoiseTrader ! message
    testProbe.expectMsgPF() {
        //since the seed is fixed  then the demand should be the same
       case Demand(x: Double, NoiseDemand, `testNoiseTrader`) => Math.abs(x - 4.76635) should be <= 5e-6
    }
  }

  "A noise trader actor" should "reply to second price message with different demand" in {
    val testSim = new Simulation(fundamentalValue=1, totalValue=1)
    val testProbe = TestProbe()
    val testProbe2 = TestProbe()
    val message = Price(1, testProbe.ref)
    val message2 = Price(1, testProbe2.ref)
    val testNoiseTrader = system.actorOf(Props(new NoiseTrader(rho=1, sigma=1, sim=testSim, seed=1)))
    testNoiseTrader ! message
    testNoiseTrader ! message2
    testProbe2.expectMsgPF() {
      case Demand(x: Double, NoiseDemand, `testNoiseTrader`) => Math.abs(x - 2.59451) should be <= 5e-6
    }
  }

  "A noise trader actor" should "reply to request for quote message with the same demand" in {
    val testSim = new Simulation(fundamentalValue=1, totalValue=1)
    val testProbe = TestProbe()
    val testProbe2 = TestProbe()
    val testProbe3 = TestProbe()
    val message = Price(1, testProbe.ref)
    val message2 = RequestForQuote(1, testProbe2.ref)
    val message3 = RequestForQuote(1, testProbe3.ref)
    val testNoiseTrader = system.actorOf(Props(new NoiseTrader(rho=1, sigma=1, sim=testSim, seed=1)))
    testNoiseTrader ! message
    testNoiseTrader ! message2
    testNoiseTrader ! message3
    testProbe2.expectMsgPF() {
      case Demand(x: Double, NoiseDemand, `testNoiseTrader`) => Math.abs(x - 4.76635) should be <= 5e-6
    }
    testProbe3.expectMsgPF() {
      case Demand(x: Double, NoiseDemand, `testNoiseTrader`) => Math.abs(x - 4.76635) should be <= 5e-6
    }
  }

}

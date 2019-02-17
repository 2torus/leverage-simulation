package leveragesim

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import leveragesim.DemandType.HedgeFundDemand
import leveragesim.Messages.{Demand, Price, RequestForQuote}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import org.scalatest.prop.TableDrivenPropertyChecks.{Table, forAll}


class HedgeFundTraderSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("HedgeFundTraderSpec"))
  override def afterAll: Unit = {
    shutdown(system)
  }

  "HedgeFundTrader actor" should "respond to price message with demand" in {
    val testSim = new Simulation(fundamentalValue = 1, totalValue = 1)
    val testProbe = TestProbe()
    val message = Price(1, testProbe.ref)
    val message2 = RequestForQuote(1, testProbe.ref)
    val testHedgeFundTrader = system.actorOf(Props(new HedgeFundTrader(initialWealth=1, maxLeverage=1, beta=1, sim=testSim)))
    testHedgeFundTrader ! message
    testHedgeFundTrader ! message2
    testProbe.expectMsg(Demand(0, HedgeFundDemand, testHedgeFundTrader))
  }

  it should "respond to request for quote message without misprice with zero demand" in {
    val testSim = new Simulation(fundamentalValue = 1, totalValue = 1)
    val testProbe = TestProbe()
    val message = RequestForQuote(1, testProbe.ref)
    val testHedgeFundTrader = system.actorOf(Props(new HedgeFundTrader(initialWealth=1, maxLeverage=1, beta=1, sim=testSim)))
    testHedgeFundTrader ! message
    testProbe.expectMsg(Demand(0, HedgeFundDemand, testHedgeFundTrader))
  }

  it should "respond to request for quote message with misprice with non zero demand" in {
    val testSim = new Simulation(fundamentalValue = 1, totalValue = 1)
    val testProbe = TestProbe()
    val prices = Table("price", 0.3, 0.5, 0.7)
    forAll(prices) { price =>
      val message = RequestForQuote(price, testProbe.ref)
      val testHedgeFundTrader = system.actorOf(Props(new HedgeFundTrader(initialWealth = 1, maxLeverage = 2, beta = 1, sim = testSim)))
      // mispriceCritical = maxLeverage / beta == 2 is not achievable with these values.
      testHedgeFundTrader ! message
      testProbe.expectMsg(Demand((1 - price) / price, HedgeFundDemand, testHedgeFundTrader))
    }
  }

  "HedgeFundTrader actor which is leveraged out" should "respond to request for quote message with misprice with non zero demand" in {
    val testSim = new Simulation(fundamentalValue = 1, totalValue = 1)
    val testProbe = TestProbe()
    val prices = Table("price", 0.3, 0.5, 0.7)
    forAll(prices) { price =>
      val message = RequestForQuote(price, testProbe.ref)
      val testHedgeFundTrader = system.actorOf(Props(new HedgeFundTrader(initialWealth = 1, maxLeverage = 2, beta = 8, sim = testSim)))
      // mispriceCritical = maxLeverage / beta == 0.25 is smaller than the test misprices causing max leverage
      testHedgeFundTrader ! message
      testProbe.expectMsg(Demand(2 / price, HedgeFundDemand, testHedgeFundTrader))
    }
  }



}

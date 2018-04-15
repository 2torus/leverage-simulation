package leveragesim

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import leveragesim.DemandType.HedgeFundDemand
import leveragesim.Messages.{Demand, Price}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}



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
    val testHedgeFundTrader = system.actorOf(Props(new HedgeFundTrader(initialWealth=1, maxLeverage=1, beta=1, sim=testSim)))
    testHedgeFundTrader ! message
    testProbe.expectMsg(Demand(0, HedgeFundDemand, testHedgeFundTrader))
  }
}

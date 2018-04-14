package leveragesim

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.typesafe.scalalogging.Logger
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
    val logger = Logger(classOf[NoiseTraderSpec])
    val testProbe = TestProbe()
    val message = Price(1, testProbe.ref)
    val testNoiseTrader = system.actorOf(Props(new NoiseTrader(1, 1, 1, 1, 1)))
    testNoiseTrader ! message
    testProbe.expectMsgPF() { //
       case Demand(x: Double, "noise", `testNoiseTrader`) => Math.abs(x - 4.76635) should be <= 5e-6
    }
  }
}

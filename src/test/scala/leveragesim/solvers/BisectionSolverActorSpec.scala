package leveragesim.solvers

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class BisectionSolverActorSpec (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {
  class SquareActor extends Actor {
    def receive = {
      case (x:Double, sender:ActorRef) => sender ! x*x
    }
  }
  def this() = this(ActorSystem("HedgeFundTraderSpec"))
  override def afterAll: Unit = {
    shutdown(system)
  }
  "BisectionSolverActor" should "find the root of 5" in {
    val testProbe = TestProbe()
  }


}

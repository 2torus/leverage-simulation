package leveragesim.solvers

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import leveragesim.solvers.BisectionSolverActor.Success
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

//noinspection TypeAnnotation
class SquareActor extends Actor  with ActorLogging {
  log.info("actor started")
  def receive = {
    case (x:Double, sender:ActorRef) =>
      log.info(s"Received query at $x")
      sender ! x*x
    case msg => log.info(s"Received $msg")
  }
}

class BisectionSolverActorSpec (_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("BisectionSolverSpec"))
  override def afterAll: Unit = {
    shutdown(system)
  }
  "BisectionSolverActor" should "find the root of 5" in {
    val testProbe = TestProbe()
    val squareActor = system.actorOf(Props(new SquareActor()))
    system.actorOf(BisectionSolverActor.props(5, (0, 3),1e-7, squareActor, testProbe.ref))
    testProbe.expectMsgPF() {
      case Success(x) => Math.abs(x - Math.sqrt(5)) should be <= 1e-7
      //case msg: println(s"received $msg")
    }
  }

    it should "find the negative root of 5" in {
      val testProbe = TestProbe()
      val squareActor = system.actorOf(Props(new SquareActor()))
      system.actorOf(BisectionSolverActor.props(5, (0,-3), 1e-7, squareActor, testProbe.ref))
      testProbe.expectMsgPF() {
        case Success(x) => Math.abs(-x - Math.sqrt(5)) should be <= 1e-7
        //case msg: println(s"received $msg")
      }

  }

}


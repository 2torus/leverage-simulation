package leveragesim.solvers

import akka.actor.{Actor, ActorRef, Props}

object BisectionSolverActor {
  def props(targetValue: Double,
            interval: (Double, Double),
            relTol: Double,
            actorToQuery: ActorRef,
            querier: ActorRef) = Props(new BisectionSolverActor(targetValue, interval, relTol, actorToQuery, querier))

  case class Success(value: Double)

  case object Failure

}

//noinspection TypeAnnotation
/**
  * Simple bisection to explore solver actor behavior
  *
  */
class BisectionSolverActor(targetValue: Double,
                           var interval: (Double, Double),
                           relTol: Double,
                           actorToQuery: ActorRef,
                           querier: ActorRef) extends Actor {

  import BisectionSolverActor._

  def midPoint(interval: (Double, Double)) = (interval._1 + interval._2) / 2

  var nextIter = midPoint(interval)

  var numIters = 0
  val maxIters = 1000

  def success(value: Double) = Math.abs(value - targetValue) < relTol * Math.abs(targetValue)

  actorToQuery ! (nextIter, self)

  def receive = {
    case value: Double if success(value) => querier ! Success(nextIter)
    case value: Double if numIters == maxIters => querier ! Failure
    case value: Double =>
      numIters += 1
      if (value > targetValue) {
        interval = (interval._1, nextIter)
      } else {
        interval = (nextIter, interval._2)
      }
      nextIter = midPoint(interval)
      actorToQuery ! (nextIter, self)
  }
}

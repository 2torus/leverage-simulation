package leveragesim

import akka.actor.ActorRef

/**
  * Created by torus on 4/14/18.
  */
object Messages {
  case class Demand(units:Double, senderType:String, sender:ActorRef)
  case class Price(units:Double, exchange:ActorRef)
}

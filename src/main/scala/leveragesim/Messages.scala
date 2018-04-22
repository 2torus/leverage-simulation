package leveragesim

import akka.actor.ActorRef
import leveragesim.DemandType.DemandType

object Messages {
  case class Demand(units:Double, senderType:DemandType, sender:ActorRef)
  case class DemandQuote(units: Double , senderType: DemandType, sender:ActorRef)
  case class Price(units:Double, exchange:ActorRef)
  case class PriceQuote(units: Double, exchange: ActorRef)

}

object DemandType {
  sealed trait DemandType extends Product with Serializable
  case object NoiseDemand extends  DemandType
  case object HedgeFundDemand extends DemandType
}

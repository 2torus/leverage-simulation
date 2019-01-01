package leveragesim

import akka.actor.Actor
import leveragesim.DemandType.HedgeFundDemand
import leveragesim.Messages.{Demand, Price, RequestForQuote}

class HedgeFundTrader(initialWealth: Double, maxLeverage: Double, beta: Double, sim: Simulation) extends Actor {
  require(initialWealth > 0)
  var wealth = initialWealth
  var demand = 0.0
  var stock = 0.0
  val mispriceCritical = maxLeverage / beta
  def receive = {

    case RequestForQuote(price, exchange) =>
      val misprice = sim.fundamentalValue - price
      demand = misprice match  {
        case _ if misprice <= 0 => 0
        case _ if misprice < mispriceCritical =>
          beta * misprice * wealth / price
        case _ if misprice >= mispriceCritical =>
          maxLeverage * wealth / price
      }
      exchange ! Demand(demand, HedgeFundDemand, self)

    case Price(price, exchange) =>
      val misprice = sim.fundamentalValue - price
      misprice match  {
      case _ if misprice <= 0 =>
        wealth += stock * price
        stock = 0
        exchange ! Demand(0, HedgeFundDemand, self)
      }
  }
}

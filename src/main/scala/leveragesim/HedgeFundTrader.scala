package leveragesim

import akka.actor.Actor
import leveragesim.DemandType.HedgeFundDemand
import leveragesim.Messages.{Demand, Price}

class HedgeFundTrader(initialWealth: Double, maxLeverage: Double, beta: Double, sim: Simulation) extends Actor {
  require(initialWealth > 0)
  var wealth = initialWealth
  var leverage = 0.0
  var demand = 0.0
  def receive = {
    case Price(price, exchange) =>
      val misprice = sim.fundamentalValue - price
      if (misprice <= 0) exchange ! Demand(0, HedgeFundDemand, self)
  }
}

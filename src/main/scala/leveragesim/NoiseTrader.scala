package leveragesim

import akka.actor.Actor
import leveragesim.DemandType.NoiseDemand
import leveragesim.Messages.{Demand, Price, RequestForQuote}

import scala.util.Random

class NoiseTrader(rho:Double, sigma:Double, sim:Simulation, seed:Long) extends Actor {

  require(sigma > 0)
  require(rho >= 0 && rho <= 1)
  val fundamentalReturn = Math.log(sim.fundamentalValue * sim.totalValue)
  var currPriceLog = fundamentalReturn

  val random = new Random(seed)
  def calculateDemand() = {
    val noise = sigma * random.nextGaussian()
    rho * currPriceLog + noise + (1 - rho) * fundamentalReturn
  }

  def receive = {
    case Price(price, exchange) =>
      currPriceLog = calculateDemand()
      val nextDemand = math.exp(currPriceLog) / price
      exchange ! Demand(nextDemand, NoiseDemand, self)
    case RequestForQuote(price, exchange) =>
      val nextDemand = math.exp(currPriceLog) / price
      exchange ! Demand(nextDemand, NoiseDemand, self)
  }
}

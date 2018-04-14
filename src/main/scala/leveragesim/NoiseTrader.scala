package leveragesim

import akka.actor.Actor
import leveragesim.Messages.{Demand, Price}

import scala.util.Random

class NoiseTrader(rho:Double, sigma:Double, V:Double, N:Double, seed:Long) extends Actor {
  require(V > 0)
  require(N > 0)
  require(sigma > 0)
  require(rho >= 0 && rho <= 1)
  val fundamentalReturn = Math.log(V*N)
  var currPriceLog = fundamentalReturn

  val random = new Random(seed)
  def calculate_demand() = {
    val noise = sigma * random.nextDouble
    rho * currPriceLog + noise + (1 - rho) * fundamentalReturn
  }
  def receive = {
    case Price(price, exchange) =>
      currPriceLog = calculate_demand()
      val nextDemand = math.exp(currPriceLog) / price
      exchange ! Demand(nextDemand, "noise", self )
  }
}

package leveragesim

class Simulation(val fundamentalValue: Double, val totalValue: Double) {
  require(fundamentalValue > 0)
  require(totalValue > 0)
}

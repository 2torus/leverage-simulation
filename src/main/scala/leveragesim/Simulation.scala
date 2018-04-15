package leveragesim

class Simulation(val initialWealth: Double, val totalValue: Double) {
  require(initialWealth > 0)
  require(totalValue > 0)
}

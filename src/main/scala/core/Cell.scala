package core

import core.State.State

case class Cell(state: State, humidity: Double, timesBurnt: Int = 0, fireAge: Int = 0, fireProbability: Double = 0, growthProbability: Double = 0) {
  def step(neighbors: List[(Int, Cell)])(implicit settings: Settings): Cell = {
    val cell2: Cell = this.copy(
      humidity=(
        if (state == State.WATER) 1
        else Math.clamp(
          humidity * (1 - settings.GLOBAL_HUMIDITY_DECREASE_RATE) + (
            neighbors.map(p => p._2.humidity).sum / neighbors.length - humidity
          ) * settings.HUMIDITY_PROPAGATION_RATE,
          0, 1
        )
      ),
      fireAge=0,
      fireProbability=getFireProbability(neighbors),
      growthProbability=getGrowthProbability(neighbors)
    )
    val nbFireAge: Int = neighbors.foldLeft(0)((a, b) => math.max(a, b._2.fireAge))

    state match {
      case State.ALIVE if Math.random() < cell2.fireProbability =>
        cell2.copy(
          state=State.FIRE,
          humidity=Math.max(0, cell2.humidity - settings.BURN_HUMIDITY_DECREASE),
          timesBurnt=timesBurnt + 1,
          fireAge=nbFireAge + 1
        )
      case State.FIRE =>
        cell2.copy(state=State.DEAD)
      case State.DEAD if Math.random() < cell2.growthProbability =>
        cell2.copy(
          state=State.ALIVE,
          humidity=Math.min(1, cell2.humidity + settings.GROWTH_HUMIDITY_BOOST)
        )
      case _ => cell2
    }
  }

  def getFireProbability(neighbors: List[(Int, Cell)])(implicit settings: Settings): Double = {
    settings.FIRE_PROBABILITY_RATIO * (
      neighbors.map(p => if (p._2.state == State.FIRE) {
        Math.pow(
          p._1 match {
            case 4 => 0.5
            case 2 | 7 => 0.75
            case 0 | 5 => 1.5
            case 3 => 2
            case _ => 1
          },
          settings.WIND_SPEED
        )
      } else 0).sum + settings.FIRE_PROBABILITY_OFFSET
    ) * Math.sqrt(1 - humidity)
  }

  def getGrowthProbability(neighbors: List[(Int, Cell)])(implicit settings: Settings): Double = {
    settings.GROWTH_PROBABILITY_RATIO * (
      neighbors.count(p => p._2.state == State.ALIVE) + settings.GROWTH_PROBABILITY_OFFSET
    ) * Math.sqrt(humidity)
  }
}

object Cell {
  def random(implicit settings: Settings): Cell = {
    val state: State = if (Math.random() < settings.INITIAL_FIRE_PROBABILITY) {
      State.FIRE
    } else {
      State.ALIVE
    }
    val humidity: Double = Math.random() * 0.1
    return Cell(state, humidity)
  }
}
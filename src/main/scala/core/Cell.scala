package core

import core.State.State

case class Cell(state: State, humidity: Double, windDirection: Int) {
  private case class Stats(alive: Int = 0, dead: Int = 0, burning: Int = 0)

  def step(neighbors: List[Cell]): Cell = {
    //print(neighbors.length)
    val stats: Stats = neighbors.foldLeft(Stats())((stats, elem) => {
      elem.state match {
        case State.ALIVE => Stats(stats.alive + 1, stats.dead, stats.burning)
        case State.FIRE => Stats(stats.alive, stats.dead, stats.burning + 1)
        case State.DEAD => Stats(stats.alive, stats.dead + 1, stats.burning)
      }
    })

    /*if (stats.alive > stats.dead && stats.alive > stats.burning) {
      Cell(State.ALIVE)
    } else if (stats.dead > stats.burning) {
      Cell(State.DEAD)
    } else {
      Cell(State.FIRE)
    }*/
    if (state == State.ALIVE && stats.burning != 0) {
      Cell(State.FIRE, humidity, windDirection)
    } else if (state == State.FIRE) {
      Cell(State.DEAD, humidity, windDirection)
    } else {
      this
    }
  }
}

object Cell {
  def random(): Cell = {
    val state: State = if (Math.random() < 0.01) {
      State.FIRE
    } else {
      State.ALIVE
    }
    val humidity: Double = Math.random()
    val windDirection: Int = (Math.random() * 8).toInt
    return Cell(state, humidity, windDirection)
  }
}
package core

import core.Cell.Properties
import core.State.State

case class Cell(state: State, properties: Properties) {
  def step(neighbors: List[(Int, Cell)]): Cell = {
    val props2: Properties = neighbors.foldLeft(getBaseProperties)(influenceProps)
    val props3: Properties = Properties(
      props2.fireProbability,
      props2.growthProbability,
      (props2.humidity + properties.humidity) / (neighbors.length + 1)
    )

    state match {
      case State.ALIVE if Math.random() < props3.fireProbability =>
        Cell(State.FIRE, Properties(0, 0, props3.humidity))
      case State.FIRE =>
        Cell(State.DEAD, Properties(0, props3.growthProbability, props3.humidity))
      case State.DEAD if Math.random() < props3.growthProbability =>
        Cell(State.ALIVE, Properties(props3.fireProbability, 0, props3.humidity))
      case _ => Cell(state, props3)
    }
  }

  private def getBaseProperties: Properties = {
    val fireProbability = properties.fireProbability - (
      if (state == State.ALIVE) {properties.humidity * 0.5}
      else {0}
    )
    val growthProbability = properties.growthProbability + (
      if (state == State.DEAD) {properties.humidity * 0.5}
      else {0}
    )
    Properties(fireProbability, growthProbability, 0)
  }

  private def influenceProps(props: Properties, dirAndNb: (Int, Cell)): Properties = {
    val dir: Int = dirAndNb._1
    val nb: Cell = dirAndNb._2

    val fireProbability = props.fireProbability + (
      if (state == State.ALIVE && nb.state == State.FIRE) {0.2}
      else {0}
    )
    val growthProbability = props.growthProbability - (
      if (state == State.DEAD && nb.state == State.FIRE) {0.1}
      else {0}
    )
    val humidity = props.humidity + nb.properties.humidity + (
      if (nb.state == State.WATER) {0.1}
      else {0}
    )

    return Properties(
      Math.clamp(fireProbability, 0, 1),
      Math.clamp(growthProbability, 0, 1),
      Math.clamp(humidity, 0, 1)
    )
  }
}

object Cell {
  case class Properties(fireProbability: Double, growthProbability: Double, humidity: Double) {
    def setHumidity(humidity: Double): Properties = {
      Properties(fireProbability, growthProbability, humidity)
    }
  }

  def random(): Cell = {
    val state: State = if (Math.random() < 0.01) {
      State.FIRE
    } else {
      State.ALIVE
    }
    val humidity: Double = Math.random() * 0.3
    //val windDirection: Int = (Math.random() * 8).toInt
    return Cell(state, Properties(0, 0, humidity))
  }
}
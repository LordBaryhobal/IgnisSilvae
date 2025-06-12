package core

import core.Cell.Properties
import core.State.State

case class Cell(state: State, properties: Properties, timesBurnt: Int = 0, fireAge: Int = 0) {
  def step(neighbors: List[(Int, Cell)]): Cell = {
    val props2: Properties = neighbors.foldLeft(getBaseProperties)(influenceProps)
    val nbFireAge: Int = neighbors.foldLeft(0)((a, b) => math.max(a, b._2.fireAge))

    state match {
      case State.ALIVE if Math.random() < props2.fireProbability =>
        Cell(State.FIRE, Properties(0, 0, props2.humidity), timesBurnt + 1, nbFireAge + 1)
      case State.FIRE =>
        Cell(State.DEAD, Properties(0, props2.growthProbability, props2.humidity), timesBurnt)
      case State.DEAD if Math.random() < props2.growthProbability / 5 =>
        Cell(State.ALIVE, Properties(
          Math.max(0, props2.fireProbability - Settings.GROWTH_FIRE_DECREASE),
          0,
          Math.min(1, props2.humidity + Settings.GROWTH_HUMIDITY_INCREASE)
        ), timesBurnt)
      case _ => Cell(state, props2, timesBurnt)
    }
  }

  private def getBaseProperties: Properties = {
    val fireProbability = properties.fireProbability - (
      if (state == State.ALIVE) {properties.humidity * Settings.BASE_HUMIDITY_FIRE_DECREASE}
      else {0}
    )
    val growthProbability = properties.growthProbability + (
      if (state == State.DEAD) {properties.humidity * Settings.BASE_HUMIDITY_GROWTH_INCREASE}
      else {0}
    )
    Properties(fireProbability, growthProbability, properties.humidity)
  }

  private def influenceProps(props: Properties, dirAndNb: (Int, Cell)): Properties = {
    val dir: Int = dirAndNb._1
    val nb: Cell = dirAndNb._2

    val fireProbability = props.fireProbability + (
      if (state == State.ALIVE && nb.state == State.FIRE) {Settings.NB_FIRE_INFLUENCE}
      else {0}
    )
    val growthProbability = props.growthProbability + (
      if (state == State.DEAD && nb.state == State.FIRE) {-Settings.NB_FIRE_GROWTH_DECREASE}
      else if (state == State.DEAD && nb.state == State.ALIVE) {Settings.NB_ALIVE_GROWTH_INCREASE}
      else {0}
    )
    val humidity = props.humidity + (nb.properties.humidity - properties.humidity) * Settings.NB_HUMIDITY_INFLUENCE + (
      if (nb.state == State.WATER) {Settings.NB_WATER_HUMIDITY_INCREASE}
      else if (nb.state == State.FIRE) {-Settings.NB_FIRE_HUMIDITY_DECREASE}
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
    val state: State = if (Math.random() < Settings.INITIAL_FIRE_PROBABILITY) {
      State.FIRE
    } else {
      State.ALIVE
    }
    val humidity: Double = Math.random() * 0.3
    //val windDirection: Int = (Math.random() * 8).toInt
    return Cell(state, Properties(0, 0, humidity))
  }
}
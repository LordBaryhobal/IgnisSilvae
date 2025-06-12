package core

import core.World.dirFromOffset
import noise.OpenSimplexNoise

import scala.util.Random

case class World(grid: List[List[Cell]], width: Int, height: Int) {
  def getNeighbors(x: Int, y: Int): List[(Int, Cell)] = {
    (for (
      y2 <- y - 1 to y + 1;
      x2 <- x - 1 to x + 1
      if y2 >= 0 && y2 < height
      if x2 >= 0 && x2 < width
      if x2 != x || y2 != y
    ) yield {
      (dirFromOffset(x2-x, y2-y), grid(y2)(x2))
    }).toList
  }

  def step(): World = {
    val grid2: List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield grid(y)(x).step(
          getNeighbors(x, y)
        )
      ).toList
    ).toList
    return World(grid2, width, height)
  }

  def getFireDensity: Double = {
    grid.foldLeft(0) {
      (cnt, row) => row.foldLeft(cnt) {
        (cnt2, cell) => if (cell.state == State.FIRE) {cnt2 + 1} else cnt2
      }
    } / (width * height).toDouble
  }

  def getTreeDensity: Double = {
    grid.foldLeft(0) {
      (cnt, row) => row.foldLeft(cnt) {
        (cnt2, cell) => if (cell.state == State.ALIVE) {cnt2 + 1} else cnt2
      }
    } / (width * height).toDouble
  }

  def getMeanFireAge: Double = {
    val p: (Int, Int) = grid.foldLeft((0, 0)) {
      (p, row) => row.foldLeft(p) {
        (p2, cell) => if (cell.state == State.FIRE) {(p._1 + cell.fireAge, p._2 + 1)} else p2
      }
    }
    return p._1 / p._2.toDouble
  }

  def printStats(): Unit = {
    case class Stats(cnt: Int, fire: Double, growth: Double, humidity: Double)
    val stats: Stats = grid.foldLeft(Stats(0, 0, 0, 0)) {
      (stats1, row) => row.foldLeft(stats1) {
        (stats2, cell) => Stats(
          stats2.cnt + 1,
          stats2.fire + cell.properties.fireProbability,
          stats2.growth + cell.properties.growthProbability,
          stats2.humidity + cell.properties.humidity
        )
      }
    }

    //println(s"Mean fire probability: ${stats.fire / stats.cnt * 100} %")
    //println(s"Mean growth probability: ${stats.growth / stats.cnt * 100} %")
    //println(s"Mean humidity: ${stats.humidity / stats.cnt * 100} %")
  }
}


object World {
  val OFFSETS: List[(Int, Int)] = List(
    (-1, -1),
    ( 0, -1),
    ( 1, -1),
    (-1,  0),
    ( 1,  0),
    (-1,  1),
    ( 0,  1),
    ( 1,  1)
  )

  def make(width: Int, height: Int): World = {
    val random: Random = new Random()
    val seed: Long = random.nextLong()
    val noise: OpenSimplexNoise = new OpenSimplexNoise(seed)
    println(s"Seed: $seed")
    val grid: List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield {
          val value: Double = (noise.eval(
            x / Settings.SIMPLEX_FEATURE_SIZE,
            y / Settings.SIMPLEX_FEATURE_SIZE,
            0.0
          ) + 1) / 2
          if (value > Settings.WATER_THRESHOLD) {
            Cell.random()
          } else {
            Cell(State.WATER, Cell.Properties(0, 0, 1))
          }
        }
      ).toList
    ).toList

    return World(
      grid,
      width,
      height
    )
  }

  def dirFromOffset(dx: Int, dy: Int): Int = {
    OFFSETS.indexOf((dx, dy))
  }
}
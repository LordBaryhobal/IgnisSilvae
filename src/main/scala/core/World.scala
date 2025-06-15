package core

import noise.OpenSimplexNoise

import scala.util.Random

case class World(settings: Settings, grid: List[List[Cell]], width: Int, height: Int) {
  implicit val simSettings: Settings = settings

  def getNeighbors(x: Int, y: Int): List[(Int, Cell)] = {
    World.OFFSETS_WITH_I.map {
      case ((dx, dy), dir) => {
        val x2 = x + dx
        val y2 = y + dy
        if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height) {
          Some((dir, grid(y2)(x2)))
        } else None
      }
    }.filterNot(n => n.isEmpty).map(n => n.get)
  }

  def step(): World = {
    val grid2: List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield grid(y)(x).step(
          getNeighbors(x, y)
        )
      ).toList
    ).toList
    return World(settings, grid2, width, height)
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
}


object World {
  private val OFFSETS: List[(Int, Int)] = List(
    (-1, -1),
    ( 0, -1),
    ( 1, -1),
    (-1,  0),
    ( 1,  0),
    (-1,  1),
    ( 0,  1),
    ( 1,  1)
  )
  private val OFFSETS_WITH_I: List[((Int, Int), Int)] = OFFSETS.zipWithIndex

  def make(settings: Settings): World = {
    val width: Int = settings.WORLD_WIDTH
    val height: Int = settings.WORLD_HEIGHT
    val random: Random = new Random()
    val seed: Long = random.nextLong()
    val noise: OpenSimplexNoise = new OpenSimplexNoise(seed)
    println(s"Seed: $seed")
    val grid: List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield {
          val value: Double = (noise.eval(
            x / settings.SIMPLEX_FEATURE_SIZE,
            y / settings.SIMPLEX_FEATURE_SIZE,
            0.0
          ) + 1) / 2
          if (value > settings.WATER_THRESHOLD) {
            Cell.random(settings)
          } else {
            Cell(State.WATER, 1)
          }
        }
      ).toList
    ).toList

    return World(
      settings,
      grid,
      width,
      height
    )
  }
}
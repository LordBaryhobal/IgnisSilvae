package core

case class World(grid: List[List[Cell]], width: Int, height: Int) {
  def getNeighbors(x: Int, y: Int): List[Cell] = {
    (for (
      y2 <- y - 1 to y + 1;
      x2 <- x - 1 to x + 1
      if y2 >= 0 && y2 < height
      if x2 >= 0 && x2 < width
      if x2 != x || y2 != y
    ) yield grid(y2)(x2)).toList
  }

  def step(): World = {
    //println("Step")
    val grid2: List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield grid(y)(x).step(
          getNeighbors(x, y)
        )
      ).toList
    ).toList
    return World(grid2, width, height)
  }
}


object World {
  def make(width: Int, height: Int): World = {
    val grid: List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield Cell.random()
      ).toList
    ).toList

    return World(
      grid,
      width,
      height
    )
  }
}
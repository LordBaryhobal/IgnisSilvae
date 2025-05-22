package core

case class World(val grid: List[List[Cell]], val width: Int, val height: Int) {

}


object World {
  def make(width: Int, height: Int): World = {
    val grid : List[List[Cell]] = (
      for (y <- 0 until height) yield (
        for (x <- 0 until width) yield Cell(
          (Math.random() * 3).toInt match {
            case 0 => State.ALIVE
            case 1 => State.FIRE
            case _ => State.DEAD
          }
        )
      ).toList
    ).toList

    return World(
      grid,
      width,
      height
    )
  }
}
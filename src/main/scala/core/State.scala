package core

object State extends Enumeration {
  type State = Value
  val ALIVE, FIRE, DEAD, WATER = Value
}

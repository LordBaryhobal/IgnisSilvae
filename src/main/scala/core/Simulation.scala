package core

import java.io.{FileOutputStream, OutputStream, PrintWriter}
import java.net.Socket
import java.nio.ByteBuffer
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine

class Simulation {
  private var world: World = World.make(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT)
  private val burnFrequency: Array[Array[Double]] = Array.fill(world.height, world.width)(0)
  private val fireAge: Array[Array[Double]] = Array.fill(world.height, world.width)(0)
  private val socket: Option[Socket] = if (Settings.SOCKET_ENABLED) Some(
    new Socket(Settings.SOCKET_HOST, Settings.SOCKET_PORT)
  ) else None
  private val socketOut: Option[OutputStream] = if (Settings.SOCKET_ENABLED) Some(socket.get.getOutputStream) else None
  private var stepI: Int = 0

  private val history: ArrayBuffer[HistoryEntry] = new ArrayBuffer[HistoryEntry]()
  logStats()

  def stop(): Unit = {
    if (Settings.SOCKET_ENABLED) {
      socket.get.close()
    }
  }

  def getWorld: World = world

  def getBurnFrequencyAt(x: Int, y: Int): Double = burnFrequency(y)(x)
  def getFireAgeAt(x: Int, y: Int): Double = fireAge(y)(x)

  def step(): Unit = {
    world = world.step()
    world.printStats()
    val maxBurns: Double = world.grid.foldLeft(0)((max, row) => {
      row.foldLeft(max)((max2, cell) => {
        math.max(max2, cell.timesBurnt)
      })
    })
    val maxFireAge: Double = world.grid.foldLeft(0)((max, row) => {
      row.foldLeft(max)((max2, cell) => {
        math.max(max2, cell.fireAge)
      })
    })
    world.grid.zipWithIndex.foreach(p1 => {
      val y: Int = p1._2
      p1._1.zipWithIndex.foreach(p2 => {
        val x: Int = p2._2
        burnFrequency(y)(x) = p2._1.timesBurnt / maxBurns
        fireAge(y)(x) = p2._1.fireAge / 100.0
      })
    })
    logStats()
    stepI += 1
  }

  def logStats(): Unit = {
    val fireDensity: Double = world.getFireDensity
    val treeDensity: Double = world.getTreeDensity
    val meanAge: Double = world.getMeanFireAge
    history.addOne(HistoryEntry(fireDensity, treeDensity, meanAge))

    val nBytes: Int = 4 + 8 + 8 + 8

    if (Settings.SOCKET_ENABLED) {
      val buf: ByteBuffer = (
        ByteBuffer.allocateDirect(nBytes)
          .putInt(stepI)
          .putDouble(fireDensity)
          .putDouble(treeDensity)
          .putDouble(meanAge)
          .flip()
      )
      val arr: Array[Byte] = Array.fill(nBytes)(0)
      buf.get(arr)
      socketOut.get.write(arr)
      socketOut.get.flush()
    }
  }

  def exportStats(path: String = "stats.csv"): Unit = {
    val writer: PrintWriter = new PrintWriter(new FileOutputStream(path))
    writer.println("step,fire_density,tree_density,fire_age")
    history.zipWithIndex.foreach(p => {
      writer.println(s"${p._2},${p._1.fireDensity},${p._1.treeDensity},${p._1.fireAge}")
    })
    writer.close()
  }

  private case class HistoryEntry(fireDensity: Double, treeDensity: Double, fireAge: Double)
}

object Simulation {
  def runSimulation(maxSteps: Int = -1): Simulation = {
    val simulation: Simulation = new Simulation
    var running: Boolean = true
    val thread: Thread = new Thread(() => {
      while (running && (maxSteps == -1 || maxSteps > simulation.stepI)) {
        print(s"\rStep ${simulation.stepI}")
        simulation.step()
      }
      simulation.stop()
    })
    println("Starting simulation")
    if (maxSteps == -1) println("Press ENTER to stop")
    thread.start()

    if (maxSteps == -1) {
      readLine()
      println()
      println("Stopping simulation")
      running = false
    }
    thread.join()
    return simulation
  }

  def main(args: Array[String]): Unit = {
    List.range(0, 10).foreach(n => {
      Settings.BASE_HUMIDITY_FIRE_DECREASE = n / 10.0
      println(s"BASE_HUMIDITY_FIRE_DECREASE = ${Settings.BASE_HUMIDITY_FIRE_DECREASE}")
      val simulation: Simulation = runSimulation(1000)
      simulation.exportStats(s"stats_${n}.csv")
    })
  }
}

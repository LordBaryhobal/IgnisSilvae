package core

import java.io.{FileOutputStream, OutputStream, PrintWriter}
import java.lang.reflect.Field
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.CollectionConverters._
import scala.io.StdIn.readLine

class Simulation(val settings: Settings) {
  private var world: World = World.make(settings)
  private val burnFrequency: Array[Array[Double]] = Array.fill(world.height, world.width)(0)
  private val fireAge: Array[Array[Double]] = Array.fill(world.height, world.width)(0)
  private val socket: Option[Socket] = if (settings.SOCKET_ENABLED) Some(
    new Socket(settings.SOCKET_HOST, settings.SOCKET_PORT)
  ) else None
  private val socketOut: Option[OutputStream] = if (settings.SOCKET_ENABLED) Some(socket.get.getOutputStream) else None
  private var stepI: Int = 0

  private val history: ArrayBuffer[HistoryEntry] = new ArrayBuffer[HistoryEntry]()
  logStats()

  def stop(): Unit = {
    if (settings.SOCKET_ENABLED) {
      socket.get.close()
    }
  }

  def getWorld: World = world

  def getBurnFrequencyAt(x: Int, y: Int): Double = burnFrequency(y)(x)
  def getFireAgeAt(x: Int, y: Int): Double = fireAge(y)(x)

  def step(): Unit = {
    world = world.step()
    val maxBurns: Double = world.grid.foldLeft(0)((max, row) => {
      row.foldLeft(max)((max2, cell) => {
        math.max(max2, cell.timesBurnt)
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

    if (settings.SOCKET_ENABLED) {
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
    history.zipWithIndex.foreach { case (entry, i) =>
      writer.println(s"$i,${entry.fireDensity},${entry.treeDensity},${entry.fireAge}")
    }
    writer.close()
  }

  private case class HistoryEntry(fireDensity: Double, treeDensity: Double, fireAge: Double)
}

object Simulation {
  private def runSimulation(settings: Settings, maxSteps: Int = -1, debug: Boolean = true): Simulation = {
    val simulation: Simulation = new Simulation(settings)
    var running: Boolean = true
    val thread: Thread = new Thread(() => {
      while (running && (maxSteps == -1 || maxSteps > simulation.stepI)) {
        if (debug) print(s"\rStep ${simulation.stepI}")
        simulation.step()
      }
      simulation.stop()
    })
    if (debug) {
      println("Starting simulation")
      if (maxSteps == -1) println("Press ENTER to stop")
    }
    thread.start()

    if (maxSteps == -1) {
      readLine()
      if (debug) {
        println()
        println("Stopping simulation")
      }
      running = false
    }
    thread.join()
    return simulation
  }

  private def multiSimulation(settings: Settings,
                              setting: String,
                              vMin: Double,
                              vMax: Double,
                              samples: Int,
                              maxSteps: Int,
                              outputDir: Path): Unit = {

    if (Files.notExists(outputDir)) Files.createDirectory(outputDir)

    val finished: AtomicInteger = new AtomicInteger(0)

    val t0: Long = System.currentTimeMillis()
    List.range(0, samples).par.foreach(i => {
      val value: Double = vMin + (vMax - vMin) * i / (samples - 1)
      val settings2: Settings = settings.copy
      val field: Field = settings2.getClass.getDeclaredField(setting)
      field.setAccessible(true)
      field.setDouble(settings2, value)
      println(s"Starting simulation $i ($setting = $value)")
      val simulation: Simulation = runSimulation(settings2, maxSteps, debug=false)
      val path: Path = Paths.get(outputDir.toString, s"$i.csv")
      simulation.exportStats(path.toString)
      println(s"Simulation $i has finished (${finished.incrementAndGet()} / $samples)")
    })
    val t1: Long = System.currentTimeMillis()
    println(s"Completed in ${(t1 - t0) / 1000.0}s")
  }

  def main(args: Array[String]): Unit = {
    multiSimulation(
      new Settings(),
      "FIRE_PROBABILITY_RATIO",
      0,
      1,
      64,
      1000,
      Paths.get("stats/fire_probability_ratio")
    )
  }
}

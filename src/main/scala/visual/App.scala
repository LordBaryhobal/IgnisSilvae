package visual

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.{Files, Gdx}
import core.{Cell, State, World}
import org.lwjgl.opengl.Display
import visual.Layer.Layer

import java.io.{FileOutputStream, PrintWriter}
import java.nio.ByteBuffer
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


class App extends PortableApplication(800, 800) {
  private val TITLE: String = "IgnisSilvae"
  private val iconSizes: Array[Int] = Array(16, 32, 48, 64, 128, 256)
  System.setProperty("LWJGL_WM_CLASS", TITLE) // See https://github.com/LWJGL/lwjgl/issues/67

  private val size = 8

  private val textures: mutable.Map[Color, Texture] = new mutable.HashMap()

  private var world: World = World.make(100, 100)
  private var i: Int = 1
  private var layer: Layer = Layer.ALL
  private val fireDensity: ArrayBuffer[Double] = new ArrayBuffer[Double]()
  logFireDensity()

  override def onInit(): Unit = {
    setTitle(TITLE)
    setIcons(iconSizes.map((size: Int) => s"res/icon_$size.png"))
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    val winWidth: Float = getWindowWidth
    val winHeight: Float = getWindowHeight
    //val size: Float = Math.min(winWidth / world.width, winHeight / world.height)
    val ox: Float = (winWidth - (world.width - 1) * size) / 2
    val oy: Float = (winHeight - (world.height - 1) * size) / 2
    world.grid.zipWithIndex.foreach(p1 => {
      val y: Int = p1._2
      p1._1.zipWithIndex.foreach(p2 => {
        val x: Int = p2._2
        val color: Color = getCellColor(p2._1)
        val texture: Texture = getColorTexture(color)
        g.draw(texture, ox + x * size, oy + y * size)
      })
    })

    g.drawString(0f, 800f, "Layer: " + (layer match {
      case Layer.ALL => "All"
      case Layer.STATE => "State"
      case Layer.FIRE_PROBABILITY => "Fire probability"
      case Layer.GROWTH_PROBABILITY => "Growth probability"
      case Layer.HUMIDITY => "Humidity"
    }))

    g.drawFPS()

    if (i == 0) {
      world = world.step()
      world.printStats()
      logFireDensity()
    }

    i += 1
    //i %= 60
    i %= 5

  }

  private def setIcons(paths: Array[String]): Unit = {
    val icons: Array[ByteBuffer] = new Array(paths.length)
    for ((path: String, i: Int) <- paths.zipWithIndex) {
      var pixmap: Pixmap = new Pixmap(Gdx.files.getFileHandle(path, Files.FileType.Internal))
      if (pixmap.getFormat != Format.RGBA8888) {
        val rgba: Pixmap = new Pixmap(pixmap.getWidth, pixmap.getHeight, Format.RGBA8888)
        rgba.drawPixmap(pixmap, 0, 0)
        pixmap.dispose()
        pixmap = rgba
      }
      icons(i) = ByteBuffer.allocateDirect(pixmap.getPixels.limit())
      icons(i).put(pixmap.getPixels).flip()
      pixmap.dispose()
    }
    Display.setIcon(icons)
  }

  def getColorTexture(color: Color): Texture = {
    textures.get(color) match {
      case Some(texture) => texture
      case None => {
        val pixmap: Pixmap = new Pixmap(size, size, Format.RGB888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture: Texture = new Texture(pixmap)
        textures.put(color, texture)
        texture
      }
    }
  }

  def getCellColor(cell: Cell): Color = {
    layer match {
      case Layer.ALL => {
        cell.state match {
          case State.ALIVE => Color.GREEN
          case State.FIRE => Color.ORANGE
          case State.DEAD => Color.BLACK
          case _ => Color.GRAY
        }
      }
      case Layer.STATE => {
        cell.state match {
          case State.ALIVE => Color.GREEN
          case State.FIRE => Color.ORANGE
          case State.DEAD => Color.BLACK
          case _ => Color.GRAY
        }
      }
      case Layer.FIRE_PROBABILITY => {
        Color.GREEN.cpy().lerp(Color.RED, cell.properties.fireProbability.toFloat)
      }
      case Layer.GROWTH_PROBABILITY => {
        Color.GRAY.cpy().lerp(Color.GREEN, cell.properties.growthProbability.toFloat)
      }
      case Layer.HUMIDITY => {
        Color.WHITE.cpy().lerp(Color.BLUE, cell.properties.humidity.toFloat)
      }
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    if (keycode == Keys.NUM_1 || keycode == Keys.A) {layer = Layer.ALL}
    else if (keycode == Keys.NUM_2 || keycode == Keys.S) {layer = Layer.STATE}
    else if (keycode == Keys.NUM_3 || keycode == Keys.F) {layer = Layer.FIRE_PROBABILITY}
    else if (keycode == Keys.NUM_4 || keycode == Keys.G) {layer = Layer.GROWTH_PROBABILITY}
    else if (keycode == Keys.NUM_5 || keycode == Keys.H) {layer = Layer.HUMIDITY}
    else if (keycode == Keys.E) {exportStats()}
  }

  def logFireDensity(): Unit = {
    val density: Double = world.getFireDensity
    fireDensity.addOne(density)
  }

  def exportStats(): Unit = {
    val writer: PrintWriter = new PrintWriter(new FileOutputStream("fire_density.csv"))
    fireDensity.foreach(d => {
      writer.println(d)
    })
    writer.close()
  }
}

object App {
  private var instance: App = _

  def main(args: Array[String]): Unit = {
    instance = new App
  }
}
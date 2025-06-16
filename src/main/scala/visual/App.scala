package visual

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.{Files, Gdx}
import core._
import org.lwjgl.opengl.Display
import visual.Layer.Layer

import java.nio.ByteBuffer
import scala.collection.mutable


class App(settings: Settings) extends PortableApplication(settings.CELL_SIZE * settings.WORLD_WIDTH, settings.CELL_SIZE * settings.WORLD_HEIGHT) {
  private val TITLE: String = "IgnisSilvae"
  private val iconSizes: Array[Int] = Array(16, 32, 48, 64, 128, 256)
  System.setProperty("LWJGL_WM_CLASS", TITLE) // See https://github.com/LWJGL/lwjgl/issues/67

  private val size = settings.CELL_SIZE

  private val textures: mutable.Map[Color, Texture] = new mutable.HashMap()

  private val sim: Simulation = new Simulation(settings)

  private var layer: Layer = Layer.STATE
  private var i: Int = 1

  private var fast: Boolean = false

  override def onInit(): Unit = {
    setTitle(TITLE)
    setIcons(iconSizes.map((size: Int) => s"res/icon_$size.png"))
  }

  override def exit(): Unit = {
    sim.stop()
    super.exit()
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val world: World = sim.getWorld
    g.clear()
    val winWidth: Float = getWindowWidth
    val winHeight: Float = getWindowHeight
    val ox: Float = (winWidth - world.width * size) / 2
    val oy: Float = (winHeight - world.height * size) / 2
    world.grid.zipWithIndex.foreach(p1 => {
      val y: Int = p1._2
      p1._1.zipWithIndex.foreach(p2 => {
        val x: Int = p2._2
        val color: Color = getCellColor(p2._1, x, y)
        val texture: Texture = getColorTexture(color)
        g.draw(texture, ox + x * size, oy + y * size)
      })
    })

    g.setColor(Color.WHITE)
    g.drawString(0f, getWindowHeight, "Layer: " + (layer match {
      case Layer.STATE => "State"
      case Layer.TIMES_BURNT => "Burn frequency"
      case Layer.FIRE_PROBABILITY => "Fire probability"
      case Layer.GROWTH_PROBABILITY => "Growth probability"
      case Layer.HUMIDITY => "Humidity"
      case Layer.FIRE_AGE => "Fire age"
    }))

    g.drawFPS()

    if (i == 0 || fast) {
      sim.step()
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

  def getCellColor(cell: Cell, x: Int, y: Int): Color = {
    layer match {
      case Layer.STATE => {
        cell.state match {
          case State.ALIVE => Color.GREEN
          case State.FIRE => Color.ORANGE
          case State.DEAD => Color.BLACK
          case State.WATER => Color.BLUE
          case _ => Color.GRAY
        }
      }
      case Layer.TIMES_BURNT => {
        cell.state match {
          case State.WATER => Color.BLUE
          case _ => lerpColor(Color.GREEN, Color.RED, sim.getBurnFrequencyAt(x, y))
        }
      }
      case Layer.FIRE_PROBABILITY => {
        if (cell.state == State.WATER) Color.BLUE
        else lerpColor(Color.GREEN, Color.RED, cell.fireProbability)
      }
      case Layer.GROWTH_PROBABILITY => {
        if (cell.state == State.WATER) Color.BLUE
        else lerpColors(List(Color.GRAY, Color.YELLOW, Color.GREEN, Color.BLUE), cell.growthProbability)
      }
      case Layer.HUMIDITY => {
        lerpColor(Color.WHITE, Color.BLUE, cell.humidity)
      }
      case Layer.FIRE_AGE => {
        cell.state match {
          case State.FIRE => lerpColor(Color.GRAY, Color.RED, sim.getFireAgeAt(x, y))
          case _ => Color.BLACK
        }
      }
    }
  }

  private def lerpColor(color1: Color, color2: Color, value: Double): Color = {
    color1.cpy().lerp(color2, math.round(value * 100).toFloat / 100)
  }

  private def lerpColors(colors: List[Color], value: Double): Color = {
    val i: Int = ((colors.length - 1) * value).toInt
    if (i == colors.length - 1) return colors(i)
    val j: Int = i + 1
    val value2: Double = value * (colors.length - 1) - i
    colors(i).cpy().lerp(colors(j), math.round(value2 * 100).toFloat / 100)
  }

  override def onKeyDown(keycode: Int): Unit = {
    if (keycode == Keys.NUM_1 || keycode == Keys.S) {layer = Layer.STATE}
    else if (keycode == Keys.NUM_2 || keycode == Keys.B) {layer = Layer.TIMES_BURNT}
    else if (keycode == Keys.NUM_3 || keycode == Keys.F) {layer = Layer.FIRE_PROBABILITY}
    else if (keycode == Keys.NUM_4 || keycode == Keys.G) {layer = Layer.GROWTH_PROBABILITY}
    else if (keycode == Keys.NUM_5 || keycode == Keys.H) {layer = Layer.HUMIDITY}
    else if (keycode == Keys.NUM_6 || keycode == Keys.A) {layer = Layer.FIRE_AGE}
    else if (keycode == Keys.E) {sim.exportStats()}
    else if (keycode == Keys.SPACE) {fast = !fast}
  }
}

object App {
  private var instance: App = _

  def main(args: Array[String]): Unit = {
    instance = new App(new Settings())
  }
}
package visual

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import core.{State, World}

class App extends PortableApplication(800, 800) {
  private val TITLE: String = "IgnisSilvae"
  private val iconSizes: Array[Int] = Array(16, 32, 64, 128, 256)
  System.setProperty("LWJGL_WM_CLASS", TITLE) // See https://github.com/LWJGL/lwjgl/issues/67

  val world: World = World.make(10, 10)

  override def onInit(): Unit = {
    setTitle(TITLE)
    //setIcons(iconSizes.map((size: Int) => s"res/images/iconb$size.png"))
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    val winWidth: Float = getWindowWidth
    val winHeight: Float = getWindowHeight
    val size: Float = Math.min(winWidth / world.width, winHeight / world.height)
    val ox: Float = (winWidth - (world.width - 1) * size) / 2
    val oy: Float = (winHeight - (world.height - 1) * size) / 2
    world.grid.zipWithIndex.foreach(p1 => {
      val y: Int = p1._2
      p1._1.zipWithIndex.foreach(p2 => {
        val x: Int = p2._2
        val color: Color = p2._1.state match {
          case State.ALIVE => Color.GREEN
          case State.FIRE => Color.ORANGE
          case State.DEAD => Color.BLACK
        }
      g.drawFilledRectangle(ox + x * size, oy + y * size, size, size, 0f, color)
      })
    })
  }
}

object App {
  private var instance: App = _

  def main(args: Array[String]): Unit = {
    instance = new App
  }
}
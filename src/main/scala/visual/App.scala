package visual

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Color, Pixmap}
import com.badlogic.gdx.{Files, Gdx}
import core.{State, World}
import org.lwjgl.opengl.Display

import java.nio.ByteBuffer


class App extends PortableApplication(800, 800) {
  private val TITLE: String = "IgnisSilvae"
  private val iconSizes: Array[Int] = Array(16, 32, 48, 64, 128, 256)
  System.setProperty("LWJGL_WM_CLASS", TITLE) // See https://github.com/LWJGL/lwjgl/issues/67

  private var world: World = World.make(100, 100)
  private var i: Int = 1

  override def onInit(): Unit = {
    setTitle(TITLE)
    setIcons(iconSizes.map((size: Int) => s"res/icon_$size.png"))
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
          case _ => Color.GRAY
        }
        g.drawFilledRectangle(ox + x * size, oy + y * size, size, size, 0f, color)
      })
    })

    g.drawFPS()
    if (i == 0) {
      world = world.step()
    }

    i += 1
    //i %= 60
    i %= 10
  }

  private def setIcons(paths: Array[String]): Unit = {
    val icons: Array[ByteBuffer] = new Array(paths.length)
    for ((path: String, i: Int) <- paths.zipWithIndex) {
      var pixmap: Pixmap = new Pixmap(Gdx.files.getFileHandle(path, Files.FileType.Internal))
      if (pixmap.getFormat != Format.RGBA8888) {
        val rgba: Pixmap = new Pixmap(pixmap.getWidth, pixmap.getHeight, Format.RGBA8888)
        //rgba.setBlending(Blending.None)
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
}

object App {
  private var instance: App = _

  def main(args: Array[String]): Unit = {
    instance = new App
  }
}
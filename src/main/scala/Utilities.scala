package magicresize

import java.io.{File, FileInputStream, FileOutputStream, BufferedOutputStream}
import java.util
import org.apache.commons.io.IOUtils
import scala.math.{sqrt, pow, Numeric}
import ps.tricerato.pureimage.{RGB, Gray, Image}
import scala.collection.immutable.Stream

object Utilities {
 
  def getFileBytes(fileName: String): Array[Byte] = IOUtils.toByteArray(new FileInputStream(new File(fileName)))
  
  def writeBytesToFile(bytes: Array[Byte], fileName: String): Unit = {
    val out = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
    out.write(bytes)
    out.flush()
    out.close()
  }
  
  def distance(a:RGB, b:RGB): Double = sqrt(pow(a.red-b.red, 2) + pow(a.green-b.green, 2) + pow(a.blue-b.blue, 2))

  def gradient(a:RGB, b:RGB): Double = distance(a, b) / 2.0
  
  def magnitude(a:Double, b:Double): Double = sqrt(pow(a, 2) + pow(b, 2))

  def gradientMagnitude(img: Image[RGB]): Image[Gray] = {
    new Image[Gray] {
      def width: Int = img.width
      def height: Int = img.height
      def apply(x: Int, y:Int): Gray = {
        val xGradient = if (x == 0 || x == img.width-1) 0 else gradient(img(x-1, y), img(x+1, y))
        val yGradient = if (y == 0 || y == img.height-1) 0 else gradient(img(x, y-1), img(x, y+1))
        Gray(magnitude(xGradient.toInt, yGradient.toInt).toInt)
      }
    }
  }

  def minSeam(img: Image[Gray]): Set[(Int, Int)] = {
    var prev = new Array[(Byte, Set[(Int, Int)])](img.width)
    var curr = new Array[(Byte, Set[(Int, Int)])](img.width)
    for (x <- 0 to img.width-1) { prev(x) = (img(x, 0).white, Set[(Int, Int)]()) }
    for (y <- 1 to img.height-1) {
      for (x <- 0 to img.width-1) {
        var minSoFar = Int.MaxValue
        var minPix = (-1, -1)
        if (x > 0) {
          if ((prev(x-1)._1 + img(x,y).white) < minSoFar) {
            minSoFar = prev(x-1)._1 + img(x,y).white
            minPix = (x-1, y-1)
          }
        }
        if ((prev(x)._1 + img(x,y).white) < minSoFar) {
          minSoFar = prev(x)._1 + img(x,y).white
          minPix = (x,y-1)
        }
        if (x < img.width-1) {
          if ((prev(x+1)._1 + img(x,y).white) < minSoFar) {
            minSoFar = prev(x+1)._1 + img(x,y).white
            minPix = (x+1,y-1)
          }
        }
        curr(x) = (minSoFar.toByte, prev(minPix._1)._2 + minPix)
      }
      prev = curr
      curr = new Array[(Byte, Set[(Int, Int)])](img.width)
    }
    var minSoFar = Int.MaxValue
    var minSeam:Set[(Int, Int)] = Set[(Int, Int)]()
    for (v <- prev) {
      if (v._1 < minSoFar) {
        minSoFar = v._1
        minSeam = v._2
      }
    }
    minSeam
  }

  def highlightSeam(img:Image[RGB], seam:Set[(Int, Int)]): Image[RGB] = {
    new Image[RGB] {
      def width = img.width;
      def height = img.height;
      def apply(x:Int, y:Int): RGB = {
        if (seam contains (x,y)) {
          RGB(255)
        } else {
          img(x,y)
        }
      }
    }
  }
}

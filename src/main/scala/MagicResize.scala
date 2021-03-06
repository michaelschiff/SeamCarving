package magicresize

import ps.tricerato.pureimage.{Input, RGBImage, Output, PNG, Gray, RGB, Image}

object MagicResize {
  def main(args:Array[String]) {
    println("loading image: " + args(0)) 
    val bytes = Utilities.getFileBytes(args(0))
    val Right(RGBImage(img)) = Input(bytes)
    println("Read Image")
    val energy = Utilities.gradientMagnitude(img)
    var seam = Utilities.minSeam(energy)
    val outputBytes = Output(Utilities.highlightSeam(new Image[RGB] {
            val width = energy.width
            val height = energy.height
            def apply(x: Int, y:Int): RGB = RGB(energy(x,y).white)
          }, seam), PNG)

    //val outputBytes = Output(energy, PNG)
    println(outputBytes)
    Utilities.writeBytesToFile(outputBytes, "/Users/michaelschiff/Desktop/test1.png")
  }
}

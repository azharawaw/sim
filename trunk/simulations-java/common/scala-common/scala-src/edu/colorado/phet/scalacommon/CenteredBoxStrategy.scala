package edu.colorado.phet.scalacommon

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.Rectangle
import javax.swing.JComponent
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas.TransformStrategy
import java.awt.geom.{AffineTransform, Rectangle2D}

//use case class to avoid compilation errors
case class CenteredBoxStrategy(modelWidth: Double, modelHeight: Double, canvas: JComponent) extends TransformStrategy {
  def getTransform(): AffineTransform = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val mv2d = getModelViewTransform2D
      //println("model dim=" + modelWidth + "x" + modelHeight + ", visible=" + getVisibleModelBounds)
      mv2d.getAffineTransform
    } else {
      new AffineTransform
    }
  }

  def sx = canvas.getWidth / modelWidth

  def sy = canvas.getHeight / modelHeight

  def getScale = if (sx < sy) sx else sy

  def getModelViewTransform2D: ModelViewTransform2D = {
    //use the smaller
    var scale = getScale
    scale = if (scale <= 0) sy else scale //if scale is negative or zero, just use scale=sy as a default
    val outputBox =
    if (scale == sx)
      new Rectangle2D.Double(0, (canvas.getHeight - canvas.getWidth) / 2.0, canvas.getWidth, canvas.getWidth)
    else
      new Rectangle2D.Double((canvas.getWidth - canvas.getHeight) / 2.0, 0, canvas.getHeight, canvas.getHeight)

    new ModelViewTransform2D(new Rectangle2D.Double(0, 0, modelWidth, modelHeight), outputBox, false)
  }

  def getVisibleModelBounds = getModelViewTransform2D.getAffineTransform.createInverse.createTransformedShape(new Rectangle(canvas.getWidth, canvas.getHeight)).getBounds2D
}
package edu.colorado.phet.movingman.ladybug.canvas

import controlpanel.{PathVisibilityModel, VectorVisibilityModel}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.geom.{AffineTransform, Rectangle2D, Point2D}
import java.awt.{Rectangle, Dimension, Color}
import javax.swing.JComponent
import model.LadybugModel
import umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas.RenderingSizeStrategy
import edu.colorado.phet.common.piccolophet.PhetPCanvas.TransformStrategy
import edu.colorado.phet.common.piccolophet.PhetPCanvas.ViewportStrategy
import umd.cs.piccolo.util.PDimension

class LadybugCanvas(model: LadybugModel, vectorVisibilityModel: VectorVisibilityModel, pathVisibilityModel: PathVisibilityModel)
        extends PhetPCanvas(new Dimension(1024, 768)) {
  setWorldTransformStrategy(new CenteredBoxStrategy(768, 768, this));
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(0, 0, 768, 768), LadybugDefaults.POSITIVE_Y_IS_UP)
  val constructed = true
  updateWorldScale

  protected override def updateWorldScale = {
    super.updateWorldScale
    if (constructed) {
      //to go from pixels to model, must go backwards through canvas transform and modelviewtransform
      val topLeft = new Point2D.Double(0, 0)
      val bottomRight = new Point2D.Double(getWidth, getHeight)

      def tx(pt: Point2D) = {
        val intermediate = getWorldTransformStrategy.getTransform.inverseTransform(pt, null)
        val model = transform.viewToModel(intermediate.getX, intermediate.getY)
        model
      }
      val out = new Rectangle2D.Double()
      out.setFrameFromDiagonal(tx(topLeft).getX, tx(topLeft).getY, tx(bottomRight).getX, tx(bottomRight).getY)
      model.setBounds(out)
    }
  }


  val worldNode = new PNode
  addWorldChild(worldNode)
  setBackground(new Color(200, 255, 240))

  val ladybugNode = new LadybugNode(model, model.ladybug, transform, vectorVisibilityModel)
  addNode(ladybugNode)
  val dotTrace = new LadybugDotTraceNode(model, transform, () => pathVisibilityModel.dotsVisible, pathVisibilityModel, 0.7)
  addNode(dotTrace)
  val fadeTrace = new LadybugFadeTraceNode(model, transform, () => pathVisibilityModel.fadeVisible, pathVisibilityModel, 0.7)
  addNode(fadeTrace)
  addNode(new ReturnLadybugButton(model, this)) //todo: perhaps this should be a screen child

  def addNode(node: PNode) = worldNode.addChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)

  def clearTrace() = {
    dotTrace.clearTrace
    fadeTrace.clearTrace
  }

  def setLadybugDraggable(draggable: Boolean) = ladybugNode.setDraggable(draggable)
}
package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode

/**
 * Class VectorView can inspect a MotionSeriesObject and add graphical representations of its vectors onto a specified VectorDisplay
 */
class VectorView(motionSeriesObject: MotionSeriesObject, vectorViewModel: VectorViewModel, coordinateFrameModel: CoordinateFrameModel, fbdWidth: Int) {
  def addAllVectorsAllComponents(o: MotionSeriesObject, vectorDisplay: VectorDisplay) {
    addVectorAllComponents(o, o.appliedForceVector, vectorDisplay)
    addVectorAllComponents(o, o.gravityForceVector, vectorDisplay)
    addVectorAllComponents(o, o.normalForceVector, vectorDisplay)
    addVectorAllComponents(o, o.frictionForceVector, vectorDisplay)
    addVectorAllComponents(o, o.wallForceVector, vectorDisplay)
    addAllVectorsAllComponents(o, o.totalForceVector,
                               new Vector2DModel(new Vector2D(0, fbdWidth / 4)), 2, //Needs a separate offset since it should be shown above other force arrows
                               () => vectorViewModel.sumOfForcesVector, vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }

  def addVectorAllComponents(motionSeriesObject: MotionSeriesObject, vector: MotionSeriesObjectVector, vectorDisplay: VectorDisplay) {
    addAllVectorsAllComponents(motionSeriesObject, vector, new Vector2DModel, 0, () => true, vectorDisplay)
  }

  def addAllVectorsAllComponents(motionSeriesObject: MotionSeriesObject,
                                 vector: MotionSeriesObjectVector,
                                 freeBodyDiagramOffset: Vector2DModel,
                                 playAreaOffset: Double,
                                 selectedVectorVisible: () => Boolean,
                                 vectorDisplay: VectorDisplay) = {
    vectorViewModel.addListener(update)
    update()

    addVector(motionSeriesObject, vector, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
    def update() {
      vector.setVisible(vectorViewModel.originalVectors && selectedVectorVisible())
    }
  }

  def addVector(motionSeriesObject: MotionSeriesObject, vector: Vector, freeBodyDiagramOffset: Vector2DModel, offsetPlayArea: Double, vectorDisplay: VectorDisplay) = {
    vectorDisplay.addVector(vector, freeBodyDiagramOffset, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea)
    motionSeriesObject.removalListeners += ( () => vectorDisplay.removeVector(vector) )
  }
}

trait VectorDisplay {
  def addVector(vector: Vector, offsetFBD: Vector2DModel, maxLabelDist: Int, offsetPlayArea: Double)

  def removeVector(vector: Vector)
}

class PlayAreaVectorDisplay(transform: ModelViewTransform2D, motionSeriesObject: MotionSeriesObject) extends PNode with VectorDisplay {
  def addVector(vector: Vector, offset2D: Vector2DModel, maxOffset: Int, offset: Double) {
    val defaultCenter = motionSeriesObject.height / 2.0
    def getValue = motionSeriesObject.position2D + new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2) * ( offset + defaultCenter )
    val myoffset = new Vector2DModel(getValue)
    motionSeriesObject.position2DProperty.addListener(() => myoffset.value = getValue)
    addChild(new BodyVectorNode(transform, vector, myoffset, motionSeriesObject, MotionSeriesDefaults.PLAY_AREA_FORCE_VECTOR_SCALE))
  }

  def removeVector(vector: Vector) = null //TODO: memory leak
}
package edu.colorado.phet.motionseries.model

import edu.colorado.phet.common.motion.model.TimeData
import edu.colorado.phet.common.motion.MotionMath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable

object MovingManMotionSeriesObject {
  def apply(model: MotionSeriesModel, x: Double, width: Double, height: Double) = {
    new MovingManMotionSeriesObject(new MotionSeriesObjectState(x, 0, 10, 0, 0, 0.0, 0.0, 0.0), height, width, model.positionMapper, model.rampSegmentAccessor, model.rampChangeAdapter,
      model.surfaceFriction, model.wallsBounce, model.surfaceFrictionStrategy, model.walls, model.wallRange, model.thermalEnergyStrategy)
  }
}

class MovingManMotionSeriesObject(_state: MotionSeriesObjectState,
                    _height: Double,
                    _width: Double,
                    positionMapper: Double => Vector2D,
                    rampSegmentAccessor: Double => RampSegment,
                    model: Observable,
                    surfaceFriction: () => Boolean,
                    wallsBounce: () => Boolean,
                    __surfaceFrictionStrategy: SurfaceFrictionStrategy,
                    _wallsExist: => Boolean,
                    wallRange: () => Range,
                    thermalEnergyStrategy: Double => Double)
        extends ForceMotionSeriesObject(_state, _height, _width, positionMapper, rampSegmentAccessor, model, surfaceFriction, wallsBounce, __surfaceFrictionStrategy, _wallsExist, wallRange, thermalEnergyStrategy) {

  private object velocityMode
  private object positionMode
  private object accelerationMode

  private var _mode: AnyRef = accelerationMode

  def mode = _mode

  def setAccelerationMode() = {
    _mode = accelerationMode
  }

  def setVelocityMode() = {
    _mode = velocityMode
    motionStrategy = new VelocityMotionStrategy(this)
  }

  def setPositionMode() = {
    _mode = positionMode
    motionStrategy = new PositionMotionStrategy(this)
  }

  override def acceleration = {
    if (mode == positionMode) {
      if (stateHistory.length <= 3)
        0.0
      else {
        val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
        yield new TimeData(stateHistory(stateHistory.length - 1 - i).position, stateHistory(stateHistory.length - 1 - i).time)
        MotionMath.getSecondDerivative(timeData.toArray).getValue
      }
    }
    else if (mode == velocityMode) {
      //I investigated taking 2nd derivative directly, but it seemed easier and more accurate to take 1st derivative of velocity?
      val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
      yield new TimeData(stateHistory(stateHistory.length - 1 - i).velocity, stateHistory(stateHistory.length - 1 - i).time)
      MotionMath.estimateDerivative(timeData.toArray)
    }
    else //if (mode == accelerationMode)
      {
        super.acceleration
      }
  }
}

abstract class MovingManStrategy(bead: ForceMotionSeriesObject) extends MotionStrategy(bead) {
  def position2D = bead.positionMapper(bead.position)

  def getAngle = 0.0
}

class PositionMotionStrategy(bead: ForceMotionSeriesObject) extends MovingManStrategy(bead) {
  def isCrashed = false

  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(bead: ForceMotionSeriesObject) = new PositionMotionStrategy(bead)
  }

  def stepInTime(dt: Double) = {
    val mixingFactor = 0.5
    //maybe a better assumption is constant velocity or constant acceleration ?
    val dst = bead.desiredPosition * mixingFactor + bead.position * (1 - mixingFactor)
    bead.setPosition(dst) //attempt at filtering

    //todo: move closer to bead computation of acceleration derivatives
    val timeData = for (i <- 0 until java.lang.Math.min(15, bead.stateHistory.length))
    yield new TimeData(bead.stateHistory(bead.stateHistory.length - 1 - i).position, bead.stateHistory(bead.stateHistory.length - 1 - i).time)
    val vel = MotionMath.estimateDerivative(timeData.toArray)
    bead.setVelocity(vel)
    bead.setTime(bead.time + dt)
  }
}

class VelocityMotionStrategy(bead: ForceMotionSeriesObject) extends MovingManStrategy(bead) {
  def isCrashed = false

  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(bead: ForceMotionSeriesObject) = new VelocityMotionStrategy(bead)
  }

  def stepInTime(dt: Double) = {
    bead.setPosition(bead.position + bead.velocity * dt)
    bead.setTime(bead.time + dt)
  }
}
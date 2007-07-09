package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.MotionBodyState;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.motion.model.ITimeSeries;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform extends MotionBody {
    private SerializablePoint2D center = new SerializablePoint2D( 200, 200 );
    private double radius = 200.0;

    public boolean containsPosition( Point2D loc ) {
        return loc.distance( center ) < radius;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public void setAngle( double angle ) {
        super.getMotionBodyState().setPosition( angle );
    }

}

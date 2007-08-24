package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.motion.model.IPositionDriven;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.MathUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:26:37 PM
 */
public class RotationPlatformDragHandler extends PBasicInputEventHandler {
    double initAngle;
    public Point2D initLoc;
    private final IPositionDriven environment;
    private final RotationPlatform rotationPlatform;
    private PNode rotationPlatformNode;

    public RotationPlatformDragHandler( PNode rotationPlatformNode, IPositionDriven environment, RotationPlatform rotationPlatform ) {
        this.rotationPlatformNode = rotationPlatformNode;
        this.environment = environment;
        this.rotationPlatform = rotationPlatform;
    }

    public void mousePressed( PInputEvent event ) {
//        resetDrag( rotationPlatformNode.getAngle(), event );
        resetDrag( rotationPlatform.getAngle().getValue(), event );//todo: used to be angle of graphic, maybe model value is incorrect
        environment.setPositionDriven();
    }

    public void mouseReleased( PInputEvent event ) {
    }

    public void mouseDragged( PInputEvent event ) {
        Point2D loc = event.getPositionRelativeTo( rotationPlatformNode );
        Point2D center = rotationPlatform.getCenter();
        Vector2D.Double a = new Vector2D.Double( center, initLoc );
        Vector2D.Double b = new Vector2D.Double( center, loc );
        double angleDiff = b.getAngle() - a.getAngle();
//                System.out.println( "a=" + a + ", b=" + b + ", center=" + center + ", angleDiff = " + angleDiff );

        angleDiff = MathUtil.clampAngle( angleDiff, -Math.PI, Math.PI );

        double angle = initAngle + angleDiff;
//                System.out.println( "angleDiff=" + angleDiff + ", angle=" + angle );
        rotationPlatform.setAngle( angle );
        resetDrag( angle, event );//have to reset drag in order to keep track of the winding number
    }

    private void resetDrag( double angle, PInputEvent event ) {
        initAngle = angle;
        initLoc = event.getPositionRelativeTo( rotationPlatformNode );
    }
}

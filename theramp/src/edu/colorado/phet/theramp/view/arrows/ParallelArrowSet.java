/** Sam Reid*/
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class ParallelArrowSet extends AbstractArrowSet {

    public ParallelArrowSet( final RampPanel component ) {
        super( component );
        RampLookAndFeel ralf = new RampLookAndFeel();
        final RampModel rampModel = component.getRampModule().getRampModel();
        String sub = "||";
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, "Applied", ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector appliedForce = rampModel.getAppliedForce();
                return appliedForce.toParallelVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, "Total", ralf.getNetForceColor(), 45, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getTotalForce();
                return totalForce.toParallelVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, "Friction", ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getFrictionForce();
                return totalForce.toParallelVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, "Weight", ralf.getGravityParallelColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getGravityForce();
                return totalForce.toParallelVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, "Normal", ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getNormalForce();
                return totalForce.toParallelVector();
            }
        }, component.getBlockGraphic(), sub );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( totalArrowGraphic );
        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );
        setIgnoreMouse( true );
    }

}

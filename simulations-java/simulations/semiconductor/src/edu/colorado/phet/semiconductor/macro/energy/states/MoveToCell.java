// Copyright 2002-2011, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:52:53 PM
 */
public class MoveToCell implements BandParticleState {
    //    private BandParticle bp;
    private EnergyCell target;
    Speed speed;

    public MoveToCell( BandParticle bp, EnergyCell target, Speed speed ) {
//        this.bp = bp;
        this.target = target;
        this.speed = speed;
//        bp.setEnergyCell(target);
        bp.setEnergyCell( target );
        bp.setExcited( true );
    }

    public MoveToCell( BandParticle bp, EnergyCell target, final double speed ) {
        this( bp, target, new Speed() {
            public double getSpeed() {
                return speed;
            }
        } );
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        particle.setEnergyCell( target );
        double distPerStep = speed.getSpeed() * dt;
        Vector2D targetLoc = target.getPosition();
        Vector2D myLoc = particle.getPosition();
        ImmutableVector2D dx = targetLoc.getSubtractedInstance( myLoc );
//        particle.setVelocity(dx.getScaledInstance(1.0/dt));
        double dist = dx.getMagnitude();
        if ( dist <= distPerStep ) {
            //got there
            particle.setPosition( targetLoc );
            return true;
        }
        else {
            ImmutableVector2D dir = dx.getInstanceOfMagnitude( distPerStep );
            ImmutableVector2D newLoc = myLoc.getAddedInstance( dir );
            particle.setPosition( new Vector2D( newLoc.getX(), newLoc.getY() ) );
            return false;
        }
    }

}

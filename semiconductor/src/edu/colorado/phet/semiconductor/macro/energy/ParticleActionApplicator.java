/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 1:29:19 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class ParticleActionApplicator implements ModelElement {
    ArrayList particleActions = new ArrayList();
    EnergySection energySection;

    public ParticleActionApplicator( EnergySection energySection ) {
        this.energySection = energySection;
    }

    public void addParticleAction( ParticleAction action ) {
        particleActions.add( action );
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            for( int j = 0; j < particleActions.size(); j++ ) {
                ParticleAction particleAction = (ParticleAction)particleActions.get( j );
                particleAction.apply( bp );
            }
        }
    }

}

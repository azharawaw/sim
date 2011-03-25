//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.flexcommon.FlexSimStrings;

/**
 * In the Buoyancy sim, the scale readout must account for the simulation DT since there is a dynamical model running.
 * //TODO: why is this important for Buoyancy but not Density?
 */
public class BuoyancyScale extends Scale {
    public function BuoyancyScale( x: Number, y: Number, model: DensityModel ) {
        super( x, y, model );
    }

    override public function getScaleReadout(): String {
        // scaled by DT-frame because we are measuring the 'normal impulses'
        //impulse I=Fdt
        //F=I/dt
        var force: Number = totalImpulse / DensityModel.DT_PER_FRAME;
        return FlexSimStrings.get( "properties.weightNewtonValue", "{0} N", [DensityAndBuoyancyConstants.format( force )] );
    }
}
}
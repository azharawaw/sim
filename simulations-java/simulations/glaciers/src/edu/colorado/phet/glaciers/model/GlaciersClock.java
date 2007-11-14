/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * GlaciersClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClock extends ConstantDtClock {

    private DoubleRange _dtRange;
    private String _units;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersClock( int framesPerSecond, DoubleRange dtRange, String units ) {
        super( 1000 / framesPerSecond, dtRange.getDefault() );
        _dtRange = dtRange;
        _units = new String( units );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Reset the clock when dt is changed.
     * 
     * @param dt
     */
    public void setDt( double dt ) {
        if ( dt < _dtRange.getMin() || dt > _dtRange.getMax() ) {
            throw new IllegalArgumentException( "dt is out of range: " + dt );
        }
        super.setDt( dt );
        resetSimulationTime();
        System.out.println( "GlaciersClock.setDt dt=" + dt );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public DoubleRange getDtRange() {
        return _dtRange;
    }
    
    public String getUnits() {
        return new String( _units );
    }
}

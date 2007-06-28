
package edu.colorado.phet.opticaltweezers.model;

import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.clock.ConstantDtClock;


/**
 * OTClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final DoubleRange _slowRange;
    private final DoubleRange _fastRange;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OTClock( int framesPerSecond, DoubleRange slowRange, DoubleRange fastRange, double dt ) {
        super( 1000 / framesPerSecond, dt );
        
        if ( slowRange.getMax() > fastRange.getMin() ) {
            throw new IllegalArgumentException( "slowRange and fastRange overlap" );
        }
        if ( dt < slowRange.getMin() || dt > fastRange.getMax() ) {
            throw new IllegalArgumentException( "dt out of range: " + dt );
        }
        
        _slowRange = slowRange;
        _fastRange = fastRange;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public DoubleRange getSlowRange() {
        return _slowRange;
    }
    
    public DoubleRange getFastRange() {
        return _fastRange;
    }
}

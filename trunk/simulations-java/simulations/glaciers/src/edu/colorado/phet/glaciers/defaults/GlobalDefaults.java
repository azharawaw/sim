/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private! */ class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final int CLOCK_TIME_COLUMNS = 10;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( 1, 100, 1 ); // years
    
    // Climate
    public static final DoubleRange SNOWFALL_RANGE = new DoubleRange( 0, 100, 0 ); //XXX units?
    public static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 0, 100, 0 );  //XXX units?
}

/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model element for the Capacitance meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitanceMeter extends BarMeter {

    public CapacitanceMeter( final BatteryCapacitorCircuit circuit, World world, Point3D location, boolean visible ) {
        super( world, location, visible, circuit.getCapacitor().getTotalCapacitance() );
        circuit.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                setValue( circuit.getCapacitor().getTotalCapacitance() );
            }
        });
    }
}

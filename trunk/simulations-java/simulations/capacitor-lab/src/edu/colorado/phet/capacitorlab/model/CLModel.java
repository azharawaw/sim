/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;


/**
 * Model for the "Capacitor Lab" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModel {
    
    private static final double BATTERY_VOLTAGE = 0;
    private static final double PLATE_WIDTH = 100;
    private static final double PLATE_SPACING = 50;
    private static final double DIELECTRIC_OFFSET = 0;
    
    public final Battery battery;
    public final Capacitor capacitor;

    public CLModel() {
        battery = new Battery( BATTERY_VOLTAGE );
        capacitor = new Capacitor( PLATE_WIDTH, PLATE_SPACING, DIELECTRIC_OFFSET );
    }
    
    public Battery getBattery() {
        return battery;
    }
    
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    public void reset() {
        battery.setVoltage( BATTERY_VOLTAGE );
        capacitor.setPlateWidth( PLATE_WIDTH );
        capacitor.setPlateSpacing( PLATE_SPACING );
        capacitor.setDielectricOffset( DIELECTRIC_OFFSET );
    }
}

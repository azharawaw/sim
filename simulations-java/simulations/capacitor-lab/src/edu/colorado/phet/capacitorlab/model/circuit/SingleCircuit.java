// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryBottomToCapacitorBottoms;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryTopToCapacitorTops;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery connected to a single capacitor.
 * This circuit is used in all 3 modules.
 * <p/>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SingleCircuit extends AbstractCircuit {

    // immutable instance data
    private final IClock clock;
    private final Capacitor capacitor;
    private final ArrayList<Wire> wires;

    // mutable instance data
    private Property<Boolean> batteryConnectedProperty; // is the battery connected to the circuit?
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected

    public SingleCircuit( IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation, Point3D capacitorLocation,
                          double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        this( clock, mvt, batteryLocation, capacitorLocation, plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, true /* batteryConnected */ );
    }

    public SingleCircuit( IClock clock, final CLModelViewTransform3D mvt, Point3D batteryLocation, Point3D capacitorLocation,
                          double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset,
                          boolean batteryConnected ) {
        super( CLStrings.SINGLE, clock, mvt, batteryLocation );

        this.clock = clock;
        this.capacitor = new Capacitor( capacitorLocation, plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
        this.batteryConnectedProperty = new Property<Boolean>( batteryConnected );
        this.disconnectedPlateCharge = getTotalCharge();

        // Create the wires
        wires = new ArrayList<Wire>() {{
            add( new WireBatteryTopToCapacitorTops( mvt, CLConstants.WIRE_THICKNESS, getBattery(), capacitor ) );
            add( new WireBatteryBottomToCapacitorBottoms( mvt, CLConstants.WIRE_THICKNESS, getBattery(), capacitor ) );
        }};

        // observe battery
        getBattery().addVoltageObserver( new SimpleObserver() {
            public void update() {
                if ( isBatteryConnected() ) {
                    updatePlateVoltages();
                }
            }
        } );

        // observe capacitor
        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                if ( !isBatteryConnected() ) {
                    updatePlateVoltages();
                }
                fireCircuitChanged();
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        capacitor.reset();
        batteryConnectedProperty.reset();
    }

    //----------------------------------------------------------------------------------
    // Circuit components
    //----------------------------------------------------------------------------------

    public Capacitor getCapacitor() {
        return capacitor;
    }

    public ArrayList<Capacitor> getCapacitors() {
        return new ArrayList<Capacitor>() {{
            add( capacitor );
        }};
    }

    public ArrayList<Wire> getWires() {
        return wires;
    }

    public Wire getTopWire() {
        return wires.get( 0 );
    }

    public Wire getBottomWire() {
        return wires.get( wires.size() - 1 );
    }

    //----------------------------------------------------------------------------------
    // Battery connectivity
    //----------------------------------------------------------------------------------

    /**
     * Is the battery connected to the capacitor?
     *
     * @return
     */
    public boolean isBatteryConnected() {
        return batteryConnectedProperty.get();
    }

    /**
     * Determines whether the battery is connected to the capacitor.
     *
     * @param batteryConnected
     */
    public void setBatteryConnected( boolean batteryConnected ) {
        if ( batteryConnected != isBatteryConnected() ) {
            /*
             * When disconnecting the battery, set the disconnected plate charge to
             * whatever the total plate charge was with the battery connected.
             * Need to do this before changing the property value.
             */
            if ( !batteryConnected ) {
                disconnectedPlateCharge = getTotalCharge();
            }
            batteryConnectedProperty.set( batteryConnected );
            updatePlateVoltages();
            fireCircuitChanged();
        }
    }

    // Updates the plate voltage, depending on whether the battery is connected.
    private void updatePlateVoltages() {
        double V = getBattery().getVoltage();
        if ( !isBatteryConnected() ) {
            V = disconnectedPlateCharge / capacitor.getTotalCapacitance(); // V = Q/C
        }
        capacitor.setPlatesVoltage( V );
    }

    //----------------------------------------------------------------------------------
    // Plate voltage (V)
    //----------------------------------------------------------------------------------

    @Override public double getTotalVoltage() {
        if ( isBatteryConnected() ) {
            return super.getTotalVoltage();
        }
        else {
            return capacitor.getPlatesVoltage();
        }
    }

    // @see ICircuit.getVoltageAt
    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( ( isBatteryConnected() && ( getTopWire().intersects( shape ) || getBattery().intersectsTopTerminal( shape ) ) ) || capacitor.intersectsTopPlateShape( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( ( isBatteryConnected() && ( getBottomWire().intersects( shape ) || getBattery().intersectsBottomTerminal( shape ) ) ) || capacitor.intersectsBottomPlateShape( shape ) ) {
            voltage = 0;
        }
        return voltage;
    }

    //----------------------------------------------------------------------------------
    // Capacitance (C)
    //----------------------------------------------------------------------------------

    // @see ICircuit.getTotalCapacitance
    public double getTotalCapacitance() {
        return capacitor.getTotalCapacitance();
    }

    //----------------------------------------------------------------------------------
    // Plate charge (Q)
    //----------------------------------------------------------------------------------

    /**
     * Sets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @param disconnectedPlateCharge Coulombs
     */
    public void setDisconnectedPlateCharge( double disconnectedPlateCharge ) {
        if ( disconnectedPlateCharge != this.disconnectedPlateCharge ) {
            this.disconnectedPlateCharge = disconnectedPlateCharge;
            if ( !isBatteryConnected() ) {
                updatePlateVoltages();
                fireCircuitChanged();
            }
        }
    }

    /**
     * Gets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getDisconnectedPlateCharge() {
        return disconnectedPlateCharge;
    }

    // @see ICircuit.getTotalCharge
    @Override public double getTotalCharge() {
        return capacitor.getTotalPlateCharge();
    }
}

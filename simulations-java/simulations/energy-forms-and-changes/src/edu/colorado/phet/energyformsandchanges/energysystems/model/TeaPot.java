// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.TEAPOT_LARGE;

/**
 * Class that represents the steam-generating tea pot in the model.
 *
 * @author John Blanco
 */
public class TeaPot extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double MAX_ENERGY_PRODUCTION_RATE = 200; // In joules/second
    private static final double MAX_ENERGY_CHANGE_RATE = 20; // In joules/second
    private static final double COOLING_CONSTANT = 0.1; // Controls rate at which tea pot cools down, empirically determined.
    private static final double COOL_DOWN_COMPLETE_THRESHOLD = 30; // In joules/second
    public static final ModelElementImage TEAPOT_IMAGE = new ModelElementImage( TEAPOT_LARGE, new Vector2D( 0.0, 0.015 ) );
    public static final double ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT = 100; // In joules, but empirically determined.
    public static final double MAX_ENERGY_CHUNK_DISTANCE = 0.5; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final Property<Double> heatCoolAmount = new Property<Double>( 0.0 );
    private Property<Double> energyProductionRate = new Property<Double>( 0.0 );
    private double energyProducedSinceLastChunk = 0;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected TeaPot() {
        super( EnergyFormsAndChangesResources.Images.TEAPOT_ICON );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {
        if ( isActive() ) {
            if ( heatCoolAmount.get() > 0 || energyProductionRate.get() > COOL_DOWN_COMPLETE_THRESHOLD ) {
                // Calculate the energy production rate.
                double energyProductionIncreaseRate = heatCoolAmount.get() * MAX_ENERGY_CHANGE_RATE; // Analogous to acceleration.
                double energyProductionDecreaseRate = energyProductionRate.get() * COOLING_CONSTANT; // Analogous to friction.
                energyProductionRate.set( Math.min( energyProductionRate.get() + energyProductionIncreaseRate * dt - energyProductionDecreaseRate * dt,
                                                    MAX_ENERGY_PRODUCTION_RATE ) ); // Analogous to velocity.
            }
            else {
                // Clamp the energy production rate to zero so that it doesn't
                // trickle on forever.
                energyProductionRate.set( 0.0 );
            }

            // See if it's time to emit a new energy chunk.
            energyProducedSinceLastChunk += energyProductionRate.get() * dt;
            if ( energyProducedSinceLastChunk > ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT ) {
                // It's time, so emit one.
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.MECHANICAL, getPosition(), new BooleanProperty( true ) );
                energyChunk.setVelocity( new Vector2D( 0.1, 0.1 ) );
                energyChunkList.add( energyChunk );
                energyProducedSinceLastChunk -= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT;
            }

            // Move all energy chunks that are under this element's control.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {
                energyChunk.translateBasedOnVelocity( dt );
                if ( getPosition().distance( energyChunk.position.get() ) > MAX_ENERGY_CHUNK_DISTANCE ) {
                    // Time to remove this chunk.
                    energyChunkList.remove( energyChunk );
                }
            }
        }
        return new Energy( EnergyType.MECHANICAL, energyProductionRate.get() * dt, Math.PI / 2 );
    }

    @Override public void deactivate() {
        super.deactivate();
        heatCoolAmount.reset();
        energyProductionRate.reset();
        energyProducedSinceLastChunk = 0;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectTeapotButton;
    }

    public ObservableProperty<Double> getEnergyProductionRate() {
        return energyProductionRate;
    }
}

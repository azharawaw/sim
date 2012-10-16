// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
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

    // Images used in representing this energy system element in the view.
    public static final Vector2D TEAPOT_OFFSET = new Vector2D( 0.0, 0.015 );
    public static final ModelElementImage TEAPOT_IMAGE = new ModelElementImage( TEAPOT_LARGE, TEAPOT_OFFSET );

    // Offsets and other constants used for energy paths.
    private static final Vector2D ENERGY_CHUNK_EXIT_POINT = TEAPOT_OFFSET.plus( new Vector2D( TEAPOT_IMAGE.getWidth() / 2, TEAPOT_IMAGE.getHeight() / 2 ).plus( -0.006, -0.01 ) );
    private static final double THERMAL_ENERGY_CHUNK_TRAVEL_DISTANCE = 0.05; // In meters.
    private static final double THERMAL_ENERGY_CHUNK_Y_ORIGIN = -0.05; // In meters, must be coordinated with heater position.
    private static final DoubleRange THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE = new DoubleRange( -0.015, 0.015 ); // In meters, must be coordinated with heater position.

    // Miscellaneous other constants.
    public static final double MAX_ENERGY_PRODUCTION_RATE = 200; // In joules/second
    private static final double MAX_ENERGY_CHANGE_RATE = 20; // In joules/second
    private static final double COOLING_CONSTANT = 0.1; // Controls rate at which tea pot cools down, empirically determined.
    private static final double COOL_DOWN_COMPLETE_THRESHOLD = 30; // In joules/second
    public static final double ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT = 100; // In joules, but empirically determined.
    public static final double MAX_ENERGY_CHUNK_DISTANCE = 0.5; // In meters.
    private static final DoubleRange ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE = new DoubleRange( 0.12, 0.15 );
    private static final Random RAND = new Random();

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final Property<Double> heatCoolAmount = new Property<Double>( 0.0 );
    private Property<Double> energyProductionRate = new Property<Double>( 0.0 );
    private double heatEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
    private double steamEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
    private ObservableProperty<Boolean> energyChunksVisible;
    private ObservableProperty<Boolean> steamPowerableElementInPlace;

    // List of chunks that are not being transferred to the next energy system
    // element.
    public final List<EnergyChunk> exemptFromTransferEnergyChunks = new ArrayList<EnergyChunk>();

    // Flag for whether next chunk should be transferred or kept, used to
    // alternate transfer with non-transfer.
    private boolean transferNextAvailableChunk = true;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public TeaPot( BooleanProperty energyChunksVisible, ObservableProperty<Boolean> steamPowerableElementInPlace ) {
        super( EnergyFormsAndChangesResources.Images.TEAPOT_ICON );
        this.energyChunksVisible = energyChunksVisible;
        this.steamPowerableElementInPlace = steamPowerableElementInPlace;
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
                steamEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
            }

            // See if it's time to emit a new energy chunk from the heater.
            heatEnergyProducedSinceLastChunk += Math.max( heatCoolAmount.get(), 0 ) * MAX_ENERGY_PRODUCTION_RATE * dt;
            if ( heatEnergyProducedSinceLastChunk >= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT ) {
                // Emit a new thermal energy chunk.
                Vector2D initialPosition = new Vector2D( getPosition().getX() + THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE.getMin() + RAND.nextDouble() * THERMAL_ENERGY_CHUNK_X_ORIGIN_RANGE.getLength(),
                                                         getPosition().getY() + THERMAL_ENERGY_CHUNK_Y_ORIGIN );
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.THERMAL, initialPosition, energyChunksVisible );
                energyChunk.setVelocity( new Vector2D( 0, EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                energyChunkList.add( energyChunk );
                heatEnergyProducedSinceLastChunk -= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT;
            }

            // See if it's time to emit a new energy chunk from the teapot's
            // spout.
            steamEnergyProducedSinceLastChunk += energyProductionRate.get() * dt;
            if ( steamEnergyProducedSinceLastChunk >= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT ) {
                // It's time, so emit one.
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.MECHANICAL, getPosition().plus( ENERGY_CHUNK_EXIT_POINT ), energyChunksVisible );
                energyChunk.setVelocity( new Vector2D( EFACConstants.ENERGY_CHUNK_VELOCITY, 0 ).getRotatedInstance( Math.PI / 4 ) );
                energyChunkList.add( energyChunk );
                steamEnergyProducedSinceLastChunk -= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT;
            }

            // Move all energy chunks that are under this element's control.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {
                energyChunk.translateBasedOnVelocity( dt );

                // See if chunk is in the location where it can be transferred
                // to the next energy system.
                if ( energyChunk.energyType.get() == EnergyType.MECHANICAL ) {
                    if ( steamPowerableElementInPlace.get() &&
                         ENERGY_CHUNK_TRANSFER_DISTANCE_RANGE.contains( getPosition().distance( energyChunk.position.get() ) ) &&
                         !exemptFromTransferEnergyChunks.contains( energyChunk ) ) {

                        if ( transferNextAvailableChunk ) {
                            // Send this chunk to the next energy system.
                            outgoingEnergyChunks.add( energyChunk );

                            // Alternate sending or keeping chunks.
                            transferNextAvailableChunk = false;
                        }
                        else {
                            // Don't transfer this chunk.
                            exemptFromTransferEnergyChunks.add( energyChunk );

                            // Set up to transfer the next one.
                            transferNextAvailableChunk = true;
                        }
                    }
                    else if ( getPosition().distance( energyChunk.position.get() ) > MAX_ENERGY_CHUNK_DISTANCE ) {
                        // Time to remove this chunk.
                        energyChunkList.remove( energyChunk );
                    }
                }
                else if ( energyChunk.energyType.get() == EnergyType.THERMAL ) {
                    if ( energyChunk.position.get().getY() - THERMAL_ENERGY_CHUNK_Y_ORIGIN > THERMAL_ENERGY_CHUNK_TRAVEL_DISTANCE ) {
                        // Remove the chunk.
                        energyChunkList.remove( energyChunk );
                    }
                }
            }
        }
        return new Energy( EnergyType.MECHANICAL, energyProductionRate.get() * dt, Math.PI / 2 );
    }

    @Override public void deactivate() {
        super.deactivate();
        heatCoolAmount.reset();
        energyProductionRate.reset();
        steamEnergyProducedSinceLastChunk = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT / 2;
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        exemptFromTransferEnergyChunks.clear();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectTeapotButton;
    }

    public ObservableProperty<Double> getEnergyProductionRate() {
        return energyProductionRate;
    }
}
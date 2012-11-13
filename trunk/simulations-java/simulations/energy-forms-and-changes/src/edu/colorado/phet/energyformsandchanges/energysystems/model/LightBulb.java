// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * Base class for light bulbs in the model.
 *
 * @author John Blanco
 */
public class LightBulb extends EnergyUser {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Images used to represent this element in the view.
    public static final ModelElementImage WIRE_FLAT_IMAGE = new ModelElementImage( WIRE_BLACK_86, new Vector2D( -0.042, -0.04 ) );
    public static final ModelElementImage WIRE_CURVE_IMAGE = new ModelElementImage( WIRE_BLACK_RIGHT, new Vector2D( -0.009, -0.016 ) );
    public static final ModelElementImage ELEMENT_BASE_FRONT_IMAGE = new ModelElementImage( ELEMENT_BASE_FRONT, new Vector2D( 0, 0.0 ) );
    public static final ModelElementImage ELEMENT_BASE_BACK_IMAGE = new ModelElementImage( ELEMENT_BASE_BACK, new Vector2D( 0, 0.0 ) );

    // Offsets need for creating the path followed by the energy chunks.  These
    // were empirically determined based on images, will need to change if the
    // images are changed.
    private static final Vector2D OFFSET_TO_LEFT_SIDE_OF_WIRE_BEND = new Vector2D( -0.02, -0.04 );
    private static final Vector2D OFFSET_TO_FIRST_WIRE_CURVE_POINT = new Vector2D( -0.01, -0.0375 );
    private static final Vector2D OFFSET_TO_SECOND_WIRE_CURVE_POINT = new Vector2D( -0.001, -0.025 );
    private static final Vector2D OFFSET_TO_THIRD_WIRE_CURVE_POINT = new Vector2D( -0.0005, -0.0175 );
    private static final Vector2D OFFSET_TO_BOTTOM_OF_CONNECTOR = new Vector2D( 0, -0.01 );
    private static final Vector2D OFFSET_TO_RADIATE_POINT = new Vector2D( 0, 0.066 );

    // Miscellaneous other constants.
    private static final double RADIATED_ENERGY_CHUNK_MAX_DISTANCE = 0.5;
    private static final Random RAND = new Random();
    private static final DoubleRange THERMAL_ENERGY_CHUNK_TIME_ON_FILAMENT = new DoubleRange( 2, 2.5 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final double energyToFullyLight; // In joules/sec, a.k.a. watts.
    private final IUserComponent userComponent;

    public final Property<Double> litProportion = new Property<Double>( 0.0 );

    private List<EnergyChunkPathMover> electricalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> thermalEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private List<EnergyChunkPathMover> lightEnergyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected LightBulb( IUserComponent userComponent, Image icon, double energyToFullyLight ) {
        super( icon );
        this.userComponent = userComponent;
        this.energyToFullyLight = energyToFullyLight;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {

        if ( isActive() ) {
            // Handle any incoming energy chunks.
            if ( !incomingEnergyChunks.isEmpty() ) {
                for ( EnergyChunk incomingEnergyChunk : incomingEnergyChunks ) {
                    if ( incomingEnergyChunk.energyType.get() == EnergyType.ELECTRICAL ) {

                        // Add the energy chunk to the list of those under management.
                        energyChunkList.add( incomingEnergyChunk );

                        // And a "mover" that will move this energy chunk through
                        // the wire to the bulb.
                        electricalEnergyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk, createElectricalEnergyChunkPath( getPosition() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    }
                    else {
                        // By design, this shouldn't happen, so warn if it does.
                        System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                    }
                }
                incomingEnergyChunks.clear();
            }

            // Move the electrical energy chunks.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( electricalEnergyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    electricalEnergyChunkMovers.remove( energyChunkMover );
                    // Turn this energy chunk into thermal energy on the filament.
                    energyChunkMover.energyChunk.energyType.set( EnergyType.THERMAL );
                    List<Vector2D> energyChunkPath = createThermalEnergyChunkPath( energyChunkMover.energyChunk.position.get() );
                    thermalEnergyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                            energyChunkPath,
                                                                            getTotalPathLength( energyChunkMover.energyChunk.position.get(), energyChunkPath ) / generateThermalChunkTimeOnFilament() ) );
                }
            }

            // Move the thermal energy chunks.
            for ( EnergyChunkPathMover thermalEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( thermalEnergyChunkMovers ) ) {
                thermalEnergyChunkMover.moveAlongPath( dt );
                if ( thermalEnergyChunkMover.isPathFullyTraversed() ) {
                    // Cause this energy chunk to be radiated from the bulb.
                    thermalEnergyChunkMovers.remove( thermalEnergyChunkMover );
                    thermalEnergyChunkMover.energyChunk.energyType.set( EnergyType.LIGHT );
                    List<Vector2D> lightPath = new ArrayList<Vector2D>() {{
                        add( getPosition().plus( OFFSET_TO_RADIATE_POINT ).plus( new Vector2D( 0, RADIATED_ENERGY_CHUNK_MAX_DISTANCE ).getRotatedInstance( ( RAND.nextDouble() - 0.5 ) * ( Math.PI / 2 ) ) ) );
                    }};
                    lightEnergyChunkMovers.add( new EnergyChunkPathMover( thermalEnergyChunkMover.energyChunk, lightPath, EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                }
            }

            // Move the light energy chunks.
            for ( EnergyChunkPathMover lightEnergyChunkMover : new ArrayList<EnergyChunkPathMover>( lightEnergyChunkMovers ) ) {
                lightEnergyChunkMover.moveAlongPath( dt );
                if ( lightEnergyChunkMover.isPathFullyTraversed() ) {
                    // Remove the chunk and its mover.
                    energyChunkList.remove( lightEnergyChunkMover.energyChunk );
                    lightEnergyChunkMovers.remove( lightEnergyChunkMover );
                }
            }

            // Set how lit the bulb is.
            if ( isActive() && incomingEnergy.type == EnergyType.ELECTRICAL ) {
                litProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / energyToFullyLight, 1 ) );
            }
            else {
                litProportion.set( 0.0 );
            }
        }
    }

    private boolean goRightNextTime = true;

    private List<Vector2D> createThermalEnergyChunkPath( final Vector2D startingPoint ) {
        // TODO: Make some things constants, refine, clean up, and all that.
        final double filamentWidth = 0.03;
        return new ArrayList<Vector2D>() {{
            add( startingPoint.plus( new Vector2D( ( 0.5 + RAND.nextDouble() / 2 ) * filamentWidth / 2 * ( goRightNextTime ? 1 : -1 ), 0 ) ) );
            goRightNextTime = !goRightNextTime;
        }};
    }

    private static List<Vector2D> createElectricalEnergyChunkPath( final Vector2D centerPosition ) {
        return new ArrayList<Vector2D>() {{
            add( centerPosition.plus( OFFSET_TO_LEFT_SIDE_OF_WIRE_BEND ) );
            add( centerPosition.plus( OFFSET_TO_FIRST_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_SECOND_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_THIRD_WIRE_CURVE_POINT ) );
            add( centerPosition.plus( OFFSET_TO_BOTTOM_OF_CONNECTOR ) );
            add( centerPosition.plus( OFFSET_TO_RADIATE_POINT ) );
        }};
    }

    private static double generateThermalChunkTimeOnFilament() {
        return THERMAL_ENERGY_CHUNK_TIME_ON_FILAMENT.getMin() + RAND.nextDouble() * THERMAL_ENERGY_CHUNK_TIME_ON_FILAMENT.getLength();
    }

    private static double getTotalPathLength( Vector2D startingLocation, List<Vector2D> pathPoints ) {
        if ( pathPoints.size() == 0 ) {
            return 0;
        }
        double pathLength = startingLocation.distance( pathPoints.get( 0 ) );
        for ( int i = 0; i < pathPoints.size() - 1; i++ ) {
            pathLength += pathPoints.get( i ).distance( pathPoints.get( i + 1 ) );
        }
        return pathLength;
    }

    @Override public void deactivate() {
        super.deactivate();
        litProportion.set( 0.0 );
    }

    @Override public IUserComponent getUserComponent() {
        return userComponent;
    }
}

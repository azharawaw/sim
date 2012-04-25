// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;

/**
 * Primary model class for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class IntroModel {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Size of the lab table top.
    private static final double LAB_TABLE_WIDTH = 0.5; // In meters.

    // Size of the thermometer shelf.
    private static final double THERMOMETER_SHELF_WIDTH = 0.1; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    protected final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of all shelves in the model.
    private final List<Shelf> shelfList = new ArrayList<Shelf>();

    // Burners.
    private final Burner leftBurner;
    private final Burner rightBurner;

    // Movable thermal model objects.
    private final Brick brick;
    private final LeadBlock leadBlock;
    private final Beaker beaker;

    // Thermometers.
    private final Thermometer thermometer1;
    private final Thermometer thermometer2;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public IntroModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the main lab bench shelf.  The center of the shelf is the point
        // (0, 0) in model space.
        shelfList.add( new Shelf( new Point2D.Double( -LAB_TABLE_WIDTH / 2, 0 ),
                                  LAB_TABLE_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_LONG,
                                  LAB_TABLE_WIDTH * 0.015,
                                  LAB_TABLE_WIDTH * 0.05,
                                  Math.PI / 2 ) );

        // Add the thermometer shelf.
        shelfList.add( new Shelf( new Point2D.Double( -0.2, 0.15 ),
                                  THERMOMETER_SHELF_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_SHORT,
                                  THERMOMETER_SHELF_WIDTH * 0.05,
                                  THERMOMETER_SHELF_WIDTH * 0.20,
                                  Math.PI / 2 ) );

        // Add the burners.
        rightBurner = new Burner( new Point2D.Double( 0.18, 0 ) );
        leftBurner = new Burner( new Point2D.Double( 0.08, 0 ) );

        // Add and position the beaker.
        beaker = new Beaker( new ImmutableVector2D( -0.01, 0 ) );

        // Add and position the blocks
        brick = new Brick( new ImmutableVector2D( -0.1, 0 ) );
        leadBlock = new LeadBlock( new ImmutableVector2D( -0.175, 0 ) );

        // Add and position the thermometers.
        thermometer1 = new Thermometer( new ImmutableVector2D( -0.2, 0.22 ) );
        thermometer2 = new Thermometer( new ImmutableVector2D( -0.15, 0.22 ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Update the state of the model.
     *
     * @param dt
     */
    private void stepInTime( double dt ) {

        // Cause any user-movable model elements that are not supported by a
        // surface to fall (or, in some cases, jump up) towards the nearest
        // supporting surface.
        for ( UserMovableModelElement movableModelElement : Arrays.asList( leadBlock, brick, beaker ) ) {
            if ( !movableModelElement.userControlled.get() && movableModelElement.getSupportingSurface() == null && movableModelElement.position.get().getY() != 0 ) {
                double acceleration = -9.8; // meters/s*s
                double velocity = movableModelElement.verticalVelocity.get() + acceleration * dt;
                double proposedYPos = movableModelElement.position.get().getY() + velocity * dt;
                double minYPos = 0;
                Property<HorizontalSurface> potentialSupportingSurface = findBestSupportSurface( movableModelElement );
                if ( potentialSupportingSurface != null ) {
                    minYPos = potentialSupportingSurface.get().yPos;

                    // Center the movableModelElement on its new parent
                    double targetX = potentialSupportingSurface.get().getCenterX();
                    movableModelElement.setX( targetX );
                }
                if ( proposedYPos < minYPos ) {
                    // The element has landed on the ground or some other surface.
                    proposedYPos = minYPos;
                    movableModelElement.verticalVelocity.set( 0.0 );
                    if ( potentialSupportingSurface != null ) {
                        movableModelElement.setSupportingSurface( potentialSupportingSurface );
                    }
                }
                else {
                    movableModelElement.verticalVelocity.set( velocity );
                }
                movableModelElement.position.set( new ImmutableVector2D( movableModelElement.position.get().getX(), proposedYPos ) );
            }
        }

        // Update the fluid level in the beaker, which could be displaced by
        // one or more of the blocks.
        beaker.updateFluidLevel( Arrays.asList( brick.getRect(), leadBlock.getRect() ) );
    }

    public List<Shelf> getShelfList() {
        return shelfList;
    }

    public void reset() {
        // TODO.
    }

    public IClock getClock() {
        return clock;
    }

    public Brick getBrick() {
        return brick;
    }

    public LeadBlock getLeadBlock() {
        return leadBlock;
    }

    public Burner getLeftBurner() {
        return leftBurner;
    }

    public Burner getRightBurner() {
        return rightBurner;
    }

    public Beaker getBeaker() {
        return beaker;
    }

    public Thermometer getThermometer1() {
        return thermometer1;
    }

    public Thermometer getThermometer2() {
        return thermometer2;
    }

    private Property<HorizontalSurface> findBestSupportSurface( UserMovableModelElement element ) {
        Property<HorizontalSurface> bestOverlappingSurface = null;

        // Check each of the possible supporting elements in the model to see
        // if this element can go on top of it.
        for ( ModelElement potentialSupportingElement : Arrays.asList( leftBurner, rightBurner, brick, leadBlock, beaker ) ) {
            if ( potentialSupportingElement == element || potentialSupportingElement.isStackedUpon( element ) ) {
                // The potential supporting element is either the same as the
                // test element or is sitting on top of the test element.  In
                // either case, it can't be used to support the test element,
                // so skip it.
                continue;
            }
            if ( potentialSupportingElement != element && element.getBottomSurfaceProperty().get().overlapsWith( potentialSupportingElement.getTopSurfaceProperty().get() ) ) {

                // There is at least some overlap.  Determine if this surface
                // is the best one so far.
                double surfaceOverlap = getHorizontalOverlap( potentialSupportingElement.getTopSurfaceProperty().get(), element.getBottomSurfaceProperty().get() );

                // The following nasty 'if' clause determines if the potential
                // supporting surface is a better one than we currently have
                // based on whether we have one at all, or has more overlap
                // than the previous best choice, or is directly above the
                // current one.
                if ( bestOverlappingSurface == null ||
                     ( surfaceOverlap > getHorizontalOverlap( bestOverlappingSurface.get(), element.getBottomSurfaceProperty().get() ) &&
                       !isDirectlyAbove( bestOverlappingSurface.get(), potentialSupportingElement.getTopSurfaceProperty().get() ) ) ||
                     ( isDirectlyAbove( potentialSupportingElement.getTopSurfaceProperty().get(), bestOverlappingSurface.get() ) ) ) {
                    bestOverlappingSurface = potentialSupportingElement.getTopSurfaceProperty();
                }
            }
        }
        return bestOverlappingSurface;
    }

    public List<Block> getBlockList() {
        return Arrays.asList( brick, leadBlock );
    }

    // Get the amount of overlap in the x direction between two horizontal surfaces.
    private double getHorizontalOverlap( HorizontalSurface s1, HorizontalSurface s2 ) {
        double lowestMax = Math.min( s1.xRange.getMax(), s2.xRange.getMax() );
        double highestMin = Math.max( s1.xRange.getMin(), s2.xRange.getMin() );
        return Math.max( lowestMax - highestMin, 0 );
    }

    // Returns true if surface s1's center is above surface s2.
    private boolean isDirectlyAbove( HorizontalSurface s1, HorizontalSurface s2 ) {
        return s2.xRange.contains( s1.getCenterX() ) && s1.yPos > s2.yPos;
    }
}

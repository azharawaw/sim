// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.PlateType;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

public class RiftingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public static final float RIDGE_TOP_Y = -1500;
    public static final float SPREAD_START_TIME = 10.0f;

    public static final float RIFT_PLATE_SPEED = 30000f / 2;

    public RiftingBehavior( final PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        plate.getLithosphere().moveToFront();
        plate.getCrust().moveToFront();
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;

        // TODO: why are we having terrain issues with this?
//        removeEarthEdges();

        moveSpreading( millionsOfYears );
    }

    private void moveSpreading( float millionsOfYears ) {
        float idealChunkWidth = plate.getSimpleChunkWidth();

        final Set<Integer> elevationColumnsChanged = new HashSet<Integer>();

        final float xOffset = RIFT_PLATE_SPEED * (float) plate.getSide().getSign() * millionsOfYears;

        // move all of the lithosphere
        final Region[] mobileRegions = { getPlate().getLithosphere(), getPlate().getCrust() };
        for ( Region region : mobileRegions ) {
            for ( Sample sample : region.getSamples() ) {
                sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( xOffset, 0, 0 ) ) );
            }
        }

        // synchronize the terrain with the crust top
        for ( int i = 0; i < getPlate().getCrust().getTopBoundary().samples.size(); i++ ) {
            Sample crustSample = getPlate().getCrust().getTopBoundary().samples.get( i );
            TerrainSample frontTerrainSample = getPlate().getTerrain().getSample( i, getPlate().getTerrain().getFrontZIndex() );

            float oldXPosition = getPlate().getTerrain().xPositions.get( i );
            ImmutableVector3F delta = crustSample.getPosition().minus( new ImmutableVector3F( oldXPosition,
                                                                                              frontTerrainSample.getElevation(), 0 ) );

            for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                final TerrainSample terrainSample = getPlate().getTerrain().getSample( i, row );
                terrainSample.setElevation( terrainSample.getElevation() + delta.y );
            }

            getPlate().getTerrain().xPositions.set( i, oldXPosition + delta.x );
        }

        // if our "shortened" segment needs to be updated, then we update it
        {
            final Sample centerTopSample = getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 );
            final Sample nextSample = getSampleFromCenter( plate.getCrust().getTopBoundary(), 1 );
            final float currentWidth = Math.abs( nextSample.getPosition().x - centerTopSample.getPosition().x );
            if ( currentWidth < idealChunkWidth * 1.001 ) {
                float currentPosition = centerTopSample.getPosition().x;
                float idealPosition = nextSample.getPosition().x - idealChunkWidth * plate.getSide().getSign();

                if ( idealPosition * plate.getSign() < 0 ) {
                    idealPosition = 0;
                }

                final float offset = idealPosition - currentPosition;
                final int index = plate.getSide().opposite().getIndex( plate.getCrust().getTopBoundary().samples );
                shiftColumn( index, offset );

                if ( idealPosition != 0 && plate.getCrust().getTopBoundary().samples.get( index ).getDensity() == PlateType.YOUNG_OCEANIC.getDensity() ) {
                    sinkOceanicCrust( Math.abs( idealPosition ) / RIFT_PLATE_SPEED, index );
                    elevationColumnsChanged.add( index );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * add fresh crust
        *----------------------------------------------------------------------------*/
        final Side zeroSide = plate.getSide().opposite();
        while ( getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 ).getPosition().x * plate.getSide().getSign() > 0.0001 ) {
            plate.addSection( zeroSide, PlateType.YOUNG_OCEANIC );

            // if we add to the left, we need to update all of our "already elevation changed" indices
            if ( zeroSide == Side.LEFT ) {
                Set<Integer> copy = new HashSet<Integer>( elevationColumnsChanged );
                elevationColumnsChanged.clear();
                for ( Integer column : copy ) {
                    elevationColumnsChanged.add( column + 1 );
                }
            }

            { // update the new section positions
                // re-layout that section
                final int newIndex = zeroSide.getIndex( plate.getCrust().getTopBoundary().samples );
                final float crustBottom = RIDGE_TOP_Y - PlateType.YOUNG_OCEANIC.getCrustThickness();
                plate.getCrust().layoutColumn( newIndex,
                                               RIDGE_TOP_Y,
                                               crustBottom,
                                               plate.getTextureStrategy(), true ); // essentially reset the textures

                // make the mantle part of the lithosphere have zero thickness here
                plate.getLithosphere().layoutColumn( newIndex,
                                                     crustBottom, crustBottom,
                                                     plate.getTextureStrategy(), true );

                for ( TerrainSample sample : plate.getTerrain().getColumn( newIndex ) ) {
                    sample.setElevation( RIDGE_TOP_Y );
                }
            }

            // this will reference the newly created section top are on the same side, then we need to process the heights like normal
            final float newX = getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 ).getPosition().x;
            if ( newX * plate.getSide().getSign() > 0 ) {
                // our created column is on the correct side (there was a lot of room)
                // we need to compensate for how much time should have passed

                int newIndex = zeroSide.getIndex( plate.getCrust().getTopBoundary().samples );

                // we can actually compute the "passed" years directly here from our position
                sinkOceanicCrust( newX / RIFT_PLATE_SPEED, newIndex );
                elevationColumnsChanged.add( newIndex );
            }
            else {
                // our column needs to be put in the exact center (x=0)
                final List<Sample> topSamples = plate.getCrust().getTopBoundary().samples;
                final int index = zeroSide.getIndex( topSamples );

                // shift over the entire column
                shiftColumn( index, -zeroSide.getEnd( topSamples ).getPosition().x );
            }
        }

        /*---------------------------------------------------------------------------*
        * handle oceanic crust changes
        *----------------------------------------------------------------------------*/
        {
            for ( int columnIndex = 0; columnIndex < plate.getCrust().getTopBoundary().samples.size(); columnIndex++ ) {
                Sample topSample = plate.getCrust().getTopBoundary().samples.get( columnIndex );
                if ( topSample.getDensity() == PlateType.CONTINENTAL.getDensity() ) {
                    /*---------------------------------------------------------------------------*
                    * continental modifications
                    *----------------------------------------------------------------------------*/

                    // blending crust sizes here (and preferably lithosphere too?)
                    float fakeNeighborhoodY = topSample.getPosition().y;
                    int count = 1;
                    if ( columnIndex > 0 ) {
                        fakeNeighborhoodY += plate.getCrust().getTopBoundary().samples.get( columnIndex - 1 ).getPosition().y;
                        count += 1;
                    }
                    if ( columnIndex < plate.getCrust().getTopBoundary().samples.size() - 1 ) {
                        fakeNeighborhoodY += plate.getCrust().getTopBoundary().samples.get( columnIndex + 1 ).getPosition().y;
                        count += 1;
                    }
                    fakeNeighborhoodY /= count;

                    float currentCrustTop = plate.getCrust().getTopElevation( columnIndex );
                    float currentCrustBottom = plate.getCrust().getBottomElevation( columnIndex );
                    float currentLithosphereBottom = plate.getLithosphere().getBottomElevation( columnIndex );
                    float currentCrustWidth = currentCrustTop - currentCrustBottom;

                    // try subtracting off top and bottom, and see how much all of this would change
                    float resizeFactor = ( currentCrustWidth - 2 * ( currentCrustTop - fakeNeighborhoodY ) ) / currentCrustWidth;

                    // don't ever grow the crust height
                    if ( resizeFactor > 1 ) {
                        resizeFactor = 1;
                    }
                    resizeFactor = (float) Math.pow( resizeFactor, 2 * millionsOfYears );
                    float center = ( currentCrustTop + currentCrustBottom ) / 2;

                    final float newCrustTop = ( currentCrustTop - center ) * resizeFactor + center;

                    // x^2 the resizing factor for the bottoms so they shrink faster
                    final float newCrustBottom = ( currentCrustBottom - center ) * resizeFactor * resizeFactor + center;
                    final float newLithosphereBottom = ( currentLithosphereBottom - center ) * resizeFactor * resizeFactor + center;
                    plate.getCrust().layoutColumn( columnIndex,
                                                   newCrustTop,
                                                   newCrustBottom,
                                                   plate.getTextureStrategy(), true );
                    plate.getLithosphere().layoutColumn( columnIndex,
                                                         newCrustBottom,
                                                         newLithosphereBottom,
                                                         plate.getTextureStrategy(), true );
                    plate.getTerrain().shiftColumnElevation( columnIndex, newCrustTop - currentCrustTop );
                    // TODO: change the lithosphere!
                }
                else {
                    /*---------------------------------------------------------------------------*
                    * oceanic modifications
                    *----------------------------------------------------------------------------*/

                    // TODO: if multiple crusty parts are created at a time (or FPS isn't constant) this part is off in the middle. FIX
                    // TODO: if multiple crusty parts are created at a time (or FPS isn't constant) this part is off in the middle. FIX
                    // TODO: if multiple crusty parts are created at a time (or FPS isn't constant) this part is off in the middle. FIX
                    // TODO: if multiple crusty parts are created at a time (or FPS isn't constant) this part is off in the middle. FIX

                    // sink the crust, advancing us along an arctangent-sloped curve
                    // don't sink the crust on ones that we have already done
                    if ( topSample.getPosition().x != 0 && !elevationColumnsChanged.contains( columnIndex ) ) {
                        sinkOceanicCrust( millionsOfYears, columnIndex );
                    }
                }
            }
        }

        riftPostProcess();
    }

    private void sinkOceanicCrust( float millionsOfYears, int columnIndex ) {
        float topY = RIDGE_TOP_Y;
        float bottomY = PlateType.OLD_OCEANIC.getCrustTopY();

        {
            /*---------------------------------------------------------------------------*
            * sink the plate as it moves out
            *----------------------------------------------------------------------------*/

            final float magicConstant1 = 4;

            float currentTopY = plate.getCrust().getTopElevation( columnIndex );
            float currentRatio = ( currentTopY - topY ) / ( bottomY - topY );

            // invert arctanget, offset, then apply normally
            float currentT = (float) Math.tan( currentRatio * ( Math.PI / 2 ) );
            float newT = currentT + millionsOfYears * magicConstant1;
            float newRatio = (float) ( Math.atan( newT ) / ( Math.PI / 2 ) );

            // some necessary assertions for any future debugging
            assert currentRatio >= 0;
            assert currentRatio <= 1;
            assert currentT >= 0;
            assert newRatio >= 0;
            assert newRatio <= 1;

            float newTopY = ( 1 - newRatio ) * topY + ( newRatio ) * bottomY;
            float offsetY = newTopY - currentTopY;
            for ( Region region : new Region[] { plate.getCrust(), plate.getLithosphere() } ) {
                for ( Boundary boundary : region.getBoundaries() ) {
                    final Sample sample = boundary.samples.get( columnIndex );
                    sample.setPosition( sample.getPosition().plus( ImmutableVector3F.Y_UNIT.times( offsetY ) ) );
                }
            }
            plate.getTerrain().shiftColumnElevation( columnIndex, offsetY );
        }

        {
            /*---------------------------------------------------------------------------*
            * accrue more lithosphere here
            *----------------------------------------------------------------------------*/
            float currentMantleTop = plate.getLithosphere().getTopElevation( columnIndex );
            float currentLithosphereBottom = plate.getLithosphere().getBottomElevation( columnIndex );

            float magicConstant2 = 1;

            // 0 = thinnest, 1 = thickest
            final float maxThickness = PlateType.YOUNG_OCEANIC.getMantleLithosphereThickness();
            float currentRatio = ( currentMantleTop - currentLithosphereBottom ) / maxThickness;

            // invert arctanget, offset, then apply normally
            float currentT = (float) Math.tan( currentRatio * ( Math.PI / 2 ) );
            float newT = currentT + millionsOfYears * magicConstant2;
            float newRatio = (float) ( Math.atan( newT ) / ( Math.PI / 2 ) );

            float newLithosphereBottom = currentMantleTop - maxThickness * newRatio;
            plate.getLithosphere().layoutColumn( columnIndex,
                                                 currentMantleTop,
                                                 newLithosphereBottom,
                                                 plate.getTextureStrategy(), true );
        }
    }

    private void shiftColumn( int columnIndex, float xOffset ) {
        for ( Region region : new Region[] { plate.getCrust(), plate.getLithosphere() } ) {
            for ( Boundary boundary : region.getBoundaries() ) {
                boundary.samples.get( columnIndex ).shiftWithTexture( new ImmutableVector3F( xOffset, 0, 0 ), plate.getTextureStrategy() );
            }
        }

        plate.getTerrain().xPositions.set( columnIndex, plate.getTerrain().xPositions.get( columnIndex ) + xOffset );
        final ImmutableVector2F offset2D = new ImmutableVector2F( xOffset, 0 );

        for ( TerrainSample sample : plate.getTerrain().getColumn( columnIndex ) ) {
            sample.shiftWithTexture( offset2D, plate.getTextureStrategy() );
        }
        plate.getTerrain().columnsModified.updateListeners();
    }

    private void riftPostProcess() {
        getPlate().getTerrain().elevationChanged.updateListeners();

        glueMantleTopToLithosphere( 1000 );
        redistributeMantle();
    }

    private Sample getSampleFromCenter( Boundary boundary, int offsetFromCenter ) {
        return boundary.getEdgeSample( getPlate().getSide().opposite(), offsetFromCenter );
    }
}

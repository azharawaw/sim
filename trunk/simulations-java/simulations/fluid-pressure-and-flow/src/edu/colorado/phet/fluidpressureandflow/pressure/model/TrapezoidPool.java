// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * A pair of trapezoidal pools.
 *
 * @author Sam Reid
 */
public class TrapezoidPool implements IPool {

    //Units in meters, describes the leftmost chamber and is used to create both
    private final double widthAtTop = 1;
    private final double widthAtBottom = 4;
    public final double centerAtLeftChamberOpening = -2.9;
    private final double separation = 3.9;//Between centers

    private final double yAtTop = 0;
    public final double height = 3;
    public final Property<Double> flowRatePercentage = new Property<Double>( 0.0 );
    public final ObservableProperty<Boolean> faucetEnabled = new Property<Boolean>( true );
    private double passageHeight = 0.25;

    //Thickness into the screen, for computing volumes.
    private double thickness = 1.0;

    @Override public Shape getContainerShape() {
        return getShape( height );
    }

    public Shape getShape( double height ) {
        return new Area( leftChamber() ) {{
            add( new Area( rightChamber() ) );
            add( new Area( passage() ) );
        }};
    }

    //In meters squared, determine the area of the cross section the user can see.
    //This is used for filling up the container at a constant volumetric rate
//    public double getArea( double height ) {
//        return widthAtTop + widthAtBottom;
//        return 0.0;
//    }

    private Shape passage() {
        return new Rectangle2D.Double( centerAtLeftChamberOpening, -height + passageHeight, separation, passageHeight );
    }

    public Shape fromLines( final ImmutableVector2D... pts ) {
        if ( pts.length == 0 ) { return new Rectangle2D.Double( 0, 0, 0, 0 ); }
        return new DoubleGeneralPath( pts[0] ) {{
            for ( int i = 1; i < pts.length; i++ ) {
                lineTo( pts[i] );
            }
        }}.getGeneralPath();
    }

    private Shape leftChamber() {
        final ImmutableVector2D topLeft = new ImmutableVector2D( centerAtLeftChamberOpening - widthAtTop / 2, yAtTop );
        final ImmutableVector2D topRight = topLeft.plus( widthAtTop, 0 );
        final ImmutableVector2D bottomLeft = new ImmutableVector2D( centerAtLeftChamberOpening - widthAtBottom / 2, yAtTop - height );
        final ImmutableVector2D bottomRight = bottomLeft.plus( widthAtBottom, 0 );

        return fromLines( topLeft, bottomLeft, bottomRight, topRight );
    }

    private Shape rightChamber() {
        final ImmutableVector2D topLeft = new ImmutableVector2D( centerAtLeftChamberOpening + separation - widthAtBottom / 2, yAtTop );
        final ImmutableVector2D topRight = topLeft.plus( widthAtBottom, 0 );
        final ImmutableVector2D bottomLeft = new ImmutableVector2D( centerAtLeftChamberOpening + separation - widthAtTop / 2, yAtTop - height );
        final ImmutableVector2D bottomRight = bottomLeft.plus( widthAtTop, 0 );

        return fromLines( topLeft, bottomLeft, bottomRight, topRight );
    }

    @Override public double getHeight() {
        return getContainerShape().getBounds2D().getHeight();
    }

    @Override public Shape getWaterShape() {
        return new Rectangle2D.Double();
    }
}
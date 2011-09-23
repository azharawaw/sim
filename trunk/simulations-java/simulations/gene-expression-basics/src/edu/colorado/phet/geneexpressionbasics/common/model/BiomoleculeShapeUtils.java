// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Several utilities for making it easier to create some complex and somewhat
 * random shapes.  This was created initially to make it easier to create the
 * shapes associated with biomolecules, but may have other uses.
 *
 * @author John Blanco
 */
public class BiomoleculeShapeUtils {

    /* not intended for instantiation */
    private BiomoleculeShapeUtils() {
    }

    public static Shape createShapeFromPoints( List<Point2D> points ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( Point2D point : points ) {
            path.lineTo( point );
        }
        path.closePath();
        return path.getGeneralPath();
    }

    /**
     * Create a shape from a set of points.  The points must be in an order
     * that, if connected by straight lines, would form a closed shape.  If not,
     * the shape will look quite odd.
     *
     * @param points
     * @return
     */
    public static Shape createRoundedShapeFromPoints( List<Point2D> points ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( int i = 0; i < points.size(); i++ ) {
            ImmutableVector2D segmentStartPoint = new ImmutableVector2D( points.get( i ) );
            ImmutableVector2D segmentEndPoint = new ImmutableVector2D( points.get( ( i + 1 ) % points.size() ) );
            ImmutableVector2D previousPoint = new ImmutableVector2D( points.get( i - 1 >= 0 ? i - 1 : points.size() - 1 ) );
            ImmutableVector2D nextPoint = new ImmutableVector2D( points.get( ( i + 2 ) % points.size() ) );
            ImmutableVector2D controlPoint1 = extrapolateControlPoint( previousPoint, segmentStartPoint, segmentEndPoint );
            ImmutableVector2D controlPoint2 = extrapolateControlPoint( nextPoint, segmentEndPoint, segmentStartPoint );
            path.curveTo( controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), segmentEndPoint.getX(), segmentEndPoint.getY() );
        }
        return path.getGeneralPath();
    }

    /**
     * Create a distorted shape from a list of points.  This is useful when
     * trying to animate some sort of deviation from a basic shape.
     * <p/>
     * Note that this works best when the points are centered around the point
     * (0, 0), and may not work at all otherwise (it has never been tested).
     *
     * @param points
     * @param distortionFactor
     * @param randomNumberSeed
     * @return
     */
    public static Shape createdDistortedRoundedShapeFromPoints( List<Point2D> points, double distortionFactor, long randomNumberSeed ) {
        // Determine the center location of the undistorted shape.
        Shape undistortedShape = createRoundedShapeFromPoints( points );
        Point2D undistortedShapeCenter = new Point2D.Double( undistortedShape.getBounds2D().getCenterX(), undistortedShape.getBounds2D().getCenterY() );
        // Alter the positions of the points that define the shape in order to
        // define a distorted version of the shape.
        Random rand = new Random( randomNumberSeed );
        List<Point2D> alteredPoints = new ArrayList<Point2D>();
        for ( Point2D point : points ) {
            Vector2D pointAsVector = new Vector2D( point );
            pointAsVector.scale( 1 + ( rand.nextDouble() - 0.5 ) * distortionFactor );
            alteredPoints.add( pointAsVector.toPoint2D() );
        }
        // Create the basis for the new shape.
        Shape distortedShape = createRoundedShapeFromPoints( alteredPoints );
        // Determine the center of the new shape.
        Point2D distortedShapeCenter = new Point2D.Double( distortedShape.getBounds2D().getCenterX(), distortedShape.getBounds2D().getCenterY() );
        // Shift the new shape so that the center is in the same place as the old one.
        distortedShape = AffineTransform.getTranslateInstance( undistortedShapeCenter.getX() - distortedShapeCenter.getX(),
                                                               undistortedShapeCenter.getY() - distortedShapeCenter.getY() ).createTransformedShape( distortedShape );

        return distortedShape;
    }

    /**
     * Create a shape based on a set of points.  The points must be in an order
     * that, if connected by straight lines, would form a closed shape.  Some of
     * the segments will be straight lines and some will be curved, and which is
     * which will be based on a pseudo-random variable.
     *
     * @param points
     * @param seed
     * @return
     */
    public static Shape createRandomShapeFromPoints( List<Point2D> points, int seed ) {
        Random rand = new Random( seed );
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( int i = 0; i < points.size(); i++ ) {
            ImmutableVector2D segmentStartPoint = new ImmutableVector2D( points.get( i ) );
            ImmutableVector2D segmentEndPoint = new ImmutableVector2D( points.get( ( i + 1 ) % points.size() ) );
            ImmutableVector2D previousPoint = new ImmutableVector2D( points.get( i - 1 >= 0 ? i - 1 : points.size() - 1 ) );
            ImmutableVector2D nextPoint = new ImmutableVector2D( points.get( ( i + 2 ) % points.size() ) );
            ImmutableVector2D controlPoint1 = extrapolateControlPoint( previousPoint, segmentStartPoint, segmentEndPoint );
            ImmutableVector2D controlPoint2 = extrapolateControlPoint( nextPoint, segmentEndPoint, segmentStartPoint );
            if ( rand.nextBoolean() ) {
                // Curved segment.
                path.curveTo( controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), segmentEndPoint.getX(), segmentEndPoint.getY() );
            }
            else {
                // Straight segment.
                path.lineTo( segmentEndPoint.getX(), segmentEndPoint.getY() );
            }
        }
        return path.getGeneralPath();
    }

    public static Shape createRandomShape( Dimension2D size, int seed ) {
        Random rand = new Random( seed );
        DoubleGeneralPath path = new DoubleGeneralPath();
        List<Point2D> pointList = new ArrayList<Point2D>();
        // Create a series of points that will enclose a space.
        for ( double angle = 0; angle < 1.9 * Math.PI; angle += Math.PI / 10 + rand.nextDouble() * Math.PI / 10 ) {
            pointList.add( ImmutableVector2D.createPolar( 0.5 + rand.nextDouble(), angle ).toPoint2D() );
        }

        Shape unscaledShape = createRandomShapeFromPoints( pointList, seed );

        // Scale the shape to the specified size.
        double horizontalScale = size.getWidth() / unscaledShape.getBounds2D().getWidth();
        double verticalScale = size.getHeight() / unscaledShape.getBounds2D().getHeight();
        Shape scaledShape = AffineTransform.getScaleInstance( horizontalScale, verticalScale ).createTransformedShape( unscaledShape );
        return scaledShape;
    }

    /**
     * Create a curvy line from a list of points.  The points are assumed to be
     * in order.
     *
     * @param points
     * @return
     */
    public static Shape createCurvyLineFromPoints( List<Point2D> points ) {
        assert points.size() > 0;

        // Control points, used throughout the code below for curving the line.
        ImmutableVector2D cp1, cp2;

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        if ( points.size() == 1 || points.size() == 2 ) {
            // Can't really create a curve from this, so draw a straight line
            // to the end point and call it good.
            path.lineTo( points.get( points.size() - 1 ) );
            return path.getGeneralPath();
        }
        // Create the first curved segment.
        cp1 = extrapolateControlPoint( new ImmutableVector2D( points.get( 2 ) ),
                                       new ImmutableVector2D( points.get( 1 ) ),
                                       new ImmutableVector2D( points.get( 0 ) ) );
        path.quadTo( cp1.getX(), cp1.getY(), points.get( 1 ).getX(), points.get( 1 ).getY() );
        // Create the middle segments.
        for ( int i = 1; i < points.size() - 2; i++ ) {
            ImmutableVector2D segmentStartPoint = new ImmutableVector2D( points.get( i ) );
            ImmutableVector2D segmentEndPoint = new ImmutableVector2D( points.get( ( i + 1 ) ) );
            ImmutableVector2D previousPoint = new ImmutableVector2D( points.get( i - 1 ) );
            ImmutableVector2D nextPoint = new ImmutableVector2D( points.get( ( i + 2 ) ) );
            ImmutableVector2D controlPoint1 = extrapolateControlPoint( previousPoint, segmentStartPoint, segmentEndPoint );
            ImmutableVector2D controlPoint2 = extrapolateControlPoint( nextPoint, segmentEndPoint, segmentStartPoint );
            path.curveTo( controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), segmentEndPoint.getX(), segmentEndPoint.getY() );
        }
        // Create the final curved segment.
        cp1 = extrapolateControlPoint( new ImmutableVector2D( points.get( points.size() - 3 ) ),
                                       new ImmutableVector2D( points.get( points.size() - 2 ) ),
                                       new ImmutableVector2D( points.get( points.size() - 1 ) ) );
        path.quadTo( cp1.getX(), cp1.getY(), points.get( points.size() - 1 ).getX(), points.get( points.size() - 1 ).getY() );
        return path.getGeneralPath();
    }

    public static Shape createSegmentedLineFromPoints( List<Point2D> points ) {
        assert points.size() > 0;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( points.get( 0 ) );
        for ( Point2D point : points ) {
            path.lineTo( point );
        }
        return path.getGeneralPath();
    }

    // Extrapolate a control point for a curve based on three points.  This
    // is used to "go around the corner" at y, starting from x, and heading
    // towards z.  The control point is for the y-to-z segment.
    private static ImmutableVector2D extrapolateControlPoint( ImmutableVector2D x, ImmutableVector2D y, ImmutableVector2D z ) {
        ImmutableVector2D xy = y.getSubtractedInstance( x );
        ImmutableVector2D yz = z.getSubtractedInstance( y );
        return y.getAddedInstance( xy.getScaledInstance( 0.25 ).getAddedInstance( yz.getScaledInstance( 0.25 ) ) );
    }

    // First extrapolation attempt.  Should work well in theory, but didn't
    // due to issues with angles, pi vs -pi, etc.
    private static ImmutableVector2D extrapolateControlPoint2( ImmutableVector2D x, ImmutableVector2D y, ImmutableVector2D z ) {
        Vector2D scaledYZ = new Vector2D( z.getSubtractedInstance( y ) ).scale( 0.33 );
        double xyAngle = x.getSubtractedInstance( y ).getAngle();
        if ( xyAngle < 0 ) {
            xyAngle += Math.PI;
        }
        double zyAngle = z.getSubtractedInstance( y ).getAngle();
        if ( zyAngle < 0 ) {
            zyAngle += Math.PI;
        }
        double xyzAngle = xyAngle - zyAngle;
        return y.getAddedInstance( scaledYZ.rotate( xyzAngle / 2 ) );
    }

    public static void main( String[] args ) {
        System.out.println( "Shape testing..." );
        List<Point2D> vLikePointList = new ArrayList<Point2D>() {{
            // V-like shape.
            add( new Point2D.Double( 0, 0 ) );
            add( new Point2D.Double( 20, -10 ) );
            add( new Point2D.Double( 0, 40 ) );
            add( new Point2D.Double( -20, -10 ) );
        }};

        List<Point2D> squarePointList = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 0, 0 ) );
            add( new Point2D.Double( 20, 0 ) );
            add( new Point2D.Double( 20, -20 ) );
            add( new Point2D.Double( 0, -20 ) );
            // Unit square - useful for debug in some cases.
//            add( new Point2D.Double( 0, 0 ) );
//            add( new Point2D.Double( 1, 0 ) );
//            add( new Point2D.Double( 1, -1 ) );
//            add( new Point2D.Double( 0, -1 ) );
        }};

        // Add the shapes.  Many are translated somewhat to avoid overlap.
        PCanvas canvas = new PCanvas();
        Shape shape = BiomoleculeShapeUtils.createShapeFromPoints( vLikePointList );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 50 ).createTransformedShape( shape ), Color.PINK ) );
        shape = BiomoleculeShapeUtils.createRoundedShapeFromPoints( vLikePointList );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 100, 50 ).createTransformedShape( shape ), Color.GREEN ) );
        shape = BiomoleculeShapeUtils.createRandomShapeFromPoints( vLikePointList, 101 );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 100 ).createTransformedShape( shape ), Color.ORANGE ) );
        shape = BiomoleculeShapeUtils.createRandomShape( new PDimension( 40, 40 ), 100 );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 100, 100 ).createTransformedShape( shape ), Color.BLUE ) );
        shape = BiomoleculeShapeUtils.createRandomShape( new PDimension( 80, 40 ), 123 );
        canvas.getLayer().addChild( new PhetPPath( AffineTransform.getTranslateInstance( 50, 200 ).createTransformedShape( shape ), Color.MAGENTA ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame( "Shape Util Testing" );
        frame.setContentPane( canvas );
        frame.setSize( 500, 500 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

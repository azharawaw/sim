// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.GreaterThan;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.LessThan;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.LGResources.Images;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating the source-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationNode2 extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );
    private static final PhetFont FONT = new PhetFont( Font.BOLD, 38 );

    private final Property<Double> rise, run, intercept;

    public SlopeInterceptEquationNode2( final Property<SlopeInterceptLine> interactiveLine, IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.intercept = new Property<Double>( interactiveLine.get().intercept );

        // y = mx + b
        PText yNode = new PhetPText( "y", FONT );
        PText equalsNode = new PhetPText( "=", FONT );
        PNode riseNode = new UpDownValueNode( this.rise, riseRange, FONT, FORMAT );
        final PPath lineNode = new PPath( new Line2D.Double( 0, 0, riseNode.getFullBoundsReference().getWidth(), 0 ) );
        PNode runNode = new UpDownValueNode( this.run, runRange, FONT, FORMAT );
        PText xNode = new PhetPText( "x", FONT );
        final PText interceptSignNode = new PhetPText( "+", FONT );
        PNode interceptNode = new UpDownValueNode( this.intercept, interceptRange, FONT, FORMAT, true /* abs */ );

        // rendering order
        {
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            addChild( interceptSignNode );
            addChild( interceptNode );
        }

        // layout
        {
            final double xSpacing = 6;
            final double ySpacing = 4;
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + xSpacing,
                                  yNode.getYOffset() );
            final double centerY = equalsNode.getFullBoundsReference().getCenterY();
            riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + xSpacing,
                                centerY - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            lineNode.setOffset( riseNode.getXOffset(),
                                centerY );
            runNode.setOffset( riseNode.getXOffset(),
                               centerY + ySpacing );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                             yNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + xSpacing,
                                         xNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + xSpacing,
                                     xNode.getYOffset() );
        }

        // sync the model with the controls
        rise.addObserver( new VoidFunction1<Double>() {
            public void apply( Double rise ) {
                interactiveLine.set( new SlopeInterceptLine( rise, interactiveLine.get().run, interactiveLine.get().intercept ) );
            }
        } );
        run.addObserver( new VoidFunction1<Double>() {
            public void apply( Double run ) {
                interactiveLine.set( new SlopeInterceptLine( interactiveLine.get().rise, run, interactiveLine.get().intercept ) );
            }
        } );
        intercept.addObserver( new VoidFunction1<Double>() {
            public void apply( Double intercept ) {
                interactiveLine.set( new SlopeInterceptLine( interactiveLine.get().rise, interactiveLine.get().run, intercept ) );
                interceptSignNode.setText( intercept > 0 ? "+" : "-" );
            }
        } );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                intercept.set( line.intercept );
            }
        } );
    }

    // Spinner button for incrementing a value
    private static class UpSpinnerButtonNode extends SpinnerButtonNode {
        public UpSpinnerButtonNode( final Property<Double> value, double maxValue ) {
            super( Images.SPINNER_UP, Images.SPINNER_UP_PRESSED, Images.SPINNER_UP_DISABLED,
                   new VoidFunction0() {
                       public void apply() {
                           value.set( value.get() + 1 );
                       }
                   }, new LessThan( value, maxValue ) );
        }
    }

    // Spinner button for decrementing a value
    private static class DownSpinnerButtonNode extends SpinnerButtonNode {
        public DownSpinnerButtonNode( final Property<Double> value, double minValue ) {
            super( Images.SPINNER_DOWN, Images.SPINNER_DOWN_PRESSED, Images.SPINNER_DOWN_DISABLED,
                   new VoidFunction0() {
                       public void apply() {
                           value.set( value.get() - 1 );
                       }
                   }, new GreaterThan( value, minValue ) );
        }
    }

    private static class UpDownValueNode extends PNode {

        public UpDownValueNode( final Property<Double> value, IntegerRange range, PhetFont font, final NumberFormat format ) {
            this( value, range, font, format, false /* abs */ );
        }

        public UpDownValueNode( final Property<Double> value, IntegerRange range, PhetFont font, final NumberFormat format, final boolean abs ) {

            UpSpinnerButtonNode upButton = new UpSpinnerButtonNode( value, range.getMax() );
            DownSpinnerButtonNode downButton = new DownSpinnerButtonNode( value, range.getMin() );
            final PText textNode = new PhetPText( font );
            textNode.setPickable( false );

            // compute max text width
            textNode.setText( format.format( abs ? Math.abs( range.getMin() ) : range.getMin() ) );
            double minValueWidth = textNode.getFullBoundsReference().getWidth();
            textNode.setText( format.format( abs ? Math.abs( range.getMax() ) : range.getMax() ) );
            double maxValueWidth = textNode.getFullBoundsReference().getWidth();
            final double maxWidth = Math.max( minValueWidth, maxValueWidth );
            PPath horizontalStrutNode = new PPath( new Rectangle2D.Double( 0, 0, maxWidth, 1 ) ) {{
                setStroke( null );
                setPickable( false );
            }};

            // rendering order
            addChild( horizontalStrutNode );
            addChild( upButton );
            addChild( downButton );
            addChild( textNode );

            // layout
            horizontalStrutNode.setOffset( 0, 0 );
            textNode.setOffset( maxWidth - textNode.getFullBoundsReference().getWidth(), 0 );
            upButton.setOffset( textNode.getFullBoundsReference().getMaxX() + 3,
                                textNode.getFullBoundsReference().getCenterY() - upButton.getFullBoundsReference().getHeight() - 1 );
            downButton.setOffset( upButton.getXOffset(),
                                  textNode.getFullBoundsReference().getCenterY() + 1 );

            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    textNode.setText( format.format( abs ? Math.abs( value ) : value ) );
                    textNode.setOffset( maxWidth - textNode.getFullBoundsReference().getWidth(), textNode.getYOffset() );
                }
            } );
        }
    }
}

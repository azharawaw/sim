// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.CubicCurve2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.Parameters;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.beerslawlab.common.view.TiledBackgroundNode;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationMeter;
import edu.colorado.phet.beerslawlab.concentration.model.Dropper;
import edu.colorado.phet.beerslawlab.concentration.model.Faucet;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Concentration meter, with probe.
 * <p/>
 * The probe needs to register the concentration when of all possible fluids that it may contact, including:
 * <ul>
 * <li>solution in the beaker
 * <li>output of the solvent faucet
 * <li>output of the drain faucet
 * <li>output of the dropper
 * </ul>
 * <p/>
 * Rather than trying to model the shapes of all of these fluids, we handle "probe is in fluid"
 * herein via intersection of view shapes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationMeterNode extends PhetPNode {

    public static final String VALUE_PATTERN = "0.000";
    private static final Color WIRE_COLOR =  new Color( 133, 0, 66 ).darker(); // a little darker than the meter body

    private final ConcentrationMeter meter;
    private final Solution solution;
    private final SolutionNode solutionNode;
    private final Faucet solventFaucet, drainFaucet;
    private final OutputFluidNode solventFluidNode, drainFluidNode;
    private final Dropper dropper;
    private final StockSolutionNode stockSolutionNode;

    private final ProbeNode probeNode;

    public ConcentrationMeterNode( ConcentrationMeter meter,
                                   Solution solution, SolutionNode solutionNode,
                                   Faucet solventFaucet, OutputFluidNode solventFluidNode,
                                   Faucet drainFaucet, OutputFluidNode drainFluidNode,
                                   Dropper dropper, StockSolutionNode stockSolutionNode ) {

        this.meter = meter;
        this.solutionNode = solutionNode;
        this.solution = solution;
        this.solventFaucet = solventFaucet;
        this.solventFluidNode = solventFluidNode;
        this.drainFaucet = drainFaucet;
        this.drainFluidNode = drainFluidNode;
        this.dropper = dropper;
        this.stockSolutionNode = stockSolutionNode;

        // nodes
        BodyNode bodyNode = new BodyNode( meter );
        probeNode = new ProbeNode( meter, solutionNode, solventFluidNode, drainFluidNode, stockSolutionNode );
        PNode wireNode = new WireNode( probeNode, bodyNode );
//        WireNode wireNode = new WireNode( meter, new ImmutableVector2D( bodyNode.getFullBoundsReference().getWidth() / 2, bodyNode.getFullBoundsReference().getHeight() / 2 ) );

        // rendering order
        addChild( wireNode );
        addChild( bodyNode );
        addChild( probeNode );

        //NOTE: layout is handled by child nodes observing model elements.

        // Update the meter value
        RichSimpleObserver valueUpdater = new RichSimpleObserver() {
            public void update() {
                updateValue();
            }
        };
        valueUpdater.observe( meter.probe.location, solution.solute, solution.volume, solution.concentration,
                              dropper.flowRate, solventFaucet.flowRate, drainFaucet.flowRate, dropper.location );
    }

    private void updateValue() {
        if ( probeNode.isInSolution() ) {
            meter.setValue( solution.concentration.get() );
        }
        else if ( probeNode.isInSolvent() ) {
            meter.setValue( 0d );
        }
        else if ( probeNode.isInDrain() ) {
            meter.setValue( solution.concentration.get() );
        }
        else if ( probeNode.isInStockSolution() ) {
            meter.setValue( dropper.solute.get().stockSolutionConcentration );
        }
        else {
            meter.setValue( null );
        }
    }

    // Meter body, origin at upper left.
    private static class BodyNode extends PNode {

        private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( VALUE_PATTERN );
        private static final String NO_VALUE = "-";

        // image-specific locations and dimensions
        private static final double TITLE_Y_OFFSET = 12;
        private static final double TEXT_X_MARGIN = 20;  // specific to image files
        private static final double VALUE_X_MARGIN = 30; // specific to image files
        private static final double VALUE_Y_OFFSET = 67; // specific to image files

        public BodyNode( final ConcentrationMeter meter ) {

            // text nodes
            PText titleNode = new PText( Strings.CONCENTRATION ) {{
                setTextPaint( Color.WHITE );
                setFont( new PhetFont( Font.BOLD, 18 ) );
            }};
            PText unitsNode = new PText( MessageFormat.format( Strings.PATTERN_PARENTHESES_0TEXT, Strings.UNITS_MOLES_PER_LITER ) ) {{
                setTextPaint( Color.WHITE );
                setFont( new PhetFont( Font.BOLD, 16 ) );
            }};
            final PText valueNode = new PText( VALUE_PATTERN ) {{
                setFont( new PhetFont( 24 ) );
            }};

            // create a background that fits the text
            final double maxTextWidth = Math.max( titleNode.getFullBoundsReference().getWidth(), Math.max( unitsNode.getFullBoundsReference().getWidth(), valueNode.getFullBoundsReference().getWidth() ) );
            final double bodyWidth = ( 2 * TEXT_X_MARGIN ) + maxTextWidth;
            final PImage imageNode = new TiledBackgroundNode( bodyWidth, Images.CONCENTRATION_METER_BODY_LEFT, Images.CONCENTRATION_METER_BODY_CENTER, Images.CONCENTRATION_METER_BODY_RIGHT );

            // rendering order
            addChild( imageNode );
            addChild( titleNode );
            addChild( unitsNode );
            addChild( valueNode );

            // layout
            titleNode.setOffset( ( imageNode.getFullBoundsReference().getWidth() - titleNode.getFullBoundsReference().getWidth() ) / 2, TITLE_Y_OFFSET );
            unitsNode.setOffset( ( imageNode.getFullBoundsReference().getWidth() - unitsNode.getFullBoundsReference().getWidth() ) / 2,
                                 titleNode.getFullBoundsReference().getMaxY() + 3 );
            valueNode.setOffset( 0, VALUE_Y_OFFSET ); //NOTE: x offset will be adjusted when value is set, to maintain right justification

            // body location
            meter.body.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            } );

            // displayed value
            meter.addValueObserver( new SimpleObserver() {
                public void update() {
                    Double value = meter.getValue();
                    if ( value == null ) {
                        valueNode.setText( NO_VALUE );
                        // centered
                        valueNode.setOffset( imageNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 ),
                                             valueNode.getYOffset() );
                    }
                    else {
                        // eg, "0.23400 M"
                        valueNode.setText( VALUE_FORMAT.format( value ) );
                        // right justified
                        valueNode.setOffset( imageNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth() - VALUE_X_MARGIN,
                                             valueNode.getYOffset() );
                    }
                }
            } );

            addInputEventListener( new NonInteractiveEventHandler( UserComponents.concentrationMeterBody ) );
        }
    }

    // Meter probe, origin at geometric center.
    private static class ProbeNode extends PNode {

        private final ConcentrationMeter meter;
        private final SolutionNode solutionNode;
        private final OutputFluidNode solventFluidNode;
        private final OutputFluidNode drainFluidNode;
        private final StockSolutionNode stockSolutionNode;

        public ProbeNode( final ConcentrationMeter meter, SolutionNode solutionNode, OutputFluidNode solventFluidNode, OutputFluidNode drainFluidNode, StockSolutionNode stockSolutionNode ) {

            this.meter = meter;
            this.solutionNode = solutionNode;
            this.solventFluidNode = solventFluidNode;
            this.drainFluidNode = drainFluidNode;
            this.stockSolutionNode = stockSolutionNode;

            PImage imageNode = new PImage( Images.CONCENTRATION_METER_PROBE ) {{
                scale( 0.5 );
            }};
            addChild( imageNode );
            imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() / 2 );

            // body location
            meter.probe.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new MovableDragHandler( UserComponents.concentrationMeterProbe, meter.probe, this ) {
                @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).add( Parameters.isInSolution, isInSolution() );
                }
            } );
        }

        private boolean isInSolution() {
            return isInNode( solutionNode );
        }

        private boolean isInSolvent() {
            return isInNode( solventFluidNode );
        }

        private boolean isInDrain() {
            return isInNode( drainFluidNode );
        }

        private boolean isInStockSolution() {
            return isInNode( stockSolutionNode );
        }

        private boolean isInNode( PNode node ) {
            //TODO ...or should this return true if any part of ProbeNode intersects node?
            return node.getFullBoundsReference().contains( meter.probe.location.get().toPoint2D() );
        }
    }

    // Wire that connects the probe to the body of the meter.
    private static class WireNode extends PPath {

        // The y coordinate of the body's control point varies with the x distance between the body and probe.
        private static final Function BODY_CTRL_Y = new LinearFunction( 0, 800, 0, 200 ); // x distance -> y coordinate

        public WireNode( final PNode probeNode, final PNode bodyNode ) {

            setStroke( new BasicStroke( 8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1f ) );
            setStrokePaint( WIRE_COLOR );

            final PropertyChangeListener updateCurve = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {

                    // Connect bottom-center of body to right-center of probe.
                    ImmutableVector2D bodyConnectionPoint = new ImmutableVector2D( bodyNode.getFullBoundsReference().getCenterX(), bodyNode.getFullBoundsReference().getMaxY() - 10 );
                    ImmutableVector2D probeConnectionPoint = new ImmutableVector2D( probeNode.getFullBoundsReference().getMaxX(), probeNode.getFullBoundsReference().getCenterY() );

                    // control points
                    ImmutableVector2D c1Offset = new ImmutableVector2D( 0, BODY_CTRL_Y.evaluate( bodyNode.getFullBoundsReference().getCenterX() - probeNode.getFullBoundsReference().getX() ) );
                    ImmutableVector2D c2Offset = new ImmutableVector2D( 50, 0 );
                    ImmutableVector2D c1 = new ImmutableVector2D( bodyConnectionPoint.getX() + c1Offset.getX(), bodyConnectionPoint.getY() + c1Offset.getY() );
                    ImmutableVector2D c2 = new ImmutableVector2D( probeConnectionPoint.getX() + c2Offset.getX(), probeConnectionPoint.getY() + c2Offset.getY() );

                    // cubic curve
                    setPathTo( new CubicCurve2D.Double( bodyConnectionPoint.getX(), bodyConnectionPoint.getY(),
                                                        c1.getX(), c1.getY(),
                                                        c2.getX(), c2.getY(),
                                                        probeConnectionPoint.getX(), probeConnectionPoint.getY() ) );
                }
            };

            // Update when bounds of the body or probe changes.
            probeNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, updateCurve );
            bodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, updateCurve );
        }
    }
}

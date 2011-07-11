// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Slider control for electronegativity.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronegativityControlNode extends PhetPNode {

     // track
    private static final PDimension TRACK_SIZE = new PDimension( 5, 125 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );

    // knob
    private static final PDimension KNOB_SIZE = new PDimension( 20, 15 );
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_NORMAL_COLOR = Color.GREEN;
    private static final Color KNOB_HIGHLIGHT_COLOR = Color.YELLOW;
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;

    // background
    private static final double BACKGROUND_X_MARGIN = 3;
    private static final double BACKGROUND_Y_MARGIN = ( KNOB_SIZE.getHeight() / 2 ) + 4;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_FILL_COLOR = Color.WHITE;

    private final Atom atom;
    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final DoubleRange range;

    /**
     * Constructor
     *
     * @param atom         the atom whose electronegativity we're controlling
     * @param range        range of electronegativity
     * @param snapInterval knob will snap to this increment when released
     */
    public ElectronegativityControlNode( final Atom atom, DoubleRange range, double snapInterval ) {

        this.atom = atom;
        this.range = range;

        // nodes
        trackNode = new TrackNode();
        knobNode = new KnobNode( this, trackNode, range, snapInterval, atom );
        PText atomNameNode = new PText( MessageFormat.format( MPStrings.PATTERN_0ATOM_NAME, atom.name ) ) {{
            setFont( new PhetFont( Font.BOLD, 22 ) );
        }};
        PText labelNode = new PText( MPStrings.ELECTRONEGATIVITY ) {{
            setFont( new PhetFont( 16 ) );
        }};


        // background, sized to fit around track and knob.
        Rectangle2D backgroundRect = new Rectangle2D.Double( 0, 0,
                                                             knobNode.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_X_MARGIN ),
                                                             trackNode.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_Y_MARGIN ) );
        PPath backgroundNode = new PPath( backgroundRect );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundNode.setPaint( BACKGROUND_FILL_COLOR );

        // rendering order
        addChild( backgroundNode );
        addChild( trackNode );
        addChild( knobNode );
        addChild( labelNode );
        addChild( atomNameNode );

        // layout
        {
            // background at origin
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            // track centered in background
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( trackNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getCenterY() - ( trackNode.getFullBoundsReference().getHeight() / 2 );
            trackNode.setOffset( x, y );
            // knob centered in track
            x = backgroundNode.getFullBoundsReference().getCenterX() + ( knobNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getCenterY();
            knobNode.setOffset( x, y );
            // label centered above the track
            x = backgroundNode.getFullBoundsReference().getCenterX() - (labelNode.getFullBoundsReference().getWidth() / 2 );
            y = backgroundNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - 2;
            labelNode.setOffset( x, y );
            // atom name centered above label
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( atomNameNode.getFullBoundsReference().getWidth() / 2 );
            y = labelNode.getFullBoundsReference().getMinY() - atomNameNode.getFullBoundsReference().getHeight() - 2;
            atomNameNode.setOffset( x, y );
        }

        atom.electronegativity.addObserver( new SimpleObserver() {
            public void update() {
                updateControl();
            }
        } );
    }

    // Updates the control to match the capacitor model.
    private void updateControl() {
        double electronegativity = atom.electronegativity.get();
        // knob location
        double x = knobNode.getXOffset();
        double y = trackNode.getYOffset() + trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - electronegativity ) / range.getLength() );
        knobNode.setOffset( x, y );
    }

    /*
     * The track that the bar moves in.
     * Origin is at upper-left corner.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            setPathTo( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) );
            setPaint( TRACK_FILL_COLOR );
            setStrokePaint( TRACK_STROKE_COLOR );
            setStroke( TRACK_STROKE );
        }
    }

    /*
     * The slider knob, points to the right.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PPath {

        public KnobNode( PNode relativeNode, PNode trackNode, DoubleRange range, double snapInterval, final Atom atom ) {

            float w = (float) KNOB_SIZE.getWidth();
            float h = (float) KNOB_SIZE.getHeight();
            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f );
            path.lineTo( 0.35f * -w, h / 2f );
            path.lineTo( -w, h / 2f );
            path.lineTo( -w, -h / 2f );
            path.lineTo( 0.35f * -w, -h / 2f );
            path.closePath();

            setPathTo( path );
            setPaint( KNOB_NORMAL_COLOR );
            setStroke( KNOB_STROKE );
            setStrokePaint( KNOB_STROKE_COLOR );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( this, KNOB_NORMAL_COLOR, KNOB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new KnobDragHandler( relativeNode, trackNode, this, range, snapInterval,
                                                        new VoidFunction1<Double>() {
                                                            public void apply( Double value ) {
                                                                atom.electronegativity.set( value );
                                                            }
                                                        } ) );
        }
    }

    // Drag handler for the knob, snaps to closet value.
    private static class KnobDragHandler extends VerticalSliderDragHandler {

        private final double snapInterval; // slider snaps to closet model value in this interval

        // see superclass for constructor params
        public KnobDragHandler( PNode relativeNode, PNode trackNode, PNode knobNode, DoubleRange range, double snapInterval, VoidFunction1<Double> updateFunction ) {
            super( relativeNode, trackNode, knobNode, range, updateFunction );
            this.snapInterval = snapInterval;
        }

        // snaps to the closest value
        @Override protected double adjustValue( double value ) {
            return Math.floor( ( value / snapInterval ) + 0.5d ) * snapInterval;
        }
    }

    // test
    public static void main( String[] args ) {

        final Atom atom = new Atom( "Y", 3 );
        atom.electronegativity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                System.out.println( "electronegativity=" + value );
            }
        } );
        DoubleRange range = new DoubleRange( 0.7, 4 );
        double snapInterval = 0.1;

        ElectronegativityControlNode controlNode = new ElectronegativityControlNode( atom, range, snapInterval );
        controlNode.setOffset( 100, 100 );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 300, 400 ) );
        canvas.getLayer().addChild( controlNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

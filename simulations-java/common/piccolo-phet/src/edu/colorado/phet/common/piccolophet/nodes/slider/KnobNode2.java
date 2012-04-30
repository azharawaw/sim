// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Or;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * PNode that represents a knob.  This was created primarily for use with
 * sliders, but may have other uses.
 * TODO: add an external shadow for use in non-black-background situations
 */
public class KnobNode2 extends PComposite {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double DEFAULT_SIZE = 40;
    public static final Style DEFAULT_STYLE = Style.RECTANGLE;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Properties for button state
    private final Property<Boolean> enabled = new Property<Boolean>( true );
    private final BooleanProperty entered = new BooleanProperty( false );
    private final BooleanProperty pressed = new BooleanProperty( false );
    private final Or focused = entered.or( pressed );

    // Store the cursor handler and moused-over component so the cursor can be
    // changed from a hand to an arrow when the KnobNode becomes disabled
    private final CursorHandler cursorHandler = new CursorHandler();
    private PComponent component;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public KnobNode2() {
        this( DEFAULT_SIZE, Style.RECTANGLE );
    }

    public KnobNode2( Style style, ColorScheme colorScheme ) {
        this( DEFAULT_SIZE, style, colorScheme );
    }

    public KnobNode2( final double size, Style style ) {
        this( size, style, new ColorScheme() );
    }

    /**
     * Primary constructor.
     */
    public KnobNode2( final double width, Style style, final ColorScheme colorScheme ) {

        //---------------------------------------------------------------------
        // Create the components of the knob.
        //---------------------------------------------------------------------

        Shape knobShape = createKnobShape( style, width );
        final PhetPPath knobShapeNode = new PhetPPath( knobShape ) {{
            setPaint( colorScheme.enabledColor );
            setStroke( new BasicStroke( 2f ) );

            //When enabled/disabled or focused/unfocused, change the appearance.
            new RichSimpleObserver() {
                @Override public void update() {
                    setPaint( !enabled.get() ? colorScheme.disabledColor : ( focused.get() ? colorScheme.highlightedColor : colorScheme.enabledColor ) );
                    setStrokePaint( !enabled.get() ? Color.gray : focused.get() ?
                                                                  new Color( 160, 160, 160 ) :
                                                                  Color.lightGray );
                }
            }.observe( enabled, focused );
        }};
        addChild( knobShapeNode );

        //---------------------------------------------------------------------
        // Event handling
        //---------------------------------------------------------------------

        addInputEventListener( cursorHandler );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( PInputEvent event ) {
                entered.set( true );
            }

            @Override public void mousePressed( PInputEvent event ) {
                pressed.set( true );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                pressed.set( false );
            }

            @Override public void mouseExited( PInputEvent event ) {
                entered.set( false );
            }
        } );

        // Store the component so the mouse can be changed from hand to arrow
        // when disabled
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseMoved( PInputEvent event ) {
                component = event.getComponent();
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Sets whether the KnobNode is enabled.  Disabling grays it out and makes it ignore mouse interaction.
     *
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        if ( !enabled ) {
            entered.set( false );
            pressed.set( false );
        }
        this.enabled.set( enabled );
        setPickable( enabled );
        setChildrenPickable( enabled );
        if ( component != null && !enabled ) {
            cursorHandler.mouseExited( (JComponent) component );
        }
    }

    private static Shape createKnobShape( Style knobStyle, final double width ) {
        Area knobShape = null;
        switch( knobStyle ) {
            case RECTANGLE:
                knobShape = new Area( new RoundRectangle2D.Double( 0, 0, width, width * 0.4, width / 10, width / 10 ) );
                break;
            case POINTED_RECTANGLE: {
                double unPointedProportion = 0.7;
                double height = width * 0.4;
                knobShape = new Area( new RoundRectangle2D.Double( 0, 0, width * unPointedProportion, height, width / 10, width / 10 ) );
                DoubleGeneralPath pointerPath = new DoubleGeneralPath( width, height / 2 );
                pointerPath.lineTo( width * unPointedProportion, 0 );
                pointerPath.lineTo( width * 0.1, 0 );
                pointerPath.lineTo( width * 0.1, height );
                pointerPath.lineTo( width * unPointedProportion, height );
                pointerPath.closePath();
                knobShape.add( new Area( pointerPath.getGeneralPath() ) );
                break;
            }
            case HELMET: {
                final double height = width * 0.9;

                DoubleGeneralPath path = new DoubleGeneralPath( width, height / 2 );
                path.lineTo( width * 0.75, 0 );
                path.curveTo( -width * 0.3, -height * 0.3, -width * 0.3, height * 1.2, width * 0.75, height );
                knobShape = new Area( path.getGeneralPath() );

//                knobShape = new Area( new Ellipse2D.Double( 0, 0, width, height ) );
//                knobShape.subtract( new Area( new DoubleGeneralPath( width / 2, height ) {{
//                    lineToRelative( -width * 2, -width * 1.3 );
//                    lineToRelative( 0, width * 4 );
//                }}.getGeneralPath() ) );
//                knobShape.subtract( new Area( new DoubleGeneralPath( width / 2, height ) {{
//                    lineToRelative( width * 2, -width * 1.3 );
//                    lineToRelative( 0, width * 4 );
//                }}.getGeneralPath() ) );
//                break;
            }
        }
        return knobShape;
    }

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 500, 400 );

        PhetPCanvas canvas = new PhetPCanvas();

        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        // Add the default knob to the canvas.
        canvas.addWorldChild( new KnobNode2() {{
            setOffset( 10, 10 );
        }} );

        canvas.addWorldChild( new KnobNode2( Style.RECTANGLE, new KnobNode2.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
            setOffset( 10, 50 );
        }} );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

    //-------------------------------------------------------------------------
    // Inner Classes, Enums, and Interfaces
    //-------------------------------------------------------------------------

    /**
     * Direction that the pointer portion of the knob, if there is one, points.
     * For knobs with no pointers, this can be used to set a knob to be
     * rotated 90 degrees.
     */
    public static enum Direction {
        NORTH,
        SOUTH, // Default
        EAST,
        WEST
    }

    /**
     * Styles that can be used to specify the various pre-fab knob node shapes.
     */
    public static enum Style {
        RECTANGLE,
        POINTED_RECTANGLE,
        HELMET
    }

    /**
     * Color scheme for the KnobNode.  Tri-color gradients are used for each
     * state.
     */
    public static class ColorScheme {
        public final Color enabledColor;
        public final Color disabledColor;
        public final Color highlightedColor;

        /**
         * Convenience constructor that creates a ColorScheme based on a single central color
         *
         * @param enabledColor
         */
        public ColorScheme( Color enabledColor ) {
            this( enabledColor, ColorUtils.darkerColor( enabledColor, 0.5 ), ColorUtils.brighterColor( enabledColor, 0.5 ) );
        }

        /**
         * Default gray color scheme.
         */
        public ColorScheme() {
            this( new Color( 220, 220, 220 ) );
        }

        /**
         * Fully explicit constructor for the ColorScheme
         */
        public ColorScheme( Color enabledColor, Color disabledColor, Color highlightedColor ) {
            this.enabledColor = enabledColor;
            this.disabledColor = disabledColor;
            this.highlightedColor = highlightedColor;
        }
    }
}
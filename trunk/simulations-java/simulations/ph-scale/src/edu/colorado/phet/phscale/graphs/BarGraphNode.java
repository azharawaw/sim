/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.graphs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.util.ConstantPowerOfTenNumberFormat;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;


public class BarGraphNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // graph outline
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;
    
    // units label
    private static final Font UNITS_FONT = new PhetFont( 14 );
    private static final Color UNITS_COLOR = Color.BLACK;
    
    // bars
    private static final Stroke BAR_STROKE = null;
    private static final Color BAR_STROKE_COLOR = Color.BLACK;
    
    // numeric values
    private static final Font VALUE_FONT = new PhetFont( 16 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final double VALUE_Y_MARGIN = 10;
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final DecimalFormat H2O_FORMAT = new DefaultDecimalFormat( "#0" );
    
    // ticks
    private static final boolean DRAW_TICKS_ON_RIGHT = false;
    private static final double TICK_LENGTH = 6;
    private static final Stroke TICK_STROKE = new BasicStroke( 1f );
    private static final Paint TICK_COLOR = Color.BLACK;
    private static final Font TICK_LABEL_FONT = new PhetFont( 14 );
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    private static final double TICKS_TOP_MARGIN = 10;
    
    // log ticks
    private static final int NUMBER_OF_LOG_TICKS = 19;
    private static final int BIGGEST_LOG_TICK_EXPONENT = 2;
    private static final int LOG_TICK_EXPONENT_SPACING = 2;
    
    // linear ticks
    private static final int NUMBER_OF_LINEAR_TICKS = 19;
    private static final double LINEAR_TICK_MANTISSA_SPACING = 0.5;
    private static final int BIGGEST_LINEAR_TICK_EXPONENT = 1;
    private static final int SMALLEST_LINEAR_TICK_EXPONENT = -15;
    
    // horizontal gridlines
    private static final boolean DRAW_GRIDLINES = true;
    private static final Stroke GRIDLINE_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 ); // dashed
    private static final Paint GRIDLINE_COLOR = new Color(192, 192, 192, 100 ); // translucent gray
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final PDimension _graphOutlineSize;
    private final PPath _graphOutlineNode;
    private final FormattedNumberNode _h3oNumberNode, _ohNumberNode, _h2oNumberNode;
    private final PNode _logTicksNode, _linearTicksNode;
    private final PNode _linearTicksParentNode;
    private final GeneralPath _h3oBarShape, _ohBarShape, _h2oBarShape;
    private final PPath _h3oBarNode, _ohBarNode, _h2oBarNode;
    private final PText _unitsNode;
    private final JButton _zoomInButton, _zoomOutButton;
    private final PSwing _zoomPanelWrapper;
    
    private boolean _logScale;
    private boolean _concentrationUnits;
    private int _linearTicksExponent;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BarGraphNode( PDimension graphOutlineSize, Liquid liquid ) {
        
        _graphOutlineSize = new PDimension( graphOutlineSize );
        _logScale = true;
        _concentrationUnits = true;
        _linearTicksExponent = BIGGEST_LINEAR_TICK_EXPONENT;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                updateValues();
                updateBars();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        Rectangle2D r = new Rectangle2D.Double( 0, 0, graphOutlineSize.getWidth(), graphOutlineSize.getHeight() );
        _graphOutlineNode = new PPath( r );
        _graphOutlineNode.setStroke( OUTLINE_STROKE );
        _graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        _graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        _graphOutlineNode.setPickable( false );
        _graphOutlineNode.setChildrenPickable( false );
        addChild( _graphOutlineNode );
        
        _unitsNode = new PText( "?" );
        _unitsNode.setFont( UNITS_FONT );
        _unitsNode.setTextPaint( UNITS_COLOR );
        addChild( _unitsNode );
        
        _h3oNumberNode = createNumberNode( H3O_FORMAT );
        addChild( _h3oNumberNode );
        
        _ohNumberNode = createNumberNode( OH_FORMAT );
        addChild( _ohNumberNode );
        
        _h2oNumberNode = createNumberNode( H2O_FORMAT );
        addChild( _h2oNumberNode );
        
        updateValues(); // do this before setting offsets so that bounds are reasonable
        
        _logTicksNode = createLogTicksNode( graphOutlineSize );
        _logTicksNode.setVisible( _logScale );
        addChild( _logTicksNode );
        
        _linearTicksNode = createLinearTicksNode( graphOutlineSize, _linearTicksExponent );
        _logTicksNode.setVisible( !_logScale );
        _linearTicksParentNode = new PNode();
        _linearTicksParentNode.addChild( _linearTicksNode );
        addChild( _linearTicksParentNode );
        
        _h3oBarShape = new GeneralPath();
        _ohBarShape = new GeneralPath();
        _h2oBarShape = new GeneralPath();
        
        _h3oBarNode = new PPath();
        _h3oBarNode.setPaint( PHScaleConstants.H3O_COLOR );
        _h3oBarNode.setStroke( BAR_STROKE );
        _h3oBarNode.setStrokePaint( BAR_STROKE_COLOR );
        addChild( _h3oBarNode );
        
        _ohBarNode = new PPath();
        _ohBarNode.setPaint( PHScaleConstants.OH_COLOR );
        _ohBarNode.setStroke( BAR_STROKE );
        _ohBarNode.setStrokePaint( BAR_STROKE_COLOR );
        addChild( _ohBarNode );
        
        _h2oBarNode = new PPath();
        _h2oBarNode.setPaint( PHScaleConstants.H2O_COLOR );
        _h2oBarNode.setStroke( BAR_STROKE );
        _h2oBarNode.setStrokePaint( BAR_STROKE_COLOR );
        addChild( _h2oBarNode );
        
        // Zoom controls
        {
            _zoomInButton = new JButton( PHScaleStrings.BUTTON_ZOOM_IN );
            _zoomInButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomIn();
                }
            } );

            _zoomOutButton = new JButton( PHScaleStrings.BUTTON_ZOOM_OUT );
            _zoomOutButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomOut();
                }
            } );

            JPanel zoomPanel = new JPanel();
            zoomPanel.setOpaque( false );
            EasyGridBagLayout zoomPanelLayout = new EasyGridBagLayout( zoomPanel );
            zoomPanel.setLayout( zoomPanelLayout );
            int row = 0;
            int column = 0;
            zoomPanelLayout.addFilledComponent( _zoomInButton, row++, column, GridBagConstraints.HORIZONTAL );
            zoomPanelLayout.addFilledComponent( _zoomOutButton, row++, column, GridBagConstraints.HORIZONTAL );
            
            _zoomPanelWrapper = new PSwing( zoomPanel );
            _zoomPanelWrapper.setVisible( !_logScale );
            addChild( _zoomPanelWrapper );
        }
        
        _graphOutlineNode.setOffset( 0, 0 );
        PBounds gob = _graphOutlineNode.getFullBoundsReference();
        _logTicksNode.setOffset( _graphOutlineNode.getOffset() );
        _linearTicksParentNode.setOffset( _graphOutlineNode.getOffset() );
        _unitsNode.setOffset( gob.getX() + 10, gob.getY() + 5 );
        final double xH3O = 0.25 * gob.getWidth();
        final double xOH = 0.5 * gob.getWidth();
        final double xH2O = 0.75 * gob.getWidth();
        _h3oNumberNode.setOffset( xH3O - _h3oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h3oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _ohNumberNode.setOffset( xOH - _ohNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _ohNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        _h2oNumberNode.setOffset( xH2O - _h2oNumberNode.getFullBoundsReference().getWidth()/2, gob.getMaxY() - _h2oNumberNode.getFullBoundsReference().getHeight() - VALUE_Y_MARGIN );
        //XXX set offsets for bars
        _zoomPanelWrapper.setOffset( gob.getCenterX() - _zoomPanelWrapper.getFullBoundsReference().getWidth()/2, 30 );
        
        updateUnits();
        updateTicks();
        updateBars();
        updateControls();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private static FormattedNumberNode createNumberNode( NumberFormat format ) {
        FormattedNumberNode node = new FormattedNumberNode( format, 0, VALUE_FONT, VALUE_COLOR );
        node.rotate( -Math.PI / 2 );
        node.setPickable( false );
        node.setChildrenPickable( false );
        return node;
    }
    
    /*
     * Creates the tick marks and labels for the log scale,
     * starting from the top of the graph and working down.
     */
    private static PNode createLogTicksNode( PDimension graphOutlineSize ) {
        
        PNode parentNode = new PComposite();
        
        final double numberOfTicks = NUMBER_OF_LOG_TICKS;
        final double usableHeight = graphOutlineSize.getHeight() - TICKS_TOP_MARGIN;
        final double tickSpacing = usableHeight / ( numberOfTicks - 1 );
        
        double y = TICKS_TOP_MARGIN;
        int exponent = BIGGEST_LOG_TICK_EXPONENT;
        for ( int i = 0; i < numberOfTicks; i++ ) {
            
            if ( i % LOG_TICK_EXPONENT_SPACING == 0 ) {
                
                PPath leftTickNode = new PPath( new Line2D.Double( -( TICK_LENGTH / 2 ), y, +( TICK_LENGTH / 2 ), y ) );
                leftTickNode.setStroke( TICK_STROKE );
                leftTickNode.setStrokePaint( TICK_COLOR );
                parentNode.addChild( leftTickNode );
                
                if ( DRAW_TICKS_ON_RIGHT ) {
                    PPath rightTickNode = new PPath( new Line2D.Double( -( TICK_LENGTH / 2 ) + graphOutlineSize.getWidth(), y, +( TICK_LENGTH / 2 ) + graphOutlineSize.getWidth(), y ) );
                    rightTickNode.setStroke( TICK_STROKE );
                    rightTickNode.setStrokePaint( TICK_COLOR );
                    parentNode.addChild( rightTickNode );
                }
                
                String s = "<html>10<sup>" + String.valueOf( exponent ) + "</sup></html>";
                HTMLNode labelNode = new HTMLNode( s );
                labelNode.setFont( TICK_LABEL_FONT );
                labelNode.setHTMLColor( TICK_LABEL_COLOR );
                double xOffset = leftTickNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - 5;
                double yOffset = leftTickNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
                labelNode.setOffset( xOffset, yOffset );
                parentNode.addChild( labelNode );
            }
            
            if ( DRAW_GRIDLINES ) {
                PPath gridlineNode = new PPath( new Line2D.Double( +( TICK_LENGTH / 2 ), y, graphOutlineSize.getWidth() - ( TICK_LENGTH / 2 ), y ) );
                gridlineNode.setStroke( GRIDLINE_STROKE );
                gridlineNode.setStrokePaint( GRIDLINE_COLOR );
                parentNode.addChild( gridlineNode );
            }
            
            y += tickSpacing;
            exponent--;
        }
        
        return parentNode;
    }

    /*
     * Creates the tick marks and labels for the linear scale,
     * starting from the bottom of the graph and working up.
     */
    private static PNode createLinearTicksNode( PDimension graphOutlineSize, final int exponent ) {

        PNode parentNode = new PComposite();

        final double numberOfTicks = NUMBER_OF_LINEAR_TICKS;
        final double usableHeight = graphOutlineSize.getHeight() - TICKS_TOP_MARGIN;
        final double tickSpacing = usableHeight / ( numberOfTicks - 1 );
        
        final ConstantPowerOfTenNumberFormat labelFormat = new ConstantPowerOfTenNumberFormat( "0", exponent );

        double y = graphOutlineSize.getHeight();
        for ( int i = 0; i < numberOfTicks; i++ ) {

            if ( i % 2 == 0 ) {
                
                PPath leftTickNode = new PPath( new Line2D.Double( -( TICK_LENGTH / 2 ), y, +( TICK_LENGTH / 2 ), y ) );
                leftTickNode.setStroke( TICK_STROKE );
                leftTickNode.setStrokePaint( TICK_COLOR );
                parentNode.addChild( leftTickNode );
                
                if ( DRAW_TICKS_ON_RIGHT ) {
                    PPath rightTickNode = new PPath( new Line2D.Double( -( TICK_LENGTH / 2 ) + graphOutlineSize.getWidth(), y, +( TICK_LENGTH / 2 ) + graphOutlineSize.getWidth(), y ) );
                    rightTickNode.setStroke( TICK_STROKE );
                    rightTickNode.setStrokePaint( TICK_COLOR );
                    parentNode.addChild( rightTickNode );
                }
                
                String s = null;
                if ( i == 0 ) {
                    s = "0";
                }
                else { 
                    double mantissa = i * LINEAR_TICK_MANTISSA_SPACING * Math.pow( 10, exponent );
                    s = labelFormat.format( mantissa );
                }
                HTMLNode labelNode = new HTMLNode( s );
                labelNode.setFont( TICK_LABEL_FONT );
                labelNode.setHTMLColor( TICK_LABEL_COLOR );
                double xOffset = leftTickNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - 5;
                double yOffset = leftTickNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 );
                labelNode.setOffset( xOffset, yOffset );
                parentNode.addChild( labelNode );
            }
            
            if ( DRAW_GRIDLINES ) {
                PPath gridlineNode = new PPath( new Line2D.Double( +( TICK_LENGTH / 2 ), y, graphOutlineSize.getWidth() - ( TICK_LENGTH / 2 ), y ) );
                gridlineNode.setStroke( GRIDLINE_STROKE );
                gridlineNode.setStrokePaint( GRIDLINE_COLOR );
                parentNode.addChild( gridlineNode );
            }

            y -= tickSpacing;
        }

        return parentNode;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setLogScale( boolean logScale ) {
        if ( logScale != _logScale ) {
            _logScale = logScale;
            updateTicks();
            updateBars();
            updateControls();
        }
    }
    
    public boolean isLogScale() {
        return _logScale;
    }
    
    public void setConcentrationUnits( boolean concentrationUnits ) {
        if ( concentrationUnits != _concentrationUnits ) {
            _concentrationUnits = concentrationUnits;
            updateUnits();
            updateValues();
            updateTicks();
            updateBars();
        }
    }
    
    public boolean isConcentrationUnits() {
        return _concentrationUnits;
    }
    
    //----------------------------------------------------------------------------
    // Zoom
    //----------------------------------------------------------------------------
    
    private void zoomIn() {
        _linearTicksExponent--;
        updateTicks();
        updateBars();
        updateControls();
    }
    
    private void zoomOut() {
        _linearTicksExponent++;
        updateTicks();
        updateBars();
        updateControls();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateUnits() {
        String s = ( _concentrationUnits ? PHScaleStrings.UNITS_MOLES_PER_LITER : PHScaleStrings.UNITS_MOLES );
        _unitsNode.setText( s );
    }
    
    private void updateValues() {
        if ( _concentrationUnits ) {
            _h3oNumberNode.setValue( _liquid.getConcentrationH3O() );
            _ohNumberNode.setValue( _liquid.getConcentrationOH() );
            _h2oNumberNode.setValue( _liquid.getConcentrationH2O() );
        }
        else {
            _h3oNumberNode.setValue( _liquid.getMolesH3O() );
            _ohNumberNode.setValue( _liquid.getMolesOH() );
            _h2oNumberNode.setValue( _liquid.getMolesH2O() );
        }
    }
    
    private void updateTicks() {
        if ( !_logScale ) {
            // update linear ticks to match zoom level
            _linearTicksParentNode.removeAllChildren();
            PNode linearTicksNode = createLinearTicksNode( _graphOutlineSize, _linearTicksExponent );
            _linearTicksParentNode.addChild( linearTicksNode  );
        }
        _logTicksNode.setVisible( _logScale );
        _linearTicksNode.setVisible( !_logScale );
    }
    
    private void updateBars() {
        
        _h3oBarShape.reset();
        _ohBarShape.reset();
        _h2oBarShape.reset();
        
        if ( !_liquid.isEmpty() ) {
            if ( _logScale ) {
                updateBarShapesLog();
            }
            else { /* linear */
                updateBarsShapesLinear();
            }
        }
        
        _h3oBarNode.setPathTo( _h3oBarShape );
        _ohBarNode.setPathTo( _ohBarShape );
        _h2oBarNode.setPathTo( _h2oBarShape );
    }
    
    private void updateBarShapesLog() {
        if ( _concentrationUnits ) {
            //XXX
        }
        else {
            //XXX
        }
    }
    
    private void updateBarsShapesLinear() {
        if ( _concentrationUnits ) {
            //XXX
        }
        else {
            //XXX
        }
    }
    
    private void updateControls() {
        _zoomInButton.setEnabled( _linearTicksExponent != SMALLEST_LINEAR_TICK_EXPONENT );
        _zoomOutButton.setEnabled( _linearTicksExponent != BIGGEST_LINEAR_TICK_EXPONENT );
        _zoomPanelWrapper.setVisible( !_logScale );
    }
}

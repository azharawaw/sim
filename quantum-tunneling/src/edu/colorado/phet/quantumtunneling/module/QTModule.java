/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.ConfigureEnergyDialog;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.model.AbstractPotentialEnergy;
import edu.colorado.phet.quantumtunneling.model.BarrierPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.view.ChartNode;
import edu.colorado.phet.quantumtunneling.view.EnergyManipulator;
import edu.colorado.phet.quantumtunneling.view.LegendItem;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * QTModule is the sole module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font LEGEND_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 10; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    private static final int CANVAS_BOUNDARY_STROKE_WIDTH = 1;
    private static final double LEGEND_SCALE = 1.2;
    private static final double CONFIGURE_BUTTON_SCALE = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotentialEnergy _potentialEnergy;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private PNode _legend;
    private ChartNode _chartNode;
    private QTCombinedChart _chart;
    private Rectangle2D _energyPlotBounds;
    
    // Control
    private PSwing _configureButton;
    private QTControlPanel _controlPanel;
    private ConfigureEnergyDialog _configureEnergyDialog;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public QTModule( AbstractClock clock ) {
        super( SimStrings.get( "title.quantumTunneling" ), clock );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        /* Additional model elements are created in reset method. */
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        EventListener listener = new EventListener();
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( QTConstants.CANVAS_BACKGROUND );
            _canvas.addComponentListener( listener );
            setCanvas( _canvas );
        }
        
        // Configure button
        {
            JButton jButton = new JButton( SimStrings.get( "button.configureEnergy" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _configureButton = new PSwing( _canvas, jButton );
        }
        
        // Energy graph legend
        { 
            LegendItem totalEnergyItem = 
                new LegendItem( SimStrings.get( "legend.totalEnergy" ), QTConstants.TOTAL_ENERGY_COLOR );
            
            LegendItem potentialEnergyItem = 
                new LegendItem( SimStrings.get( "legend.potentialEnergy" ), QTConstants.POTENTIAL_ENERGY_COLOR );
            potentialEnergyItem.translate( totalEnergyItem.getFullBounds().getWidth() + 20, 0 );

            _legend = new PNode();
            _legend.scale( LEGEND_SCALE );
            _legend.addChild( totalEnergyItem );
            _legend.addChild( potentialEnergyItem );
        }
        
        // Combined chart
        {
            _chart = new QTCombinedChart();         
            _chartNode = new ChartNode( _chart );
        }
        
        // Add all the nodes to one parent node.
        {
            _parentNode = new PNode();
            
            _parentNode.addChild( _configureButton );
            _parentNode.addChild( _legend );
            _parentNode.addChild( _chartNode );

            _canvas.addScreenChild( _parentNode );
        }       
        
        // XXX EnergyManipulator tests
        {
            final Point2D startPoint = new Point2D.Double( 100, 100 );
            
            EnergyManipulator manipulator = new EnergyManipulator();
            manipulator.translate( startPoint.getX(), startPoint.getY() );
            manipulator.translate( -manipulator.getWidth() / 2, -manipulator.getHeight() / 2 ); // registration point @ center
            _parentNode.addChild( manipulator );
            
            manipulator.addInputEventListener( new CursorHandler() );
            manipulator.addInputEventListener( new PBasicInputEventHandler() {
                
                private Point2D _somePoint = new Point2D.Double();
                private boolean _outOfBounds = false;
                
                public void mouseDragged( PInputEvent event ) {
                    
                    PNode node = event.getPickedNode();
                    
                    if ( _outOfBounds ) {
                        // Don't resume dragging until the mouse cursor is inside the node.
                        Point2D mousePosition = event.getPosition();
                        Rectangle2D nodeBounds = node.getBounds();
                        nodeBounds = node.localToGlobal( nodeBounds );
                        if ( nodeBounds.contains( mousePosition ) ) {
                            _outOfBounds = false;
                        }
                    }
                    else {
                        // Determine the hypothetical point to move to.
                        PDimension delta = event.getDeltaRelativeTo( node );
                        PAffineTransform transform = node.getTransformReference( true );
                        transform.transform( new Point2D.Double( node.getWidth() / 2, node.getHeight() / 2 ), _somePoint );
                        _somePoint.setLocation( _somePoint.getX() + delta.width, _somePoint.getY() );

                        if ( _energyPlotBounds.contains( _somePoint ) ) {
                            // If the point is inside the bound, then do the drag.
                            node.translate( delta.width, 0 );
                        }
                        else {
                            // If the point is outside the bounds, drag to one of the extremes.
                            _outOfBounds = true;
                            Rectangle2D nodeBounds = node.getBounds();
                            nodeBounds = node.localToGlobal( nodeBounds );
                            if ( _somePoint.getX() < _energyPlotBounds.getX() ) {
                                // Move the manipulator to the far left
                                double deltaX = nodeBounds.getX() + ( node.getWidth() / 2 ) - _energyPlotBounds.getX();
                                node.translate( -deltaX, 0 );
                            }
                            else {
                                // Move the manipulator to the far right
                                double deltaX = _energyPlotBounds.getX() + _energyPlotBounds.getWidth() - ( node.getWidth() / 2 ) - nodeBounds.getX();
                                node.translate( deltaX, 0 );
                            }
                        }
                    }
                }
                
                public void mouseReleased( PInputEvent event ) {
                    _outOfBounds = false;
                }
            } );
        }
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new QTControlPanel( this );
        setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        layoutCanvas();
        reset();
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Lays out nodes on the canvas.
     * This is called whenever the canvas size changes.
     */
    private void layoutCanvas() {
        
        // Height of the legend along the top edge
        double legendHeight = _legend.getFullBounds().getHeight();
        
        // Location and dimensions of charts
        final double chartWidth = _canvas.getWidth() - ( 2 * X_MARGIN );
        final double chartHeight = _canvas.getHeight() - ( legendHeight  + ( 2 * Y_MARGIN ) + Y_SPACING );
        
        // Legend
        {
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( X_MARGIN + 100, Y_MARGIN );
            legendTransform.scale( LEGEND_SCALE, LEGEND_SCALE );
            legendTransform.translate( 0, 0 ); // upper left
            _legend.setTransform( legendTransform );
        }
        
        // Configure button
        {
            AffineTransform configureTransform = new AffineTransform();
            configureTransform.translate( X_MARGIN + chartWidth, ( Y_MARGIN + legendHeight + Y_SPACING ) / 2 );
            configureTransform.scale( CONFIGURE_BUTTON_SCALE, CONFIGURE_BUTTON_SCALE );
            configureTransform.translate( -_configureButton.getWidth(), -_configureButton.getHeight() / 2 ); // registration point = right center
            _configureButton.setTransform( configureTransform );
        }
        
        // Combined chart
        {
            _chartNode.setBounds( 0, 0, chartWidth, chartHeight );
            AffineTransform chartTransform = new AffineTransform();
            chartTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + Y_SPACING );
            chartTransform.translate( 0, 0 ); // registration point @ upper left
            _chartNode.setTransform( chartTransform );
        }

        // Bounds 
        ChartRenderingInfo chartInfo = _chartNode.getChartRenderingInfo();
        if ( chartInfo != null ) {
            PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
            _energyPlotBounds = plotInfo.getSubplotInfo( QTCombinedChart.ENERGY_PLOT_INDEX ).getDataArea();
            _energyPlotBounds = _chartNode.localToGlobal( _energyPlotBounds );
        }
        else {
            _energyPlotBounds = new Rectangle2D.Double();
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the module to its initial state.
     */
    public void reset() {
        
        // Model
        _totalEnergy = new TotalEnergy( 8 );
        setTotalEnergy( _totalEnergy );
        _potentialEnergy = new BarrierPotential();
        setPotentialEnergy( _potentialEnergy );
        
        _controlPanel.reset();
    }
    
    //XXX hack, remove this!
    public boolean hasHelp() {
        return true;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends ComponentAdapter implements ActionListener {

        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                layoutCanvas();
            }
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _configureButton.getComponent() ) {
                handleConfigureButton();
            }
        }
    }
    
    /*
     * When the "Configure Energy" button is pressed, open a dialog.
     */
    private void handleConfigureButton() {
        if ( _configureEnergyDialog == null ) {
            setWaitCursorEnabled( true );
            _configureEnergyDialog = new ConfigureEnergyDialog( getFrame(), this, _totalEnergy, _potentialEnergy );
            _configureEnergyDialog.addWindowListener( new WindowAdapter() {
                // User pressed the closed button in the window dressing
                public void windowClosing( WindowEvent event ) {
                    _configureEnergyDialog.cleanup();
                    _configureEnergyDialog = null;
                }
                // User pressed the close button in the dialog's action area
                public void windowClosed( WindowEvent event ) {
                    _configureEnergyDialog.cleanup();
                    _configureEnergyDialog = null;
                }
            } );
            _configureEnergyDialog.show();
            setWaitCursorEnabled( false );
        }
    }
    
    public void setPotentialEnergy( AbstractPotentialEnergy potentialEnergy ) {
        _potentialEnergy = potentialEnergy;
        _chart.setPotentialEnergy( _potentialEnergy );
        if ( _controlPanel != null ) {
            _controlPanel.setPotentialEnergy( _potentialEnergy );
        }
    }
    
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        _totalEnergy = totalEnergy;
        _chart.setTotalEnergy( _totalEnergy );
    }
}

/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import org.jfree.chart.JFreeChart;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;


public class PositionHistogramPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color CHART_BACKGROUND_COLOR = Color.WHITE;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    private ClockListener _clockListener;
    private Bead _bead;
    private Laser _laser;

    private JLabel _measurementsLabel;
    private JButton _startStopButton;
    private JButton _clearButton;
    private JButton _snapshotButton;
    private JButton _rulerButton;

    private PositionHistogramPlot _plot;

    private String _measurementsString;
    private String _startString, _stopString;

    private boolean _isRunning;
    private int _numberOfMeasurements;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param font font to used for all controls
     * @param bead
     * @param laser
     * @param binWidth
     */
    public PositionHistogramPanel( Font font, IClock clock, Bead bead, Laser laser, double binWidth ) {
        super();

        _clock = clock;
        _clockListener = new ClockAdapter() {

            public void clockTicked( ClockEvent event ) {
                if ( _isRunning ) {
                    handleClockEvent( event );
                }
            }
        };
        _clock.addClockListener( _clockListener );

        _bead = bead;

        _laser = laser;
        _laser.addObserver( this );

        _isRunning = false;
        _numberOfMeasurements = 0;

        JPanel toolPanel = createToolPanel( font );

        JPanel chartPanel = createChartPanel( font, bead, laser, binWidth );

        JPanel mainPanel = new VerticalLayoutPanel();
        mainPanel.add( toolPanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( chartPanel );
        
        add( mainPanel );
    }
    
    public void cleanup() {
        System.err.println( "implement PositionHistogramPanel.cleanup!" );//XXX
    }

    //----------------------------------------------------------------------------
    // User interface creation
    //----------------------------------------------------------------------------
    
    /*
     * Creates the tool panel that appears at the top of the dialog.
     */
    private JPanel createToolPanel( Font font ) {

        _measurementsString = OTResources.getString( "label.measurements" );
        _startString = OTResources.getString( "button.start" );
        _stopString = OTResources.getString( "button.stop" );

        _measurementsLabel = new JLabel( _measurementsString );
        _measurementsLabel.setFont( font );

        JPanel leftPanel = new HorizontalLayoutPanel();
        leftPanel.add( _measurementsLabel );

        _startStopButton = new JButton( _isRunning ? _stopString : _startString );
        _startStopButton.setFont( font );
        _startStopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleStartStopButton();
            }
        } );
        
        _clearButton = new JButton( OTResources.getString( "button.clear" ) );
        _clearButton.setFont( font );
        _clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                clearMeasurements();
            }
        } );

        Icon cameraIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_CAMERA ) );
        _snapshotButton = new JButton( cameraIcon );
        _snapshotButton.setOpaque( false );
        _snapshotButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        _snapshotButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleSnapshotButton();
            }
        } );

        Icon rulerIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_RULER ) );
        _rulerButton = new JButton( rulerIcon );
        _rulerButton.setOpaque( false );
        _rulerButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        _rulerButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                handleRulerButton();
            }
        } );

        JPanel rightPanel = new HorizontalLayoutPanel();
        rightPanel.add( _startStopButton );
        rightPanel.add( _clearButton );
        rightPanel.add( _snapshotButton );
        rightPanel.add( _rulerButton );

        JPanel toolPanel = new JPanel( new BorderLayout() );
        toolPanel.add( leftPanel, BorderLayout.WEST );
        toolPanel.add( rightPanel, BorderLayout.EAST );

        return toolPanel;
    }

    /*
     * Creates the Piccolo panel that contains the chart, ruler, etc.
     */
    private JPanel createChartPanel( Font font, Bead bead, Laser laser, double binWidth ) {

        _plot = new PositionHistogramPlot( binWidth );

        JFreeChart chart = new JFreeChart( null /* title */, null /* titleFont */, _plot, false /* createLegend */);
        chart.setAntiAlias( true );
        chart.setBorderVisible( false );
        chart.setBackgroundPaint( CHART_BACKGROUND_COLOR );
        chart.setPadding( new RectangleInsets( 0, 5, 5, 5 ) ); // top,left,bottom,right
        JFreeChartNode chartNode = new JFreeChartNode( chart );
        chartNode.setPickable( false );
        chartNode.setChildrenPickable( false );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( chartNode );

        JPanel panel = new JPanel();
        panel.add( canvas );

        return panel;
    }

    //----------------------------------------------------------------------------
    // private setters
    //----------------------------------------------------------------------------

    /*
     * Sets the number of measurements displayed.
     */
    private void setNumberOfMeasurements( int numberOfMeasurements ) {
        _numberOfMeasurements = numberOfMeasurements;
        _measurementsLabel.setText( _measurementsString + " " + String.valueOf( _numberOfMeasurements ) );
    }

    /*
     * Sets the running state of the chart.
     */
    private void setRunning( boolean isRunning ) {
        _isRunning = isRunning;
        if ( _isRunning ) {
            _clock.addClockListener( _clockListener );
            _startStopButton.setText( _stopString );
        }
        else {
            _clock.removeClockListener( _clockListener );
            _startStopButton.setText( _startString );
        }
    }

    /*
     * Clears all measurements from the chart.
     */
    private void clearMeasurements() {
        if ( _numberOfMeasurements > 0 ) {
            _plot.clear();
            setNumberOfMeasurements( 0 );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    private void handleStartStopButton() {
        setRunning( !_isRunning );
    }

    private void handleSnapshotButton() {
        System.out.println( "PositionHistogramPanel.handleSnapshotButton" );//XXX
    }

    private void handleRulerButton() {
        System.out.println( "PositionHistogramPanel.handleRulerButton" );//XXX
    }

    /*
     * Makes a position measurement and updates the plot.
     */
    private void handleClockEvent( ClockEvent event ) {
        setNumberOfMeasurements( _numberOfMeasurements + 1 );
        double x = _bead.getPositionRef().getX();
        _plot.addPosition( x );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    public void update( Observable o, Object arg ) {
        if ( o == _laser && arg == Laser.PROPERTY_POSITION ) {
            clearMeasurements();
        }
    }
}

/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.charts;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;

/**
 * GlacierLengthVersusTimeChart displays a "Glacier Length versus Time" chart.
 * The chart updates as the glacier evolves.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierLengthVersusTimeChart extends JDialog {
    
    private static final Range LENGTH_RANGE = new Range( 0, 80E3 ); // meters
    private static final Range TIME_RANGE = new Range( 0, 10E3 ); //XXX years
    
    private final Glacier _glacier;
    private final GlaciersClock _clock;
    private final ClockListener _clockListener;
    private final XYSeries _series;
    
    public GlacierLengthVersusTimeChart( Frame owner, Dimension size, Glacier glacier, GlaciersClock clock ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _glacier = glacier;
        
        _clock = clock;
        _clockListener = new ClockAdapter() {
            public void simulationTimeReset( ClockEvent clockEvent ) {
                _series.clear();
            }
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                update();
            }
        };
        _clock.addClockListener( _clockListener );
        
        // series and dataset
        _series = new XYSeries( "glacierLengthVersusTime", false /* autoSort */ );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        
        // create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_GLACIER_LENGTH_VERSUS_TIME, // title
            GlaciersStrings.AXIS_TIME, // x axis label
            GlaciersStrings.AXIS_GLACIER_LENGTH,  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        domainAxis.setRange( TIME_RANGE );//XXX time axis will 
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        rangeAxis.setRange( LENGTH_RANGE );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable( false );
        setContentPane( chartPanel );
        
        addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                cleanup();
            }
            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                cleanup();
            }
        });
        
        update();
    }
    
    private void cleanup() {
        System.out.println( "GlacierLengthVersusTimeChart.cleanup" );//XXX
        _clock.removeClockListener( _clockListener );
    }
    
    public void clear() {
        _series.clear();
    }
    
    private void update() {
        double t = _clock.getSimulationTime();
        //XXX add a data point every time the glacier's length changes
    }
}

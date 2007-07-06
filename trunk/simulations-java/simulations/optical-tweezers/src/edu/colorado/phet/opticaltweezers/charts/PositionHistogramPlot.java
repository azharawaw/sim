/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.PhetHistogramDataset;
import edu.colorado.phet.common.jfreechartphet.PhetHistogramSeries;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * PositionHistogramPlot is the plot for the position histogram chart.
 * The position histogram is normalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionHistogramPlot extends XYPlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String SERIES_KEY = "position";
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BAR_FILL_COLOR = Color.BLUE;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    private static final int OBSERVATIONS_REQUIRED_TO_ENABLE_AUTORANGE = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
//    private final double _binWidth;
    private PhetHistogramDataset _dataset;
    private PhetHistogramSeries _series;
    private NumberAxis _xAxis;
    private NumberAxis _yAxis;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public PositionHistogramPlot() {
        super();
        
        // dataset
        _dataset = new PhetHistogramDataset();
        setDataset( _dataset );
        
        // series will be create when setPositionRange is called
        _series = null;

        // renderer
        XYBarRenderer renderer = new PositionHistogramRenderer();
        renderer.setPaint( BAR_FILL_COLOR );
        renderer.setOutlinePaint( BAR_OUTLINE_COLOR );
        renderer.setDrawBarOutline( false );
        setRenderer( renderer );
        
        // x-axis, with labeled ticks
        // Range will be adjusted via setPositionRange.
        _xAxis = new NumberAxis();
        _xAxis.setLabel( OTResources.getString( "axis.relativePosition" ) );
        _xAxis.setTickLabelsVisible( true );
        _xAxis.setTickMarksVisible( true );
        _xAxis.setVisible( true );
        setDomainAxis( _xAxis );
        
        // y-axis, no label, no ticks
        _yAxis = new NumberAxis();
        _yAxis.setLabel( null );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        _yAxis.setAutoRange( false );
        _yAxis.setRange( 0, OBSERVATIONS_REQUIRED_TO_ENABLE_AUTORANGE );
        setRangeAxis( _yAxis );

        // plot configuration
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        setInsets( new RectangleInsets( 0, 0, 0, 0 ) );
        
        // Default
        setPositionRange( 0, 1, 1 );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the position range for the x axis and series.
     * A series' range is not mutable, so this involves creating a new series.
     * The data from the existing series is lost, so the plot appears to clear.
     * 
     * @param minPosition
     * @param maxPosition
     * @param binWidth
     */
    public void setPositionRange( double minPosition, double maxPosition, double binWidth ) {
        
        // set the range for the x axis
        _xAxis.setRange( minPosition, maxPosition );
        
        // set the range for the series
        final int numberOfBins = (int) ( ( maxPosition - minPosition ) / binWidth );
        if ( _series != null ) {
            _dataset.removeSeries( _series );
        }
        _series = new PhetHistogramSeries( SERIES_KEY, minPosition, maxPosition, numberOfBins );
        _dataset.addSeries( _series );
    }
    
    //----------------------------------------------------------------------------
    // Data management
    //----------------------------------------------------------------------------
    
    /**
     * Adds a position observation.
     * 
     * @param position
     */
    public void addPosition( double position ) {

        _series.addObservation( position );
        
        // When at least one bin reaches the top of the y-axis range,
        // adjust the range to fit data, so data appears normalized.
        if ( !_yAxis.isAutoRange() && _series.getMaxObservations() > OBSERVATIONS_REQUIRED_TO_ENABLE_AUTORANGE ) {
            _yAxis.setAutoRange( true );
        }
    }

    /**
     * Clears the plot.
     */
    public void clear() {
        _series.clear();
        _yAxis.setAutoRange( false );
        _yAxis.setRange( 0, OBSERVATIONS_REQUIRED_TO_ENABLE_AUTORANGE );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * If the series is empty, don't render anything.
     * When rendering an empty series, it appears as a horizontal line across the middle of the plot.
     */
    private static class PositionHistogramRenderer extends XYBarRenderer {
        public void drawItem(Graphics2D g2,
                XYItemRendererState state,
                Rectangle2D dataArea,
                PlotRenderingInfo info,
                XYPlot plot,
                ValueAxis domainAxis,
                ValueAxis rangeAxis,
                XYDataset dataset,
                int seriesIndex,
                int itemIndex,
                CrosshairState crosshairState,
                int pass) {
            PhetHistogramSeries series = ((PhetHistogramDataset)dataset).getSeries( seriesIndex );
            if ( series.getNumberOfObservations() == 0 ) {
                return;
            }
            super.drawItem( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, seriesIndex, itemIndex, crosshairState, pass );
        }
    }
}

/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSSquareWells;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSEnergyPlot is the plot that displays total and potential energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEnergyPlot extends XYPlot implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model references
    private BSAbstractPotential _potential;
    
    // View
    private XYSeries _potentialSeries;
    private int _potentialIndex; // well dataset index
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSEnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        
        int dataSetIndex = 0;
        
        // Potential series
        _potentialSeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            _potentialIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _potentialSeries );
            setDataset( _potentialIndex, dataset );
            // Renderer
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( BSConstants.POTENTIAL_ENERGY_COLOR );
            renderer.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
            setRenderer( _potentialIndex, renderer );
        }
        
        // X axis 
        BSPositionAxis xAxis = new BSPositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
        yAxis.setRange( BSConstants.ENERGY_RANGE );
        yAxis.setTickLabelPaint( BSConstants.TICK_LABEL_COLOR );
        yAxis.setTickMarkPaint( BSConstants.TICK_MARK_COLOR );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BSConstants.PLOT_BACKGROUND );
        setDomainGridlinesVisible( BSConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( BSConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( BSConstants.GRIDLINES_COLOR );
        setRangeGridlinePaint( BSConstants.GRIDLINES_COLOR );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis ); 
    }
    
    public void cleanup() {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
            _potential = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSAbstractPotential well ) {
        _potential = well;
        _potential.addObserver( this );
        update();
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _potential ) {
            updatePotential();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------

    /*
     * Updates everything.
     */
    private void update() {
        updatePotential();
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updatePotential() {
        final double minX = getDomainAxis().getLowerBound();
        final double maxX = getDomainAxis().getUpperBound();
        final double dx = 0.01; //XXX calculate based on plot bounds and pixels per sample
        Point2D[] points = _potential.getPoints( minX, maxX, dx );
        _potentialSeries.clear();
        for ( int i = 0; i < points.length; i++ ) {
            _potentialSeries.add( points[i].getX(), points[i].getY() );
        }
    }
}

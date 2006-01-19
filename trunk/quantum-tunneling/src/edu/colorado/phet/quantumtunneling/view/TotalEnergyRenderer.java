/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.WavePacket;


/**
 * TotalEnergyRenderer renders the total energy of a wave packet as a "band" of probabilites.
 * <p>
 * For total energy E, the band will be brightest at E=E0, and decrease linearly in brightness
 * to E=minE below and E=maxE above.  minE and maxE are given by:
 * <p>
 * <code>
 * minE = E0 - ((2*hbar/w) * sqrt(2*(E0-V0 )/m )) + ((2*hbar*hbar)/(m*w*w))
 * maxE = E0 + ((2*hbar/w) * sqrt(2*(E0-V0 )/m )) + ((2*hbar*hbar)/(m*w*w))
 * </code>
 * where:
 * <code>
 * E0 = average total energy
 * V0 = potential energy at the wave packet's initial center position
 * w = wave packet's initial width
 * m = mass
 * hbar = Planck's constant
 * </code>
 * <p>
 * If E0 <= V0, then the "band" is replaced with a line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergyRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double HBAR = QTConstants.HBAR;
    private static final double MASS = QTConstants.MASS;
    private static final Color CENTER_COLOR = QTConstants.TOTAL_ENERGY_COLOR;
    private static final Color EDGE_COLOR = QTConstants.CHART_BACKGROUND; // more efficient than using alpha
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private WavePacket _wavePacket;
    private AbstractPotential _potentialEnergy;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public TotalEnergyRenderer() {
        super();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setWavePacket( WavePacket wavePacket ) {
        _wavePacket = wavePacket;
    }
    
    /**
     * Sets the potential energy.  
     * <p>
     * The wave packet (set via setWavePacket) provides access to its potential
     * energy.  But there are situations (like in the "Configure Energy" dialog)
     * where we want to use a "hypothetical" potential that hasn't been applied
     * to the wave packet model yet.  So we specify the potential energy separately
     * via this method.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        _potentialEnergy = potentialEnergy;
    }
    
    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws the band that represents the range of possible energy.
     * <p>
     * This band is implemented as 2 rectangles, each with its own
     * GradientPaint.  The rectangles and gradients are arranged such
     * that the darkest color at the average total energy point, and the
     * color fades out above and below.
     */
    public void drawItem( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item, 
            CrosshairState crosshairState, 
            int pass ) {
        
        // Initialized?
        if ( _wavePacket == null || _potentialEnergy == null ) {
            return;
        }
        
        // Visible?
        if ( !getItemVisible( series, item ) ) {
            return;
        }
        
        // Do all rendering based on the first data point.
        if ( item != 0 ) {
            return;
        }
        
        // Axis (model) coordinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double packetCenter = _wavePacket.getCenter();
        final double E0 = dataset.getYValue( series, item ); // the average total energy
        final double V0 = _potentialEnergy.getEnergyAt( packetCenter );
        
        // Java2D (screen) coorinates
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, domainAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, domainAxisLocation );
        final double averageY = rangeAxis.valueToJava2D( E0, dataArea, rangeAxisLocation );
        
        if ( E0 <= V0 ) {
            // Draw a line...
            g2.setPaint( getSeriesPaint( series ) );
            g2.setStroke( getSeriesStroke( series ) );
            g2.drawLine( (int)minX, (int)averageY, (int)maxX, (int)averageY );
        }
        else {
            // Axis (model) coordinates
            final double packetWidth = _wavePacket.getWidth();
            final double term1 = ( 2 * HBAR / packetWidth ) * Math.sqrt( 2 * ( E0 - V0 ) / MASS );
            final double term2 = ( 2 * HBAR * HBAR ) / ( MASS * packetWidth * packetWidth );
            final double minE = E0 - term1 + term2; // max total energy
            final double maxE = E0 + term1 + term2; // min total energy

            // Java2D (screen) coorinates
            final double minY = rangeAxis.valueToJava2D( maxE, dataArea, rangeAxisLocation ); // +y is down!
            final double maxY = rangeAxis.valueToJava2D( minE, dataArea, rangeAxisLocation ); // +y is down!
            final double width = Math.max( maxX - minX, 1 );
            final double topHeight = Math.max( averageY - minY, 1 );
            final double bottomHeight = Math.max( maxY - averageY, 1 );
            
            // Draw a band...
            {
                Shape topShape = new Rectangle2D.Double( minX, minY, width, topHeight );
                Shape bottomShape = new Rectangle2D.Double( minX, averageY, width, bottomHeight );

                GradientPaint topGradient = new GradientPaint( (float) minX, (float) minY, EDGE_COLOR, (float) minX, (float) averageY, CENTER_COLOR );
                GradientPaint bottomGradient = new GradientPaint( (float) minX, (float) averageY, CENTER_COLOR, (float) minX, (float) maxY, EDGE_COLOR );

                g2.setPaint( topGradient );
                g2.fill( topShape );
                g2.setPaint( bottomGradient );
                g2.fill( bottomShape );
            }
        }
    }
}

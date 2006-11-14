package edu.colorado.phet.molecularreactions.util;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.SwingClock;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * edu.colorado.phet.molecularreactions.util.StripChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChart {

    private XYSeries[] series;
    private JFreeChart chart;
    private double xAxisRange;
    private XYLineAndShapeRenderer renderer;
    private XYPlot plot;

    private int buffSize = 10;
    private double[][] buffer;
    private int clockBufferIdx;
    private int buffHead = 0;
    private int buffTail = buffSize - 1;


    /**
     *
     * @param title
     * @param seriesNames
     * @param xAxisLabel
     * @param yAxisLabel
     * @param orienation
     * @param xAxisRange
     * @param minY
     * @param maxY
     */
    public StripChart( String title,
                       String[] seriesNames,
                       String xAxisLabel,
                       String yAxisLabel,
                       PlotOrientation orienation,
                       double xAxisRange,
                       double minY,
                       double maxY ) {
        this.xAxisRange = xAxisRange;

        series = new XYSeries[seriesNames.length + 1 ];
        buffer = new double[seriesNames.length + 1][buffSize];
        clockBufferIdx = seriesNames.length;
        XYSeriesCollection dataset = new XYSeriesCollection();
        for( int i = 0; i < series.length - 1; i++ ) {
            series[i] = new XYSeries( seriesNames[i] );
            dataset.addSeries( series[i] );
        }

        chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                orienation,
                true,
                true,
                false
        );

        plot = (XYPlot)chart.getPlot();
        plot.getRangeAxis().setRange( minY, maxY );

        renderer = new XYLineAndShapeRenderer();
        for( int i = 0; i < series.length; i++ ) {
            renderer.setSeriesLinesVisible( i, true );
            renderer.setSeriesShapesVisible( i, false );
        }
        renderer.setSeriesLinesVisible( series.length - 1, false );
        renderer.setSeriesShapesVisible( series.length - 1, false );
        renderer.setToolTipGenerator( new StandardXYToolTipGenerator() );
        renderer.setDefaultEntityRadius( 6 );
        plot.setRenderer( renderer );
    }

    public void setSeriesPaint( int seriesNum, Paint paint ) {
        renderer.setSeriesPaint( seriesNum, paint );    
    }

    public void setStroke( Stroke stroke ) {
        renderer.setStroke( stroke );
    }

    public void addData( int seriesNum, double x, double y ) {
        series[seriesNum].add( x, y );
//        while( x - series[seriesNum].getDataItem( 0 ).getX().doubleValue() > xAxisRange ) {
//            series[seriesNum].remove( 0 );
//        }


        XYPlot plot = (XYPlot)chart.getPlot();
        double minX = Math.max( x - xAxisRange, 0);
        double maxX = Math.max( x, xAxisRange );
        plot.getDomainAxis().setRange( minX, maxX );

//        double xOrigin = series[seriesNum].getX( 0 ).doubleValue();
//        plot.getDomainAxis().setRange( xOrigin, xOrigin + xAxisRange );

        buffHead = (buffHead + 1) % buffSize;
        if( buffHead == buffTail ) {
            buffTail = (buffTail + 1) % buffSize;
        }
        buffer[clockBufferIdx][buffHead] = x;
        buffer[seriesNum][buffHead] = y;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void setMinX( double x ) {
        XYPlot plot = (XYPlot)chart.getPlot();
        double minX = Math.min( x, getMaxTime() - xAxisRange );
        double maxX = Math.min( x + xAxisRange, getMaxTime() );
        plot.getDomainAxis().setRange( minX, maxX );
    }

    private double getMaxTime() {
        int newestIdx = (buffHead + buffSize - 1) % buffSize;
        return buffer[clockBufferIdx][newestIdx];
    }


    /**
     * Set the vertical range of the chart
     *
     * @param minY
     * @param maxY
     */
    public void setYRange( int minY, int maxY ) {
        plot.getRangeAxis().setRange( minY, maxY );
    }


    /**
     * For test purposes
     */
    public void dumpBuffer() {
        int numBufferEntries = ( buffHead - buffTail - 1 + buffSize ) % buffSize;

        System.out.println( "===================================" );

        for( int i = 0; i < numBufferEntries; i++ ) {
            int buffIdx = Math.abs(buffHead + buffSize - i) % buffSize;
            System.out.println( "buffer = " + buffer[clockBufferIdx][buffIdx ] );
        }
    }


    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Strip Chart Test" );
        final StripChart stripChart = new StripChart( "Test Chart",
                                                      new String[]{"A", "B"},
                                                      "t",
                                                      "n",
                                                      PlotOrientation.VERTICAL,
                                                      10,
                                                      -4, 4 );
        ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setPreferredSize( new java.awt.Dimension( 500, 300 ) );
        frame.setContentPane( chartPanel );
        frame.pack();
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        SwingClock clock = new SwingClock( 40, .1 );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double t = clockEvent.getSimulationTime();
                stripChart.addData( 0, t, 3 * Math.sin( t ) );
                stripChart.addData( 1, t, 3 * Math.cos( t ) );
            }
        } );
        clock.start();

    }
}

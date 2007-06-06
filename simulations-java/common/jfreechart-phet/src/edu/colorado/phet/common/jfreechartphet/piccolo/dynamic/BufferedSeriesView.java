package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 6:04:09 PM
 */
public class BufferedSeriesView extends SeriesView {
    private boolean origStateBuffered;
    private BasicStroke stroke = new BasicStroke( 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
    private PhetPPath debugRegion = new PhetPPath( new BasicStroke( 3 ), Color.blue );

    public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
        super( dynamicJFreeChartNode, seriesData );
        dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                paintAll();
            }
        } );
        dynamicJFreeChartNode.getPhetPCanvas().addScreenChild( debugRegion );
    }

    public void dataAdded() {
//        debugRegion.setPathTo( getDataArea() );
        if( getSeries().getItemCount() >= 2 ) {
            BufferedImage image = getDynamicJFreeChartNode().getBuffer();
            if( image != null ) {
                Graphics2D graphics2D = image.createGraphics();
                graphics2D.setPaint( getSeriesData().getColor() );

                graphics2D.setStroke( stroke );
                int itemCount = getSeries().getItemCount();
                Line2D.Double viewLine = new Line2D.Double( getNodePoint( itemCount - 2 ), getNodePoint( itemCount - 1 ) );
                setupRenderingHints( graphics2D );

                graphics2D.clip( translateDataArea() );
                graphics2D.draw( viewLine );

                Shape sh = stroke.createStrokedShape( viewLine );
                Rectangle2D bounds = sh.getBounds2D();
                getDynamicJFreeChartNode().localToGlobal( bounds );
                getDynamicJFreeChartNode().getPhetPCanvas().getPhetRootNode().globalToScreen( bounds );
                repaintPanel( translateDown(bounds) );
            }
        }
    }

    private Rectangle2D translateDown( Rectangle2D d) {
        return new Rectangle2D.Double( d.getX() + getDynamicJFreeChartNode().getBounds().getX(),
                                       d.getY() + getDynamicJFreeChartNode().getBounds().getY(),
                                       d.getWidth(), d.getHeight() );
    }

    private Shape translateDataArea() {
        Rectangle2D d = getDataArea();
        return new Rectangle2D.Double( d.getX() - getDynamicJFreeChartNode().getBounds().getX(),
                                       d.getY() - getDynamicJFreeChartNode().getBounds().getY(),
                                       d.getWidth(), d.getHeight() );
    }

    public Point2D getNodePoint( int i ) {
        return new Point2D.Double( super.getNodePoint( i ).getX() - getDynamicJFreeChartNode().getBounds().getX(),
                                   super.getNodePoint( i ).getY() - getDynamicJFreeChartNode().getBounds().getY() );
    }

    private void setupRenderingHints( Graphics2D graphics2D ) {
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }

    public void install() {
        super.install();
        paintAll();
        this.origStateBuffered = getDynamicJFreeChartNode().isBuffered();
        if( !origStateBuffered ) {
            getDynamicJFreeChartNode().setBuffered( true );
        }
    }

    private void paintAll() {
        BufferedImage image = getDynamicJFreeChartNode().getBuffer();
        if( image != null ) {
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setPaint( getSeriesData().getColor() );
            graphics2D.setStroke( stroke );
            GeneralPath path = toGeneralPath();
            setupRenderingHints( graphics2D );
            graphics2D.clip( getDataArea() );
            graphics2D.draw( path );
            getDynamicJFreeChartNode().getPhetPCanvas().repaint( );
//            repaintPanel( new Rectangle2D.Double( 0, 0, getDynamicJFreeChartNode().getPhetPCanvas().getWidth(), getDynamicJFreeChartNode().getPhetPCanvas().getHeight() ) );
        }
    }

    protected void repaintPanel( Rectangle2D bounds ) {
        getDynamicJFreeChartNode().getPhetPCanvas().repaint( new PBounds( bounds ) );
    }

}

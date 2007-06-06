package edu.colorado.phet.common.jfreechartphet.test;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;

import java.awt.geom.Point2D;
import java.awt.*;

/**
 * Author: Sam Reid
 * Jun 4, 2007, 3:19:47 AM
 */
public class TestInterleavedSeries extends TestDynamicJFreeChartNode {

    public TestInterleavedSeries() {

        getDynamicJFreeChartNode().addSeries( "Series 1", Color.green);
        getDynamicJFreeChartNode().addSeries( "Series 2", Color.red);
        getDynamicJFreeChartNode().addSeries( "Series 3", Color.black);
        PhetPPath phetPPath = new PhetPPath( new Rectangle( 0, 0, 100, 100 ), new BasicStroke( 2 ), Color.green );
        phetPPath.addInputEventListener( new PDragEventHandler() );
        phetPPath.addInputEventListener(new CursorHandler( ) );
        getPhetPCanvas().addScreenChild( phetPPath );
    }

    protected void updateGraph() {
        double t = ( System.currentTimeMillis() - super.getInitialTime() ) / 1000.0;
        double frequency = 1.0 / 10.0;
        double sin = Math.sin( t * 2 * Math.PI * frequency );
        Point2D.Double pt = new Point2D.Double( t / 100.0, sin );

        getDynamicJFreeChartNode().addValue( 0, pt.getX(), pt.getY() );
        getDynamicJFreeChartNode().addValue( 1, pt.getX(), pt.getY()*0.9 );
        getDynamicJFreeChartNode().addValue( 2, pt.getX(), pt.getY()*0.8 );
        getDynamicJFreeChartNode().addValue( 3, pt.getX(), pt.getY()*1.5 );
    }
    public static void main( String[] args ) {
        new TestInterleavedSeries().start();
    }
}

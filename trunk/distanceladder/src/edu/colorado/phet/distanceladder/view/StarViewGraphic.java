/**
 * Class: StarViewGraphic
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 10:17:23 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.StarView;
import edu.colorado.phet.distanceladder.model.Point2DPolar;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This graphic contains the stars as seen from the cockpit
 */
public class StarViewGraphic extends CompositeInteractiveGraphic implements SimpleObserver {
    private Container container;
    private StarView starView;
    private Rectangle2D.Double background;
    private AffineTransform starViewTx = new AffineTransform();
    private HashMap starToGraphicMap = new HashMap();

    public StarViewGraphic( Container container, StarView starView,
                            Rectangle2D.Double bounds ) {
        this.container = container;
        this.starView = starView;
        this.background = bounds;
        this.starViewTx.setToIdentity();
        this.starViewTx.translate( bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2 );
    }

    public void paint( Graphics2D g ) {

        AffineTransform orgTx = g.getTransform();
        Rectangle bounds = container.getBounds();
        double scaleX = bounds.getWidth() / background.getWidth();
        AffineTransform atx = AffineTransform.getScaleInstance( scaleX, scaleX );
        g.transform( atx );

        g.setColor( Color.black );
        g.fillRect( 0, 0, (int)background.getWidth(), (int)background.getHeight() );
        g.transform( starViewTx );

        super.paint( g );

        g.setTransform( orgTx );
    }

    public void update() {
        ArrayList visibleStars = starView.getVisibleStars();
        StarGraphic starGraphic = null;
        for( int i = 0; i < visibleStars.size(); i++ ) {
            Star visibleStar = (Star)visibleStars.get( i );
            starGraphic = (StarGraphic)starToGraphicMap.get( visibleStar );
            if( starGraphic == null ) {

                // Set the radius of the star based on how close it is to the POV
                double d = starView.getPov().distanceSq( visibleStar.getLocation() );
                double radius = Math.min( 15, Math.max( 40000 / d, 2 ));
                starGraphic = new StarGraphic( radius, visibleStar.getColor(), new Point2D.Double() );
//                starGraphic = new StarGraphic( 4, visibleStar.getColor(), new Point2D.Double() );
                starToGraphicMap.put( visibleStar, starGraphic );
                this.addGraphic( starGraphic, 1 / d );
            }
            starGraphic.update( starView.getLocation( visibleStar ) );
        }

        // Remove stars that aren't visible
        ArrayList removeList = new ArrayList();
        Iterator starIt = starToGraphicMap.keySet().iterator();
        while( starIt.hasNext() ) {
            Star star = (Star)starIt.next();
            if( !visibleStars.contains( star ) ) {
                removeList.add( star );
                StarGraphic sg = (StarGraphic)starToGraphicMap.get( star );
                this.remove( sg );
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            Star star = (Star)removeList.get( i );
            starToGraphicMap.remove( star );
        }
        container.repaint();
    }

}

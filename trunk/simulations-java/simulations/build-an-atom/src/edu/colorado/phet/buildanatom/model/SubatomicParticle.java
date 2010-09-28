package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Observable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class SubatomicParticle {
    private final Observable<Point2D.Double> position;
    private final double radius;

    public SubatomicParticle( double radius, double x, double y ) {
        this.radius = radius;
        position = new Observable<Point2D.Double>( new Point2D.Double( x,y) );
    }

    public Point2D.Double getPosition() {
        return position.getValue();
    }

    public void setPosition( Point2D.Double position ) {
        setPosition( position.x, position.y );
    }

    public void setPosition( double x, double y ) {
        position.setValue( new Point2D.Double( x,y) );
    }
    public double getDiameter() {
        return getRadius() * 2;
    }
    
    
    public double getRadius() {
        return radius;
    }
    public void translate( double dx, double dy ) {
        setPosition( position.getValue().getX() + dx, position.getValue().getY() + dy );
    }
    
    public void reset(){
        position.reset();
    }

    public void addPositionListener( SimpleObserver listener ) {
        position.addObserver(  listener );
    }
}

/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A straight segment of wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireSegment {

    private final Property<Point2D> startPoint, endPoint;

    public WireSegment( Point2D startPoint, Point2D endPoint ) {
        this.startPoint = new Property<Point2D>( new Point2D.Double( startPoint.getX(), startPoint.getY() ) );
        this.endPoint = new Property<Point2D>( new Point2D.Double( endPoint.getX(), endPoint.getY() ) );
    }

    public void addStartPointChangeListener( SimpleObserver o ) {
        startPoint.addObserver( o );
    }
    
    public Point2D getStartPoint() {
        return new Point2D.Double( startPoint.getValue().getX(), startPoint.getValue().getY() );
    }

    public void setStartPoint( Point2D startPoint ) {
        this.startPoint.setValue( new Point2D.Double( startPoint.getX(), startPoint.getY() )  );
    }

    public void addEndPointChangeListener( SimpleObserver o ) {
        endPoint.addObserver( o );
    }
    
    public Point2D getEndPoint() {
        return new Point2D.Double( endPoint.getValue().getX(), endPoint.getValue().getY() );
    }

    public void setEndPoint( Point2D endPoint ) {
        this.endPoint.setValue( new Point2D.Double( endPoint.getX(), endPoint.getY() ) );
    }
}

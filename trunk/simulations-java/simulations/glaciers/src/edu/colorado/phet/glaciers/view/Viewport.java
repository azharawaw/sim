/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *  Viewport describes the portion of the scene that's visible through the viewport,
 *  and displayed in the "zoomed" view. 
 *  
 *  @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Viewport {
    
    private String _id;
    private Rectangle2D _bounds;
    private ArrayList _listeners; // list of ViewportListener
    
    public Viewport( String id ) {
        this( id, new Rectangle2D.Double() );
    }
    
    public Viewport( String id, Rectangle2D bounds ) {
        _id = id;
        _bounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    public void setBounds( double x, double y, double w, double h ) {
        if ( x != _bounds.getX() || y != _bounds.getY() || w != _bounds.getWidth() || h != _bounds.getHeight() ) {
            System.out.println( "Viewport.setBounds id=" + _id + " x=" + x + " y=" + y + " w=" + w + " h=" + h );//XXX
            _bounds.setRect( x, y, w, h );
            notifyBoundsChanged();
        }
    }
    
    public void setBounds( Rectangle2D bounds ) {
        setBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }
    
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( _bounds.getX(), _bounds.getY(), _bounds.getWidth(), _bounds.getHeight() );
    }
    
    public Rectangle2D getBoundsReference() {
        return _bounds;
    }
    
    public double getX() {
        return _bounds.getX();
    }
    
    public double getY() {
        return _bounds.getY();
    }
    
    public double getWidth() {
        return _bounds.getWidth();
    }
    
    public double getHeight() {
        return _bounds.getHeight();
    }
    
    public void setSize( double w, double h ) {
        if ( w != _bounds.getWidth() || h != _bounds.getHeight() ) {
            System.out.println( "Viewport.setSize id=" + _id + " w=" + w + " h=" + h );//XXX
            setBounds( _bounds.getX(), _bounds.getY(), w, h );
        }
    }
    
    public void translate( double dx, double dy ) {
        if ( dx !=0 || dy != 0 ) {
            System.out.println( "Viewport.translate id=" + _id + " dx=" + dx + " dy=" + dy );//XXX
            setBounds( new Rectangle2D.Double( _bounds.getX() + dx, _bounds.getY() + dy, _bounds.getWidth(), _bounds.getHeight() ) );
        }
    }
    
    /* Implement this interface to be notified of changes to a viewport. */
    public interface ViewportListener {
        public void boundsChanged();
    }
    
    public void addViewportListener( ViewportListener listener ) {
        _listeners.add( listener );
    }

    public void removeViewportListener( ViewportListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyBoundsChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewportListener) i.next() ).boundsChanged();
        }
    }
}

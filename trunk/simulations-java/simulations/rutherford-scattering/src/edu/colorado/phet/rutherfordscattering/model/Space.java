/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * Space models the space that alpha particles travel through,
 * and where they encounter atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Space implements ModelElement {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle2D _bounds;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param bounds
     * @param gun
     * @param model
     */
    public Space( Rectangle2D bounds ) {
        _bounds = new Rectangle2D.Double();
        _bounds.setRect( bounds );
    }
    
    //----------------------------------------------------------------------------
    // Accesors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the point that is at the center of space.
     * The origin (0,0) is at the bottom-center of space.
     * 
     * @return Point2D
     */
    public Point2D getCenter() {
        return new Point2D.Double( 0, -_bounds.getHeight() / 2 );
    }
    
    //----------------------------------------------------------------------------
    // Alpha Particle management
    //----------------------------------------------------------------------------
    
    public boolean contains( AlphaParticle alphaParticle ) {
        Point2D position = alphaParticle.getPositionRef();
        return _bounds.contains( position );
    }
   
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /** Do nothing. */
    public void stepInTime( double dt ) {}
}

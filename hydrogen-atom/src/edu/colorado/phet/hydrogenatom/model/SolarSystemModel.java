/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

/**
 * SolarSystemModel models the hydrogen atom as a classical solar system.
 * <p>
 * Physical representation:
 * Proton at the center, electron spirals towards the proton.
 * (Our spiral is clockwise to be consistent with all other orbits in this sim.)
 * The electron starts at a fixed distance and random angle from the proton.
 * The radius of the spiral decreases linearly and the electron accelerates 
 * as the electron moves closer to the proton. 
 * The final state shows the electron on top of the proton.
 * In this final state, the atom is considered "destroyed".
 * <p>
 * Collision behavior:
 * The spiraling behavior should occur fast enough so that the atom is
 * destroyed before any photons or alpha particles reach it.  Therefore,
 * there are no collisions.
 * <p>
 * Absorption behavior:
 * Atom is destroyed, so it does not absorb photons or alpha particles.
 * <p>
 * Emission behavior:
 * Atom is destroyed, so it does not emit photons or alpha particles.
 */
public class SolarSystemModel extends AbstractHydrogenAtom {
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ELECTRON_POSITION = "electronPosition";
    public static final String PROPERTY_DESTROYED = "destroyed";
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /*
     * NOTE! Tweak these VERY carefully, and test often! 
     * They must be set so that the atom is destroyed before 
     * any photons or alpha particles reach it.
     */
    
    /* initial distance between electron and proton */
    private static final double ELECTRON_DISTANCE = 150;
    
    /* amount the distance between the proton and electron is reduce per dt */
    private static final double ELECTRON_DISTANCE_DELTA = 4;
    
    /* any distance smaller than this is effectively zero */
    private static final double MIN_ELECTRON_DISTANCE = 5;
    
    /* change in electron's rotation angle per dt */
    private static final double ELECTRON_ANGLE_DELTA = Math.toRadians( 16 );
    
    /* scaling of electron's rotation delta */
    private static final double ELECTRON_ACCELERATION = 1.008;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _electronPosition;
    private double _electronDistance; // absolute distance from electron to proton
    private double _electronAngle; // in radians
    private double _electronAngleDelta; // in radians
    private boolean _destroyed;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param position
     */
    public SolarSystemModel( Point2D position ) {
        super( position, 0 /* orientation */ );
        
        _electronPosition = new Point2D.Double();
        _electronDistance = ELECTRON_DISTANCE;
        _electronAngle = Math.random() * Math.toRadians( 360 );
        _electronAngleDelta = ELECTRON_ANGLE_DELTA;
        _destroyed = false;
        
        setElectronPosition( _electronAngle, _electronDistance );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Has the atom been destroyed?
     * The atom is destroyed when the electron has completed its spiral into the proton.
     * @return true or false
     */
    public boolean isDestroyed() {
        return _destroyed;
    }
    
    /**
     * Gets the absolute electron position.
     * @return Point2D
     */
    public Point2D getElectronPosition() {
        return _electronPosition;
    }
    
    /**
     * Gets the electron's position relative to the local coordinate system of the atom.
     * The proton is at the origin (0,0) of the atom's local coordinate system.
     * @return Point2D
     */
    public Point2D getRelativeElectronPosition() {
        Point2D atomPosition = getPosition();
        double x = _electronPosition.getX() - atomPosition.getX();
        double y = _electronPosition.getY() - atomPosition.getY();
        return new Point2D.Double( x, y );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        if ( !_destroyed ) {
            
            // increment the orbit angle
            _electronAngle += ( _electronAngleDelta * dt );
            // increase the rate of change of the orbit angle
            _electronAngleDelta *= ELECTRON_ACCELERATION;
            // decrease the electron's distance from the proton
            _electronDistance -= ( ELECTRON_DISTANCE_DELTA * dt );
            // is the distance effectively zero?
            if ( _electronDistance <= MIN_ELECTRON_DISTANCE ) {
                _electronDistance = 0;
            }
            
            // move the electron and notify observers
            setElectronPosition( _electronAngle, _electronDistance );
            notifyObservers( PROPERTY_ELECTRON_POSITION );
            
            // was the atom destroyed?
            if ( _electronDistance == 0 ) {
                _destroyed = true;
                notifyObservers( PROPERTY_DESTROYED ); 
            }
        }
    }
    
    /*
     * Sets the electron's position based on its angle and distance from the proton.
     */
    private void setElectronPosition( double electronAngle, double electronDistance ) {
        double x = getX() + ( electronDistance * Math.cos( electronAngle ) );
        double y = getY() + ( electronDistance * Math.sin( electronAngle ) );
        _electronPosition.setLocation( x, y );
    }
}

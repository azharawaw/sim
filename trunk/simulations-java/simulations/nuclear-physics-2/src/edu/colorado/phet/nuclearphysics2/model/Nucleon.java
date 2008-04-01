/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class the implements the behavior of nucleon (i.e. proton and neutron)
 * model elements.
 *
 * @author John Blanco
 */
public class Nucleon implements AtomicNucleusConstituent {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Velocity of this particle.
    private double _xVelocity;
    private double _yVelocity;
    
    // Boolean that controls whether this particle should exhibit quantum
    // tunneling behavior.
    private boolean _tunnelingEnabled;
    
    // Random number generator, used for creating some random behavior.
    Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a nucleon that is not moving.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     * quantum tunneling behavior. 
     */
    public Nucleon(double xPos, double yPos, boolean tunnelingEnabled)
    {
        this(xPos, yPos, 0, 0, tunnelingEnabled);
    }
    
    /**
     * Construct a nucleon.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param xVel - Initial velocity in the X direction.
     * @param yVel - Initial velocity in the Y direction.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     */
    public Nucleon(double xPos, double yPos, double xVel, double yVel, boolean tunnelingEnabled){
        _position = new Point2D.Double(xPos, yPos);
        _xVelocity = xVel;
        _yVelocity = yVel;
        _tunnelingEnabled = tunnelingEnabled;
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public Point2D getPosition()
    {
        return new Point2D.Double(_position.getX(), _position.getY());
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * This method simulates the quantum tunneling behavior, which means that
     * it causes the particle to move to some new random location within the
     * confines of the supplied parameters.
     * 
     * @param minDistance - Minimum distance from origin (0,0).  This is
     * generally 0.
     * @param nucleusRadius - Radius of the nucleus where this particle resides.
     * @param tunnelRadius - Radius at which this particle could tunnel out of nucleus.
     */
    public void tunnel(double minDistance, double nucleusRadius, double tunnelRadius)
    {
        if (_tunnelingEnabled){
            
            // Create a probability distribution that will cause the particles to
            // be fairly evenly spread around the core of the nucleus and appear
            // occasionally at the outer reaches.
    
            double multiplier = _rand.nextDouble();
            
            if (multiplier > 0.8){
                // Cause the distribution to tail off in the outer regions of the
                // nucleus.
                multiplier = _rand.nextDouble() * _rand.nextDouble();
            }
            
            double newRadius = minDistance + (multiplier * (nucleusRadius - minDistance));
            
            // Calculate the new angle, in radians, from the origin.
            double newAngle = _rand.nextDouble() * 2 * Math.PI;
            
            // Convert from polar to Cartesian coordinates.
            double xPos = Math.cos( newAngle ) * newRadius;
            double yPos = Math.sin( newAngle ) * newRadius;
            
            // Save the new position.
            _position.setLocation( xPos, yPos );
    
            // Notify all listeners of the position change.
            for (int i = 0; i < _listeners.size(); i++)
            {
                ((Listener)_listeners.get( i )).positionChanged(); 
            }        
        }
    }
    
    /**
     * Moves this particle based on its current velocity.
     */
    public void translate(){
        
        // Update the position.
        _position.x += _xVelocity;
        _position.y += _yVelocity;        

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }        
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public static interface Listener {
        void positionChanged();
    }
}

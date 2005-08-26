/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.PhysicsUtil;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * A representation of the energy state of an atom
 */
public class AtomicState {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //

    static public final double minWavelength = Photon.BLUE - 20;
    static public final double maxWavelength = Photon.GRAY;
    static public final double minEnergy = PhysicsUtil.wavelengthToEnergy( maxWavelength );
    static public final double maxEnergy = PhysicsUtil.wavelengthToEnergy( minWavelength );
    static protected double s_collisionLikelihood = 1;
    static protected final double wavelengthTolerance = 10;
    //        static protected double s_collisionLikelihood = 0.2;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private double energyLevel;
    private double wavelength;
    private double meanLifetime = Double.POSITIVE_INFINITY;
    private AtomicState nextHigherState;
    private AtomicState nextLowerState;

    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Returns the lifetime of the state. This is based on the energy level.
     * The higher the energy, the shorter the lifetime.
     *
     * @return
     */
    public double getMeanLifeTime() {
        return meanLifetime;
    }

    public void setMeanLifetime( double lifetime ) {
        this.meanLifetime = lifetime;
        listenerProxy.meanLifetimechanged( new Event( this ) );
    }

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        this.wavelength = PhysicsUtil.energyToWavelength( energyLevel );
        listenerProxy.energyLevelChanged( new Event( this ) );
    }

    public double getWavelength() {
        return wavelength;
    }

    /**
     * Determines the wavelength of a photon that would be emitted if the atom dropped to a
     * specified state
     *
     * @param nextState
     * @return
     */
    public double determineEmittedPhotonWavelength( AtomicState nextState ) {
        double energy1 = PhysicsUtil.wavelengthToEnergy( this.getWavelength() );
        double energy2 = PhysicsUtil.wavelengthToEnergy( nextState.getWavelength() );

        // todo: This was changed in discharge atom model
        double emittedWavelength = Math.min( PhysicsUtil.energyToWavelength( energy1 - energy2 ),
//        double emittedWavelength = Math.min( Photon.energyToWavelength( energy1 - energy2 + AtomicState.minEnergy ),
                                             AtomicState.maxWavelength );
        return emittedWavelength;
    }

    /**
     * Returns the wavelength of a photon that would be emitted if the atom dropped to the next
     * lower energy state
     *
     * @return
     */
    public double determineEmittedPhotonWavelength() {
        double energy1 = PhysicsUtil.wavelengthToEnergy( this.getWavelength() );
        double energy2 = PhysicsUtil.wavelengthToEnergy( this.getNextLowerEnergyState().getWavelength() );

        // todo: this isn't right. It doesn't work for upper to middle transitions
        double emittedWavelength = Math.min( PhysicsUtil.energyToWavelength( energy1 - energy2 + AtomicState.minEnergy ),
                                             AtomicState.maxWavelength );
        return emittedWavelength;
    }

    protected boolean isStimulatedBy( Photon photon ) {
        return ( Math.abs( photon.getEnergy() - this.getEnergyLevel() ) <= LaserConfig.ENERGY_TOLERANCE
                 && Math.random() < s_collisionLikelihood );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( isStimulatedBy( photon ) ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() );
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = Photon.createStimulated( photon, position, atom );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setCurrState( atom.getLowestEnergyState() );
        }

        // Is the atom raised to a higher state?
//        else if( Math.random() < s_collisionLikelihood ) {
//            AtomicState newState = getStimulatedState( atom, photon, 0 );
//            if( newState != null ) {
//                photon.removeFromSystem();
//                atom.setCurrState( newState );
//            }
//        }
    }

    /**
     * Searches through the states of a specified atom for one whose energy differential between it and
     * a specified energy matches the energy in a specified photon. The reason the energy needs to be
     * specified as a parameter is that the GroundState has to pretend it has energy of 0 for the colors
     * and such to work right, but other states can use their actual energies.
     *
     * @param atom
     * @param photon
     * @param energy
     * @return
     */
    public AtomicState getStimulatedState( Atom atom, Photon photon, double energy ) {
        AtomicState result = null;
        AtomicState[] states = atom.getStates();
        for( int stateIdx = states.length - 1;
             stateIdx >= 0 && states[stateIdx] != this && result == null;
             stateIdx-- ) {
            double de = photon.getEnergy() - ( states[stateIdx].getEnergyLevel() - energy );
            if( Math.abs( de ) < LaserConfig.ENERGY_TOLERANCE ) {
                result = states[stateIdx];
            }
        }
        return result;
    }

    public AtomicState getNextLowerEnergyState() {
        return nextLowerState;
    }

    public void setNextLowerEnergyState( AtomicState nextLowerState ) {
        this.nextLowerState = nextLowerState;
    }

    public AtomicState getNextHigherEnergyState() {
        return nextHigherState;
    }

    public void setNextHigherEnergyState( AtomicState state ) {
        nextHigherState = state;
    }

    public int hashCode() {
        return (int)( Double.doubleToLongBits( energyLevel ) + Double.doubleToLongBits( wavelength ) + Double.doubleToLongBits( meanLifetime ) );
    }

    /**
     * Tests only the energy level and wavelength. Cannot test the nextHigherState and nextLowerState
     * because that results in stack overflows.
     *
     * @param obj
     * @return
     */
    public boolean equals( Object obj ) {
        boolean result = false;
        if( obj instanceof AtomicState && obj != null ) {
            AtomicState that = (AtomicState)obj;
            result = this.energyLevel == that.energyLevel
                     && this.wavelength == that.wavelength;
        }
        return result;
    }

    /**
     * Sets the next-higher and next-lower attributes for an array of AtomicStates
     *
     * @param states
     */
    public static void linkStates( AtomicState[] states ) {
        for( int i = 1; i < states.length; i++ ) {
            states[i].setNextLowerEnergyState( states[i - 1] );
            states[i - 1].setNextHigherEnergyState( states[i] );
        }
        states[states.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    public static class MaxEnergyState extends AtomicState {
        private static MaxEnergyState instance = new MaxEnergyState();

        public static MaxEnergyState instance() {
            return instance;
        }

        private MaxEnergyState() {
            setEnergyLevel( getEnergyLevel() );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }

        public double getWavelength() {
            // The hard-coded number here is a hack so the energy level graphic can be adjusted up to the top of
            // the window. This is not great programming
            return minWavelength - 80;
        }

        public double getEnergyLevel() {
            return PhysicsUtil.wavelengthToEnergy( getWavelength() );
        }
    }

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    public static class MinEnergyState extends AtomicState {
        private static MinEnergyState instance = new MinEnergyState();

        public static MinEnergyState instance() {
            return instance;
        }

        private MinEnergyState() {
            setEnergyLevel( minEnergy );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }
    }

    public interface MeanLifetimeChangeListener extends EventListener {
        public void meanLifetimeChanged( MeanLifetimeChangeEvent event );
    }

    public class MeanLifetimeChangeEvent extends EventObject {
        public MeanLifetimeChangeEvent() {
            super( AtomicState.this );
        }

        public double getMeanLifetime() {
            return AtomicState.this.getMeanLifeTime();
        }
    }

    //-------------------------------------------------------------------
    // Events and event handling
    //-------------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerChannel.getListenerProxy();

    public class Event extends EventObject {
        public Event( Object source ) {
            super( source );
        }

        public double getEnergy() {
            return ( (AtomicState)getSource() ).getEnergyLevel();
        }

        public AtomicState getAtomicState() {
            return (AtomicState)getSource();
        }

        public double getMeanLifetime() {
            return getAtomicState().getMeanLifeTime();
        }
    }

    public interface Listener extends EventListener {
        void energyLevelChanged( Event event );

        void meanLifetimechanged( Event event );
    }

    static public class ChangeListenerAdapter implements Listener {
        public void energyLevelChanged( Event event ) {
        }

        public void meanLifetimechanged( Event event ) {
        }
    }

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }
}

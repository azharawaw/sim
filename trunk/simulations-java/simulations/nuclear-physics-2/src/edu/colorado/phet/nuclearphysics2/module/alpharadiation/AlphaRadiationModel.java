/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Radiation within this sim.
 *
 * @author John Blanco
 */
public class AlphaRadiationModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    public static final double BREAKOUT_RADIUS = 11.0; // In femtometers, but not a realistic value.
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private AtomicNucleus _atomicNucleus;
    private ConstantDtClock _clock;
    private int _tickCounter = 0;
    private ArrayList _listeners = new ArrayList();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationModel(NuclearPhysics2Clock clock)
    {
        // Since Polonium 211 has a half life of about 1/2 seconds, we need
        // to set the clock to run such that wall time and sim time are
        // roughly equivalent.
        clock.setDelay( 40 ); // This gives us a frame rate of 25 fps.
        clock.setDt( 40 );    // This makes sim time match wall time.
        
        // Create a nucleus with an atomic weight of 211, which is Polonium.
        _atomicNucleus = new AtomicNucleus(0, 0, 211);
        
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                
                _tickCounter++;
                
                // Let the nucleus know that the clock ticked so that it can 'agitate'.
                _atomicNucleus.clockTicked(clockEvent);
                
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                _tickCounter = 0;
                _atomicNucleus.reset();
            }
        });
        
        // Start the clock.
        clock.start();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * Get a reference to the nucleus, of which there is only one in this
     * model.
     * 
     * @return - Reference to the nucleus model element.
     */
    public AtomicNucleus getAtomNucleus()
    {
        return _atomicNucleus;
    }
    
    /**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        assert !_listeners.contains( listener );
        
        _listeners.add( listener );
    }

    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    /**
     * This listener interface allows listeners to get notified when an alpha
     * particle is added (i.e. come in to existence by separating from the
     * nucleus) or is removed (i.e. recombines with the nucleus).
     */
    public static interface Listener {
        /**
         * This informs the listener that an alpha particle has been added
         * to the model.
         * 
         * @param alphaParticle - Reference to the newly added particle.
         */
        public void particleAdded(AlphaParticle alphaParticle);
        
        /**
         * This is invoked when a particle is removed from the model.
         * 
         * @param alphaParticle - Reference to the particle that was removed.
         */
        public void particleRemoved(AlphaParticle alphaParticle);
    }
}

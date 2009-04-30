/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.atomicinteractions;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.AtomicInteractionDefaults;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;

/**
 * This class is where the model and view classes for the "Interaction
 * Potential" tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class AtomicInteractionsModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private DualAtomModel m_model;
    private AtomicInteractionsCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AtomicInteractionsModule( Frame parentFrame, boolean enableHeterogeneousAtoms ) {
        
        super(StatesOfMatterStrings.TITLE_INTERACTION_POTENTIAL_MODULE, 
                new ConstantDtClock(AtomicInteractionDefaults.CLOCK_FRAME_DELAY, 
                AtomicInteractionDefaults.CLOCK_DT));

        // Model
        m_model = new DualAtomModel( getClock() );

        // Canvas
        m_canvas = new AtomicInteractionsCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new AtomicInteractionsControlPanel( this, parentFrame, enableHeterogeneousAtoms ) );
        
        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }
    
    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public DualAtomModel getDualParticleModel(){
        return m_model;
    }
    
    public AtomicInteractionsCanvas getCanvas(){
        return m_canvas;
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock, which ultimately resets the model too.
        getClock().resetSimulationTime();
        setClockRunningWhenActive( AtomicInteractionDefaults.CLOCK_RUNNING );
    }
}

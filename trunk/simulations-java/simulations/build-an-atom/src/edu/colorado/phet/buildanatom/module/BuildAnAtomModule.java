/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.module;

import java.awt.Frame;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.view.BuildAnAtomCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Build Atom tab.
 */
public class BuildAnAtomModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final BuildAnAtomModel model;
    private final BuildAnAtomCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomModule( Frame parentFrame ) {
        super( BuildAnAtomStrings.TITLE_BUILD_ATOM_MODULE, new BuildAnAtomClock( BuildAnAtomDefaults.CLOCK_FRAME_RATE, BuildAnAtomDefaults.CLOCK_DT ) );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new BuildAnAtomModel( clock );

        // Canvas
        canvas = new BuildAnAtomCanvas( model );
        setSimulationPanel( canvas );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    @Override
    public void reset() {

        // reset the clock
        BuildAnAtomClock clock = model.getClock();
        clock.resetSimulationTime();

        // reset the model
        model.reset();
    }
}

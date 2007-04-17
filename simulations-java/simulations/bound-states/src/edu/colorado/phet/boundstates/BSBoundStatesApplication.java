/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

/**
 * BSBoundStatesApplication is the simulation titled "Bound States".
 * It has all 3 modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSBoundStatesApplication extends BSAbstractApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSBoundStatesApplication( PhetApplicationConfig config ) {
        super( config );
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractApplication implementation
    //----------------------------------------------------------------------------
    
    /*
     * Initializes modules.
     */
    protected void initModules() {
        addOneWellModule();
        addTwoWellsModule();
        addManyWellsModule();
        getOneWellModule().setHasWiggleMe( true );
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     * 
     * @param args command line arguments
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main( final String[] args ) {

        /* 
         * Wrap the body of main in invokeLater, so that all initialization occurs 
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the 
         * event dispatch thread. Since we don't have an easy way to separate Swing and 
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                // Initialize look-and-feel
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.initLookAndFeel();
                
                // Config
                PhetApplicationConfig config = new PhetApplicationConfig( args, BSConstants.FRAME_SETUP, BSResources.getResourceLoader(), BSConstants.FLAVOR_BOUND_STATES );

                // Create the application.
                BSAbstractApplication app = new BSBoundStatesApplication( config );
                
                // Start the application.
                app.startApplication();
            }
        } );
    }
}

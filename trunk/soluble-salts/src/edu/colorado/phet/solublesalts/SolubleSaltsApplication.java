/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
//import edu.colorado.phet.common.model.clock.IClock;
//import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.control.OptionsMenu;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.util.PMouseTracker;
//import edu.colorado.phet.piccolo.PhetPCanvas;
//import edu.colorado.phet.piccolo.util.PMouseTracker;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * SolubleSaltsApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsApplication extends PhetApplication {


    private static IClock CLOCK = new SwingClock( 1000 / SolubleSaltsConfig.FPS, SolubleSaltsConfig.DT );

    public SolubleSaltsApplication( String[] args ) {
        super( args,
               SolubleSaltsConfig.TITLE,
               SolubleSaltsConfig.DESCRIPTION,
               SolubleSaltsConfig.VERSION,
               new FrameSetup.CenteredWithSize( 1000, 740 ) );

        Module module = new SolubleSaltsModule( CLOCK );
        setModules( new Module[]{module} );

        setUpOptionsMenu();
    }

    private void setUpOptionsMenu() {
        this.getPhetFrame().addMenu( new OptionsMenu( getPhetFrame() ));
    }

    public static void main( String[] args ) {

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( "-b" ) ) {
                IonGraphic.showBondIndicators( true );
            }
            if( arg.startsWith( "-w" )) {
                int d = Integer.parseInt( arg.substring( 3 ) );
                SolubleSaltsConfig.DEFAULT_WATER_LEVEL = d;
            }
            if( arg.equals( "-o" ) ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = true;
            }
            if( arg.substring( 0, 3).equals( "-s=" )) {
                double d = Double.parseDouble( arg.substring( 3 ));
                SolubleSaltsConfig.DEFAULT_LATTICE_STICK_LIKELIHOOD = d;
            }
            if( arg.substring( 0, 3).equals( "-d=" )) {
                double d = Double.parseDouble( arg.substring( 3 ));
                SolubleSaltsConfig.DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD = d;
            }
        }

        SimStrings.init( args, SolubleSaltsConfig.STRINGS_BUNDLE_NAME );
        PhetApplication app = new SolubleSaltsApplication( args );

        app.startApplication();


        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( "-m" ) ) {
                PhetPCanvas simPanel = (PhetPCanvas)app.getActiveModule().getSimulationPanel();
                if( simPanel != null ) {
//                    PMouseTracker mouseTracker = new PMouseTracker( simPanel );
//                    simPanel.addWorldChild( mouseTracker );
                }
            }

        }

    }

}

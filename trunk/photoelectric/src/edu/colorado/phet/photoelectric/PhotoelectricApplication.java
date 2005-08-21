/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * PhotoelectricApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricApplication extends PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private String resourceBundleName = "localization/PhotoelectricStrings";
    static private String title = "The Photoelectric Effect";
    static private String description = "An exploration of the photoelectric effect";
    static private String version = "0.01";
    static private FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 680 );

    // Clock specification
    public static final double DT = 25;
//    public static final double DT = 50;
//    public static final double DT = 12;
    public static final int FPS = 25;
    private JMenu optionsMenu;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------


    public PhotoelectricApplication( String[] args ) {
        super( args,
               title,
               description,
               version,
               new SwingTimerClock( DT, FPS, AbstractClock.FRAMES_PER_SECOND),
               true,
               frameSetup );

        optionsMenu = new JMenu( SimStrings.get("Menu.Options") );

        final PhotoelectricModule photoelectricModule = new PhotoelectricModule( this );
        setModules(new Module[] { photoelectricModule } );

        final JCheckBoxMenuItem photonMI = new JCheckBoxMenuItem( "Show photons" );
        photonMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photoelectricModule.setPhotonViewEnabled( photonMI.isSelected() );
            }
        } );
        optionsMenu.add( photonMI );
        getPhetFrame().addMenu( optionsMenu );
    }

    public JMenu getOptionsMenu() {
        return optionsMenu;
    }


    public static void main( String[] args ) {
        SimStrings.init( args, resourceBundleName );
        new PhotoelectricApplication( args ).startApplication();
    }
}

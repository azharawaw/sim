/**
 * Class: EmfApplication
 * Package: edu.colorado.phet.emf
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.ClientPhetLookAndFeel;
import edu.colorado.phet.coreadditions.LecturePhetLookAndFeel;
import edu.colorado.phet.coreadditions.PhetLookAndFeel;

import java.util.Locale;
import java.util.logging.Logger;

public class EmfApplication {

    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 10;

    public static void main( String[] args ) {

        // Get a logger; the logger is automatically created if
        // it doesn't already exist
        Logger logger = Logger.getLogger( "edu.colorado.phet.PhetLogger" );

        // Web Start doesn't seem to let you specify a logging level. It
        // just logs everything.
//        ConsoleHandler logHandler = new ConsoleHandler();
//        logHandler.setLevel( Level.INFO );
//        logger.setLevel( Level.INFO );
//        logger.addHandler( logHandler );

        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey )) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            Locale.setDefault( new Locale( locale ));
        }

        SimStrings.setStrings( Config.localizedStringsPath );

        // Log a few message at different severity levels
        PhetLookAndFeel lookAndFeel = new ClientPhetLookAndFeel();
        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-p" ) ) {
                    lookAndFeel = new LecturePhetLookAndFeel();
                }
            }
        }

//        GuidedInquiry gi = null;
//        Script script = null;

//        AbstractClock clock = new SwingTimerClock( 1.5, 60, false );
        SwingTimerClock clock = new SwingTimerClock( 1, 20, true  );
        Module antennaModule = new EmfModule( clock );
        FrameSetup fs = new FrameSetup.CenteredWithSize( 1024, 768 );
        ApplicationModel appDescriptor = new ApplicationModel(
                SimStrings.get( "EmfApplication.title" ),
                SimStrings.get( "EmfApplication.description" ),
                SimStrings.get( "EmfApplication.version" ), fs );                   
        appDescriptor.setModule( antennaModule );
        appDescriptor.setInitialModule( antennaModule );
        appDescriptor.setClock( clock );
        appDescriptor.setName( "radiowaves" );

        PhetApplication application = new PhetApplication( appDescriptor );
//        PhetApplication application = new PhetApplication( appDescriptor, antennaModule,
//                                                           clock);
        PhetFrame frame = application.getApplicationView().getPhetFrame();
        PhetFrame.setDefaultLookAndFeelDecorated( true );
        frame.setIconImage( lookAndFeel.getSmallIconImage() );

        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-gi" ) ) {
                    try {
                        String giUrl = args[i + 1];
                        logger.info( "Loading gi: " + giUrl );
//                        GILoader giLoader = new GILoader();
//                        gi = giLoader.loadGI( giUrl );
//                        script = new Script( gi, application );
                    }
                    catch( Exception e ) {
                        logger.severe( "Error loading and instantiating gi" );
                    }
                }
            }
        }

        Runtime.getRuntime().gc();
        application.startApplication();
//        application.startApplication( antennaModule );

//        if( gi != null ) {
//            LaunchGuidedInquiryCmd lgiCmd = new LaunchGuidedInquiryCmd( application, script );
//            lgiCmd.doIt();
//        }
    }

}

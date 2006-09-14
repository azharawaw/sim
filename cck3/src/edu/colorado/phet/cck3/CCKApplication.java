package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.controls.LookAndFeelMenu;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */

public class CCKApplication extends PhetApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "localization/CCKStrings";

    static class CCKPhetGraphicModuleAdapter extends Module {
        private CCKModule cckModule;

        public CCKPhetGraphicModuleAdapter( String[]args ) throws IOException {
            super( "CCK-phetgraphics", new SwingClock( 30, 1 ) );
            cckModule = new CCKModule( args );
            setSimulationPanel( cckModule.getCCKApparatusPanel() );
            setControlPanel( cckModule.getControlPanel() );
        }
    }

    public static String getSubTitle( String[]args ) {
        return Arrays.asList( args ).contains( "-dynamics" ) ? ": DC + AC" : ": DC Only";
    }

    public CCKApplication( String[]args ) throws IOException {
        super( args, SimStrings.get( "CCK3Application.title" ) + getSubTitle( args ) + " (" + readVersion() + ")",
               SimStrings.get( "CCK3Application.description" ),
               SimStrings.get( "CCK3Application.version" ) );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        CCKPhetGraphicModuleAdapter phetGraphicsCCKModule = new CCKPhetGraphicModuleAdapter( args );
        CCKPiccoloModule cckPiccoloModule = new CCKPiccoloModule( args );
        Module[] modules = new Module[]{phetGraphicsCCKModule, cckPiccoloModule};
        setModules( modules );

        getPhetFrame().addMenu( new LookAndFeelMenu() );
//        getPhetFrame().addMenu( new OptionsMenu( th, cckModule ) );//todo options menu

//        this.cckModule.getApparatusPanel().addKeyListener( new CCKKeyListener( cckModule, new RepaintDebugGraphic( cckModule.getApparatusPanel(), clock ) ) );
//        if( debugMode ) {
//            application.getApplicationView().getPhetFrame().setLocation( 0, 0 );
//        }
//        clock.addClockTickListener( new ClockListener() {
//            public void clockTicked( IClock c, double dt ) {
//                cckModule.clockTickFinished();
//            }
//        } );
//        //todo this is buggy with user-set pause & play
//        application.getApplicationView().getPhetFrame().addWindowListener( new WindowAdapter() {
//            public void windowIconified( WindowEvent e ) {
//                clock.setPaused( true );
//            }
//
//            public void windowDeiconified( WindowEvent e ) {
//                clock.setPaused( false );
//            }
//        } );
    }

//    public void startApplication() {
//        super.startApplication();
//        cckModule.getApparatusPanel().requestFocus();
//        for( int i = 0; i < numModules(); i++ ) {
//            if( moduleAt( i )instanceof CCKModule ) {
//                CCKModule module = (CCKModule)moduleAt( i );
//                module.applicationStarted();
//            }
//        }
//    }

    private static String readVersion() {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "cck.version" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            return br.readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return "Version Not Found";
        }
    }

    public static void main( final String[] args ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    edu.colorado.phet.common_cck.view.util.SimStrings.init( args, CCKApplication_orig.localizedStringsPath );
//                    SimStrings.init( args, CCKApplication_orig.localizedStringsPath );
                    new CCKPhetLookAndFeel().initLookAndFeel();
                    try {
                        new CCKApplication( args ).startApplication();
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        System.out.println( "args = " + args );
    }
}

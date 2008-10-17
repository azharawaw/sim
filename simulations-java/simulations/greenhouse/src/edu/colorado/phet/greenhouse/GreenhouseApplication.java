/**
 * Class: GreenhouseApplication
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.AWTSplashWindow;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.greenhouse.coreadditions.MessageFormatter;
import edu.colorado.phet.greenhouse.coreadditions.clock.StaticClockModel;
import edu.colorado.phet.greenhouse.coreadditions.clock.SwingTimerClock;
import edu.colorado.phet.greenhouse.phetcommon.application.Module;
import edu.colorado.phet.greenhouse.phetcommon.application.PhetApplication;
import edu.colorado.phet.greenhouse.phetcommon.model.IClock;
import edu.colorado.phet.greenhouse.phetcommon.view.ApplicationDescriptor;
import edu.colorado.phet.greenhouse.phetcommon.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;

/**
 * General comments, issues:
 * I wrote this using real model coordinates and units. The origin is at the center of the earth, and the positive
 * y direction is up. Unfortunately, this has turned out to cause a host of issues.
 * <p/>
 * The snow in the ice age reflects photons, but is not really in the model. Instead I do a rough estimate of where
 * it is in the background image (in the view) and use that.
 */
public class GreenhouseApplication extends PhetApplication {

    private static PhetApplication s_application;
    private static SwingTimerClock clock;

    //todo: convert to proper use of PhetApplicationConfig for getting version
    private static final String VERSION = GreenhouseResources.getResourceLoader().getVersion().formatForTitleBar();

    public static SwingTimerClock getClock() {
        return clock;
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, Module[] modules, IClock iClock ) {
        super( applicationDescriptor, modules, iClock );
    }

    public GreenhouseApplication( ApplicationDescriptor applicationDescriptor, ApparatusPanelContainerFactory apparatusPanelContainerFactory, Module[] modules, IClock iClock ) {
        super( applicationDescriptor, apparatusPanelContainerFactory, modules, iClock );
    }

    public static void paintContentImmediately() {
        Container contentPane = s_application.getApplicationView().getPhetFrame().getContentPane();
        if ( contentPane instanceof JComponent ) {
            JComponent jComponent = (JComponent) contentPane;
            jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
        }
    }

    public static void main( String[] args ) {

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.setBackgroundColor( GreenhouseConfig.PANEL_BACKGROUND_COLOR );
                phetLookAndFeel.setTitledBorderFont( new PhetFont( Font.PLAIN, 12 ) );
                phetLookAndFeel.initLookAndFeel();

                JFrame window = new JFrame();
                AWTSplashWindow splashWindow = new AWTSplashWindow( window, GreenhouseResources.getString( "greenhouse.name" ) );
                splashWindow.setVisible( true );

                BaseGreenhouseModule greenhouseModule = new GreenhouseModule();
                BaseGreenhouseModule greenhouseModule2 = new GlassPaneModule();
                Module[] modules = new Module[]{
                        greenhouseModule,
                        greenhouseModule2
                };
                ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                        new String( GreenhouseResources.getString( "greenhouse.name" ) + " (" + VERSION + ")" ),
                        MessageFormatter.format( GreenhouseResources.getString( "greenhouse.description" ) ),
                        VERSION,
                        new FrameSetup.CenteredWithSize( 1024, 768 ) );
                clock = new SwingTimerClock( new StaticClockModel( 10, 30 ) );
                s_application = new PhetApplication( appDescriptor, modules, clock );
                s_application.getApplicationView().getPhetFrame().setResizable( false );
                s_application.startApplication( greenhouseModule );
                splashWindow.setVisible( false );
                paintContentImmediately();
                s_application.getApplicationView().getPhetFrame().addWindowFocusListener( new WindowFocusListener() {
                    public void windowGainedFocus( WindowEvent e ) {
                        paintContentImmediately();
                    }

                    public void windowLostFocus( WindowEvent e ) {
                    }
                } );
            }
        } );
    }
}

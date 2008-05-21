/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fitness;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.resources.DummyConstantStringTester;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.fitness.developer.DeveloperMenu;
import edu.colorado.phet.fitness.menu.OptionsMenu;
import edu.colorado.phet.fitness.module.fitness.FitnessModule;
import edu.colorado.phet.fitness.persistence.FitnessConfig;
import edu.colorado.phet.fitness.persistence.SimTemplateConfig;
import edu.colorado.phet.fitness.view.FitnessColorScheme;

public class FitnessApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FitnessModule fitnessModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager _persistenceManager;

    private static TabbedModulePanePiccolo _tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessApplication( PhetApplicationConfig config ) {
        super( config );
        initTabbedPane();
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the tabbed pane.
     */

    private void initTabbedPane() {

        // Create our own tabbed pane type so we can set the tab color
        TabbedPaneType tabbedPaneType = new TabbedPaneType() {
            public ITabbedModulePane createTabbedPane() {
                _tabbedModulePane = new TabbedModulePanePiccolo();
                _tabbedModulePane.setSelectedTabColor( FitnessConstants.SELECTED_TAB_COLOR );
                return _tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }

    /*
    * Initializes the modules.
    */
    private void initModules() {
        fitnessModule = new FitnessModule( getPhetFrame());
        addModule( fitnessModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( FitnessResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( FitnessResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( FitnessResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( FitnessResources.getChar( "menu.file.load.mnemonic", 'L' ) );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    load();
                }
            } );

            frame.addFileMenuItem( saveItem );
            frame.addFileMenuItem( loadItem );
            frame.addFileMenuSeparator();
        }

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        DeveloperMenu developerMenu = new DeveloperMenu( this );
        if ( developerMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
    }

    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------

    public void setSelectedTabColor( Color color ) {
        if ( _tabbedModulePane != null ) {
            _tabbedModulePane.setSelectedTabColor( color );
        }
    }

    public Color getSelectedTabColor() {
        Color color = Color.WHITE;
        if ( _tabbedModulePane != null ) {
            color = _tabbedModulePane.getSelectedTabColor();
        }
        return color;
    }

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public Color getControlPanelBackground() {
        return getModule( 0 ).getControlPanel().getBackground();
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */

    private void save() {

        SimTemplateConfig appConfig = new SimTemplateConfig();

        appConfig.setVersionString( getApplicationConfig().getVersion().toString() );
        appConfig.setVersionMajor( getApplicationConfig().getVersion().getMajor() );
        appConfig.setVersionMinor( getApplicationConfig().getVersion().getMinor() );
        appConfig.setVersionDev( getApplicationConfig().getVersion().getDev() );
        appConfig.setVersionRevision( getApplicationConfig().getVersion().getRevision() );

        FitnessConfig exampleConfig = fitnessModule.save();
        appConfig.setExampleConfig( exampleConfig );

        _persistenceManager.save( appConfig );
    }

    /*
     * Loads the simulation's configuration.
     */
    private void load() {

        Object object = _persistenceManager.load();
        if ( object != null ) {

            if ( object instanceof SimTemplateConfig ) {
                SimTemplateConfig appConfig = (SimTemplateConfig) object;

                FitnessConfig exampleConfig = appConfig.getExampleConfig();
                fitnessModule.load( exampleConfig );
            }
            else {
                String message = FitnessResources.getString( "message.notAConfigFile" );
                String title = FitnessResources.getString( "title.error" );
                DialogUtils.showErrorDialog( getPhetFrame(), message, title );
            }
        }
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
                DummyConstantStringTester.setTestScenario( new Locale( "ja" ), "\u30A8\u30CD\u30EB\u30AE\u30FC\u306E\u6642\u9593\u5909\u5316" );
                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.setFont( new PhetFont( 14, true ) );
                phetLookAndFeel.setBackgroundColor( FitnessColorScheme.getBackgroundColor() );
                phetLookAndFeel.setTextFieldBackgroundColor( Color.white );
                phetLookAndFeel.initLookAndFeel();
                PhetApplicationConfig config = new PhetApplicationConfig( args, FitnessConstants.FRAME_SETUP, FitnessResources.getResourceLoader() );

                // Create the application.
                FitnessApplication app = new FitnessApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }

    public void startApplication() {
        super.startApplication();
        fitnessModule.applicationStarted();
    }
}

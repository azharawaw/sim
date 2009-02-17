/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.dialogs.ErrorDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * JARLauncher provides functionality for running PhET simulations from double-clickable JAR files.
 * Sims are described in a top level properties file called jar-launcher.properties (generated by the build process).
 * <p>
 * The launch is performed like so:
 * 1. If there is a specified flavor, launch that flavor.
 * 2. Else if there is a single flavor, launch that flavor.
 * 3. Else display a GUI for picking and launching a flavor.
 * <p>
 * NOTE: The terms "sim" and "flavor" are used interchangeably throughout this implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 * @version $Revision$
 */
public class JARLauncher implements IProguardKeepClass {

    private static final String JAR_LAUNCHER_PROPERTIES_FILE_NAME = "jar-launcher.properties";
    public static final String FLAVOR_KEY = "flavor";
    public static final String LANGUAGE_KEY = "language";
    public static final String COUNTRY_KEY = "country";

    /**
     * Gets the properties file name (used by the build process).
     */
    public static String getPropertiesFileName() {
        return JAR_LAUNCHER_PROPERTIES_FILE_NAME;
    }
    
    //TODO: localization
    private static final String INSTRUCTIONS = "<html>This program contains {0} simulations.<br>Select the simulation that you wish to start:<br></html>";
    private static final String START_BUTTON = "Start";
    private static final String CANCEL_BUTTON = "Cancel";
    private static final String ERROR_TITLE = "Error";
    private static final String ERROR_FAILED_TO_LAUNCH = "Failed to launch JAR.";
    private static final String ERROR_NO_SIMS = "No simulations found in this JAR.";
    private static final String ERROR_SIM_NOT_FOUND = "Simulation {0} not found in this JAR.";
    
    public JARLauncher( String[] args ) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        
        // Read properties 
        Properties properties = readProperties( JAR_LAUNCHER_PROPERTIES_FILE_NAME );

        // Determine which sims are in this JAR
        SimulationInfo[] sims = getSimulations( properties, args );
        if ( sims.length == 0 ) {
            throw new RuntimeException( ERROR_NO_SIMS );
        }

        // Set the locale
        setLocaleForOfflineJARs( properties );

        // Launch a sim
        String mainFlavor = properties.getProperty( FLAVOR_KEY );
        if ( mainFlavor != null ) {
            // properties file specifies which sim to launch
            SimulationInfo sim = getFlavor( sims, mainFlavor );
            if ( sim != null ) {
                sim.launch();
            }
            else {
                Object[] fargs = { mainFlavor };
                throw new RuntimeException( MessageFormat.format( ERROR_SIM_NOT_FOUND, fargs ) );
            }
        }
        else if ( sims.length == 1 ) {
            // only one sim in this JAR
            sims[0].launch();
        }
        else {
            // ask the user
            JARLauncherGUI gui = new JARLauncherGUI( sims );
            SwingUtils.centerWindowOnScreen( gui );
            gui.setVisible( true );
        }
    }
    
    /*
     * Description of a simulations.
     * This information is gathered from the properties file.
     */
    private static class SimulationInfo {

        private final String flavor;
        private final String title;
        private final String mainClass;
        private final String[] args;

        public SimulationInfo( String flavor, String title, String mainClass, String[] args ) {
            this.flavor = flavor;
            this.title = title;
            this.mainClass = mainClass;
            this.args = args;
        }

        public String getTitle() {
            return title;
        }

        public String getMainClass() {
            return mainClass;
        }

        public void launch() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Class mainClass = Class.forName( getMainClass() );
            final Method main = mainClass.getMethod( "main", new Class[] { String[].class } );
            Thread thread = new Thread( new Runnable() {

                public void run() {
                    try {
                        main.invoke( null, new Object[] { args } );
                    }
                    catch ( IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                    catch ( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            } );
            thread.start();
        }

        public String getFlavor() {
            return flavor;
        }
    }

    /*
     * GUI, prompts the user for which sim to launch.
     */
    private static class JARLauncherGUI extends JFrame {

        private SimulationInfo selectedSim;

        /**
         * Constructor.
         *
         * @param sims an array containing information about all simulations in this project
         */
        public JARLauncherGUI( SimulationInfo[] sims ) {
            super();
            createUI( sims );
            setResizable( false );
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }

        /*
         * Creates the user interface.
         */
        private void createUI( SimulationInfo[] sims ) {

            JComponent inputPanel = createInputPanel( sims );
            JPanel actionsPanel = createActionsPanel();

            JPanel bottomPanel = new JPanel( new BorderLayout() );
            bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
            bottomPanel.add( actionsPanel, BorderLayout.CENTER );

            BorderLayout layout = new BorderLayout( 20, 20 );
            JPanel mainPanel = new JPanel( layout );
            mainPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
            mainPanel.add( inputPanel, BorderLayout.CENTER );
            mainPanel.add( bottomPanel, BorderLayout.SOUTH );

            getContentPane().add( mainPanel );
            pack();

            //Workaround for the case of many simulations
            if ( getHeight() > Toolkit.getDefaultToolkit().getScreenSize().height * 0.75 ) {
                setSize( getWidth(), (int) ( Toolkit.getDefaultToolkit().getScreenSize().height * 0.75 ) );
            }
        }

        /*
         * Creates dialog's input panel, which contains user controls.
         */
        private JComponent createInputPanel( final SimulationInfo[] sims ) {

            Object[] args = { new Integer( sims.length ) };
            JLabel instructions = new JLabel( MessageFormat.format( INSTRUCTIONS, args ) );

            JPanel inputPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
            inputPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( instructions, row++, column );

            ButtonGroup buttonGroup = new ButtonGroup();
            for ( int i = 0; i < sims.length; i++ ) {
                String title = sims[i].getTitle();
                if ( title == null || title.trim().length() == 0 ) {
                    title = sims[i].getMainClass().substring( sims[i].getMainClass().lastIndexOf( '.' ) + 1 );
                }
                JRadioButton radioButton = new JRadioButton( title, i == 0 );
                final int flavorIndex = i;
                radioButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        selectedSim = sims[flavorIndex];
                    }
                } );
                buttonGroup.add( radioButton );
                layout.addComponent( radioButton, row++, column );
            }
            selectedSim = sims[0];
            if ( sims.length > 10 ) {//workaround for case of many sims
                return new JScrollPane( inputPanel );
            }
            return inputPanel;
        }

        /*
         * Creates the dialog's actions panel.
         */
        protected JPanel createActionsPanel() {

            JButton startButton = new JButton( START_BUTTON );
            startButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    handleStart();
                }
            } );

            JButton cancelButton = new JButton( CANCEL_BUTTON );
            cancelButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    handleCancel();
                }
            } );

            final int rows = 1;
            final int columns = 1; /* same as number of buttons! */
            final int hgap = 5;
            final int vgap = 0;
            JPanel buttonPanel = new JPanel( new GridLayout( rows, columns, hgap, vgap ) );
            buttonPanel.add( startButton );
            buttonPanel.add( cancelButton );

            JPanel actionPanel = new JPanel( new FlowLayout() );
            actionPanel.add( buttonPanel );

            return actionPanel;
        }

        /*
         * Handles the "Cancel" button.
         * Closes the dialog and exits.
         */

        private void handleCancel() {
            dispose();
            System.exit( 0 );
        }

        /*
         * Handles the "Start" button.
         * Runs the selected simulation.
         */
        private void handleStart() {
            try {
                selectedSim.launch();
                dispose();
            }
            catch ( Exception e ) {
                showException( this, e );
            }
        }
    }

    /*
     * Reads the properties file.
     */
    private static Properties readProperties( String resourceName ) throws IOException {
        Properties properties = new Properties();
        URL resource = Thread.currentThread().getContextClassLoader().getResource( resourceName );
        if ( resource != null ) {
            properties.load( resource.openStream() );
        }
        else { // fallback plan, in case we're not running from a JAR file
            final File file = new File( resourceName );
            properties.load( new FileInputStream( file ) );
        }
        return properties;
    }
    
    private static SimulationInfo getFlavor( SimulationInfo[] sims, String mainFlavor ) {
        SimulationInfo sim = null;
        for ( int i = 0; i < sims.length; i++ ) {
            if ( sims[i].getFlavor().equals( mainFlavor ) ) {
                sim = sims[i];
            }
        }
        return sim;
    }

    /*
     * Reads the sims that are described by the properties.
     * Returns an array sorted by simulation title.
     */
    private static SimulationInfo[] getSimulations( Properties prop, String[] commandlineArgs ) {
        
        // gets a list of sim names
        String[] flavors = listFlavors( prop );
        
        // collect info for each sim
        ArrayList list = new ArrayList();
        for ( int i = 0; i < flavors.length; i++ ) {
            String flavor = flavors[i];
            SimulationInfo sim = getSimulation( prop, flavor, commandlineArgs );
            list.add( sim );
        }
        
        // sort the list by ascending sim title
        Collections.sort( list, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                SimulationInfo info1 = (SimulationInfo) o1;
                SimulationInfo info2 = (SimulationInfo) o2;
                return info1.getTitle().compareToIgnoreCase( info2.getTitle() );
            }
        } );
        
        // convert to array
        return (SimulationInfo[]) list.toArray( new SimulationInfo[list.size()] );
    }
    
    /*
     * Gets a list of the sim names that are identified in the properties.
     */
    private static String[] listFlavors( Properties prop ) {
        Enumeration names = prop.propertyNames();
        HashSet flavors = new HashSet();

        while ( names.hasMoreElements() ) {
            String name = (String) names.nextElement();
            if ( name.toLowerCase().startsWith( "project.flavor" ) ) {
                String suffix = name.substring( "project.flavor.".length() );
                int lastDot = suffix.indexOf( '.' );
                if ( lastDot >= 0 ) {
                    String flavor = suffix.substring( 0, lastDot );
                    flavors.add( flavor );
                }
            }
        }
        return (String[]) flavors.toArray( new String[flavors.size()] );
    }

    /*
     * Gets information for one sim.
     */
    private static SimulationInfo getSimulation( Properties prop, String flavor, String[] commandlineArgs ) {
        String mainClass = prop.getProperty( "project.flavor." + flavor + ".mainclass" );
        String title = getTitle( prop, flavor );
        String argsString = prop.getProperty( "project.flavor." + flavor + ".args" );
        String[] args = combineArgs( argsString, commandlineArgs );
        return new SimulationInfo( flavor, title, mainClass, args );
    }

    /*
     * Gets the title for one sim.
     */
    private static String getTitle( Properties prop, String flavor ) {
        //TODO: this assumes that sims and project names contain - as delimiter instead of _
        //the convention by PhetBuildAllSimJarTask is to create project_sim style names
        if ( new StringTokenizer( flavor, "_" ).countTokens() == 2 ) {
            StringTokenizer st = new StringTokenizer( flavor, "_" );
            String project = st.nextToken();
            String sim = st.nextToken();
            PhetResources resources = new PhetResources( project );
            return resources.getLocalizedString( sim + ".name" );
        }
        else {
            return prop.getProperty( "project.flavor." + flavor + ".title" );
        }
    }
    
    /*
     * Combines an args property value with the commandline args.
     * The args property is split up into individual args.
     */
    private static String[] combineArgs( String argsString, String[] commandlineArgs ) {
        // break up argsString into a collection of Strings
        StringTokenizer stringTokenizer = new StringTokenizer( argsString );
        ArrayList list = new ArrayList();
        while ( stringTokenizer.hasMoreTokens() ) {
            list.add( stringTokenizer.nextToken() );
        }
        // add the commandline args to the collection (do this last)
        list.addAll( Arrays.asList( commandlineArgs ) );
        // return an array
        return (String[]) list.toArray( new String[list.size()] );
    }

    /*
     * Looks for optional properties (language, country) that specify the locale.
     * If these properties are found, set System properties that will be read by the sim.
     */
    private static void setLocaleForOfflineJARs( Properties properties ) {
        String language = properties.getProperty( LANGUAGE_KEY );
        if ( language != null ) {
            language = language.trim();
            if ( !language.equals( "" ) ) {
                System.out.println( "JARLauncher: setting " + PhetResources.PROPERTY_JAVAWS_USER_LANGUAGE + "=" + language );
                System.setProperty( PhetResources.PROPERTY_JAVAWS_USER_LANGUAGE, language );

                // country (optional), ignored if language is not specified
                String country = properties.getProperty( COUNTRY_KEY );
                if ( country != null ) {
                    country = country.trim();
                    if ( !country.equals( "" ) ) {
                        System.out.println( "JARLauncher: setting " + PhetResources.PROPERTY_JAVAWS_USER_COUNTRY + "=" + country );
                        System.setProperty( PhetResources.PROPERTY_JAVAWS_USER_COUNTRY, country );
                    }
                }
            }
        }
    }
    
    /*
     * Displays an exception using the standard PhET error dialog.
     */
    private static void showException( Frame parent, Exception e ) {
        JDialog dialog = new ErrorDialog( parent, ERROR_TITLE, ERROR_FAILED_TO_LAUNCH, e );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setVisible( true );
    }

    public static void main( String args[] ) {
        try {
            new JARLauncher( args );
        }
        catch ( Exception e ) {
            showException( null, e );
        }
    }
}

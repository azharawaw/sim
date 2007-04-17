package edu.colorado.phet.rotation;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.jfreechartphet.test.TestDynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.test.TestDynamicJFreeChartNodeTree;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.rotation.tests.*;
import edu.colorado.phet.rotation.tests.combined.TestCombinedModelPlot2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:28:24 AM
 */

public class RotationApplication extends PhetApplication {
    public static final String TITLE = "Rotational Motion";
    public static final String DESCRIPTION = "Rotational Motion Simulation";
    private JMenu testMenu;
    private RotationModule rotationModule;

    public RotationApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, readVersion(), createFrameSetup() );
        rotationModule = new RotationModule();
        addModule( rotationModule );

        addTests();
    }

    private void addTests() {
        testMenu = new JMenu( "Tests" );
        testMenu.setMnemonic( 't' );
        getPhetFrame().addMenu( testMenu );
        addTest( "Dynamic Plot", new Runnable() {
            public void run() {
                TestDynamicJFreeChartNode.main( new String[0] );
            }
        } );
        addTest( "Dynamic Plot 2", new Runnable() {
            public void run() {
                TestDynamicJFreeChartNodeTree.main( new String[0] );
            }
        } );
        addTest( "Platform Graphics", new Runnable() {
            public void run() {
                TestPlatformNode.main( new String[0] );
            }
        } );
        addTest( "Graphs", new Runnable() {
            public void run() {
                TestGraphs.main( new String[0] );
            }
        } );
        addTest( "Combined Model Plot", new Runnable() {
            public void run() {
                TestCombinedModelPlot2.main( new String[0] );
            }
        } );
        addTest( "Graph Control", new Runnable() {
            public void run() {
                TestControlGraph.main( new String[0] );
            }
        } );
        addTest( "Graph Selection", new Runnable() {
            public void run() {
                TestGraphSelectionControl.main( new String[0] );
            }
        } );
        addTest( "Graph View", new Runnable() {
            public void run() {
                TestGraphSelectionView.main( new String[0] );
            }
        } );
        addTest( "Vector Control", new Runnable() {
            public void run() {
                TestShowVectorsControl.main( new String[0] );
            }
        } );
        addTest( "Symbol Key", new Runnable() {
            public void run() {
                TestSymbolKey.main( new String[0] );
            }
        } );
    }

    private void addTest( String name, final Runnable runnable ) {
        JMenuItem item = new JMenuItem( name );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runnable.run();
            }
        } );
        testMenu.add( item );
    }

    private static String readVersion() {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "sim.version" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            return br.readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return "Version Not Found";
        }
    }

    private static FrameSetup createFrameSetup() {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            return new FrameSetup.MaxExtent( new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, 700 ) );
        }
        else {
            return new FrameSetup.TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 );
        }
    }

    public void startApplication() {
        super.startApplication();
        rotationModule.getRotationSimulationPanel().requestFocus();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new RotationLookAndFeel().initLookAndFeel();
                new RotationApplication( args ).startApplication();
            }
        } );
    }

}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.Planet;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.umd.cs.piccolo.PNode;

/**
 * Module for Energy Skate Park Basics.
 *
 * @author Sam Reid
 */
public class EnergySkateParkBasicsModule extends AbstractEnergySkateParkModule {
    public static final double INSET = 5;
    public final ControlPanelNode energyGraphControlPanel;
    private final String PARABOLA_TRACK = "energy-skate-park/tracks/basics/parabola.esp";
    public static final Font CONTROL_FONT = new PhetFont( 15 );
    public static final Font TITLE_FONT = new PhetFont( Font.BOLD, 16 );

    //Flag to indicate that friction has been enabled
    public final BooleanProperty frictionEnabled = new BooleanProperty( false );

    //Flag to indicate whether the skater should stick to the track
    public final BooleanProperty stickToTrack = new BooleanProperty( true );

    //Property that indicates currently loaded track.
    public final Property<String> currentTrackFileName = new Property<String>( "" );

    private final ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    public EnergySkateParkBasicsModule( String name, final PhetFrame phetFrame, boolean splinesMovable, double floorFriction ) {
        super( name, phetFrame, new EnergySkateParkOptions(), splinesMovable, false, floorFriction, false /* hasZoomControls */ );

        //Don't allow users to apply rocket force with the keyboard
        energySkateParkSimulationPanel.setThrustEnabled( false );

        //Show the sky for the earth background
        new Planet.Earth().apply( this );

        //Control panels are floating not docked
        setControlPanel( null );

        //When the user changes the "stick to track" setting, enable/disable the roller coaster mode
        stickToTrack.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean stickToTrack ) {
                getEnergySkateParkModel().setRollerCoasterMode( stickToTrack );
            }
        } );

        //Add the energy graph control panel
        energyGraphControlPanel = new ViewControlPanel( this, energySkateParkSimulationPanel, barChartDialog );
        energySkateParkSimulationPanel.getRootNode().addChild( energyGraphControlPanel );

        //Move the legend out from behind the control panel
        energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
            public void apply() {
                energySkateParkSimulationPanel.getRootNode().getLegend().translate( -energyGraphControlPanel.getFullBounds().getWidth() - INSET, 0 );
            }
        } );
    }

    public void addResetListener( VoidFunction0 resetListener ) {
        resetListeners.add( resetListener );
    }

    //Load the specified track and set its roller coaster mode, used when the user presses different track selection buttons
    public void loadTrack( String filename, double xOffset ) {
        EnergySkateParkIO.open( filename, this );
        currentTrackFileName.set( filename );
        getEnergySkateParkModel().setRollerCoasterMode( stickToTrack.get() );

        //Move it so it sits on the ground with y=0
        adjustSplines( xOffset );

        //Initialize the skater beneath the track so the user has to move it to start the sim
        initSkater();
    }

    //Initialize the skater beneath the track so the user has to move it to start the sim
    public void initSkater() {
        getEnergySkateParkModel().getBody( 0 ).setPosition( 10, 0 );
        getEnergySkateParkModel().getBody( 0 ).setVelocity( 0, 0 );
        getEnergySkateParkModel().getBody( 0 ).setFrictionCoefficient( getCoefficientOfFriction() );
    }

    protected void loadDefaultTrack() {
        loadTrack( PARABOLA_TRACK, 0.0 );
    }

    //Move splines so they sit on the ground with y=0 and move by the specified x amount
    private void adjustSplines( double xOffset ) {
        System.out.println( "xOffset = " + xOffset );
        for ( int i = 0; i < getEnergySkateParkModel().getNumSplines(); i++ ) {
            EnergySkateParkSpline spline = getEnergySkateParkModel().getSpline( i );
            spline.translate( xOffset, -spline.getMinControlPointY() );
        }
    }

    //Show the reset all button below the bottom control panel
    public void addResetAllButton( final PNode parent ) {

        //Add the "Reset all" button
        energySkateParkSimulationPanel.getRootNode().addChild( new TextButtonNode( PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.button.resetAll" ) ) {{
            setFont( CONTROL_FONT );
            setBackground( Color.ORANGE );

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( energyGraphControlPanel.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, parent.getFullBounds().getMaxY() + INSET * 2 );
                }
            } );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reset();
                }
            } );
        }} );
    }

    //Show buttons that allows the user to choose different tracks
    public void addTrackSelectionControlPanel() {
        final ControlPanelNode trackSelectionNode = new ControlPanelNode( new VBox(
                new TrackButton( this, PARABOLA_TRACK, 0.0 ),
                new TrackButton( this, "energy-skate-park/tracks/basics/to-the-floor.esp", 0.0 ),

                //Move the double-well to the right so it will have its lowest peak directly on the 6 meter mark for the grid
                new TrackButton( this, "energy-skate-park/tracks/basics/double-well.esp", 6.0 - 5.88033509545687 )
        ), EnergySkateParkLookAndFeel.backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( INSET, INSET );
                }
            } );
        }};
        energySkateParkSimulationPanel.getRootNode().addChild( trackSelectionNode );
    }

    @Override public void reset() {
        super.reset();

        frictionEnabled.reset();
        stickToTrack.reset();

        for ( VoidFunction0 resetListener : resetListeners ) {
            resetListener.apply();
        }

        loadDefaultTrack();
    }
}
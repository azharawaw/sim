/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.fitness.FitnessApplication;
import edu.colorado.phet.fitness.FitnessConstants;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.defaults.ExampleDefaults;
import edu.colorado.phet.fitness.model.FitnessUnits;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.persistence.FitnessConfig;

public class FitnessModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FitnessModel _model;
    private FitnessCanvas _canvas;
    //    private FitnessControlPanel _controlPanel;
    private ClockControlPanel _clockControlPanel;
    private JFrame parentFrame;
    private boolean inited = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessModule( final JFrame parentFrame ) {
        super( FitnessStrings.TITLE_FITNESS_MODULE, new FitnessClock(), FitnessDefaults.STARTS_PAUSED );
        this.parentFrame = parentFrame;

        // Model
        FitnessClock clock1 = (FitnessClock) getClock();
        _model = new FitnessModel( clock1 );

        // Canvas
        _canvas = new FitnessCanvas( _model, parentFrame );
        setSimulationPanel( _canvas );

        // Control Panel
//        _controlPanel = new FitnessControlPanel( this, parentFrame );
        setControlPanel( null );
        setLogoPanelVisible( false );

        // Clock controls
        _clockControlPanel = new ClockControlPanel( getClock() ) {
            public void setTimeDisplay( double time ) {
                super.setTimeDisplay( FitnessUnits.secondsToYears( time ) );
            }
        };
        _clockControlPanel.setRestartButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( FitnessStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        _clockControlPanel.setRestartButtonVisible( false );
        _clockControlPanel.setStepButtonText( "Next Month" );
        _clockControlPanel.setTimeFormat( "0.0" );
        JButton button = new JButton( "Reset All" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _model.resetAll();
                _canvas.resetAll();
            }
        } );

        JButton disclaimerButton = new JButton( "Disclaimer" );
        disclaimerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( parentFrame, FitnessStrings.DISCLAIMER );
            }
        } );
        _clockControlPanel.add( disclaimerButton, 0 );
        _clockControlPanel.add( Box.createHorizontalStrut( 100 ), 1 );
        _clockControlPanel.add( button, 2 );

//        JComponent timeSpeedSlider = createTimeSpeedSlider();

//        _clockControlPanel.addBetweenTimeDisplayAndButtons( timeSpeedSlider );
        setClockControlPanel( _clockControlPanel );

        // Controller
//        FitnessController controller = new FitnessController( _model, _canvas, _controlPanel );

        // Help
//        if ( hasHelp() ) {
        //XXX add help items
//            HelpItem

//        }

//        addWiggleMe();
        // Set initial state
        setHelpEnabled( true );
        reset();
    }

    public void activate() {
        super.activate();
        if ( !inited ) {
            MotionHelpBalloon motionHelpBalloon = new DefaultWiggleMe( _canvas, "Start the simulation" );
            motionHelpBalloon.setArrowTailPosition( MotionHelpBalloon.BOTTOM_CENTER );
            motionHelpBalloon.animateTo( _clockControlPanel.getPlayPauseButton(), 15 );
            setHelpPane( new HelpPane( parentFrame ) );
            getDefaultHelpPane().add( motionHelpBalloon );
            inited = true;
        }
    }
//----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
//        activate();
//        setHelpEnabled( true );

        // Clock
        FitnessClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( FitnessDefaults.CLOCK_DT );
        setClockRunningWhenActive( !FitnessDefaults.STARTS_PAUSED );
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public FitnessConfig save() {

        FitnessConfig config = new FitnessConfig();

        // Module
        config.setActive( isActive() );

        // Clock
//        ConstantDtClock clock = _model.getClock();
//        config.setClockDt( clock.getDt() );
//        config.setClockRunning( getClockRunningWhenActive() );

        // FitnessModelElement
//        Human fitnessModelElement = _model.getHuman();
//        config.setFitnessModelElementPosition( fitnessModelElement.getPositionReference() );
//        config.setFitnessModelElementOrientation( fitnessModelElement.getOrientation() );

        // Control panel settings that are specific to the view
        //XXX

        return config;
    }

    public void load( FitnessConfig config ) {

        // Module
        if ( config.isActive() ) {
            FitnessApplication.instance().setActiveModule( this );
        }

        // Clock
//        ConstantDtClock clock = _model.getClock();
//        clock.setDt( config.getClockDt() );
//        setClockRunningWhenActive( config.isClockRunning() );

        // FitnessModelElement
//        FitnessModelElement fitnessModelElement = _model.getFitnessModelElement();
//        fitnessModelElement.setPosition( config.getFitnessModelElementPosition() );
//        fitnessModelElement.setOrientation( config.getFitnessModelElementOrientation() );

        // Control panel settings that are specific to the view
        //XXX

    }

    public Human getHuman() {
        return _model.getHuman();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                PhetApplicationConfig config = new PhetApplicationConfig( args, FitnessConstants.FRAME_SETUP, FitnessResources.getResourceLoader() );

                // Create the application.
                FitnessApplication app = new FitnessApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }

    public void applicationStarted() {
        _canvas.applicationStarted();
    }
}
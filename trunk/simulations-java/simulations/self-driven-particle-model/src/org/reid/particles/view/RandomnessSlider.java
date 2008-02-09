/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

import org.reid.particles.SelfDrivenParticleModelApplication;
import org.reid.particles.model.ParticleModel;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.unit1.Unit1;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 21, 2005
 * Time: 11:05:16 AM
 * Copyright (c) Aug 21, 2005 by Sam Reid
 */

public class RandomnessSlider extends ModelSlider {
    private ParticleModel particleModel;

    public RandomnessSlider( final ParticleModel particleModel ) {
        super( "Randomness", "radians", 0, Math.PI * 2, particleModel.getRandomness(), new DecimalFormat( "0.00" ) );
        this.particleModel = particleModel;
        setModelTicks( new double[]{0, Math.PI, Math.PI * 2} );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getParticleModel().setRandomness( getValue() );
            }
        } );
        getParticleModel().addListener( new ParticleModel.Adapter() {
            public void randomnessChanged() {
                setValue( getParticleModel().getRandomness() );
            }
        } );
    }

    private ParticleModel getParticleModel() {
        return particleModel;
    }

    //    final ModelSlider randomnessSlider = new ModelSlider( "Randomness", "radians", 0, Math.PI * 2, model.getAngleRandomness(), new DecimalFormat( "0.00" ) );
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
//        JPanel contentPane = new JPanel();
//        PhetPCanvas pane = new PhetPCanvas();
        SelfDrivenParticleModelApplication tutorialApplication = new SelfDrivenParticleModelApplication();
//        PhetPCanvas pane = new BasicTutorialCanvas( tutorialApplication, new Unit1( tutorialApplication ) );
        BasicTutorialCanvas pane = new BasicTutorialCanvas( tutorialApplication, new Unit1( tutorialApplication ) );
//        TutorialCanvas pane = new TutorialCanvas( );
        pane.setZoomEventHandler( new PZoomEventHandler() );
        pane.addChild( new PSwing( pane, new RandomnessSlider( new ParticleModel( 400, 400 ) ) ) );
        frame.setContentPane( pane );
        frame.pack();
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();

    }
}

/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import edu.colorado.phet.piccolo.pswing.PSwing;
import org.reid.particles.model.ParticleModel;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;
import org.reid.particles.view.NumberSliderPanel;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 10:11:09 PM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class AddRemoveParticles60 extends Page {
    private PSwing numberPanelGraphic;

    public AddRemoveParticles60( BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "I'll set the randomness to be 2 pi radians (completely random).  Now add many (30+) particles." +
                 "  It will be quite impressive" +
                 " when we reduce the randomness with this many particles." );

        NumberSliderPanel numberSliderPanel = new NumberSliderPanel( basicPage, 0, 50, 5,new int[]{0,10,20,30,40,50} );
        getParticleModel().addListener( new ParticleModel.Adapter() {
            public void particleCountChanged() {
                int number = getParticleModel().numParticles();
                System.out.println( "number = " + number );
                if( number >= 30 ) {
                    advance();
                }
            }
        } );
        numberPanelGraphic = new PSwing( basicPage, numberSliderPanel );
    }

    public void init() {
        super.init();
        getParticleModel().setRandomness( Math.PI * 2 );
        numberPanelGraphic.setOffset( getUniverseGraphic().getFullBounds().getMaxX(), getUniverseGraphic().getFullBounds().getCenterY() );
        addChild( numberPanelGraphic );
    }

    public void teardown() {
        super.teardown();
        removeChild( numberPanelGraphic );
    }
}

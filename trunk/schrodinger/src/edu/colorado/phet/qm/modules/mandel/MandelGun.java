/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.qm.controls.IntensitySlider;
import edu.colorado.phet.qm.controls.SRRWavelengthSlider;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.GunControlPanel;
import edu.colorado.phet.qm.view.gun.SRRWavelengthSliderComponent;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 9:33:23 PM
 * Copyright (c) Mar 2, 2006 by Sam Reid
 */

public class MandelGun extends PhetPNode {
    PImage pimage;
    private GunControlPanel gunControlPanel;

    public MandelGun( String image, SchrodingerPanel schrodingerPanel ) {
        pimage = PImageFactory.create( image );
        addChild( pimage );
        gunControlPanel = new GunControlPanel( schrodingerPanel );

        final IntensitySlider intensitySlider = new IntensitySlider( Color.blue, IntensitySlider.HORIZONTAL, new Dimension( 140, 30 ) );

        VerticalLayoutPanel vlp = new VerticalLayoutPanel();
        vlp.addFullWidth( intensitySlider );
        vlp.addFullWidth( new JLabel( "GunControl goes here" ) );
        final SRRWavelengthSlider wavelengthSliderGraphic = new SRRWavelengthSlider( schrodingerPanel );
        SRRWavelengthSliderComponent srrWavelengthSliderComponent = new SRRWavelengthSliderComponent( wavelengthSliderGraphic );
        vlp.addFullWidth( srrWavelengthSliderComponent );
        gunControlPanel.setGunControls( vlp );
        addChild( gunControlPanel.getPSwing() );

        wavelengthSliderGraphic.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                intensitySlider.setColor( wavelengthSliderGraphic.getVisibleColor() );
            }
        } );
//        addChild( new SRRWavelengthSlider( schrodingerPanel ));
    }
}

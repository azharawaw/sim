/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.umd.cs.piccolo.PCanvas;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 11:00:29 PM
 * Copyright (c) Jan 22, 2006 by Sam Reid
 */

public class SRRWavelengthSliderComponent extends PCanvas {
    private SRRWavelengthSlider wavelengthSliderGraphic;

    public SRRWavelengthSliderComponent() {
        this( new SRRWavelengthSlider() );
    }

    public SRRWavelengthSliderComponent( SRRWavelengthSlider wavelengthSliderGraphic ) {
        this.wavelengthSliderGraphic = wavelengthSliderGraphic;
        getLayer().addChild( wavelengthSliderGraphic );
        setBounds( 0, 0, 400, 400 );
        setPreferredSize( new Dimension( (int)wavelengthSliderGraphic.getFullBounds().getWidth(), (int)wavelengthSliderGraphic.getFullBounds().getHeight() ) );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        setOpaque( false );
        setBackground( new Color( 0, 0, 0, 0 ) );
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                System.out.println( "SRRWavelengthSliderComponent.mousePressed" );
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );

    }

    public void setOpaque( boolean isOpaque ) {
        super.setOpaque( isOpaque );
        wavelengthSliderGraphic.setOpaque( isOpaque );
    }

    public void addChangeListener( ChangeListener changeListener ) {
        wavelengthSliderGraphic.addChangeListener( changeListener );
    }

    public Color getColor() {
        return wavelengthSliderGraphic.getVisibleColor();
    }
}

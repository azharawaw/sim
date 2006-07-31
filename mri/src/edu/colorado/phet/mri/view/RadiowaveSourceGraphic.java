/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.EmRepSelector;
import edu.colorado.phet.mri.model.RadiowaveSource;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.model.PhotonSource;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * RadiowaveSourceGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RadiowaveSourceGraphic extends PNode {

    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private double panelDepth = 90;

    public RadiowaveSourceGraphic( final RadiowaveSource radiowaveSource, PhetPCanvas canvas, AbstractMriModule module ) {

        // todo: this line and variable is just for debugging
        double length = 700;
//        double length = 600;

        double w = 0;
        double h = 0;
        double x = 0;
        double y = 0;
        if( radiowaveSource.getOrientation() == RadiowaveSource.HORIZONTAL ) {
            w = length;
            h = panelDepth;
            x = radiowaveSource.getPosition().getX() - w / 2;
            y = radiowaveSource.getPosition().getY();
        }
        else if( radiowaveSource.getOrientation() == RadiowaveSource.VERTICAL ) {
            w = panelDepth;
            h = length;
            x = radiowaveSource.getPosition().getX();
            y = radiowaveSource.getPosition().getY() + h / 2;
        }
        setOffset( x, y );

        Rectangle2D box = new Rectangle2D.Double( 0, 0, length, h );
        PPath boxGraphic = new PPath( box );
        boxGraphic.setPaint( new Color( 80, 80, 80 ) );
        addChild( boxGraphic );

        // Frequency control
        Insets controlInsets = new Insets( 5, 5, 5, 5 );
        final ModelSlider freqCtrl = new ModelSlider( "Frequency",
                                                      "MHz",
                                                      MriConfig.MIN_FEQUENCY,
                                                      MriConfig.MAX_FEQUENCY,
                                                      MriConfig.MIN_FEQUENCY + ( MriConfig.MAX_FEQUENCY - MriConfig.MIN_FEQUENCY ) / 2,
                                                      new DecimalFormat( "0.0" ) );
        freqCtrl.setFont( font );
        freqCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setFrequency( freqCtrl.getValue() * MriConfig.FREQUENCY_UNIT );
            }
        } );
        radiowaveSource.setFrequency( freqCtrl.getValue() * MriConfig.FREQUENCY_UNIT );
        PSwing freqPSwing = new PSwing( canvas, freqCtrl );
        freqPSwing.setOffset( length - controlInsets.right - freqPSwing.getBounds().getWidth(),
                              controlInsets.top );
        addChild( freqPSwing );

        // Power control
        final ModelSlider powerCtrl = new ModelSlider( "Power",
                                                       "",
                                                       0,
                                                       MriConfig.MAX_POWER,
                                                       0,
                                                       new DecimalFormat( "0.0" ) );
        powerCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setPower( powerCtrl.getValue() );
            }
        } );
        powerCtrl.setValue( powerCtrl.getValue() );
        PSwing powerPSwing = new PSwing( canvas, powerCtrl );
        powerPSwing.setOffset( controlInsets.left, controlInsets.top );
        addChild( powerPSwing );

        // Controls for the photon/wave view choice
        EmRepSelector emRepSelector = new EmRepSelector( module );
        PSwing emRepPSwing = new PSwing( canvas, emRepSelector );
        emRepPSwing.setOffset( ( length - emRepPSwing.getBounds().getWidth() ) / 2,
                               panelDepth - controlInsets.bottom - emRepPSwing.getBounds().getHeight() );
        addChild( emRepPSwing );

        // Label
        PText title = new PText( "Radiowave\nSource" );
        title.setPaint( new Color( 0, 0, 0, 0 ) );
        title.setTextPaint( Color.white );
        title.setFont( font );
        title.setJustification( javax.swing.JLabel.CENTER_ALIGNMENT );
        title.setOffset( length / 2 - title.getBounds().getWidth() / 2, 10 );
        addChild( title );

        // Update the sliders if the radiowave source changes its state through some mechanism other than our
        // sliders
        radiowaveSource.addChangeListener( new PhotonSource.ChangeListener() {
            public void rateChangeOccurred( PhotonSource.ChangeEvent event ) {
                powerCtrl.setValue( ( (RadiowaveSource)event.getPhotonSource() ).getPower() );
            }

            public void wavelengthChanged( PhotonSource.ChangeEvent event ) {
                freqCtrl.setValue( PhysicsUtil.wavelengthToFrequency( event.getPhotonSource().getWavelength() ) );
            }
        } );
    }
}

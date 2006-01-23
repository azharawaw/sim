/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SingleParticleGunGraphic extends AbstractGunGraphic {
    private JButton fireOne;
    private GunParticle currentObject;
    private GunParticle[] gunItems;
    private AutoFire autoFire;
    private ImageIcon outIcon;
    private ImageIcon inIcon;
    private PhotonBeamParticle photonBeamParticle;
    protected final JCheckBox autoFireJCheckBox;
    private GunControlPanel gunControlPanel;

    public SingleParticleGunGraphic( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        fireOne = new JButton( "Fire" );
        fireOne.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        fireOne.setForeground( Color.red );
        fireOne.setMargin( new Insets( 2, 2, 2, 2 ) );

        addButtonEnableDisable();

        try {
            outIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/button-out-40.gif" ) );
            inIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/button-in-40.gif" ) );
            fireOne.setIcon( outIcon );
            fireOne.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    fireOne.setIcon( inIcon );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    fireOne.setIcon( outIcon );
                }
            }
        } );

        fireOne.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireOne.setEnabled( false );
                clearAndFire();
                fireOne.setIcon( outIcon );
                updateGunLocation();
                getSchrodingerPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    updateGunLocation();
                    getGunImageGraphic().translate( 0, 10 );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    updateGunLocation();
                }
            }
        } );

        autoFire = new AutoFire( this, schrodingerPanel.getIntensityDisplay() );
        autoFireJCheckBox = new AutoFireCheckBox( autoFire );

        this.gunControlPanel = createGunControlPanel();
        addChild( gunControlPanel.getPSwing() );

        setGunParticle( gunItems[0] );
    }

    private GunControlPanel createGunControlPanel() {
        GunControlPanel gunControlPanel = new GunControlPanel( getSchrodingerPanel() );
        gunControlPanel.setFillNone();
        gunControlPanel.add( fireOne );
        gunControlPanel.add( autoFireJCheckBox );

        return gunControlPanel;
    }

    protected void layoutChildren() {
        super.layoutChildren();
        gunControlPanel.getPSwing().setOffset( getGunImageGraphic().getWidth() - 10, getControlOffsetY() );
    }

    protected Point getGunLocation() {
        if( currentObject != null ) {
            return currentObject.getGunLocation();
        }
        else {
            return new Point();
        }
    }

    private boolean fireButtonEnabled() {
        return fireOne.isEnabled();
    }

    private void addButtonEnableDisable() {
        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double magnitude = getSchrodingerModule().getDiscreteModel().getWavefunction().getMagnitude();
                if( magnitude <= AutoFire.THRESHOLD ) {
                    if( !fireButtonEnabled() ) {
                        fireOne.setEnabled( true );
                    }
                }
                else {
                    if( fireButtonEnabled() ) {
                        fireOne.setEnabled( false );
                    }
                }
            }
        } );
    }

    public void clearAndFire() {
        clearWavefunction();
        fireParticle();
        fireOne.setEnabled( false );
    }

    private void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void fireParticle() {
        currentObject.fireParticle();
        notifyFireListeners();
    }

    public GunParticle getCurrentObject() {
        return currentObject;
    }

    public void addMomentumChangeListener( MomentumChangeListener momentumChangeListener ) {
        for( int i = 0; i < gunItems.length; i++ ) {
            gunItems[i].addMomentumChangeListerner( momentumChangeListener );
        }
    }

    protected void setGunParticle( GunParticle particle ) {
        if( particle != currentObject ) {
            getDiscreteModel().clearWavefunction();
            if( currentObject != null ) {
                currentObject.deactivate( this );
            }
            particle.activate( this );
            currentObject = particle;
        }
        updateGunLocation();
    }

    protected ImagePComboBox initComboBox() {
        Photon photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
        PhotonBeam photonBeam = new PhotonBeam( this, photon );
        photonBeamParticle = new PhotonBeamParticle( this, "Photons", photonBeam );

        gunItems = new GunParticle[]{
                photonBeamParticle,
                DefaultGunParticle.createElectron( this ),
                DefaultGunParticle.createNeutron( this ),
                DefaultGunParticle.createHelium( this ),
        };

        final ImagePComboBox imageComboBox = new ImagePComboBox( gunItems );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setGunParticle( gunItems[index] );
            }
        } );
        return imageComboBox;
    }

    protected void setGunControls( JComponent gunControls ) {
        gunControlPanel.setGunControls( gunControls );
    }

    public GunParticle[] getGunItems() {
        return gunItems;
    }

    public void reset() {
        photonBeamParticle.reset();
    }

    public Map getModelParameters() {
        Map sup = super.getModelParameters();
        sup.putAll( currentObject.getModelParameters() );
        return sup;
    }

    public boolean isFiring() {
        return currentObject.isFiring();
    }
}

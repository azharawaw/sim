/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.flourescent.model.*;
import edu.colorado.phet.flourescent.view.ElectronGraphic;
import edu.colorado.phet.flourescent.view.EnergyLevelMonitorPanel;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.LaserEnergyLevelMonitorPanel;
import edu.colorado.phet.lasers.view.EnergyLevelsDialog;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SingleAtomModule extends DischargeLampModule {
    private DischargeLampAtom atom;

    /**
     * Constructor
     *
     * @param clock
     */
    protected SingleAtomModule( String name, AbstractClock clock, int numEnergyLevels ) {
        super( name, clock );
        addAtom( getTube(), numEnergyLevels );

        // Make the area from which the cathode emits electrons very small
        super.getCathode().setLength( 1 );

        // Add module-specific controls
        addControls();
    }

    /**
     *
     */
    private void addControls() {

        // Add an energy level monitor panel. Note that the panel has a null layout, so we have to put it in a
        // panel that does have one, so it gets laid out properly
        final EnergyLevelMonitorPanel elmp = new EnergyLevelMonitorPanel( this, getClock(), atom.getStates(), 150, 300 );
        JPanel jp = new JPanel( );
        jp.add( elmp );
        getControlPanel().add( jp );

        // Add a button for firing a single electron. This also tells the energy level panel that if an
        // electron has been produced
        JButton singleShotBtn = new JButton( "Fire electron" );
        singleShotBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Electron electron = getCathode().produceElectron();
                if( electron != null ) {
                    elmp.addElectron( electron );
                }
            }
        } );
        getControlPanel().add( singleShotBtn );

        JCheckBox slowMotionCB = new JCheckBox( new AbstractAction( "Run in slow motion" ) {
            public void actionPerformed( ActionEvent e ) {
                JCheckBox cb = (JCheckBox)e.getSource();
                if( cb.isSelected() ) {
                    getClock().setDt( FluorescentLightsConfig.DT / 5 );
                }
                else {
                    getClock().setDt( FluorescentLightsConfig.DT );
                }
            }
        });
        getControlPanel().add( slowMotionCB );
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numEnergyLevels
     */
    private void addAtom( ResonatingCavity tube, int numEnergyLevels ) {
        Rectangle2D tubeBounds = tube.getBounds();
        atom = new DischargeLampAtom( (LaserModel)getModel(), numEnergyLevels );
        atom.setPosition( tubeBounds.getX() + tubeBounds.getWidth() / 2,
                          tubeBounds.getY() + tubeBounds.getHeight() / 2 );
        AtomGraphic atomGraphic = addAtom( atom );

        // Make the atom movable with the mouse within the bounds of the tube
        Rectangle2D atomBounds = new Rectangle2D.Double( tubeBounds.getMinX() + atom.getRadius(),
                                                         tubeBounds.getMinY() + atom.getRadius(),
                                                         tubeBounds.getWidth() - atom.getRadius() * 2,
                                                         tubeBounds.getHeight() - atom.getRadius() * 2 );
        atomGraphic.setIsMouseable( true, atomBounds );
        atomGraphic.setCursorHand();
    }
}

/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;


/**
 * AbstractModule is the base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param title the module title
     * @param clock the simulation clock
     */
    public OTAbstractModule( String title, IClock clock ) {
        super( title, clock );
        setLogoPanel( null );
    }

    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void resetAll();

    /**
     * Saves the module's configuration by writing it to a provided configuration object.
     *
     * @param appConfig
     */
    public abstract void save( OTConfig appConfig );

    /**
     * Loads the module's configuration by reading it from a provided configuration object.
     *
     * @param appConfig
     */
    public abstract void load( OTConfig appConfig );

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets this module's frame.
     *
     * @return JFrame
     */
    public JFrame getFrame() {
        return NonPiccoloPhetApplication.instance().getPhetFrame();
    }

    /**
     * Adds a listener to the module's clock.
     *
     * @param listener
     */
    public void addClockListener( ClockListener listener ) {
        getClock().addClockListener( listener );
    }

    /**
     * Removes a listener from the module's clock.
     *
     * @param listener
     */
    public void removeClockListener( ClockListener listener ) {
        getClock().removeClockListener( listener );
    }

    /**
     * Sets the background color of the control panel and clock control panel.
     *
     * @param color
     */
    public void setControlPanelBackground( Color color ) {

        Class[] excludedClasses = { JTextComponent.class };
        boolean processContentsOfExcludedContainers = false;

        Component controlPanel = getControlPanel();
        SwingUtils.setBackgroundDeep( controlPanel, color, excludedClasses, processContentsOfExcludedContainers );

        Component clockControlPanel = getClockControlPanel();
        SwingUtils.setBackgroundDeep( clockControlPanel, color, excludedClasses, processContentsOfExcludedContainers );
    }

    /**
     * Gets the background color of the control panel and clock control panel.
     *
     * @return Color
     */
    public Color getControlPanelBackground() {
        Component controlPanel = getControlPanel();
        return controlPanel.getBackground();
    }
}

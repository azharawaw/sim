/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.capacitorlab.CapacitorLabApplication;
import edu.colorado.phet.capacitorlab.module.CLModule;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Menu item for enabling/disabling the rendering of Shapes related to voltage measurement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltageShapesDebugMenuItem extends JCheckBoxMenuItem {

    public VoltageShapesDebugMenuItem( final CapacitorLabApplication app ) {
        super( "Show voltage measurement shapes" );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for ( Module module : app.getModules() ) {
                    if ( module instanceof CLModule ) {
                        ( (CLModule) module ).setVoltageShapesDebugEnabled( isSelected() );
                    }
                }
            }
        } );
    }
}

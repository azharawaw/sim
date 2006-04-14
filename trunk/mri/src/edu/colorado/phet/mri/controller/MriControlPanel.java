/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.view.BFieldGraphicPanel;
import edu.colorado.phet.mri.view.MonitorPanel;

import javax.swing.*;
import java.awt.*;

/**
 * MriControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriControlPanel extends ControlPanel {

    /**
     * Constructor
     *
     * @param module
     */
    public MriControlPanel( MriModuleA module ) {
        MriModel model = (MriModel)module.getModel();

        JComponent fadingMagnetsControl = new FadingMagnetControl( model );
        MonitorPanel monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );
        BFieldGraphicPanel bFieldGraphicPanel = new BFieldGraphicPanel( model.getLowerMagnet() );

        addControl( fadingMagnetsControl );
        addControl( monitorPanel );
        addControl( bFieldGraphicPanel );
        addControl( new PrecessionControl( model ) );
        addControl( new SpinDeterminationControl( model ) );
        addControl( new MonitorPanelRepControl( monitorPanel ) );
    }
}

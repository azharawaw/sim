/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import javax.swing.JLabel;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;


public class RutherfordAtomControlPanel extends AbstractControlPanel {

    public RutherfordAtomControlPanel( RutherfordAtomModule module ) {
        super( module );
        
        addControl( new JLabel( "Alpha Particle Properties:" ) );
    }

}

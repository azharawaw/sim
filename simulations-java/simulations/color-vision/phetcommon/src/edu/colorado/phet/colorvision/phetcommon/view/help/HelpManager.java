/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.colorvision.phetcommon.view.help;

import edu.colorado.phet.colorvision.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.colorvision.phetcommon.view.CompositeGraphic;

import java.awt.*;

/**
 * HelpManager
 *
 * @author ?
 * @version $Revision:14443 $
 */
public class HelpManager extends CompositeGraphic {
//public class HelpManager extends GraphicLayerSet {
    private static double HELP_LAYER = Double.POSITIVE_INFINITY;
    private int numHelpItems;

    public HelpManager() {
//        super( null );
    }

    public HelpManager( Component component ) {
//        super( component );
    }

//    public void setComponent( Component component ) {
//        super.setComponent( component );
//    }

    public void setHelpEnabled( ApparatusPanel apparatusPanel, boolean h ) {
        if( h ) {
            apparatusPanel.addGraphic( this, HELP_LAYER );
        }
        else {
            apparatusPanel.removeGraphic( this );
        }
        apparatusPanel.repaint();
    }

    public int getNumHelpItems() {
        return numHelpItems;
    }

}

package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Oct 3, 2004
 * Time: 8:35:49 PM
 */
public class GrabBagReadoutGraphic extends ReadoutGraphic {
    public GrabBagReadoutGraphic( CCKPhetgraphicsModule module, Branch branch, ModelViewTransform2D transform, ApparatusPanel apparatusPanel, DecimalFormat decimalFormat ) {
        super( module, branch, transform, apparatusPanel, decimalFormat );
        super.setVisible( false );
    }

    public void setVisible( boolean visible ) {
        //do nothing.
    }
}

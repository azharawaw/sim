// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2Node extends PComposite {

    public H2Node() {

        // atom nodes
        AtomNode atomLeft = new BigAtomNode( BCEColors.H );
        AtomNode atomRight = new BigAtomNode( BCEColors.H );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomRight );
        parentNode.addChild( atomLeft );

        // layout
        double x = 0;
        double y = 0;
        atomLeft.setOffset( x, y );
        x = atomLeft.getFullBoundsReference().getMaxX() + ( 0.25 * atomRight.getFullBoundsReference().getWidth() );
        y = atomLeft.getYOffset();
        atomRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}

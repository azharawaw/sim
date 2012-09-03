// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.controlpanel;

import fj.data.List;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;

import static fj.Ord.doubleOrd;

/**
 * Adds padding around a node to make it a standardized size.  Different than PadBoundsNode from piccolo-phet because that neither centers the node nor allows different
 * sizes in the x and y directions.
 *
 * @author Sam Reid
 */
public class PaddedNode extends PNode {
    public PaddedNode( final double maxIconWidth, final double maxIconHeight, final PNode icon ) {
        assert icon.getFullBoundsReference().getWidth() <= maxIconWidth && icon.getFullBoundsReference().getHeight() <= maxIconHeight;
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, maxIconWidth, maxIconHeight ) ) {{
            setVisible( false );
            setPickable( false );
            setChildrenPickable( false );
        }} );
        addChild( new ZeroOffsetNode( icon ) {{
            setOffset( maxIconWidth / 2 - icon.getFullBounds().getWidth() / 2, maxIconHeight / 2 - icon.getFullBounds().getHeight() / 2 );
        }} );
    }

    public PaddedNode( final Dimension2D maxSize, final PNode icon ) {
        this( maxSize.getWidth(), maxSize.getHeight(), icon );
    }

    public static Dimension2DDouble getMaxSize( final List<PNode> icons ) {
        return new Dimension2DDouble( icons.map( PhetPNode._fullWidth ).maximum( doubleOrd ), icons.map( PhetPNode._fullHeight ).maximum( doubleOrd ) );
    }
}
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PoolNode extends PNode {
    public PoolNode( ModelViewTransform2D transform2D, Pool pool ) {
        int x = 15;
        Color topColor = new Color( 105 - x, 227 - x, 253 - x, 158 );//must be transparent so objects can submerge
        Color bottomColor = new Color( 50 - x, 151 - x, 173 - x, 188 );
        double yBottom = transform2D.modelToViewYDouble( -pool.getHeight() );//fade color halfway down
        double yTop = transform2D.modelToViewYDouble( 0 );
        final Shape viewShape = transform2D.createTransformedShape( pool.getShape() );
        PhetPPath path = new PhetPPath( viewShape, new GradientPaint( 0, (float) yTop, topColor, 0, (float) yBottom, bottomColor ) );
        addChild( path );
        setPickable( false );
        setChildrenPickable( false );
    }
}

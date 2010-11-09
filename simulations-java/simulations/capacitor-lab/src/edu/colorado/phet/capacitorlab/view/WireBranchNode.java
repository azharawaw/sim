package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.WireBranch;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


public class WireBranchNode extends PComposite {
    
    private static final Stroke WIRE_STROKE = new BasicStroke( 1f );
    private static final Color WIRE_STROKE_COLOR = Color.BLACK;
    private static final Color WIRE_FILL_COLOR = Color.LIGHT_GRAY;

    public WireBranchNode( final WireBranch wireBranch, final ModelViewTransform mvt ) {
        
        final PPath pathNode = new PhetPPath( mvt.modelToView( wireBranch.getShape() ), WIRE_FILL_COLOR, WIRE_STROKE, WIRE_STROKE_COLOR );
        addChild( pathNode );
        
        wireBranch.addShapeObserver( new SimpleObserver() {
            public void update() {
                pathNode.setPathTo( mvt.modelToView( wireBranch.getShape() ) );
            }
        });
    }
}

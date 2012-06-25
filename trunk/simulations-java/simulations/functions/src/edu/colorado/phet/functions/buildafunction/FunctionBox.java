package edu.colorado.phet.functions.buildafunction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Picture on front of a function, not including inputs/outputs or 3d effect.
 *
 * @author Sam Reid
 */
public class FunctionBox extends PNode {

    public static final int DEFAULT_WIDTH = 150;
    public static final int DEFAULT_HEIGHT = 150;

    public FunctionBox() {
        addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, 20, 20 ), new Color( 255, 255, 255, 230 ), new BasicStroke( 2 ), Color.black ) );
    }
}
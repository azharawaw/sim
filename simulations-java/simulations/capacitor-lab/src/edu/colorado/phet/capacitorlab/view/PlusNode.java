/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;
import java.awt.Paint;

import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Plus sign, created using PPaths because PText("+") cannot be accurately centered.
 * Origin at geometric center.
 */
public class PlusNode extends PComposite {
    
    /**
     * Constructor
     * @param width width of the horizontal bar
     * @param height height of the horizontal bar
     * @param paint
     */
    public PlusNode( double width, double height, Paint paint ) {
        MinusNode horizontalNode = new MinusNode( width, height, Color.RED );
        addChild( horizontalNode );
        MinusNode verticalNode = new MinusNode( width, height, Color.RED );
        verticalNode.rotate( Math.PI / 2 );
        addChild( verticalNode );
    }
}

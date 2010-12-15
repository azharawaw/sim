/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * OH- molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OHMinusNode extends AbstractMoleculeNode {
    
    public OHMinusNode() {
        
        // attributes
        double diameterBig = 24;
        double diameterSmall = 14;
        Color color = ABSColors.OH_MINUS;
        Color hiliteColor = Color.WHITE;
        Stroke stroke = new BasicStroke( 0.5f );
        Color strokeColor = color.darker();

        // atom nodes
        SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
        SphericalNode atomSmall = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
        
        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmall );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX();
        y = atomBig.getFullBoundsReference().getCenterY() - ( 0.45 * atomSmall.getFullBoundsReference().getHeight() );
        atomSmall.setOffset( x, y );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}

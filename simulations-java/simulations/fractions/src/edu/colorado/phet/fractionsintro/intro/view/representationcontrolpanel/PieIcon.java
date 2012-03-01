// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * @author Sam Reid
 */
public class PieIcon extends ShapeIcon {
    public PieIcon( SettableProperty<Representation> chosenRepresentation, Color color ) {
        super( new ArrayList<Shape>(),
               new ArrayList<Shape>() {{
//                   add( new Arc2D.Double( 0, 0, DIM * 2, DIM * 2, 0, 360, Arc2D.PIE ) );
                   add( new Ellipse2D.Double( 0, 0, DIM * 2, DIM * 2 ) );
               }}, chosenRepresentation, Representation.PIE, color
        );
    }
}

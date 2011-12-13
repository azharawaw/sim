// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.PNode;

/**
 * Place in the center of the play area which shows the selected representation such as pies.
 *
 * @author Sam Reid
 */
public class RepresentationArea extends PNode {
    public RepresentationArea( Property<ChosenRepresentation> chosenRepresentation, Property<Integer> numerator, Property<Integer> denominator ) {
        addChild( new HorizontalBarChosenRepresentationNode( chosenRepresentation, numerator, denominator ) );
        addChild( new VerticalBarChosenRepresentationNode( chosenRepresentation, numerator, denominator ) {{
            setOffset( 0, -80 );
        }} );
        addChild( new GridFractionNode( chosenRepresentation, numerator, denominator ) {{
            setOffset( 0, -50 );
        }} );
        addChild( new PieSetFractionNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.PIE ) ) );
        addChild( new NumberLineNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.NUMBER_LINE ) ) {{
            setOffset( 10, 20 );
        }} );
    }
}
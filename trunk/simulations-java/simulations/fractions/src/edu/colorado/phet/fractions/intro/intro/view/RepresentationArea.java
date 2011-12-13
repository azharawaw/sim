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

        //Y offset values sampled with this inner class listener:
//        addInputEventListener( new PBasicInputEventHandler() {
//                        @Override public void mouseDragged( PInputEvent event ) {
//                            translate( 0, event.getDeltaRelativeTo( RepresentationArea.this.getParent() ).getHeight() );
//                            System.out.println( "offset y = " + getOffset().getY() );
//                        }
//                    } );

        addChild( new HorizontalBarChosenRepresentationNode( chosenRepresentation, numerator, denominator ) {{
            setOffset( 0, -29 );
        }} );
        addChild( new VerticalBarChosenRepresentationNode( chosenRepresentation, numerator, denominator ) {{
            setOffset( 0, -73 );
        }} );

        //Since it is unclear how to subdivide a single grid while keeping it the same size, we have discarded this representation for now.
//        addChild( new GridFractionNode( chosenRepresentation, numerator, denominator ) {{
//            setOffset( 0, -50 );
//        }} );
        addChild( new PieSetFractionNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.PIE ) ) {{
            setOffset( 0, -48 );//Sampled with
        }} );
        addChild( new NumberLineNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.NUMBER_LINE ) ) {{
            setOffset( 10, 15 );
        }} );

        addChild( new CakeSetFractionNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.CAKE ) ) {{
            setOffset( 0, 0 );
        }} );
    }
}
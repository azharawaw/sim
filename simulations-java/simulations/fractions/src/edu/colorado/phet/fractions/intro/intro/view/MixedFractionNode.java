// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that shows a fraction (numerator and denominator and dividing line) along with controls to change the values.
 * Layout is not normalized (top left is not 0,0)
 *
 * @author Sam Reid
 */
public class MixedFractionNode extends PNode {
    public MixedFractionNode( final Property<Integer> integer, final Property<Integer> numerator, final Property<Integer> denominator ) {
        final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 150, 0 ), new BasicStroke( 12 ), Color.black );
        addChild( line );

        addChild( new ZeroOffsetNode( new FractionNumberNode( integer ) ) {{
            setOffset( line.getFullBounds().getX() - getFullBounds().getWidth() - 10, line.getFullBounds().getY() - getFullBounds().getHeight() / 2 );
            integer.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer value ) {
                    setVisible( value != 0 );
                }
            } );
        }} );

        final ZeroOffsetNode den = new ZeroOffsetNode( new FractionNumberNode( numerator ) ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }};
        addChild( den );
        final ZeroOffsetNode num = new ZeroOffsetNode( new FractionNumberNode( denominator ) ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() );
        }};
        addChild( num );

        numerator.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer integer ) {
                line.setVisible( integer != 0 );
                den.setVisible( integer != 0 );
                num.setVisible( integer != 0 );
            }
        } );
    }
}
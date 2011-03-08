// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.Color;

import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This Piccolo node depicts the name, e.g. "Helium", of an atom.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ElementNameIndicator extends PNode {
    private final PText elementName;

    public ElementNameIndicator( final IDynamicAtom atom, final BooleanProperty showLabels, final boolean showMassNumber ) {
        elementName = new PText() {
            {
                setFont( new PhetFont( 18, true ) );
                setTextPaint( Color.red );
            }
        };
        addChild( elementName );

        final SimpleObserver update = new SimpleObserver() {
            public void update() {
                if ( atom.getNumProtons() > 0 ){
                    elementName.setText( atom.getName() + ( showMassNumber ? "-" + atom.getMassNumber() : "" ) );
                }
                else{
                    // Can't set to a 0-length string or it can mess up layout in canvas.
                    elementName.setText( " " );
                }
                elementName.setOffset( -elementName.getFullBoundsReference().width / 2, -elementName.getFullBoundsReference().height / 2 );
                elementName.setVisible( showLabels.getValue() );
            }
        };
        atom.addObserver( update );
        showLabels.addObserver( update );
        update.update();
    }
}

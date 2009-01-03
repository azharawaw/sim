package edu.colorado.phet.theramp.v2.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.theramp.v2.model.RampModel;
import edu.colorado.phet.theramp.v2.model.RampObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class RampModelView extends PNode {
    private TestRampModule module;

    public RampModelView( TestRampModule module ) {
        this.module = module;
        module.addListener( new TestRampModule.Listener() {
            public void notifyChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        boolean createNewNodes = true;
//        boolean createNewNodes = false;
        if ( createNewNodes ) {
            updateNewNodes();
        }
        else {
            updateOldNodes();
        }

    }

    private void updateNewNodes() {
        removeAllChildren();
        RampModel state = module.getCurrentState();
        for ( int i = 0; i < state.getObjectCount(); i++ ) {
            addChild( createNode( state.getObject( i ) ) );
        }
    }

    private void updateOldNodes() {

        if ( getChildrenCount() == 0 ) {
            updateNewNodes();
        }
        else {
            getChild( 0 ).translate( 1, 1 );
        }
    }

    private PNode createNode( RampObject object ) {
        Rectangle2D.Double aDouble = new Rectangle2D.Double( object.getPosition().getX(), object.getPosition().getY(), 10, 10 );
//        System.out.println( "aDouble = " + aDouble );
        return new PPath( aDouble );
    }
}

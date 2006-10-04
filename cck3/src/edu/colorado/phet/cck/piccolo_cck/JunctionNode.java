package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.common.DynamicPopupMenuHandler;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 15, 2006
 * Time: 9:40:58 PM
 * Copyright (c) Sep 15, 2006 by Sam Reid
 */

public class JunctionNode extends PhetPNode {
    private double strokeWidthModelCoords = CCKModel.JUNCTION_GRAPHIC_STROKE_WIDTH;
    private Stroke shapeStroke = new BasicStroke( 2 );
    private CCKModel cckModel;
    private Junction junction;
    private CircuitNode circuitNode;
    private Component component;
    private PPath shapePNode;
    private PPath highlightPNode;
    private CircuitInteractionModel circuitInteractionModel;

    public JunctionNode( final CCKModel cckModel, final Junction junction, final CircuitNode circuitNode, Component component ) {
        this.cckModel = cckModel;
        this.junction = junction;
        this.circuitNode = circuitNode;
        this.component = component;
        this.circuitInteractionModel = new CircuitInteractionModel( getCircuit() );
        shapePNode = new PPath();
        shapePNode.setStroke( shapeStroke );
        highlightPNode = new PPath();
        highlightPNode.setStroke( new BasicStroke( (float)( 3 / 80.0 ) ) );
        highlightPNode.setStrokePaint( Color.yellow );

        addChild( shapePNode );
        addChild( highlightPNode );

        junction.addObserver( new SimpleObserver() {
            public void update() {
                JunctionNode.this.update();
            }
        } );
        shapePNode.setStrokePaint( Color.red );
        shapePNode.setPaint( new Color( 0, 0, 0, 0 ) );
        addInputEventListener( new PBasicInputEventHandler() {

            public void mouseDragged( PInputEvent event ) {
                Point2D pt = event.getPositionRelativeTo( JunctionNode.this );
                Point2D target = new Point2D.Double( pt.getX(), pt.getY() );
                circuitInteractionModel.dragJunction( junction, target );
            }

            public void mousePressed( PInputEvent event ) {
                getCircuit().setSelection( junction );
            }

            public void mouseReleased( PInputEvent event ) {
                circuitInteractionModel.dropJunction( junction );
            }
        } );
        addInputEventListener( new DynamicPopupMenuHandler( component, new DynamicPopupMenuHandler.JPopupMenuFactory() {
            public JPopupMenu createPopupMenu() {
                return new JunctionNodePopupMenu( cckModel, junction );
            }
        } ) );
        addInputEventListener( new CursorHandler() );
        update();
        circuitNode.getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                update();
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
                update();
            }

            public void branchRemoved( Branch branch ) {
                update();
            }

            public void junctionRemoved( Junction junction ) {
                update();
            }
        } );
    }

    private Stroke createStroke( double strokeWidth ) {
        float scale = (float)80.0;
        float[] dash = new float[]{3 / scale, 6 / scale};
        return (Stroke)new BasicStroke( (float)strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.CAP_BUTT, 3, dash, 0 );
    }

    private void update() {
        shapePNode.setPathTo( junction.getShape() );
        shapePNode.setStroke( createStroke( strokeWidthModelCoords * ( isConnected() ? 1.2 : 2 ) ) );
        shapePNode.setStrokePaint( isConnected() ? Color.black : Color.red );

        highlightPNode.setPathTo( junction.createCircle( CCKModel.JUNCTION_RADIUS * 1.6 ) );
        highlightPNode.setStroke( new BasicStroke( (float)( 3.0 / 80.0 ) ) );
        highlightPNode.setVisible( junction.isSelected() );
    }

    private boolean isConnected() {
        return getCircuit().getNeighbors( junction ).length > 1;
    }

    public Junction getJunction() {
        return junction;
    }

    private Circuit getCircuit() {
        return cckModel.getCircuit();
    }
}

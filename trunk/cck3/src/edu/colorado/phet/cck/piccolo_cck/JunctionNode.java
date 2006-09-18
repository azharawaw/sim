package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
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
    private PPath shapePNode;
    private PPath highlightPNode;
    private CircuitInteractionModel circuitInteractionModel;

    public JunctionNode( final CCKModel cckModel, final Junction junction, final CircuitNode circuitNode ) {
        this.cckModel = cckModel;
        this.junction = junction;
        this.circuitNode = circuitNode;
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

        addInputEventListener( new CursorHandler() );
        update();
    }

    private Stroke createStroke( double strokeWidth ) {
        float scale = (float)80.0;
        float[] dash = new float[]{3 / scale, 6 / scale};
        return (Stroke)new BasicStroke( (float)strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.CAP_BUTT, 3, dash, 0 );
    }

    private void update() {
        shapePNode.setPathTo( createCircle( CCKModel.JUNCTION_RADIUS * 1.1 ) );
        shapePNode.setStroke( createStroke( strokeWidthModelCoords * 2 ) );

        highlightPNode.setPathTo( createCircle( CCKModel.JUNCTION_RADIUS * 1.6 ) );
        highlightPNode.setStroke( new BasicStroke( (float)( 3.0 / 80.0 ) ) );
        highlightPNode.setVisible( junction.isSelected() );
    }

    private Ellipse2D createCircle( double radius ) {
        Ellipse2D.Double circle = new Ellipse2D.Double();
        circle.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius, junction.getY() + radius );
        return circle;
    }

    Junction getJunction() {
        return junction;
    }

    private Circuit getCircuit() {
        return cckModel.getCircuit();
    }
}

/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 2:05:05 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class JunctionGraphic extends CompositePhetGraphic {
    private PhetShapeGraphic shapeGraphic;
    private Junction junction;
    private ModelViewTransform2D transform;
    private double radius;
    private Circuit circuit;
    private double strokeWidthModelCoords = CCK3Module.JUNCTION_GRAPHIC_STROKE_WIDTH;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private PhetShapeGraphic highlightGraphic;

    public JunctionGraphic( Component parent, Junction junction, ModelViewTransform2D transform, double radius, Circuit circuit ) {
        super( parent );

        shapeGraphic = new PhetShapeGraphic( parent, new Area(), new BasicStroke( 2 ), Color.black );
        highlightGraphic = new PhetShapeGraphic( parent, new Area(), new BasicStroke( 4 ), Color.yellow );
        addGraphic( highlightGraphic );
        addGraphic( shapeGraphic );
        this.junction = junction;
        this.transform = transform;
        this.radius = radius;
        this.circuit = circuit;
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        junction.addObserver( simpleObserver );
        circuit.addCircuitListener( new CircuitListener() {
            public void junctionRemoved( Junction junction ) {
                changed();
            }

            public void branchRemoved( Branch branch ) {
                changed();
            }

            public void junctionsMoved() {
            }

            public void branchesMoved( Branch[] branches ) {
            }

            public void junctionAdded( Junction junction ) {
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
            }

        } );

        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );


        changed();
        setVisible( true );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        highlightGraphic.setVisible( visible && junction.isSelected() );
    }

    public double getRadius() {
        return radius;
    }

    private Stroke createStroke( double strokeWidth ) {
        float[] dash = new float[]{3, 6};
        float strokeWidthView = (float)transform.getAffineTransform().transform( new Point2D.Double( strokeWidth, 0 ), null ).getX();
        Stroke s = new BasicStroke( strokeWidthView, BasicStroke.CAP_SQUARE, BasicStroke.CAP_BUTT, 3, dash, 0 );
        return s;
    }

    private void changed() {
        boolean selected = junction.isSelected();
        highlightGraphic.setVisible( selected );
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        ellipse.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius, junction.getY() + radius );
        shapeGraphic.setShape( transform.createTransformedShape( ellipse ) );

        Ellipse2D.Double highlightEllipse = new Ellipse2D.Double();
        highlightEllipse.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius * 1.33, junction.getY() + radius * 1.33 );
        highlightGraphic.setShape( transform.createTransformedShape( highlightEllipse ) );

        int numConnections = circuit.getNeighbors( getJunction() ).length;
        if( numConnections == 1 ) {
            shapeGraphic.setBorderColor( Color.red );//setOutlinePaint( Color.red );
            shapeGraphic.setStroke( createStroke( strokeWidthModelCoords * 1.5 ) );
        }
        else {
            shapeGraphic.setBorderColor( Color.black );
            shapeGraphic.setStroke( createStroke( strokeWidthModelCoords ) );
        }
    }

    public Junction getJunction() {
        return junction;
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public void delete() {
        junction.removeObserver( simpleObserver );
        transform.removeTransformListener( transformListener );
    }

    public Shape getShape() {
        return this.shapeGraphic.getShape();
    }
}

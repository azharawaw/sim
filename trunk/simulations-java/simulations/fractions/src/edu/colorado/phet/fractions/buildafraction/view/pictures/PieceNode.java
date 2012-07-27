// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.buildafraction.model.pictures.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * @author Sam Reid
 */
public abstract class PieceNode extends Stackable {
    public final Integer pieceSize;
    protected double initialScale = Double.NaN;
    protected final PieceContext context;
    protected PhetPPath pathNode;
    public static final BasicStroke stroke = new BasicStroke( 2 );
    double pieceRotation = 0.0;

    public PieceNode( final Integer pieceSize, final PieceContext context, PhetPPath pathNode, final ShapeType shapeType ) {
        this.pieceSize = pieceSize;
        this.context = context;
        this.pathNode = pathNode;
    }

    protected void installInputListeners() {
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );

                dragStarted();
                PieceNode.this.moveToFront();
                setPositionInStack( Option.<Integer>none() );
                final AnimateToScale activity = new AnimateToScale( PieceNode.this, 200 );
                addActivity( activity );

                activity.setDelegate( new PActivityDelegate() {
                    public void activityStarted( final PActivity activity ) {
                    }

                    public void activityStepped( final PActivity activity ) {
                        stepTowardMouse( event );
                    }

                    public void activityFinished( final PActivity activity ) {
                    }
                } );
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                Option<Double> originalAngle = context.getNextAngle( PieceNode.this );
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.width, delta.height );
                Option<Double> newAngle = context.getNextAngle( PieceNode.this );
                if ( originalAngle.isSome() && newAngle.isSome() && !originalAngle.some().equals( newAngle.some() ) ) {
                    rotateTo( newAngle.some(), event );
                }
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( PieceNode.this, event );

                //Protect against multiple copies accidentally being added
                dragEnded();
            }
        } );
    }

    protected void rotateTo( final double angle, final PInputEvent event ) {}

    protected void stepTowardMouse( final PInputEvent event ) { }

    protected void dragEnded() { }

    protected void dragStarted() { }

    protected double getAnimateToScale() { return this.initialScale; }

    protected AffineTransform getShadowOffset() {return getTranslateInstance( 6, 6 );}

    public void setInitialScale( double s ) {
        this.initialScale = s;
        setScale( s );
    }

    public Fraction toFraction() { return new Fraction( 1, pieceSize );}

    public static final F<PieceNode, Fraction> _toFraction = new F<PieceNode, Fraction>() {
        @Override public Fraction f( final PieceNode r ) {
            return r.toFraction();
        }
    };

    public void moveToTopOfStack() {
        stack.moveToTopOfStack( this );
    }

    //Show drop shadow when moving back to toolbox
    public PTransformActivity animateTo( Vector2D v ) {
        PTransformActivity a = super.animateTo( v );
        a.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
                showShadow();
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                hideShadow();
            }
        } );
        return a;
    }

    protected abstract void hideShadow();

    protected abstract void showShadow();
}
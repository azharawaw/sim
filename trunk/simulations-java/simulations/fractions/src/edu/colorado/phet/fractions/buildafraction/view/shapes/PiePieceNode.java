// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.data.Option;

import java.awt.Shape;

import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.circleDiameter;
import static java.lang.Math.PI;

/**
 * PieceNode for drawing a circular pie piece, which rotates as it is dragged.
 *
 * @author Sam Reid
 */
public class PiePieceNode extends PieceNode {

    //Shadow to be shown while dragging (and maybe animating).  Shown with add/remove child so it doesn't disrupt bounds/layout
    private final PhetPPath pieShadow;
    private final PNode pieBackground;
    private final int pieceDenominator;
    private final ShapeSceneNode shapeSceneNode;
    private final PhetPPath shape;

    public PiePieceNode( final int pieceDenominator, final ShapeSceneNode shapeSceneNode, final PhetPPath shape ) {
        super( pieceDenominator, shapeSceneNode, shape );
        this.pieceDenominator = pieceDenominator;
        this.shapeSceneNode = shapeSceneNode;
        this.shape = shape;
        pieBackground = new PNode() {{
            addChild( new PhetPPath( ContainerShapeNode.createPieSlice( 1 ), BuildAFractionCanvas.TRANSPARENT ) );
        }};
        addChild( new ZeroOffsetNode( pieBackground ) );
        pieShadow = new PhetPPath( getShadowOffset().createTransformedShape( this.pathNode.getPathReference() ), ShapeNode.SHADOW_PAINT );
        pieBackground.addChild( this.pathNode );

        installInputListeners();
    }

    @Override protected void dragStarted() {
        showShadow();
        final Option<Double> nextAngle = context.getNextAngle( this );
        if ( nextAngle.isSome() ) {
            animateToAngle( nextAngle.some() );
        }
    }

    protected void showShadow() {
        hideShadow();
        pieBackground.addChild( 0, pieShadow );
    }

    @SuppressWarnings("unchecked") @Override public PieceNode copy() {
        PiePieceNode copy = new PiePieceNode( pieceDenominator, shapeSceneNode, copy( shape ) );
        copy.setInitialScale( initialScale );
        copy.setStack( stack );
        copy.setPositionInStack( getPositionInStack() );
        stack.cards = stack.cards.snoc( copy );
        return copy;
    }

    public static PhetPPath copy( final PhetPPath shape ) {
        return new PhetPPath( shape.getPathReference(), shape.getPaint(), shape.getStroke(), shape.getStrokePaint() );
    }

    //If it moved closer to a different target site, update rotation.
    @Override protected void rotateTo( final double angle, final PInputEvent event ) {
        PActivity activity = animateToAngle( angle );
        activity.setDelegate( new PActivityDelegateAdapter() {
            public void activityStepped( final PActivity activity ) {
                stepTowardMouse( event );
            }
        } );
    }

    @Override protected void dragEnded() {
        hideShadow();
    }

    protected void hideShadow() {
        while ( pieBackground.getChildrenReference().contains( pieShadow ) ) {
            pieBackground.removeChild( pieShadow );
        }
    }

    AnimateToAngle animateToAngle( final double angle ) {
        final AnimateToAngle activity = new AnimateToAngle( this, BuildAFractionModule.ANIMATION_TIME, reduceWindingNumber( angle ) );
        addActivity( activity );
        return activity;
    }

    //Make it so the piece won't rotate further than necessary
    private double reduceWindingNumber( final double angle ) {
        double originalAngle = pieceRotation;
        double delta = angle - originalAngle;
        while ( delta > PI ) { delta -= PI * 2; }
        while ( delta < -PI ) { delta += PI * 2; }
        return originalAngle + delta;
    }

    public void setPieceRotation( final double angle ) {
        final double extent = PI * 2.0 / pieceSize;
        Shape shape = new CircularShapeFunction( extent, circleDiameter / 2 ).createShape( ZERO, angle );
        pathNode.setPathTo( shape );
        pieShadow.setPathTo( getShadowOffset().createTransformedShape( shape ) );
        this.pieceRotation = angle;
    }

    @Override public void animateToTopOfStack() {
        animateToAngle( 0 );
        super.animateToTopOfStack();
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ModelObject;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TestSquare;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class ManualGeneExpressionCanvas extends PhetPCanvas {
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;

    public ManualGeneExpressionCanvas( final ManualGeneExpressionModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  The test chamber is centered
        // at (0, 0) in model space, and this transform is set up to place
        // the chamber where we want it on the canvas.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.5 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        final Property<ModelViewTransform> mvtProperty = new Property<ModelViewTransform>( mvt );

        // Set the background color.
        setBackground( new Color( 190, 231, 251 ) );

        // Set up the node where all controls should be placed.  These will
        // stay in one place and not scroll.
        PNode controlsRootNode = new PNode();
        addWorldChild( controlsRootNode );

        // Set up the root node for all model objects.  Nodes placed under
        // this one will scroll when the user moves along the DNA strand.
        final PNode modelRootNode = new PNode();
        addWorldChild( modelRootNode );

        // TODO: Testing and prototyping, remove eventually.
        for ( ModelObject modelObject : model.getModelObjects() ) {
            if ( modelObject instanceof TestSquare ) {
                modelRootNode.addChild( new ModelObjectNode( mvtProperty, modelObject, ( (TestSquare) modelObject ).getColor() ) );
            }
            else {
                modelRootNode.addChild( new ModelObjectNode( mvtProperty, modelObject, Color.RED ) );
            }
        }

        // Add buttons for moving to next and previous genes.
        controlsRootNode.addChild( new TextButtonNode( "Next Gene ->" ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - 20, 500 );
            setBackground( Color.GREEN );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    modelRootNode.animateToPositionScaleRotation( -200, 0, 1, 0, 1000 );
                }
            } );
        }} );
        controlsRootNode.addChild( new TextButtonNode( "<- Prev Gene" ) {{
            setOffset( 20, 500 );
            setBackground( Color.GREEN );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    modelRootNode.animateToPositionScaleRotation( 200, 0, 1, 0, 1000 );
                }
            } );
        }} );
    }
}

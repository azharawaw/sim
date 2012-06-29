// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.pictures.PictureSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.pictures.SceneContext;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;
import edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements MainContext, SceneContext {

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    private PNode currentScene;

    public BuildAFractionCanvas( final BuildAFractionModel model, final boolean dev ) {
        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );

        currentScene = new DefaultLevelSelectionScreen( "Build a Fraction", this );
        addChild( currentScene );
    }

    public static enum Direction {
        RIGHT,
        LEFT,
        DOWN
    }

    private void animateTo( final PNode node, Direction direction ) {
        Vector2D nodeOffset = direction == Direction.RIGHT ? new Vector2D( STAGE_SIZE.width, 0 ) :
                              direction == Direction.LEFT ? new Vector2D( -STAGE_SIZE.width, 0 ) :
                              direction == Direction.DOWN ? new Vector2D( 0, STAGE_SIZE.height ) :
                              Vector2D.ZERO;
        node.setOffset( nodeOffset.toPoint2D() );
        addChild( node );

        final PNode oldNode = currentScene;

        Vector2D oldNodeOffset = direction == Direction.RIGHT ? new Vector2D( -STAGE_SIZE.width, 0 ) :
                                 direction == Direction.LEFT ? new Vector2D( STAGE_SIZE.width, 0 ) :
                                 direction == Direction.DOWN ? new Vector2D( 0, -STAGE_SIZE.height ) :
                                 Vector2D.ZERO;

        PActivity activity = currentScene.animateToPositionScaleRotation( oldNodeOffset.x, oldNodeOffset.y, 1, 0, 400 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {

                PInterpolatingActivity fade = oldNode.animateToTransparency( 0, 1000 );
                fade.setDelegate( new PActivityDelegate() {
                    public void activityStarted( final PActivity activity ) {
                    }

                    public void activityStepped( final PActivity activity ) {
                    }

                    public void activityFinished( final PActivity activity ) {
                        oldNode.removeFromParent();
                    }
                } );
            }
        } );
        node.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
        currentScene = node;
    }

    public void levelButtonPressed( final AbstractLevelSelectionNode parent, final LevelInfo info ) {
        animateTo( createLevelNode( info.levelIndex, info.levelType ), Direction.RIGHT );
    }

    private PNode createLevelNode( final int levelIndex, final LevelType levelType ) {
        return levelType == LevelType.SHAPES ? new PictureSceneNode( levelIndex, new BuildAFractionModel( true ), STAGE_SIZE, this ) :
               new NumberSceneNode( levelIndex, rootNode, new BuildAFractionModel( true ), STAGE_SIZE, this );
    }

    public void goToNextPictureLevel( final int newLevelIndex ) {
        animateTo( new PictureSceneNode( newLevelIndex, new BuildAFractionModel( true ), STAGE_SIZE, this ), Direction.RIGHT );
    }

    public void goToNextNumberLevel( final int newLevelIndex ) {
        animateTo( new NumberSceneNode( newLevelIndex, rootNode, new BuildAFractionModel( true ), STAGE_SIZE, this ), Direction.RIGHT );
    }

    public void goToLevelSelectionScreen() {
        animateTo( new DefaultLevelSelectionScreen( "Build a Fraction", this ), Direction.LEFT );
    }

    public void resamplePictureLevel( final int levelIndex ) {
        animateTo( new PictureSceneNode( levelIndex, new BuildAFractionModel( true ), STAGE_SIZE, this ), Direction.DOWN );
    }
}
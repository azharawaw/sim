// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class view for "Graph the Line" (GTL) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_ChallengeNode extends ChallengeNode {

    public GTL_ChallengeNode( final LineGameModel model, final GTL_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {

        PNode titleNode = new PhetPText( Strings.GRAPH_THE_LINE, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );

        // The equation for the answer.
        final EquationNode answerEquationNode = createEquationNode( challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color );

        // The equation for the current guess will be a child of this node, to maintain rendering order.
        final PNode guessEquationParent = new PNode();
        guessEquationParent.setVisible( false );

        // icons for indicating correct vs incorrect
        final PNode answerCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessIncorrectNode = new PImage( Images.X_MARK );

        final GTL_GraphNode graphNode = createGraphNode( challenge );

        final FaceNode faceNode = new FaceNode( LineGameConstants.FACE_DIAMETER, LineGameConstants.FACE_COLOR );

        final PText pointsAwardedNode = new PhetPText( "", LineGameConstants.POINTS_AWARDED_FONT, LineGameConstants.POINTS_AWARDED_COLOR );

        // Buttons
        final Font buttonFont = LineGameConstants.BUTTON_FONT;
        final Color buttonForeground = LineGameConstants.BUTTON_COLOR;
        final TextButtonNode checkButton = new TextButtonNode( Strings.CHECK, buttonFont, buttonForeground );
        final TextButtonNode tryAgainButton = new TextButtonNode( Strings.TRY_AGAIN, buttonFont, buttonForeground );
        final TextButtonNode showAnswerButton = new TextButtonNode( Strings.SHOW_ANSWER, buttonFont, buttonForeground );
        final TextButtonNode nextButton = new TextButtonNode( Strings.NEXT, buttonFont, buttonForeground );

        // Point tools
        Rectangle2D pointToolDragBounds = new Rectangle2D.Double( 0, 0, challengeSize.getWidth(), challengeSize.getHeight() );
        PointToolNode pointToolNode1 = new PointToolNode( challenge.pointTool1, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        PointToolNode pointToolNode2 = new PointToolNode( challenge.pointTool2, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        pointToolNode1.scale( LineGameConstants.POINT_TOOL_SCALE );
        pointToolNode2.scale( LineGameConstants.POINT_TOOL_SCALE );

        // Point tools moveToFront when dragged, so we give them a common parent to preserve rendering order of the reset of the scenegraph.
        PNode pointToolParent = new PNode();
        pointToolParent.addChild( pointToolNode1 );
        pointToolParent.addChild( pointToolNode2 );

        // non-interactive nodes
        {
            titleNode.setPickable( false );
            titleNode.setChildrenPickable( false );
            answerEquationNode.setPickable( false );
            answerEquationNode.setChildrenPickable( false );
            guessEquationParent.setPickable( false );
            guessEquationParent.setChildrenPickable( false );
            answerCorrectNode.setPickable( false );
            guessCorrectNode.setPickable( false );
            guessIncorrectNode.setPickable( false );
            faceNode.setPickable( false );
            faceNode.setChildrenPickable( false );
            pointsAwardedNode.setPickable( false );
            pointsAwardedNode.setChildrenPickable( false );
        }

        // rendering order
        {
            addChild( titleNode );
            addChild( answerEquationNode );
            addChild( answerCorrectNode );
            addChild( guessEquationParent );
            addChild( guessCorrectNode );
            addChild( guessIncorrectNode );
            addChild( graphNode );
            addChild( checkButton );
            addChild( tryAgainButton );
            addChild( showAnswerButton );
            addChild( nextButton );
            addChild( pointToolParent );
            addChild( faceNode );
            addChild( pointsAwardedNode );
        }

        // layout
        {
            // title centered at top
            titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 10 );

            // equation centered in left half of challenge space
            answerEquationNode.setOffset( ( 0.25 * challengeSize.getWidth() ) - ( answerEquationNode.getFullBoundsReference().getWidth() / 2 ),
                                          ( challengeSize.getHeight() / 2 ) - ( answerEquationNode.getFullBoundsReference().getHeight() / 2 ) );
            // correct/incorrect icons are to left of equations
            answerCorrectNode.setOffset( answerEquationNode.getFullBoundsReference().getMinX() - answerCorrectNode.getFullBoundsReference().getWidth() - 20, //TODO
                                         answerEquationNode.getFullBoundsReference().getCenterY() - ( answerCorrectNode.getFullBoundsReference().getHeight() / 2 ) );
            guessCorrectNode.setOffset( answerCorrectNode.getXOffset(),
                                        answerCorrectNode.getFullBoundsReference().getMaxY() + 10 ); //TODO
            guessIncorrectNode.setOffset( guessCorrectNode.getOffset() ); //TODO

            // graphNode is positioned automatically based on mvt's origin offset.
            // buttons centered at bottom of challenge space
            final double ySpacing = 15;
            final double buttonCenterX = ( challengeSize.getWidth() / 2 );
            final double buttonCenterY = graphNode.getFullBoundsReference().getMaxY() + ySpacing;
            checkButton.setOffset( buttonCenterX - ( checkButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            tryAgainButton.setOffset( buttonCenterX - ( tryAgainButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            showAnswerButton.setOffset( buttonCenterX - ( showAnswerButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            nextButton.setOffset( buttonCenterX - ( nextButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );

            // face centered in the challenge space
            faceNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                ( challengeSize.getHeight() / 2 ) - ( faceNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // Update visibility of the correct/incorrect icons.
        final VoidFunction0 updateIcons = new VoidFunction0() {
            public void apply() {
                answerCorrectNode.setVisible( model.state.get() == PlayState.NEXT );
                guessCorrectNode.setVisible( answerCorrectNode.getVisible() && challenge.isCorrect() );
                guessIncorrectNode.setVisible( answerCorrectNode.getVisible() && !challenge.isCorrect() );
            }
        };

        // Function that keeps the guess equation updated as the user manipulates the line.
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                guessEquationParent.removeAllChildren();
                guessEquationParent.addChild( createEquationNode( line, LineGameConstants.STATIC_EQUATION_FONT, LineGameConstants.GUESS_COLOR ) );
                guessEquationParent.setOffset( answerEquationNode.getXOffset(),
                                               answerEquationNode.getFullBoundsReference().getMaxY() + 30 );
                updateIcons.apply();
            }
        } );

        // state changes
        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {

                // visibility of face
                faceNode.setVisible( state == PlayState.TRY_AGAIN ||
                                     state == PlayState.SHOW_ANSWER ||
                                     ( state == PlayState.NEXT && challenge.isCorrect() ) );

                // visibility of points
                pointsAwardedNode.setVisible( faceNode.getVisible() && challenge.isCorrect() );

                // visibility of buttons
                checkButton.setVisible( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK );
                tryAgainButton.setVisible( state == PlayState.TRY_AGAIN );
                showAnswerButton.setVisible( state == PlayState.SHOW_ANSWER );
                nextButton.setVisible( state == PlayState.NEXT );

                // states in which the graph is interactive
                graphNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || ( state == PlayState.NEXT && !challenge.isCorrect() ) );
                graphNode.setChildrenPickable( graphNode.getPickable() );

                // Show the equation for the user's guess at the end of the challenge.
                guessEquationParent.setVisible( state == PlayState.NEXT );

                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( challenge.isCorrect() ) {
                    faceNode.smile();
                    audioPlayer.correctAnswer();
                    challenge.guess.set( challenge.guess.get().withColor( LineGameConstants.ANSWER_COLOR ) );
                    final int points = model.computePoints( model.state.get() == PlayState.FIRST_CHECK ? 1 : 2 );  //TODO handle this better
                    model.results.score.set( model.results.score.get() + points );
                    pointsAwardedNode.setText( MessageFormat.format( Strings.POINTS_AWARDED, String.valueOf( points ) ) );
                    // center points below face
                    pointsAwardedNode.setOffset( faceNode.getFullBoundsReference().getCenterX() - ( pointsAwardedNode.getFullBoundsReference().getWidth() / 2 ),
                                                 faceNode.getFullBoundsReference().getMaxY() + 10 );
                    model.state.set( PlayState.NEXT );
                }
                else {
                    faceNode.frown();
                    audioPlayer.wrongAnswer();
                    pointsAwardedNode.setText( "" );
                    if ( model.state.get() == PlayState.FIRST_CHECK ) {
                        model.state.set( PlayState.TRY_AGAIN );
                    }
                    else {
                        model.state.set( PlayState.SHOW_ANSWER );
                    }
                }
            }
        } );

        // "Try Again" button
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.state.set( PlayState.SECOND_CHECK );
            }
        } );

        // "Show Answer" button
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphNode.setAnswerVisible( true );
                model.state.set( PlayState.NEXT );
            }
        } );

        // "Next" button
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.state.set( PlayState.FIRST_CHECK );
            }
        } );
    }

    // Creates the equation portion of the view.
    protected abstract EquationNode createEquationNode( Line line, PhetFont font, Color color );

    // Creates the graph portion of the view.
    protected abstract GTL_GraphNode createGraphNode( GTL_Challenge challenge );
}
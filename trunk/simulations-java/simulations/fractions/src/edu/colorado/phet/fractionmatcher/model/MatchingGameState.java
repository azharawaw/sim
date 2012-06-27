// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.fractionmatcher.model.Motions.*;
import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static fj.data.List.range;

/**
 * Immutable class for the state of the matching game model.
 *
 * @author Sam Reid
 */
public @Data class MatchingGameState {

    //Movable fractions
    public final List<MovableFraction> fractions;

    //Cells where the fractions start
    public final List<Cell> startCells;

    //Cells where the fractions start
    public final List<Cell> scoreCells;

    //Number of times the user scored by getting a fraction into a score cell, used for iterating to the next score cell for animation
    public final int scored;

    //Scales that fractions can be dropped on
    public final Scale leftScale = new Scale( new Vector2D( 220, 320 ) );
    public final Scale rightScale = new Scale( new Vector2D( 570, 320 ) );

    //Time (in sim time) of when something was dropped on the scale, for purposes of animating the bar
    public final long leftScaleDropTime;
    public final long rightScaleDropTime;

    //Issues general to other games
    public final GameInfo info;

    //How long the bar graphs have been animating in seconds, 0 if not started yet
    public final double barGraphAnimationTime;

    //List of results from completing games.
    public final List<GameResult> gameResults;

    public static MatchingGameState initialState( AbstractLevelFactory factory ) {
        return newLevel( 1, List.<GameResult>nil(), factory ).withMode( Mode.CHOOSING_SETTINGS );
    }

    public static MatchingGameState newLevel( int level, List<GameResult> gameResults, AbstractLevelFactory factory ) {
        final List<Cell> startCells = createCells( 100, 415 + 12, 130, 120, 6, 2, 0, 0 ); //REVIEW collapse 415+12, or doc why it's expressed this way
        final List<Cell> scoreCells = createCells( 10, 12, 155, 90, 6, 1, 10, 0 );
        return new MatchingGameState( factory.createLevel( level, startCells ), startCells, scoreCells, 0, 0, 0, new GameInfo( level, false, 0, Mode.WAITING_FOR_USER_TO_CHECK_ANSWER, 0, 0, 0, true ), 0, gameResults );
    }

    //Create adjacent cells from which fractions can be dragged
    private static List<Cell> createCells( final int x, final int y, final int width, final int height, int columns, final int rows, final double spacingX, final double spacingY ) {
        return range( 0, columns ).bind( new F<Integer, List<Cell>>() {
            @Override public List<Cell> f( final Integer column ) {
                return range( 0, rows ).map( new F<Integer, Cell>() {
                    @Override public Cell f( Integer row ) {
                        return new Cell( new ImmutableRectangle2D( x + column * ( width + spacingX ), y + row * ( height + spacingY ), width, height ) );
                    }
                } );
            }
        } );
    }

    public Mode getMode() { return info.mode; }

    public int getChecks() {return info.checks;}

    public MatchingGameState withFractions( List<MovableFraction> fractions ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    MatchingGameState withScored( int scored ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    public MatchingGameState withAudio( boolean audio ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info.withAudio( audio ), barGraphAnimationTime, gameResults ); }

    public MatchingGameState withLeftScaleDropTime( long leftScaleDropTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    public MatchingGameState withRightScaleDropTime( long rightScaleDropTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    //NOTE: changing modes resets the bar graph animation time
    public MatchingGameState withMode( final Mode state ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info.withMode( state ), 0.0, gameResults ); }

    public MatchingGameState withInfo( final GameInfo info ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    MatchingGameState withBarGraphAnimationTime( final double barGraphAnimationTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    MatchingGameState withGameResults( final List<GameResult> gameResults ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults ); }

    public MatchingGameState stepInTime( final double dt ) {
        return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction f ) {
                return f.stepInTime( new UpdateArgs( f, dt, MatchingGameState.this ) );
            }
        } ) ).
                withInfo( info.withTime( info.time + (long) ( dt * 1000.0 ) ) ).
                withBarGraphAnimationTime( info.mode == Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS || info.mode == Mode.SHOWING_WHY_ANSWER_WRONG || info.mode == Mode.USER_CHECKED_CORRECT_ANSWER ? barGraphAnimationTime + dt : barGraphAnimationTime );
    }

    public Option<MovableFraction> getScaleFraction( final Scale scale ) {
        return fractions.find( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( MovableFraction m ) {
                return m.position.equals( scale.getAttachmentPoint( m ) );
            }
        } );
    }

    double getScaleValue( Scale scale ) { return getScaleFraction( scale ).isSome() ? getScaleFraction( scale ).some().getFractionValue() : 0.0; }

    public double getLeftScaleValue() { return getScaleValue( leftScale ); }

    public double getRightScaleValue() { return getScaleValue( rightScale ); }

    //Move fractions from the scale back to a free starting cell
    public MatchingGameState jettisonFraction( Scale scale ) {
        final Option<MovableFraction> scaleFraction = getScaleFraction( scale );
        return scaleFraction.option( this, new F<MovableFraction, MatchingGameState>() {
            @Override public MatchingGameState f( final MovableFraction match ) {
                return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( MovableFraction m ) {
                        return match.equals( m ) ? m.withMotion( moveToCell( getClosestFreeStartCell( m ) ) ) : m;
                    }
                } ) );
            }
        } );
    }

    boolean isOnScale( Scale scale, final MovableFraction movableFraction ) {
        return getScaleFraction( scale ).option( false, new F<MovableFraction, Boolean>() {
            @Override public Boolean f( MovableFraction m ) {
                return m.equals( movableFraction );
            }
        } );
    }

    boolean isOnScale( final MovableFraction m ) { return isOnScale( leftScale, m ) || isOnScale( rightScale, m ); }

    public MatchingGameState animateMatchToScoreCell() {
        return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction m ) {
                double width = m.withScale( 0.5 ).getNodeWithCorrectScale().getFullBounds().getWidth();
                final Cell cell = scoreCells.index( scored );
                final int offset = 15;
                final F<UpdateArgs, MovableFraction> moveToLeftSide = moveToPosition( new Vector2D( cell.rectangle.getCenter().getX() - width / 2 - offset, cell.rectangle.getCenter().getY() ) );
                final F<UpdateArgs, MovableFraction> moveToRightSide = moveToPosition( new Vector2D( cell.rectangle.getCenter().getX() + width / 2 + offset, cell.rectangle.getCenter().getY() ) );

                //Could shrink values >1 more so they will both fit in the score box using m.getValue() > 1 ? 0.5 : 0.5.  Not perfect, will have to be fine-tuned.
                final F<UpdateArgs, MovableFraction> shrink = scale( 0.5 );
                return isOnScale( leftScale, m ) ? m.withMotion( composite( moveToLeftSide, shrink ) ).withScored( true ) :
                       isOnScale( rightScale, m ) ? m.withMotion( composite( moveToRightSide, shrink ) ).withScored( true ) :
                       m;
            }
        } ) ).withScored( scored + 1 );
    }

    boolean getLastDroppedScaleRight() { return rightScaleDropTime > leftScaleDropTime; }

    public Cell getClosestFreeStartCell( final MovableFraction f ) {
        return startCells.filter( new F<Cell, Boolean>() {
            @Override public Boolean f( final Cell cell ) {
                return isFree( cell );
            }
        } ).sort( ord( new F<Cell, Double>() {
            @Override public Double f( final Cell u ) {
                return u.getPosition().distance( f.position );
            }
        } ) ).head();
    }

    //Find out if a cell is free for a fraction to move to.  True if no other fraction is there and no other fraction is moving there
    private boolean isFree( final Cell cell ) {
        return !isTaken( cell );
    }

    public boolean allStartCellsFree() {
        return startCells.filter( new F<Cell, Boolean>() {
            @Override public Boolean f( final Cell cell ) {
                return isFree( cell );
            }
        } ).length() == startCells.length();
    }

    private boolean isTaken( final Cell cell ) {
        return fractions.exists( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( final MovableFraction movableFraction ) {
                return !movableFraction.dragging && movableFraction.position.equals( cell.getPosition() );
            }
        } );
    }

    //The user pressed show answer.  We need to take the last object off the scale, and move the right one to the scale.
    public MatchingGameState animateToCorrectAnswer() {

        //find which side needs a match and what the value should be
        final double valueToMatch = getLastDroppedScaleRight() ? getLeftScaleValue() : getRightScaleValue();

        //Find the answer to match with.  It must have the same value and not be on either scale
        final MovableFraction match = fractions.find( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( final MovableFraction m ) {
                return Math.abs( m.getFractionValue() - valueToMatch ) < 1E-8 && !isOnScale( m ) && !m.scored;
            }
        } ).some();

        return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction m ) {

                //Move the bad value back to a free cell
                final boolean matchesLeft = isOnScale( leftScale, m ) && !getLastDroppedScaleRight();
                final boolean matchesRight = isOnScale( rightScale, m ) && getLastDroppedScaleRight();
                if ( matchesLeft || matchesRight ) {
                    return m.withMotion( moveToCell( getClosestFreeStartCell( m ) ) );
                }

                //Replace the latest thing the user placed
                if ( m == match ) {
                    return m.withMotion( getLastDroppedScaleRight() ? MOVE_TO_RIGHT_SCALE : MOVE_TO_LEFT_SCALE );
                }

                return m;
            }
        } ) );
    }

    public MatchingGameState newGame( final int level, final int score, final int maxPoints ) {
        return withMode( Mode.CHOOSING_SETTINGS ).withGameResults( gameResults.snoc( new GameResult( level, score ) ) );
    }

    public MatchingGameState withChecks( final int checks ) { return withInfo( info.withChecks( checks ) ); }

    public MatchingGameState withScore( final int score ) { return withInfo( info.withScore( score ) ); }

    public MatchingGameState withTimerVisible( final Boolean timerVisible ) { return withInfo( info.withTimerVisible( timerVisible ) ); }
}
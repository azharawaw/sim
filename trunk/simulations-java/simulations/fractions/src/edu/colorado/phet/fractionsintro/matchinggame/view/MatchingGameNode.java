// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Scale;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static java.awt.Color.lightGray;
import static java.awt.Color.yellow;

/**
 * Displays an immutable matching game state
 *
 * @author Sam Reid
 */
public class MatchingGameNode extends FNode {
    public MatchingGameNode( final SettableProperty<MatchingGameState> model, final PNode rootNode ) {
        final MatchingGameState state = model.get();
        final PNode scales = new RichPNode( state.leftScale.toNode(), state.rightScale.toNode() );
        addChild( scales );

        addChild( new ZeroOffsetNode( new BarGraphNode( state.getLeftScaleValue(), state.leftScaleDropTime, state.getRightScaleValue(), state.rightScaleDropTime ) ) {{
            setOffset( scales.getFullBounds().getCenterX() - getFullWidth() / 2, scales.getFullBounds().getCenterY() - getFullHeight() - 15 );
        }} );

        state.getScales().map( new F<Scale, PNode>() {
            @Override public PNode f( Scale s ) {
                return s.toNode();
            }
        } ).foreach( addChild );
        state.scoreCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRoundedRectangle( 20, 20 ), lightGray );
            }
        } ).foreach( addChild );
        if ( state.getLeftScaleValue() > 0 && state.getRightScaleValue() > 0 ) {
            final String string = state.getLeftScaleValue() < state.getRightScaleValue() ? "<" :
                                  state.getLeftScaleValue() > state.getRightScaleValue() ? ">" :
                                  "=";
            final PhetFont textFont = new PhetFont( 100, true );
            final PNode sign = new PhetPPath( textFont.createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), string ).getOutline(), yellow, new BasicStroke( 3 ), Color.black );
            sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() + 10 );
            addChild( sign );

            //If they match, show a "Keep" button. This allows the student to look at the right answer as long as they want before moving it to the scoreboard.
            if ( state.getLeftScaleValue() == state.getRightScaleValue() ) {
                addChild( new HTMLImageButtonNode( "Keep<br>Match", new PhetFont( 16, true ), Color.orange ) {{
                    centerFullBoundsOnPoint( sign.getFullBounds().getCenterX(), sign.getFullBounds().getMaxY() + getFullHeight() / 2 + 10 );
                    addActionListener( new ActionListener() {
                        @Override public void actionPerformed( ActionEvent e ) {
                            model.set( model.get().animateMatchToScoreCell() );
                        }
                    } );
                }} );
            }
        }
        state.scoreCells.take( state.scored ).map( new F<Cell, PNode>() {
            @Override public PNode f( final Cell cell ) {
                return new PhetPText( "=" ) {{centerFullBoundsOnPoint( cell.rectangle.getCenter() );}};
            }
        } ).foreach( addChild );

        //Render the cells, costs about 50% of a CPU--could be optimized
        state.cells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ).foreach( addChild );
        state.fractions.map( new F<MovableFraction, PNode>() {
            @Override public PNode f( final MovableFraction f ) {
                return new MovableFractionNode( model, f, f.toNode(), rootNode );
            }
        } ).foreach( addChild );

        if ( state.scored == state.scoreCells.length() ) {
            final int newLevel = state.level + 1;
            addChild( new HTMLImageButtonNode( "Level " + newLevel, Color.orange ) {{
                setOffset( AbstractFractionsCanvas.STAGE_SIZE.getWidth() - getFullWidth(), AbstractFractionsCanvas.STAGE_SIZE.getHeight() / 2 - getFullHeight() / 2 );
                addActionListener( new ActionListener() {
                    @Override public void actionPerformed( ActionEvent e ) {
                        model.set( MatchingGameState.initialState( newLevel ) );
                    }
                } );
            }} );
        }
        addChild( new PImage( state.audio ? Images.SOUND_MAX : Images.SOUND_MIN ) {{
            setOffset( FractionsIntroCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 30, FractionsIntroCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    model.set( state.audio( !state.audio ) );
                }
            } );
        }} );
    }
}

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractions.buildafraction.view.pictures.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionmatcher.view.PaddedIcon;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.*;

/**
 * Node that shows the levels and lets the user choose the level and settings
 *
 * @author Sam Reid
 */
public class AbstractLevelSelectionNode extends PNode {

    protected final ResetAllButtonNode resetAllButton;

    //Rows + Columns
    public AbstractLevelSelectionNode( final String title, final List<List<LevelInfo>> page1Levels, final MainContext context ) {

        //Title text, only shown when the user is choosing a level
        PNode titleText = new PNode() {{
            addChild( new PhetPText( title, new PhetFont( 38, true ) ) );
        }};

        titleText.centerFullBoundsOnPoint( STAGE_SIZE.width / 2, INSET + titleText.getFullBounds().getHeight() / 2 );
        addChild( titleText );

        VBox page1Box = toButtonSetNode( page1Levels, context );
        addChild( page1Box );

        resetAllButton = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                context.reset();
            }
        }, context.getComponent(), CONTROL_FONT, Color.black, RefreshButtonNode.BUTTON_COLOR ) {{
            setOffset( STAGE_SIZE.width - this.getFullWidth() - INSET, STAGE_SIZE.height - this.getFullHeight() - INSET );
            setConfirmationEnabled( false );
        }};

        addChild( resetAllButton );
    }

    private VBox toButtonSetNode( final List<List<LevelInfo>> page1Levels, final MainContext context ) {
        ArrayList<HBox> boxes = new ArrayList<HBox>();
        for ( List<LevelInfo> list : page1Levels ) {
            List<PNode> icons = list.map( new F<LevelInfo, PNode>() {
                @Override public PNode f( final LevelInfo levelInfo ) {
                    return toLevelIcon( AbstractLevelSelectionNode.this, levelInfo, page1Levels, context );
                }
            } );
            boxes.add( new HBox( 25, icons.array( PNode[].class ) ) );
        }
        return new VBox( 20, boxes.toArray( new PNode[boxes.size()] ) ) {{
            centerFullBoundsOnPoint( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 );
        }};
    }

    private static PNode toLevelIcon( final AbstractLevelSelectionNode parent, final LevelInfo info, final List<List<LevelInfo>> allLevels, final MainContext context ) {
        final List<PNode> nodes = allLevels.bind( new F<List<LevelInfo>, List<PNode>>() {
            @Override public List<PNode> f( final List<LevelInfo> list ) {
                return list.map( LevelInfo._icon );
            }
        } );
        final Dimension2DDouble maxSize = PaddedIcon.getMaxSize( nodes );

        final Property<Boolean> selected = new Property<Boolean>( false );
        final PaddedIcon centerIcon = new PaddedIcon( maxSize, info.icon );
        LevelIconNode node = new LevelIconNode( info.name, centerIcon, info.filledStars, info.maxStars );
        ToggleButtonNode button = new ToggleButtonNode( node, selected, new VoidFunction0() {
            public void apply() {
                SimSharingManager.sendButtonPressed( UserComponentChain.chain( Components.levelButton, info.name ) );
                selected.set( true );

                //Show it pressed in for a minute before starting up.
                new Timer( 100, new ActionListener() {
                    public void actionPerformed( final ActionEvent e ) {

                        //TODO: Start game with this level.
                        context.levelButtonPressed( parent, info );

                        //prep for next time
                        selected.set( false );
                    }
                } ) {{
                    setInitialDelay( 100 );
                    setRepeats( false );
                }}.start();
            }
        } );

        return button;
    }

    //Button icon for a single level, shows the level name, a shape and the progress stars
    public static class LevelIconNode extends PNode {
        public LevelIconNode( final String text, PNode icon, int numStars, int maxStars ) {
            addChild( new VBox( new PhetPText( text, new PhetFont( 18, true ) ), icon, new StarSetNode2( numStars, maxStars ) ) );
        }

    }
}
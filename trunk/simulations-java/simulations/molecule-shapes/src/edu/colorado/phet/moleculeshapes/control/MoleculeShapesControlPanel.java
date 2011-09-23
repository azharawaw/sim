// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.jmephet.JMEActionListener;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.TitledControlPanelNode.TitleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The main Molecule Shapes control panel on the right hand side. It is composed of multiple sub-panels,
 * namely "bonding", "lone pair", "options" and "real examples".
 */
public class MoleculeShapesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    private final MoleculeShapesPanelNode realMoleculePanel;
    private RealMoleculePanelNode realMoleculeNode;

    /*---------------------------------------------------------------------------*
    * information for width computations
    *----------------------------------------------------------------------------*/

    private static final double TITLE_PADDING = 5;
    private static final String[] TITLE_STRINGS = new String[] {
            Strings.CONTROL__BONDING, Strings.CONTROL__LONE_PAIR, Strings.CONTROL__OPTIONS, Strings.REAL_EXAMPLES__TITLE
    };
    private static final String[] CHECKBOX_STRINGS = new String[] {
            Strings.CONTROL__SHOW_BOND_ANGLES, Strings.CONTROL__SHOW_LONE_PAIRS
    };
    public static final double INNER_WIDTH = Math.ceil( getRequiredInnerWidth() );

    public MoleculeShapesControlPanel( final MoleculeShapesModule module, RealMoleculeOverlayNode overlayNode ) {

        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode bondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, INNER_WIDTH, 10 ) );

            final double spaceBetweenTypes = 8;

            final PNode singleNode = new BondTypeControlNode( module, new PImage( Images.SINGLE_BOND_SMALL ), 1 ) {{
                setOffset( 0, 10 );
            }};
            addChild( singleNode );
            final PNode doubleNode = new BondTypeControlNode( module, new PImage( Images.DOUBLE_BOND_SMALL ), 2 ) {{
                setOffset( 0, singleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( doubleNode );
            final PNode tripleNode = new BondTypeControlNode( module, new PImage( Images.TRIPLE_BOND_SMALL ), 3 ) {{
                setOffset( 0, doubleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( tripleNode );
        }}, Strings.CONTROL__BONDING );
        addChild( bondingPanel );

        // put it on 0 vertically
        setOffset( 0, 10 );

        /*---------------------------------------------------------------------------*
        * lone pair panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode nonBondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, INNER_WIDTH, 10 ) );

            /*---------------------------------------------------------------------------*
            * lone pair control
            *----------------------------------------------------------------------------*/
            final BondTypeControlNode lonePairNode = new BondTypeControlNode( module, new PImage( Images.LONE_PAIR_SMALL ), 0 ) {
                {
                    setOffset( 0, 10 );

                    // make sure to update our state when "show lone pairs" changes
                    MoleculeShapesModule.showLonePairs.addObserver( JMEUtils.swingObserver( new Runnable() {
                        public void run() {
                            updateState();
                        }
                    } ), false );
                }

                @Override protected boolean isEnabled() {
                    // add the extra constraint on visibility
                    return super.isEnabled() && MoleculeShapesModule.showLonePairs.get();
                }
            };
            addChild( lonePairNode );
        }}, Strings.CONTROL__LONE_PAIR ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( nonBondingPanel );

        final PNode removeAllButtonNode = new TextButtonNode( Strings.CONTROL__REMOVE_ALL,
                                                              MoleculeShapesConstants.REMOVE_BUTTON_FONT,
                                                              MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR.get() ) {{
            addActionListener( new JMEActionListener( new Runnable() {
                public void run() {
                    module.getMolecule().removeAllPairs();
                }
            } ) );

            MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR.addObserver(
                    new SimpleObserver() {
                        public void update() {
                            setBackground( MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR.get() );
                            repaint();
                        }
                    }, false );

            MoleculeShapesConstants.REMOVE_BUTTON_TEXT_COLOR.addObserver( new SimpleObserver() {
                public void update() {
                    setEnabledTextColor( MoleculeShapesConstants.REMOVE_BUTTON_TEXT_COLOR.get() );
                    repaint(); // TODO: repaint shouldn't be necessary?
                }
            } );

            setOffset( ( nonBondingPanel.getFullBounds().getWidth() - getFullBounds().getWidth() ) / 2,
                       nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER - 4 );
        }};
        addChild( removeAllButtonNode );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new PNode() {{
            // enforce the width constraint
            addChild( new Spacer( 0, 0, INNER_WIDTH, 10 ) );

            PNode checkboxContainer = new PNode();

            /*---------------------------------------------------------------------------*
            * show lone pairs
            *----------------------------------------------------------------------------*/
            final PNode showLonePairsNode = new PropertyCheckBoxNode( Strings.CONTROL__SHOW_LONE_PAIRS, MoleculeShapesModule.showLonePairs ) {{
                // enabled when there are lone pairs on the molecule
                Runnable updateEnabled = new Runnable() {
                    public void run() {
                        setEnabled( !module.getMolecule().getLonePairs().isEmpty() );
                    }
                };
                module.getMolecule().onGroupChanged.addUpdateListener( JMEUtils.swingUpdateListener( updateEnabled ), false );

                /*
                 * Run this in the current thread. should be in EDT for construction. Needed since the other call
                 * is fired off to run in the next JME frame, so we have a flickering initial effect otherwise.
                 */
                updateEnabled.run();
            }};
            checkboxContainer.addChild( showLonePairsNode );

            /*---------------------------------------------------------------------------*
            * show bond angles
            *----------------------------------------------------------------------------*/
            checkboxContainer.addChild( new PropertyCheckBoxNode( Strings.CONTROL__SHOW_BOND_ANGLES, MoleculeShapesProperties.showBondAngles ) {{
                // enabled when there are 2 or more bonds (or always)
                Runnable updateEnabled = new Runnable() {
                    public void run() {
                        setEnabled( !MoleculeShapesProperties.disableNAShowBondAngles.get()
                                    || module.getMolecule().getBondedGroups().size() >= 2 );
                    }
                };
                module.getMolecule().onGroupChanged.addUpdateListener( JMEUtils.swingUpdateListener( updateEnabled ), false );
                MoleculeShapesProperties.disableNAShowBondAngles.addObserver( JMEUtils.swingObserver( updateEnabled ) );

                setOffset( 0, showLonePairsNode.getFullBounds().getMaxY() );
            }} );

            checkboxContainer.setOffset( ( INNER_WIDTH - checkboxContainer.getFullBounds().getWidth() ) / 2, 0 );
            addChild( checkboxContainer );

        }}, Strings.CONTROL__OPTIONS ) {{
            setOffset( 0, removeAllButtonNode.getFullBounds().getMaxY() + PANEL_SPACER * 1.5 );
        }};
        addChild( optionsPanel );

        /*---------------------------------------------------------------------------*
        * real molecules panel
        *----------------------------------------------------------------------------*/
        final Property<Boolean> minimized = new Property<Boolean>( true ); // TODO: move this to a better location

        realMoleculeNode = new RealMoleculePanelNode( module.getMolecule(), module, overlayNode, minimized );
        realMoleculePanel = new MoleculeShapesPanelNode( realMoleculeNode, new PNode() {{
            final PText title = new TitledControlPanelNode.TitleNode( Strings.REAL_EXAMPLES__TITLE );
            addChild( title );

            final double TEXT_PADDING = 4;
            addChild( new MinimizeMaximizeButtonNode( minimized ) {{
                setOffset( title.getWidth() + TEXT_PADDING, ( title.getFullBounds().getHeight() - getFullBounds().getHeight() ) / 2 + 1 );
            }} );
        }} ) {{
            setOffset( 0, optionsPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( realMoleculePanel );
    }

    public boolean isOverlayVisible() {
        return realMoleculeNode.isOverlayVisible();
    }

    /**
     * @return Where to position the 3D molecule overlay
     */
    public PBounds getOverlayBounds() {
        return realMoleculeNode.getOverlayBounds();
    }

    /*---------------------------------------------------------------------------*
    * computation of required width
    *----------------------------------------------------------------------------*/

    public static double getRequiredInnerWidth() {
        double maxWidth = MoleculeShapesConstants.RIGHT_MIN_WIDTH;
        for ( String titleString : TITLE_STRINGS ) {
            double width = new TitleNode( titleString ).getFullBounds().getWidth();
            if ( titleString.equals( Strings.REAL_EXAMPLES__TITLE ) ) {
                width += 45;
            }
            else {
                width += 25;
            }
            maxWidth = Math.max( maxWidth, width );
        }
        for ( String checkboxString : CHECKBOX_STRINGS ) {
            maxWidth = Math.max( maxWidth, new PropertyCheckBoxNode( checkboxString, new Property<Boolean>( true ) ).getFullBounds().getWidth() );
        }
        return maxWidth;
    }

}

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.StubGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.DnaMoleculeNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.model.MessengerRnaProductionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel that controls the concentration and affinity for a control
 * panel.
 *
 * @author John Blanco
 */
public class TranscriptionFactorControlPanel extends PNode {

    private static final ModelViewTransform TRANSCRIPTION_FACTOR_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  0.08 );
    private static final ModelViewTransform DNA_FRAGMENT_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  0.1 );

    /**
     * Constructor.
     *
     * @param model
     * @param positive
     */
    public TranscriptionFactorControlPanel( MessengerRnaProductionModel model, boolean positive ) {

        final TranscriptionFactorConfig transcriptionFactorConfig;
        String titleText;
        if ( positive ) {
            transcriptionFactorConfig = model.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = "<center>Positive<br>Transcription Factor</center>";
        }
        else {
            transcriptionFactorConfig = model.NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = "<center>Negative<br>Transcription Factor</center>";
        }

        PNode title = new HTMLNode( titleText ) {{
            setFont( new PhetFont( 14 ) );
        }};

        PNode contents = new VBox(
                20,
                title,
                new ConcentrationController( transcriptionFactorConfig ),
                new AffinityController( transcriptionFactorConfig )
        );

        addChild( new ControlPanelNode( contents ) );
    }

    // Class definition for slider that controls the concentration of a
    // transcription factor.
    private static class ConcentrationController extends PNode {

        private ConcentrationController( TranscriptionFactorConfig transcriptionFactorConfig ) {
            // TODO: i18n
            PText caption = new PText( "Concentration" ) {{
                setFont( new PhetFont( 14, false ) );
            }};
            PNode molecule = new MobileBiomoleculeNode( TRANSCRIPTION_FACTOR_MVT, new TranscriptionFactor( new StubGeneExpressionModel(), transcriptionFactorConfig ) );
            molecule.setPickable( false );
            molecule.setChildrenPickable( false );
            addChild( new VBox( 5,
                                caption,
                                molecule,
                                new HorizontalSliderWithLabelsAtEnds( new UserComponent( UserComponents.transcriptionFactorLevelSlider ),
                                                                      // TODO: i18n
                                                                      "None",
                                                                      "Lots " ) ) );
        }
    }

    private static class AffinityController extends PNode {

        private static final double ARROW_LENGTH = 30;
        private static final double ARROW_HEAD_HEIGHT = 10;

        private AffinityController( TranscriptionFactorConfig transcriptionFactorConfig ) {
            // TODO: i18n
            PText caption = new PText( "Affinity" ) {{
                setFont( new PhetFont( 14, false ) );
            }};
            PNode arrowNode = new DoubleArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( ARROW_LENGTH, 0 ), ARROW_HEAD_HEIGHT / 2, ARROW_HEAD_HEIGHT, ARROW_HEAD_HEIGHT / 3 );
            arrowNode.setPaint( Color.BLACK );
            PNode affinityKey = new HBox(
                    new MobileBiomoleculeNode( TRANSCRIPTION_FACTOR_MVT, new TranscriptionFactor( new StubGeneExpressionModel(), transcriptionFactorConfig ) ),
                    arrowNode,
                    new DnaMoleculeNode( new DnaMolecule( new StubGeneExpressionModel(), DnaMolecule.BASE_PAIRS_PER_TWIST + 1, 0 ), DNA_FRAGMENT_MVT, 2, false )
            );
            affinityKey.setPickable( false );
            affinityKey.setChildrenPickable( false );
            addChild( new VBox( 5,
                                caption,
                                affinityKey,
                                new HorizontalSliderWithLabelsAtEnds( new UserComponent( UserComponents.transcriptionFactorLevelSlider ),
                                                                      // TODO: i18n
                                                                      "Low",
                                                                      "High " ) ) );
        }
    }

    // Convenience class for a horizontal slider with labels at ends.
    private static class HorizontalSliderWithLabelsAtEnds extends PNode {
        private static final double OVERALL_WIDTH = 150;
        private static final Font LABEL_FONT = new PhetFont( 12 );
        private static final double INTER_ELEMENT_SPACING = 5;

        private HorizontalSliderWithLabelsAtEnds( UserComponent userComponent, String leftLabel, String rightLabel ) {
            PText leftLabelNode = new PText( leftLabel ) {{
                setFont( LABEL_FONT );
            }};
            PText rightLabelNode = new PText( rightLabel ) {{
                setFont( LABEL_FONT );
            }};
            double sliderWidth = OVERALL_WIDTH - leftLabelNode.getFullBoundsReference().width -
                                 rightLabelNode.getFullBoundsReference().width - ( 2 * INTER_ELEMENT_SPACING );
            PNode sliderNode = new HSliderNode( userComponent, 0, 1, sliderWidth, 5, new Property<Double>( 0.0 ), new BooleanProperty( true ) );
            addChild( new HBox( INTER_ELEMENT_SPACING, leftLabelNode, sliderNode, rightLabelNode ) );
        }
    }
}

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
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

    public static final double TRANSCRIPTION_FACTOR_SCALE = 0.08;
    private static final ModelViewTransform TRANSCRIPTION_FACTOR_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  TRANSCRIPTION_FACTOR_SCALE );

    public static final double DNA_SCALE = 0.1;
    private static final ModelViewTransform DNA_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  DNA_SCALE );

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

        PNode transcriptionFactorNode = new MobileBiomoleculeNode( TRANSCRIPTION_FACTOR_MVT, new TranscriptionFactor( transcriptionFactorConfig ) );
        PNode dnaFragmentNode = new DnaMoleculeNode( new DnaMolecule( DnaMolecule.BASE_PAIRS_PER_TWIST + 1, 0.0 ), DNA_MVT, 2, false );
        PNode contents = new VBox(
                20,
                title,
                new ConcentrationController( transcriptionFactorConfig ),
                new AffinityController( transcriptionFactorNode, dnaFragmentNode, new Property<Double>( 0.0 ) ) // TODO: Need to hook up to actual model.
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

}

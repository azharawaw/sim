// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "GeneExpressionBasics" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class GeneExpressionBasicsResources {
    public static final String PROJECT_NAME = "gene-expression-basics";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    //Strings
    public static class Strings {
        public static final String AFFINITY = RESOURCES.getLocalizedString( "affinity" );
        public static final String BIOMOLECULE_TOOLBOX = RESOURCES.getLocalizedString( "biomoleculeToolbox" );
        public static final String GENE = RESOURCES.getLocalizedString( "gene" );
        public static final String HIGH = RESOURCES.getLocalizedString( "high" );
        public static final String LOW = RESOURCES.getLocalizedString( "low" );
        public static final String MRNA_DESTROYER = RESOURCES.getLocalizedString( "mrnaDestroyer" );
        public static final String NEGATIVE_TRANSCRIPTION_FACTOR = RESOURCES.getLocalizedString( "negativeTranscriptionFactor" );
        public static final String NEXT_GENE = RESOURCES.getLocalizedString( "nextGene" );
        public static final String POSITIVE_TRANSCRIPTION_FACTOR = RESOURCES.getLocalizedString( "positiveTranscriptionFactor" );
        public static final String PREVIOUS_GENE = RESOURCES.getLocalizedString( "previousGene" );
        public static final String REGULATORY_REGION = RESOURCES.getLocalizedString( "regulatoryRegion" );
        public static final String RIBOSOME = RESOURCES.getLocalizedString( "ribosome" );
        public static final String RNA_POLYMERASE = RESOURCES.getLocalizedString( "rnaPolymerase" );
        public static final String TRANSCRIBED_REGION = RESOURCES.getLocalizedString( "transcribedRegion" );
        public static final String YOUR_PROTEIN_COLLECTION = RESOURCES.getLocalizedString( "yourProteinCollection" );
    }

    //Images
    public static class Images {
        public static final BufferedImage ECOLI = RESOURCES.getImage( "ecoli.jpg" );
        public static final BufferedImage GRAY_ARROW = RESOURCES.getImage( "gray-arrow.png" );
    }
}
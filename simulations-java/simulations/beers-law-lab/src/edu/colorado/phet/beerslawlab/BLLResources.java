// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab;

import java.awt.Image;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLResources {

    public static final String PROJECT_NAME = "beers-law-lab";
    private static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    // Localized strings
    public static class Strings {
        public static final String BEERS_LAW = RESOURCES.getLocalizedString( "beersLaw" );
        public static final String COBALT_II_NITRATE = RESOURCES.getLocalizedString( "cobaltIINitrate" );
        public static final String COBALT_CHLORIDE = RESOURCES.getLocalizedString( "cobaltChloride" );
        public static final String CONCENTRATION = RESOURCES.getLocalizedString( "concentration" );
        public static final String COPPER_SULFATE = RESOURCES.getLocalizedString( "copperSulfate" );
        public static final String EVAPORATION = RESOURCES.getLocalizedString( "evaporation" );
        public static final String KOOL_AID = RESOURCES.getLocalizedString( "koolAid" );
        public static final String LITERS = RESOURCES.getLocalizedString( "liters" );
        public static final String LOTS = RESOURCES.getLocalizedString( "lots" );
        public static final String MOLES_PER_LITER = RESOURCES.getLocalizedString( "molesPerLiter" );
        public static final String NICKEL_II_CHLORIDE = RESOURCES.getLocalizedString( "nickelIIChloride" );
        public static final String NONE = RESOURCES.getLocalizedString( "none" );
        public static final String PATTERN_0LABEL = RESOURCES.getLocalizedString( "pattern.0label" );
        public static final String PATTERN_0VALUE_1UNITS = RESOURCES.getLocalizedString( "pattern.0value.1units" );
        public static final String POTASSIUM_CHROMATE = RESOURCES.getLocalizedString( "potassiumChromate" );
        public static final String POTASSIUM_DICHROMATE = RESOURCES.getLocalizedString( "potassiumDichromate" );
        public static final String POTASSIUM_PERMANGANATE = RESOURCES.getLocalizedString( "potassiumPermanganate" );
        public static final String REMOVE_SOLUTE = RESOURCES.getLocalizedString( "removeSolute" );
        public static final String SOLID = RESOURCES.getLocalizedString( "solid" );
        public static final String SOLUTE = RESOURCES.getLocalizedString( "solute" );
        public static final String SOLUTION = RESOURCES.getLocalizedString( "solution" );
        public static final String WATER = RESOURCES.getLocalizedString( "water" );
    }

    // Symbols, no i18n needed
    public static class Symbols {
        public static final String COBALT_II_NITRATE = ChemUtils.toSubscript( "Co(NO3)2" );
        public static final String COBALT_CHLORIDE = ChemUtils.toSubscript( "CoCl2" );
        public static final String COPPER_SULFATE = ChemUtils.toSubscript( "CuSO4" );
        public static final String KOOL_AID = Strings.KOOL_AID;
        public static final String NICKEL_II_CHLORIDE = ChemUtils.toSubscript( "NiCl2" );
        public static final String POTASSIUM_CHROMATE = ChemUtils.toSubscript( "K2CrO4" );
        public static final String POTASSIUM_DICHROMATE = ChemUtils.toSubscript( "K2Cr2O7" );
        public static final String POTASSIUM_PERMANGANATE = ChemUtils.toSubscript( "KMnO4" );
        public static final String WATER = ChemUtils.toSubscript( "H2O" );
    }

    // Images
    public static class Images {
        public static final Image DROPPER = RESOURCES.getImage( "dropper.png" );
        public static final Image DROPPER_ICON = RESOURCES.getImage( "dropper-icon.png" );
        public static final Image SHAKER = RESOURCES.getImage( "shaker.png" );
        public static final Image SHAKER_ICON = RESOURCES.getImage( "shaker-icon.png" );
    }
}

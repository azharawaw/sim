// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

//REVIEW IDEA identifies unused constants here
/**
 * Resources (images and translated strings) for "Fractions" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class FractionsResources {
    public static final String PROJECT_NAME = "fractions";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    //Strings
    public static class Strings {
        public static final String CHECK_ANSWER = RESOURCES.getLocalizedString( "checkAnswer" );
        public static final String FRACTION_MATCHER = RESOURCES.getLocalizedString( "fractionMatcher" );
        public static final String LEVEL = RESOURCES.getLocalizedString( "level" );
        public static final String LEVEL__PATTERN = RESOURCES.getLocalizedString( "level.pattern" );
        public static final String MY_FRACTIONS = RESOURCES.getLocalizedString( "myFractions" );
        public static final String MY_MATCHES = RESOURCES.getLocalizedString( "myMatches" );
        public static final String NEW_GAME = RESOURCES.getLocalizedString( "newGame" );
        public static final String NUMBERS = RESOURCES.getLocalizedString( "numbers" );
        public static final String OK = RESOURCES.getLocalizedString( "ok" );
        public static final String PICTURES = RESOURCES.getLocalizedString( "pictures" );
        public static final String SCORE = RESOURCES.getLocalizedString( "score" );
        public static final String SHOW_ANSWER = RESOURCES.getLocalizedString( "showAnswer" );
        public static final String TIME = RESOURCES.getLocalizedString( "time" );
        public static final String TIME_READOUT__PATTERN = RESOURCES.getLocalizedString( "timeReadout.pattern" );
        public static final String TRY_AGAIN = RESOURCES.getLocalizedString( "tryAgain" );
    }

    //Images
    public static class Images {
        public static final BufferedImage LEFT_BUTTON_GRAY = RESOURCES.getImage( "left_button_gray.png" );
        public static final BufferedImage LEFT_BUTTON_PRESSED = RESOURCES.getImage( "left_button_pressed.png" );
        public static final BufferedImage LEFT_BUTTON_UP = RESOURCES.getImage( "left_button_up.png" );
        public static final BufferedImage LOCKED = RESOURCES.getImage( "locked.png" );
        public static final BufferedImage RIGHT_BUTTON_GRAY = RESOURCES.getImage( "right_button_gray.png" );
        public static final BufferedImage RIGHT_BUTTON_PRESSED = RESOURCES.getImage( "right_button_pressed.png" );
        public static final BufferedImage RIGHT_BUTTON_UP = RESOURCES.getImage( "right_button_up.png" );
        public static final BufferedImage ROUND_BUTTON_DOWN = RESOURCES.getImage( "round_button_down.png" );
        public static final BufferedImage ROUND_BUTTON_DOWN_GRAY = RESOURCES.getImage( "round_button_down_gray.png" );
        public static final BufferedImage ROUND_BUTTON_DOWN_PRESSED = RESOURCES.getImage( "round_button_down_pressed.png" );
        public static final BufferedImage ROUND_BUTTON_UP = RESOURCES.getImage( "round_button_up.png" );
        public static final BufferedImage ROUND_BUTTON_UP_GRAY = RESOURCES.getImage( "round_button_up_gray.png" );
        public static final BufferedImage ROUND_BUTTON_UP_PRESSED = RESOURCES.getImage( "round_button_up_pressed.png" );
        public static final BufferedImage SCALE = RESOURCES.getImage( "scale.png" );
        public static final BufferedImage SOUND_MAX = RESOURCES.getImage( "sound-max.png" );
        public static final BufferedImage SOUND_MIN = RESOURCES.getImage( "sound-min.png" );
        public static final BufferedImage SPLIT_BLUE = RESOURCES.getImage( "split-blue.png" );
        public static final BufferedImage STAR_0 = RESOURCES.getImage( "star-0.png" );
        public static final BufferedImage STAR_1 = RESOURCES.getImage( "star-1.png" );
        public static final BufferedImage STAR_2 = RESOURCES.getImage( "star-2.png" );
        public static final BufferedImage STAR_3 = RESOURCES.getImage( "star-3.png" );
        public static final BufferedImage STAR_4 = RESOURCES.getImage( "star-4.png" );
        public static final BufferedImage STAR_GOLD = RESOURCES.getImage( "star-gold.png" );
        public static final BufferedImage STAR_GRAY = RESOURCES.getImage( "star-gray.png" );
        public static final BufferedImage UNLOCKED = RESOURCES.getImage( "unlocked.png" );
        public static final BufferedImage WATER_GLASS_BACK = RESOURCES.getImage( "water_glass_back.png" );
        public static final BufferedImage WATER_GLASS_FRONT = RESOURCES.getImage( "water_glass_front.png" );
    }
}
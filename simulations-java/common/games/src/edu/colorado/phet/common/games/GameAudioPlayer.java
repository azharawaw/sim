// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.audio.AudioResourcePlayer;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Audio player for sound resources.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class GameAudioPlayer extends AudioResourcePlayer {

    private static boolean inited;

    public GameAudioPlayer( boolean enabled ) {
        super( new PhetResources( "games" ), enabled );
        init();
    }

    //Play a blank audio file to initialize the sound system so that subsequent sounds will play faster
    public void init() {
        if ( !inited ) {
            System.out.println( "GameAudioPlayer.init" );
            inited = true;
        }
    }

    public void correctAnswer() {
        playSimAudio( "correctAnswer.wav" );
    }

    public void wrongAnswer() {
        playSimAudio( "wrongAnswer.wav" );
    }

    public void gameOverZeroScore() {
        playSimAudio( "wrongAnswer.wav" );
    }

    public void gameOverImperfectScore() {
        playSimAudio( "gameOver-imperfectScore.wav" );
    }

    public void gameOverPerfectScore() {
        playSimAudio( "gameOver-perfectScore.wav" );
    }
}
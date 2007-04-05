/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.tests.utils;

import edu.colorado.phet.common.view.util.PhetAudioClip;

public class TestPhetAudioClip {
    public static void main(String[] args) throws Exception {
        PhetAudioClip clip = new PhetAudioClip( "audio/smash0.wav" );

        clip.play();

        System.out.println("Test");

        Thread.sleep(200);

        // Required because JavaSound uses a non-daemon thread
        System.exit(0);
    }
}

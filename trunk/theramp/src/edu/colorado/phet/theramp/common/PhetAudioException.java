/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

/**
 * User: Sam Reid
 * Date: Dec 31, 2005
 * Time: 11:20:21 AM
 * Copyright (c) Dec 31, 2005 by Sam Reid
 */

public class PhetAudioException extends RuntimeException {
    private PhetAudioClip phetAudioClip;

    public PhetAudioException( Exception exception, PhetAudioClip phetAudioClip ) {
        super( PhetAudioClip.class.getName() + " for URL=" + phetAudioClip.getURL(), exception );
        this.phetAudioClip = phetAudioClip;
    }

    public PhetAudioClip getPhETAudioClip() {
        return phetAudioClip;
    }
}

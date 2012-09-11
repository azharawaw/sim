// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationFactory;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all challenges that use slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SI_ChallengeNode extends ChallengeNode {

    public SI_ChallengeNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, audioPlayer, challengeSize );
    }

    // Creates the equation portion of the view.
    @Override public EquationNode createEquationNode( Line line, Color color, PhetFont font ) {
        return new SlopeInterceptEquationFactory().createNode( line.withColor( color ), font );
    }
}

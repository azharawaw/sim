// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.maketheequation;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_PS_Point_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_PS_Slope_ChallengeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for a "Make the Equation" (MTE) challenge.
 * Given a graph of a line in point-slope (PS) form, make the equation by changing the Point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_PS_Point_Challenge extends MTE_Challenge {

    public MTE_PS_Point_Challenge( Line answer ) {
        super( answer, Line.createPointSlope( 0, 0, answer.rise, answer.run ) );
    }

    @Override public PNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new MTE_PS_Point_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}

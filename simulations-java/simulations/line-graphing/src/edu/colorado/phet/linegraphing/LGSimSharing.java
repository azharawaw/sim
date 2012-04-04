// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Data-collection enums that are specific to this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGSimSharing {

    public static enum UserComponents implements IUserComponent {
        introTab, gameTab,
        yEqualsXCheckBox, yEqualsNegativeXCheckBox, pointToolCheckBox, riseOverRunCheckBox, linesCheckBox,
        riseSpinner, runSpinner, interceptSpinner,
        equationMinimizeMaximizeButton,
        pointTool,
        interceptManipulator, slopeManipulator,
        riseIncrementButton, riseDecrementButton, runIncrementButton, runDecrementButton, interceptIncrementButton, interceptDecrementButton
    }

    public static enum ParameterKeys implements IParameterKey {
        rise, run, intercept
    }
}

package edu.colorado.phet.circuitconstructionkit;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.circuitconstructionkit.common.CCKStrings;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.clock.StopwatchPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 12:04:56 AM
 */

public class StopwatchDecorator extends VerticalLayoutPanel {
    public StopwatchDecorator( IClock clock, double timeScale, String timeUnits ) {
        setBorder( new LineBorder( Color.black, 2, true ) );
        JLabel label = new JLabel( CCKStrings.getString( "stopwatch" ) );
        add( label );
        StopwatchPanel stopwatchPanel = new StopwatchPanel( clock );
        stopwatchPanel.setScaleFactor( timeScale );
        stopwatchPanel.setTimeUnits( timeUnits );
        add( stopwatchPanel );
        label.setFont( new PhetDefaultFont( Font.BOLD, 14 ) );
        label.setBackground( stopwatchPanel.getBackground() );
        label.setOpaque( true );
    }
}

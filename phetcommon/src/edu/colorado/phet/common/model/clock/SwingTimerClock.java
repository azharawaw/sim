/**
 * Class: SwingTimerClock
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.common.model.clock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTimerClock extends AbstractClock {

    Timer timer;
    private long lastTickTime;

    public SwingTimerClock( double dt, int delay ) {
        super( dt, delay, true );
    }

    /**
     * This uses a TimeScalingClockModel.
     */
    public SwingTimerClock( double dt, int delay, boolean isFixed ) {
        super( dt, delay, isFixed );
        timer = new Timer( delay, new Ticker() );
    }

    public void doStart() {
        lastTickTime = System.currentTimeMillis();
        timer.start();
    }

    public void setDelay( int delay ) {
        super.setDelay( delay );
        timer.setDelay( delay );
    }

    protected void doStop() {
        timer.stop();
    }

    protected void doPause() {
        timer.stop();
    }

    protected void doUnpause() {
        lastTickTime = System.currentTimeMillis();
        timer.restart();
    }

    private class Ticker implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            if( isRunning() ) {
                long tickTime = System.currentTimeMillis();
                long actualWaitTime = tickTime - lastTickTime;
                lastTickTime = tickTime;
                if( isRunning() ) {
                    clockTicked( getSimulationTime( actualWaitTime ) );
                }
            }
        }
    }
}

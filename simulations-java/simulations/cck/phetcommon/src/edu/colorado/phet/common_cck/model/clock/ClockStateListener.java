package edu.colorado.phet.common_cck.model.clock;


/**
 * User: Sam Reid
 * Date: Jun 11, 2003
 * Time: 10:45:32 AM
 *
 */
public interface ClockStateListener {
    void delayChanged( int waitTime );

    void dtChanged( double dt );

    void threadPriorityChanged( int priority );

    void pausedStateChanged( boolean b );
}

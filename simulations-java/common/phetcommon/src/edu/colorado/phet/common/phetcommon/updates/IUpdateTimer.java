package edu.colorado.phet.common.phetcommon.updates;

/**
 * IUpdateTimer is the interface for requesting that updates be ignored until some later time.
 */
public interface IUpdateTimer {
    
    /**
     * Sets the time at which the user requested to defer update checks.
     * @param time time since Epoch, in milliseconds
     */
    public void setStartTime( long time );
    
    /**
     * Gets the time at which the user requested to defer update checks.
     * @return time since Epoch, in milliseconds
     */
    public long getStartTime();
    
    /**
     * Sets the amount of time that is supposed to pass before be check for another update.
     * @param duration in milliseconds
     */
    public void setDuration( long duration );
    
    /**
     * Gets the amount of time that is supposed to pass before be check for another update.
     * @return milliseconds
     */
    public long getDuration();
    
    /**
     * Determines if we've exceeed the duration, and it's OK to check for updates. 
     * @return
     */
    public boolean isDurationExceeded();
}

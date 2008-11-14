/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

/**
 * Interface for controlling various aspects of a nucleus's decay behavior.
 * 
 * @author John Blanco
 */
public interface AlphaDecayControl {

	/**
	 * Activate the decay process for a nucleus.  In most cases, this will
	 * mean that a timer is set and that once the timer has expired the decay
	 * will occur.
	 */
	public void activateDecay();
	
	/**
	 * Obtain a value that indicates whether decay is active for this nucleus.
	 * 'Active', in this context, means that the nucleus is moving towards
	 * decay and at some point in the future, decay will occur (unless it is
	 * stopped by some other command).  Once a nucleus has decayed, this
	 * call should return 'false'.
	 * 
	 * @return - true if heading towards decay, false if not.
	 */
	public boolean isDecayActive();
	
	/**
	 * Obtain a value indicating the amount of time that has occurred since
	 * this nucleus was activated.  If the nucleus has already decayed, the
	 * value returned should equal the amount of simulation time that it took
	 * for it to decay.  If the nucleus has not been activated, the value
	 * returned should be zero.
	 * 
	 * @return - time, in milliseconds of simulation time, since the nucleus
	 * was activated prior to decay.
	 */
	public double getActivatedLifetime();
}

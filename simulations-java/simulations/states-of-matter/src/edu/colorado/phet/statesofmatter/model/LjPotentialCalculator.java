/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

/**
 * This class calculates the Lennard-Jones potential based on values provided
 * for the molecule size (sigma) and the interaction strength (epsilon).  Note
 * that this is a "real" calculation as opposed to a normalized calculation,
 * which has been used elsewhere in the States of Matter simulation.
 *
 * @author John Blanco
 */
public class LjPotentialCalculator {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------

    // Arbitrarily (but somewhat reasonable) default values for sigma and epsilon.
    private static final double DEFAULT_SIGMA = 300;
    private static final double DEFAULT_EPSLON = 100;
    
    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------
    
    private double m_sigma;   // Molecular diameter in picometers.
    private double m_epsilon; // Interaction strength, epsilon/k-boltzmann is in Kelvin.
    private double m_epsilonForCalcs; // Epsilon multiplied by k-boltzmann.

    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------
    
    public LjPotentialCalculator(double sigma, double epsilon) {
        if (sigma == 0) {
            throw new IllegalArgumentException("Illegal value for the sigma parameter: " + sigma);
        }
        
        m_sigma = sigma;
        m_epsilon = epsilon;
        m_epsilonForCalcs = m_epsilon * StatesOfMatterConstants.K_BOLTZMANN;
    }
    
    public LjPotentialCalculator() {
        this(DEFAULT_SIGMA, DEFAULT_EPSLON);
    }
    
    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    public double getSigma() {
        return m_sigma;
    }

    
    public void setSigma( double m_sigma ) {
        this.m_sigma = m_sigma;
    }

    
    public double getEpsilon() {
        return m_epsilon;
    }

    
    public void setEpsilon( double m_epsilon ) {
        this.m_epsilon = m_epsilon;
        m_epsilonForCalcs = m_epsilon * StatesOfMatterConstants.K_BOLTZMANN;
    }

    //-----------------------------------------------------------------------------
    // Other Public Methods
    //-----------------------------------------------------------------------------

    /**
     * Calculate the Lennard-Jones potential for the specified distance.
     * 
     * @param distance - Distance between interacting molecules in picometers.
     * @return - Strength of the potential in newton-meters (N*m).
     */
    public double calculateLjPotential(double distance){
        double distanceRatio = m_sigma / distance;
        return (4 * m_epsilonForCalcs * (Math.pow( distanceRatio, 12 ) - Math.pow( distanceRatio, 6 )));
    }
    
    /**
     * Calculate the Lennard-Jones force for the specified distance.  Recall
     * that force can be calculated as the first derivative of potential
     * energy.
     * 
     * @param distance - Distance between interacting molecules in picometers.
     * @return - Force in newtons.
     */
    public double calculateLjForce( double distance ){
        return ((48 * m_epsilonForCalcs * Math.pow( m_sigma, 12 ) / Math.pow( distance, 13 )) -
                (24 * m_epsilonForCalcs * Math.pow( m_sigma, 6 ) / Math.pow( distance, 7 )));
    }
    
    /**
     * Calculate only the repulsive component of the Lennard-Jones force for
     * the specified distance.
     * 
     * @param distance - Distance between interacting molecules in picometers.
     * @return - Force in newtons.
     */
    public double calculateRepulsiveLjForce( double distance ) {
        return (48 * m_epsilonForCalcs * Math.pow( m_sigma, 12 ) / Math.pow( distance, 13 ));
    }
    
    /**
     * Calculate only the attractive component of the Lennard-Jones force for
     * the specified distance.
     * 
     * @param distance - Distance between interacting molecules in picometers.
     * @return - Force in newtons.
     */
    public double calculateAttractiveLjForce( double distance ) {
        return (24 * m_epsilonForCalcs * Math.pow( m_sigma, 6 ) / Math.pow( distance, 7 ));
    }
    
    /**
     * Calculate the distance at which the force is 0 because the attractive
     * and repulsive forces are balanced.  Note that this is where the
     * potential energy is at a minimum.
     * 
     * @return - Distance where force is 0 (or very close) in picometers.
     */
    public double calculateMinimumForceDistance() {
        // The following equation was arrived at by solving the force equation
        // for 0.
        return m_sigma * 1.1236;
    }
    
    /**
     * Calculate the potential energy of a particle at the given distance from
     * only the attractive component of the LJ potential.  This can be used
     * for approximating the energy of the system at the beginning of a set
     * of calculations.  All calculations are done with respect to the low
     * point of the potential energy well.
     * 
     * Note: I'm not at all sure that I've done this right, and I need to
     * verify it with the physicists.  TODO
     * @return
     */
    public double calculateAttractivePotentialEnergy( double distance ){
    	if (distance < calculateMinimumForceDistance()){
    		return 0;
    	}
    	else{
    		double distanceToMin = distance - calculateMinimumForceDistance();
    		// TODO: Totally faked out as a linear equation for now.
//            return (4 * m_epsilonForCalcs * Math.pow( m_sigma, 6 ) / (5 * Math.pow( distanceToMin, 5 )));
    		return distanceToMin * 1E-20;
    	}
    }
    
    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------



}

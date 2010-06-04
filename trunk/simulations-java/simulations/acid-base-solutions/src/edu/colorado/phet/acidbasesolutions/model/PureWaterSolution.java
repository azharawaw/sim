/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * A solution of pure water, contains no solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PureWaterSolution extends AqueousSolution {

    public PureWaterSolution() {
        super( null, 0 );
    }

    public double getH2OConcentration() {
        return getWaterConcentration();
    }

    public double getH3OConcentration() {
        return Math.sqrt( getWaterEquilibriumConstant() ); // Kw = [H30] * [OH-]
    }

    public double getOHConcentration() {
        return getH3OConcentration();
    }

    public double getProductConcentration() {
        return 0;
    }

    public double getReactantConcentration() {
        return 0;
    }
}

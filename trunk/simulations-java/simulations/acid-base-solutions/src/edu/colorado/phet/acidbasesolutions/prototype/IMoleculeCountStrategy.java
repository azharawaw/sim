/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Strategies for computing numbers of molecules to be displayed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IMoleculeCountStrategy {

    public int getNumberOfMolecules( double concentration, int maxMolecules );
   
    /**
     * Returns maxMolecules regardless of the concentration.
     */
    public static class ConstantMoleculeCountStrategy implements IMoleculeCountStrategy {

        public int getNumberOfMolecules( double concentration, int maxMolecules ) {
            return maxMolecules;
        }
    }
    
    /**
     * Algorithm used in advanced-acid-base-solutions, see RatioDotsNode.getNumberOfDots.
     * Number of molecules is based on concentration and an upper bound.
     */
    public static class ConcentrationMoleculeCountStrategy implements IMoleculeCountStrategy {

        private static final double BASE_CONCENTRATION = 1E-7;
        private static final int BASE_DOTS = 2;

        public int getNumberOfMolecules( double concentration, int maxMolecules ) {
            final double raiseFactor = MathUtil.log10( concentration / BASE_CONCENTRATION );
            final double baseFactor = Math.pow( ( maxMolecules / BASE_DOTS ), ( 1 / MathUtil.log10( 1 / BASE_CONCENTRATION ) ) );
            return (int) ( BASE_DOTS * Math.pow( baseFactor, raiseFactor ) );
        }
    }
 
}

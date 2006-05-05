/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.util.IntegerRange;

/**
 * BSOneWellSpec contains the information needed to set up the "One Well" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSOneWellSpec extends BSAbstractModuleSpec {

    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.ASYMMETRIC, BSWellType.COULOMB_1D, BSWellType.COULOMB_3D, BSWellType.HARMONIC_OSCILLATOR };
    private static final BSWellType DEFAULT_WELL_TYPE = BSWellType.SQUARE;
    
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = true;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = true;

    // Ranges (min, max, default, significantDecimalPlaces)
    private static final IntegerRange NUMBER_OF_WELLS_RANGE = new IntegerRange( 1, 1, 1 );
    private static final DoubleRange MASS_MULTIPLIER_RANGE = new DoubleRange( 0.5, 1.1, 1, 1 );
    private static final DoubleRange OFFSET_RANGE = new DoubleRange( -15, 5, 0, 0 ); // eV
    private static final DoubleRange DEPTH_RANGE = new DoubleRange( 0, 20, 10, 0 ); // eV
    private static final DoubleRange WIDTH_RANGE = new DoubleRange( 0.1, 8, 1, 1 ); // nm
    private static final DoubleRange SPACING_RANGE = new DoubleRange( 0, 0, 0, 0 ); // nm (don't care for one well)
    private static final DoubleRange SEPARATION_RANGE = new DoubleRange( 0, 0, 0, 0 ); // nm (don't care for one well)
    private static final DoubleRange ANGULAR_FREQUENCY_RANGE = new DoubleRange( 0.1, 10, 1, 2 ); // fs^-1
    
    public BSOneWellSpec() {
        super();

        setWellTypes( WELL_TYPES );
        setDefaultWellType( DEFAULT_WELL_TYPE );

        setSuperpositionControlsSupported( SUPERPOSITION_CONTROLS_SUPPORTED );
        setParticleControlsSupported( PARTICLE_CONTROLS_SUPPORTED );

        setNumberOfWellsRange( NUMBER_OF_WELLS_RANGE );
        setMassMultiplierRange( MASS_MULTIPLIER_RANGE );
        setOffsetRange( OFFSET_RANGE );
        setDepthRange( DEPTH_RANGE );
        setWidthRange( WIDTH_RANGE );
        setSpacingRange( SPACING_RANGE );
        setSeparationRange( SEPARATION_RANGE );
        setAngularFrequencyRange( ANGULAR_FREQUENCY_RANGE );
    }
}

/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron;

import static edu.colorado.phet.neuron.NeuronResources.getString;

/**
 * NeuronStrings is the collection of localized strings used by this
 * simulation. * We load all strings as statics so that we will be warned at
 * startup time of any missing strings.
 *
 * @author John Blanco
 */
public class NeuronStrings {
    
    /* not intended for instantiation */
    private NeuronStrings() {}
    
    public static final String TITLE_MEMBRANE_DIFFUSION_MODULE = getString( "ModuleTitle.MembraneDiffusionModule" );
    public static final String TITLE_AXON_CROSS_SECTION_MODULE = getString( "ModuleTitle.AxonCrossSection" );

    public static final String POTASSIUM_CHEMICAL_SYMBOL = getString( "PotassiumChemicalSymbol" );
    public static final String SODIUM_CHEMICAL_SYMBOL = getString( "SodiumChemicalSymbol" );
    
    public static final String SHOW_ALL_IONS = getString( "ShowAllIons" );
    public static final String SHOW_POTENTIAL_CHART = getString( "ShowPotentialChart" );
    public static final String STIMULUS_PULSE = getString( "StimulusPulse" );
}

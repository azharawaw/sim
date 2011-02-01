// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.greenhouse.GreenhouseConfig;


/**
 * Class that represents carbon monoxide in the model.
 *
 * @author John Blanco
 */
public class CO extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final double INITIAL_CARBON_OXYGEN_DISTANCE = 170; // In picometers.
    private static final double VIBRATION_MAGNITUDE = 20; // In picometers.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final AtomicBond carbonOxygenBond = new AtomicBond( carbonAtom, oxygenAtom, 3 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public CO(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( carbonAtom );
        addAtom( oxygenAtom );
        addAtomicBond( carbonOxygenBond );

        // Set up the photon wavelengths to absorb.
        setPhotonAbsorptionStrategy( GreenhouseConfig.microWavelength, new PhotonAbsorptionStrategy.RotationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.irWavelength, new PhotonAbsorptionStrategy.VibrationStrategy( this ) );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public CO(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    @Override
    protected void setVibration( double vibrationRadians ) {
        super.setVibration( vibrationRadians );
        double multFactor = Math.sin( vibrationRadians );
        vibrationAtomOffsets.get( carbonAtom ).setComponents( VIBRATION_MAGNITUDE * multFactor, 0 );
        vibrationAtomOffsets.get( oxygenAtom ).setComponents( -VIBRATION_MAGNITUDE * multFactor, 0 );
        updateAtomPositions();
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.CO;
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        initialAtomCogOffsets.put(carbonAtom, new Vector2D(-INITIAL_CARBON_OXYGEN_DISTANCE / 2, 0));
        initialAtomCogOffsets.put(oxygenAtom, new Vector2D(INITIAL_CARBON_OXYGEN_DISTANCE / 2, 0));
        updateAtomPositions();
    }
}

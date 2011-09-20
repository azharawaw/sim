// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * Model of a make-believe triatomic (3 atoms) molecule with a very specific topology.
 * Variables are named based on the English labels applied to the atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMolecule extends Molecule2D {

    private static final double ATOM_DIAMETER = 100;
    private static final double BOND_LENGTH = 1.5 * ATOM_DIAMETER;

    public final Atom atomA, atomB, atomC; // the atoms labeled A, B, C
    public final Bond bondAB; // the bond connecting atoms A and B
    public final Bond bondBC; // the bond connecting atoms B and C
    public final Property<Double> bondAngleA; // the bond angle of atom A relative to atom B, before applying molecule rotation
    public final Property<Double> bondAngleC; // the bond angle of atom C relative to atom B, before applying molecule rotation

    public TriatomicMolecule( ImmutableVector2D location, double angle ) {
        super( location, angle );

        atomA = new Atom( MPStrings.A, ATOM_DIAMETER, MPColors.ATOM_A, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() );
        atomB = new Atom( MPStrings.B, ATOM_DIAMETER, MPColors.ATOM_B, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() + ( MPConstants.ELECTRONEGATIVITY_RANGE.getLength() / 2 ) );
        atomC = new Atom( MPStrings.C, ATOM_DIAMETER, MPColors.ATOM_C, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() );
        bondAB = new Bond( atomA, atomB );
        bondBC = new Bond( atomB, atomC );
        bondAngleA = new Property<Double>( 0.75 * Math.PI );
        bondAngleC = new Property<Double>( 0.25 * Math.PI );

        initObservers();

        // requires an additional observer, to update atom locations when bond angles change
        RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {
                updateAtomLocations();
            }
        };
        observer.observe( bondAngleA, bondAngleC );
    }

    public void reset() {
        super.reset();
        bondAngleA.reset();
        bondAngleC.reset();
    }

    public Atom[] getAtoms() {
        return new Atom[] { atomA, atomB, atomC };
    }

    protected Bond[] getBonds() {
        return new Bond[] { bondAB, bondBC };
    }

    // repositions the atoms
    protected void updateAtomLocations() {
        final double radius = BOND_LENGTH;
        // atom B remains at the molecule's location
        atomB.location.set( location );
        // atom A
        double thetaA = angle.get() + bondAngleA.get();
        double xA = PolarCartesianConverter.getX( radius, thetaA ) + location.getX();
        double yA = PolarCartesianConverter.getY( radius, thetaA ) + location.getY();
        atomA.location.set( new ImmutableVector2D( xA, yA ) );
        // atom C
        double thetaC = angle.get() + bondAngleC.get();
        double xC = PolarCartesianConverter.getX( radius, thetaC ) + location.getX();
        double yC = PolarCartesianConverter.getY( radius, thetaC ) + location.getY();
        atomC.location.set( new ImmutableVector2D( xC, yC ) );
    }

    // updates partial charges
    protected void updatePartialCharges() {
        final double deltaAB = atomA.electronegativity.get() - atomB.electronegativity.get();
        final double deltaCB = atomC.electronegativity.get() - atomB.electronegativity.get();
        // in our simplified model, partial charge and deltaEN are equivalent. not so in the real world.
        atomA.partialCharge.set( -deltaAB );
        atomC.partialCharge.set( -deltaCB );
        // atom B's participates in 2 bonds, so its partial charge is the sum
        atomB.partialCharge.set( deltaAB + deltaCB );
    }
}

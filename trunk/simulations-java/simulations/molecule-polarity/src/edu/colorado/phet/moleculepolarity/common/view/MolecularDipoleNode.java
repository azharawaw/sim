// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;

/**
 * Visual representation of a molecular dipole.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolecularDipoleNode extends DipoleNode {

    private static final double OFFSET = 55; // offset in the direction that the dipole points

    public MolecularDipoleNode( final Molecule2D molecule, final double scale ) {
        super( MPColors.MOLECULAR_DIPOLE );

        // align the dipole to be parallel with the bond, with some offset along the dipole's axis
        SimpleObserver observer = new SimpleObserver() {
            public void update() {

                ImmutableVector2D dipole = molecule.dipole.get();

                setComponentX( scale * dipole.getMagnitude() ); // for a dipole with angle=0

                // clear the transform
                setOffset( 0, 0 );
                setRotation( 0 );

                // determine the location
                Point2D p = new Point2D.Double( OFFSET, 0 );  // for a dipole with angle=0
                Point2D offset = AffineTransform.getRotateInstance( dipole.getAngle() ).transform( p, null );
                translate( molecule.location.getX() + offset.getX(), molecule.location.getY() + offset.getY() );

                // rotate to match the dipole
                rotate( dipole.getAngle() );
            }
        };
        molecule.dipole.addObserver( observer );
    }
}
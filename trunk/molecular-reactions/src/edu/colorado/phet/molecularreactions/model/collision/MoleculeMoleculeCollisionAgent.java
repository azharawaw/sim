/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.molecularreactions.model.RoundMolecule;
import edu.colorado.phet.molecularreactions.model.CompoundMolecule;
import edu.colorado.phet.molecularreactions.model.PublishingModel;

/**
 * MoleculeMoleculeCollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeMoleculeCollisionAgent implements CollisionExpert {
    private double thresholdEnergy = 0;
    private PublishingModel model;

    public MoleculeMoleculeCollisionAgent( PublishingModel model ) {
        this.model = model;
    }

    public boolean detectAndDoCollision( Collidable bodyA, Collidable bodyB ) {
        return false;
    }

    public void detectAndDoCollision( RoundMolecule moleculeA, RoundMolecule moleculeB ) {
        double distSq = moleculeA.getPosition().distanceSq( moleculeB.getPosition() );
        double radSum = moleculeA.getRadius() + moleculeB.getRadius();
        if( distSq <= radSum * radSum ) {
            // If the energy of the collision is high enough, create a compound molecule
            double e = moleculeA.getKineticEnergy() + moleculeB.getKineticEnergy();
            if( e > thresholdEnergy ) {
                CompoundMolecule compoundMolecule = new CompoundMolecule( moleculeA, moleculeB );
                model.addModelElement( compoundMolecule );
            }

            // otherwise, determine repulsive force and accelerate the two molecules

        }
    }
}

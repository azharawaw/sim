/**
 * Class: Collision
 * Package: edu.colorado.phet.physics.collision
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.IdealGasModel;

public interface Collision {
    void collide();

    Collision createIfApplicable( CollidableBody particleA, CollidableBody particleB,
                                  IdealGasModel model, double dt );
}

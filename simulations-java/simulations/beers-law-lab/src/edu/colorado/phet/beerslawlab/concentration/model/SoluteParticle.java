// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Base class for solute particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SoluteParticle {

    public final Color color;
    public final double size; // particles are square, this is the length of one side
    public final Property<ImmutableVector2D> location; // location of the particle in the coordinate frame of the beaker
    public final double orientation; // rotation angle, in radians

    public SoluteParticle( Color color, double size, ImmutableVector2D location, double orientation ) {
        this.color = color;
        this.size = size;
        this.location = new Property<ImmutableVector2D>( location );
        this.orientation = orientation;
    }
}

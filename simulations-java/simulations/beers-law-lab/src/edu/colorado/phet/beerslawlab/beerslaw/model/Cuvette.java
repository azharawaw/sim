// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A cuvette is a small tube of circular or square cross section, sealed at one end,
 * made of plastic, glass, or fused quartz (for UV light) and designed to hold samples
 * for spectroscopic experiments.
 * <p>
 * In this case, the cuvette is the vessel that holds the solution.
 * It has a fixed height, but a variable width, making it possible to change
 * the path length. Location is fixed.  Origin is at the upper-left corner.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Cuvette implements Resettable {

    public final ImmutableVector2D location; // fixed location, cm
    public final Property<Double> width; // variable width, cm
    public final double height; // fixed height, cm
    public final DoubleRange widthRange;

    public Cuvette( ImmutableVector2D location, double width, double height, DoubleRange widthRange ) {
        this.location = location;
        this.width = new Property<Double>( width );
        this.height = height;
        this.widthRange = widthRange;
    }

    public void reset() {
        width.reset();
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of the hose which can be attached to the water tower so that the user can spray water from the ground
 *
 * @author Sam Reid
 */
public class Hose {

    //Angle in radians where 0 radians is to the right and PI/2 is straight up, should be between 0 and PI/2
    public final Property<Double> angle = new Property<Double>( Math.PI / 2 );

    //Flag to indicate whether the hose has been enabled by the user
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    public final ObservableProperty<ImmutableVector2D> attachmentPoint;

    //The place where the water comes out, tuned based on the curves in the HoseNode so the path is smooth and natural-looking
    public final ImmutableVector2D outputPoint = new ImmutableVector2D( 13.275, 0 );

    //Width of the hole for the attachment point in meters
    public final double holeSize;

    public Hose( ObservableProperty<ImmutableVector2D> attachmentPoint, double holeSize ) {
        this.attachmentPoint = attachmentPoint;
        this.holeSize = holeSize;
    }
}
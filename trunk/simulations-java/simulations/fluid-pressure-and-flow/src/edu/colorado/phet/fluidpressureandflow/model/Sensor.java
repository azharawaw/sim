// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Abstract base class for different types of sensor that read a value at their (x,y) location.
 *
 * @author Sam Reid
 * @see PressureSensor
 * @see VelocitySensor
 */
public abstract class Sensor<T> {

    public final Property<ImmutableVector2D> location;
    protected Property<T> value;

    public Sensor( double x, double y, T value ) {
        location = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        this.value = new Property<T>( value );
    }

    public void reset() {
        location.reset();
        value.reset();
    }

    public ImmutableVector2D getLocation() {
        return location.getValue();
    }

    public double getX() {
        return getLocation().getX();
    }

    public double getY() {
        return getLocation().getY();
    }

    public void setLocation( double x, double y ) {
        location.setValue( new ImmutableVector2D( x, y ) );
    }

    public T getValue() {
        return value.getValue();
    }

    protected void setValue( T value ) {
        this.value.setValue( value );
    }

    public abstract double getScalarValue();

    public void addValueObserver( SimpleObserver observer ) {
        value.addObserver( observer );
    }
}

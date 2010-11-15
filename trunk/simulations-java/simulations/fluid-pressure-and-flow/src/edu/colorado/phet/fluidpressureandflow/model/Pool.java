package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class Pool {
    public static final double DEFAULT_HEIGHT = new Units().feetToMeters( 10 );//10 foot deep pool, a customary depth for the deep end
    double height = DEFAULT_HEIGHT;
    double width = 4;
    private Property<Double> liquidDensityProperty = new Property<Double>( 1000.0 );//SI

    public Pool() {
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public Shape getShape() {
        return new Rectangle2D.Double( -width / 2, -height, width, height );
    }

    public double getLiquidDensity() {
        return liquidDensityProperty.getValue();
    }

    public Property<Double> getLiquidDensityProperty() {
        return liquidDensityProperty;
    }

    public double getMinX() {
        return getShape().getBounds2D().getMinX();
    }

    public double getMaxX() {
        return getShape().getBounds2D().getMaxX();
    }

    public double getMinY() {
        return -getHeight();
    }

    public void setLiquidDensity( double value ) {
        liquidDensityProperty.setValue( value );
    }

    public void addDensityListener( SimpleObserver simpleObserver ) {
        liquidDensityProperty.addObserver( simpleObserver );
    }
}

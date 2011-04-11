// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the intensity meter, including the position of the sensor, body, the reading values, etc.
 * When multiple rays hit the sensor, they are summed up.
 *
 * @author Sam Reid
 */
public class IntensityMeter {
    public final Property<ImmutableVector2D> sensorPosition;
    public final Property<ImmutableVector2D> bodyPosition;
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    public final Property<Reading> reading = new Property<Reading>( Reading.MISS );
    private final ArrayList<Reading> rayReadings = new ArrayList<Reading>();

    public IntensityMeter( double sensorX, double sensorY, double bodyX, double bodyY ) {
        sensorPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( sensorX, sensorY ) );
        bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( bodyX, bodyY ) );
    }

    public void translateSensor( Dimension2D delta ) {
        sensorPosition.setValue( sensorPosition.getValue().plus( delta ) );
    }

    public void translateBody( Dimension2D delta ) {
        bodyPosition.setValue( bodyPosition.getValue().plus( delta ) );
    }

    public Ellipse2D.Double getSensorShape() {
        double radius = 1.215E-6;//Fine tuned to match the given image
        return new Ellipse2D.Double( sensorPosition.getValue().getX() - radius, sensorPosition.getValue().getY() - radius, radius * 2, radius * 2 );
    }

    //Should be called before a model update so that values from last computation don't leak over into the next summation
    public void clearRayReadings() {
        rayReadings.clear();
        reading.setValue( Reading.MISS );
    }

    public void addRayReading( Reading r ) {
        rayReadings.add( r );
        ArrayList<Reading> hits = new ArrayList<Reading>();
        for ( Reading rayReading : rayReadings ) {
            if ( rayReading.isHit() ) {
                hits.add( rayReading );
            }
        }
        if ( hits.size() == 0 ) {
            reading.setValue( Reading.MISS );
        }
        else {
            double total = 0.0;
            for ( Reading hit : hits ) {
                total += hit.getValue();
            }
            reading.setValue( new Reading( total ) );
        }
    }

    public void translateAll( Dimension2D dimension2D ) {
        translateBody( dimension2D );
        translateSensor( dimension2D );
    }

    public void resetAll() {
        sensorPosition.reset();
        bodyPosition.reset();
        enabled.reset();
        reading.reset();
    }

    /**
     * A single reading for the intensity meter.
     */
    public static class Reading {
        private double value;

        public static final Reading MISS = new Reading() {
            public String getString() {
                return BendingLightStrings.MISS;
            }

            @Override
            public boolean isHit() {
                return false;
            }
        };

        protected Reading() {
        }

        public Reading( double value ) {
            this.value = value;
        }

        public String getString() {
            return format( value );
        }

        public static String format( double value ) {
            return new DecimalFormat( "0.00" ).format( value * 100 ) + "%";
        }

        public boolean isHit() {
            return true;
        }

        public double getValue() {
            return value;
        }
    }
}

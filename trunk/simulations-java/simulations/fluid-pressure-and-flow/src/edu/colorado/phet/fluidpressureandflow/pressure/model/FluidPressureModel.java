// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;

import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ATMOSPHERES;

/**
 * Model for the "pressure" tab
 *
 * @author Sam Reid
 */
public class FluidPressureModel extends FluidPressureAndFlowModel {

    public final Pool squarePool = new Pool();
    public final TrapezoidPool trapezoidPool = new TrapezoidPool();

    //Pool within which the user can measure the pressure
    public final Property<IPool> pool = new Property<IPool>( squarePool );

    //Flag indicating whether the atmosphere is enabled or not.  it's nice to be able to turn it off and just focus on the water.
    //It also emphasizes that the reason that p is not zero at the top of the water IS the atmosphere.
    public SettableProperty<Boolean> atmosphere = new BooleanProperty( true );

    public FluidPressureModel() {
        super( ATMOSPHERES );

        //Show pressure partly submerged in the water, but at the top of the water
        addPressureSensor( new PressureSensor( this, 0, 0 ) );
        addPressureSensor( new PressureSensor( this, -4, 2 ) );
        addPressureSensor( new PressureSensor( this, -4, 2 ) );
        addPressureSensor( new PressureSensor( this, -4, 2 ) );

        getClock().addSimulationTimeChangeListener( new VoidFunction1<Double>() {
            @Override public void apply( final Double dt ) {
                trapezoidPool.stepInTime( dt );
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        trapezoidPool.reset();
    }

    @Override public void addPressureChangeObserver( SimpleObserver updatePressure ) {
        super.addPressureChangeObserver( updatePressure );
        atmosphere.addObserver( updatePressure );
        pool.addObserver( updatePressure );
        liquidDensity.addObserver( updatePressure );
        standardAirPressure.addObserver( updatePressure );
        gravity.addObserver( updatePressure );
    }

    //Gets the pressure at the specified location.
    @Override public double getPressure( double x, double y ) {
        return pool.get().getPressure( x, y, atmosphere.get(), standardAirPressure.get(), liquidDensity.get(), gravity.get() );
    }
}
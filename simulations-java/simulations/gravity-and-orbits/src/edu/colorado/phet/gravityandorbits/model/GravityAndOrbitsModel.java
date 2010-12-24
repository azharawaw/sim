/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;

public class GravityAndOrbitsModel {

    private final ArrayList<Body> bodies = new ArrayList<Body>();

    private final GravityAndOrbitsClock clock;
    private ArrayList<SimpleObserver> modelStepListeners = new ArrayList<SimpleObserver>();
    public boolean teacherMode;
    private final VoidFunction1<Double> stepModel;
    private final Property<Boolean> gravityEnabledProperty; //TODO: this probably doesn't belong here, maybe in GravityAndOrbitsModelState?

    public GravityAndOrbitsModel( GravityAndOrbitsClock clock ) {
        super();
        this.clock = clock;
        this.gravityEnabledProperty = new Property<Boolean>( true );

        stepModel = new VoidFunction1<Double>() {
            public void apply( Double dt ) {
                ModelState newState = new ModelState( new ArrayList<BodyState>() {{
                    for ( Body body : bodies ) {
                        add( body.toBodyState() );
                    }
                }} ).getNextState( dt,
                                   100 );//1000 looks great, 50 starts to look awkward for sun+earth+moon, but 100 seems okay
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).updateBodyStateFromModel( newState.getBodyState( i ) );
                }
                //when two bodies collide, destroy the smaller
                for ( Body body : bodies ) {
                    for ( Body other : bodies ) {
                        if ( other != body ) {
                            if ( other.collidesWidth( body ) ) {
                                getSmaller( other, body ).setCollided( true );
                            }
                        }
                    }
                }
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).allBodiesUpdated();
                }
//                System.out.println( "momentum: " + getTotalMomentum() );
            }

            private Body getSmaller( Body other, Body body ) {
                if ( other.getMass() < body.getMass() ) {
                    return other;
                }
                else {
                    return body;
                }
            }
        };
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
//                if (teacherMode) return;
                final double dt = clockEvent.getSimulationTimeChange();
                stepModel.apply( dt );
                for ( SimpleObserver modelStepListener : modelStepListeners ) {
                    modelStepListener.update();
                }
            }
        } );
    }

    private ImmutableVector2D getTotalMomentum() {
        ImmutableVector2D total = new ImmutableVector2D();
        for ( Body body : bodies ) {
            total = total.getAddedInstance( body.getVelocity().getScaledInstance( body.getMass() ) );
        }
        return total;
    }

    public GravityAndOrbitsClock getClock() {
        return clock;
    }
    
    public void setGravityEnabled( boolean enabled ) {
        gravityEnabledProperty.setValue( enabled );
    }
    
    public boolean isGravityEnabled() {
        return gravityEnabledProperty.getValue();
    }
    
    public Property<Boolean> getGravityEnabledProperty() {
        return gravityEnabledProperty;
    }

    public void resetAll() {
        gravityEnabledProperty.reset();
        resetBodies();
        getClock().resetSimulationTime();
        updateForceVectors();
    }

    public void addModelSteppedListener( SimpleObserver simpleObserver ) {
        modelStepListeners.add( simpleObserver );
    }

    public void addBody( Body body ) {
        bodies.add( body );
        body.addUserModifiedPositionListener( new VoidFunction0() {
            public void apply() {
                if ( getClock().isPaused() ) { updateForceVectors(); }
            }
        } );
        body.getMassProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( getClock().isPaused() ) { updateForceVectors(); }
            }
        } );
        updateForceVectors();
    }

    /**
     * Since we haven't (yet?) rewritten the gravity forces to auto-update when dependencies change, we update when necessary
     * (1) when a new body is added or (2) when reset is pressed.
     * This update is done by running the physics engine for dt=0.0 then applying the computed forces to the bodies.
     * <p/>
     * Without this block of code, the force vectors would be zero on sim startup until the clock is started.
     */
    private void updateForceVectors() {
        stepModel.apply( 0.0 );
    }

    public ArrayList<Body> getBodies() {
        return new ArrayList<Body>( bodies );
    }

    public void resetBodies() {
        for ( Body body : bodies ) {
            body.resetAll();
        }
    }
}

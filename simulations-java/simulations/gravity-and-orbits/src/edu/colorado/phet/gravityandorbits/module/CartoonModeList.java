// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

//REVIEW since this the highest-level place were "cartoon mode" is mentioned, I think you need to describe it more verbosely. This doc is a little too sparse and vague.

/**
 * Configuration file for setting up the cartoon mode parameters.  This is typically done by multiplying the real values by the desired scales.
 * SunEarth and SunEarthMoon should be as similar as possible (aside from the addition of the moon)
 *
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    //REVIEW alpha parameter unused
    //REVIEW inconsistent naming convention for Property params (eg clockPausedProperty vs stepping)
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty, final double alpha ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, stepping, rewinding, timeSpeedScaleProperty ),
               //REVIEW difficult to read, why not encapsulate this in subclasses? (eg, CartoonSunEarth extends EarthSun)
               new SunEarth() {{
                   sun.radius *= 50;
                   earth.radius *= 800;

                   final int earthMassScaleFactor = 10200; //Tuned by hand so there are 12 cartoon lunar orbits in one cartoon earth orbit
                   earth.mass *= earthMassScaleFactor;

                   forceScale *= 0.573 / earthMassScaleFactor;//to balance increased mass and so that forces are 1/2 grid cell in default conditions, hand tuned by checking that reducing the distance by a factor of 2 increases the force arrow by a factor of 4
                   timeScale = 365.0 / 334.0;//Have to artificially scale up the time readout so that Sun/Earth/Moon mode has a stable orbit with correct periods since masses are nonphysical
                   sun.fixed = true;//Sun shouldn't move in cartoon modes
               }}, new SunEarthMoon() {{
                    sun.radius *= 50;
                    earth.radius *= 800;
                    moon.radius *= 800;

                    final int earthMassScaleFactor = 10200; //Tuned by hand so there are 12 cartoon lunar orbits in one cartoon earth orbit
                    earth.mass *= earthMassScaleFactor;
                    moon.vx *= 21;
                    moon.y = earth.radius * 1.7;

                    forceScale *= 0.573 / earthMassScaleFactor;//to balance increased mass and so that forces are 1/2 grid cell in default conditions
                    timeScale = 365.0 / 334.0;//Have to artificially scale up the time readout so that Sun/Earth/Moon mode has a stable orbit with correct periods since masses are nonphysical
                    sun.fixed = true;//Sun shouldn't move in cartoon modes
                }}, new EarthMoon() {{
                    earth.radius *= 15;
                    moon.radius *= 15;
                    forceScale *= 0.77;//so that default gravity force takes up 1/2 cell in grid
                }}, new EarthSpaceStation() {{
                    earth.radius *= 0.8;
                    spaceStation.radius *= 8;
                }} );
    }
}

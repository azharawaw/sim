// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty, final double alpha ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new SunEarth() {{
                   sun.radius *= 50;
                   earth.radius *= 1100;
                   //Make the sun move as it does in the SunEarthMoon mode
                   final int earthMassScaleFactor = 20000;
                   earth.mass *= earthMassScaleFactor;
                   forceScale *= 0.8 / earthMassScaleFactor * 0.75;//to balance increased mass
               }}, new SunEarthMoon() {{
                    sun.radius *= 50;
                    earth.radius *= 1100;
                    moon.radius *= 800;

                    final int earthMassScaleFactor = 20000;
                    earth.mass *= earthMassScaleFactor;
                    moon.vx *= alpha;
                    moon.y = earth.radius * 2;
                    moon.mass *= earthMassScaleFactor;
                    forceScale *= 0.8 / earthMassScaleFactor * 0.75;//to balance increased mass

                    double distBetweenEarthAndMoon = earth.getPosition().minus( moon.getPosition() ).getMagnitude();
                    System.out.println( "distBetweenEarthAndMoon = " + distBetweenEarthAndMoon );

                    double distBetweenEarthAndSun = earth.getPosition().minus( sun.getPosition() ).getMagnitude();
                    System.out.println( "distBetweenEarthAndSun = " + distBetweenEarthAndSun );

                }}, new EarthMoon() {{
                    earth.radius *= 15;
                    moon.radius *= 15;
                    forceScale *= 0.75;//so that default gravity force takes up 1/2 cell in grid
                }}, new EarthSpaceStation() {{
                    earth.radius *= 0.8;
                    spaceStation.radius *= 8;
                }} );
    }
}

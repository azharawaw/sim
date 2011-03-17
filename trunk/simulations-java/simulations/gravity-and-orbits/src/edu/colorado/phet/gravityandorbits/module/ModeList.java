// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication.RESOURCES;
import static edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock.DEFAULT_DT;
import static edu.colorado.phet.gravityandorbits.view.MeasuringTape.milesToMeters;

/**
 * GravityAndOrbitsModeList enumerates and declares the possible modes in the GravityAndOrbitsModule, such as "Sun & Earth" mode.
 *
 * @author Sam Reid
 */
public class ModeList extends ArrayList<GravityAndOrbitsMode> {
    public static final double SUN_RADIUS = 6.955E8;
    public static final double SUN_MASS = 1.989E30;

    public static final double EARTH_RADIUS = 6.371E6;
    public static final double EARTH_MASS = 5.9736E24;
    public static final double EARTH_PERIHELION = 147098290E3;
    public static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = 30300;

    public static final double MOON_MASS = 7.3477E22;
    public static final double MOON_RADIUS = 1737.1E3;
    public static final double MOON_EARTH_SPEED = -1.01E3;
    public static final double MOON_SPEED = MOON_EARTH_SPEED;
    public static final double MOON_PERIGEE = 391370E3;
    public static final double MOON_X = EARTH_PERIHELION;
    public static final double MOON_Y = MOON_PERIGEE;

    //see http://en.wikipedia.org/wiki/International_Space_Station
    public static final double SPACE_STATION_RADIUS = 109;
    public static final double SPACE_STATION_MASS = 369914;
    public static final double SPACE_STATION_SPEED = 7706;
    public static final double SPACE_STATION_PERIGEE = 347000;

    private ModeListParameter p;

    public static abstract class Mode {
        double zoom;
        double dt = DEFAULT_DT;
        protected double forceScale;
        public Line2D.Double initialMeasuringTapeLocation;

        public Mode( double zoom ) {
            this.zoom = zoom;
        }

        public void center() {
            ImmutableVector2D deltaVelocity = getTotalMomentum().times( -1.0 / getTotalMass() );
            for ( BodyPrototype prototype : getBodies() ) {
                prototype.vx += deltaVelocity.getX();
                prototype.vy += deltaVelocity.getY();
            }
        }

        private ImmutableVector2D getTotalMomentum() {
            ImmutableVector2D totalMomentum = new ImmutableVector2D();
            for ( BodyPrototype body : getBodies() ) {
                totalMomentum = totalMomentum.plus( body.getMomentum() );
            }
            return totalMomentum;
        }

        private double getTotalMass() {
            double totalMass = 0.0;
            for ( BodyPrototype prototype : getBodies() ) {
                totalMass += prototype.mass;
            }
            return totalMass;
        }

        protected abstract BodyPrototype[] getBodies();
    }

    public static class SunEarth extends Mode {
        BodyPrototype sun = new BodyPrototype( SUN_MASS, SUN_RADIUS, 0, 0, 0, 0 );
        BodyPrototype earth = new BodyPrototype( EARTH_MASS, EARTH_RADIUS, EARTH_PERIHELION, 0, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION );

        public SunEarth() {
            super( 1.25 );
            initialMeasuringTapeLocation = new Line2D.Double( ( sun.x + earth.x ) / 3, -earth.x / 2, ( sun.x + earth.x ) / 3 + milesToMeters( 50000000 ), -earth.x / 2 );
            forceScale = VectorNode.FORCE_SCALE * 120;
        }

        @Override
        protected BodyPrototype[] getBodies() {
            return new BodyPrototype[] { sun, earth };
        }
    }

    public static class SunEarthMoon extends Mode {
        BodyPrototype sun = new BodyPrototype( SUN_MASS, SUN_RADIUS, 0, 0, 0, 0 );
        BodyPrototype earth = new BodyPrototype( EARTH_MASS, EARTH_RADIUS, EARTH_PERIHELION, 0, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION );
        BodyPrototype moon = new BodyPrototype( MOON_MASS, MOON_RADIUS, MOON_X, MOON_Y, MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION );

        public SunEarthMoon() {
            super( 1.25 );
            initialMeasuringTapeLocation = new Line2D.Double( ( sun.x + earth.x ) / 3, -earth.x / 2, ( sun.x + earth.x ) / 3 + milesToMeters( 50000000 ), -earth.x / 2 );
            forceScale = VectorNode.FORCE_SCALE * 120;
        }

        @Override
        protected BodyPrototype[] getBodies() {
            return new BodyPrototype[] { sun, earth, moon };
        }
    }

    public static class EarthMoon extends Mode {
        BodyPrototype earth = new BodyPrototype( EARTH_MASS, EARTH_RADIUS, EARTH_PERIHELION, 0, 0, 0 );
        BodyPrototype moon = new BodyPrototype( MOON_MASS, MOON_RADIUS, MOON_X, MOON_Y, MOON_SPEED, 0 );

        public EarthMoon() {
            super( 400 );
            initialMeasuringTapeLocation = new Line2D.Double( earth.x + earth.radius * 2, -moon.y * 0.7, earth.x + earth.radius * 2 + milesToMeters( 100000 ), -moon.y * 0.7 );
            forceScale = VectorNode.FORCE_SCALE * 45;
        }

        @Override
        protected BodyPrototype[] getBodies() {
            return new BodyPrototype[] { earth, moon };
        }
    }

    public static class EarthSpaceStation extends Mode {
        BodyPrototype earth = new BodyPrototype( EARTH_MASS, EARTH_RADIUS, 0, 0, 0, 0 );
        BodyPrototype spaceStation = new BodyPrototype( SPACE_STATION_MASS, SPACE_STATION_RADIUS, SPACE_STATION_PERIGEE + EARTH_RADIUS + SPACE_STATION_RADIUS, 0, 0, SPACE_STATION_SPEED );

        public EarthSpaceStation() {
            super( 21600 );
            initialMeasuringTapeLocation = new Line2D.Double( 3162119, 7680496, 6439098, 7680496 );//Sampled at runtime from MeasuringTape
            forceScale = VectorNode.FORCE_SCALE * 3E13;
        }

        @Override
        protected BodyPrototype[] getBodies() {
            return new BodyPrototype[] { earth, spaceStation };
        }
    }

    public ModeList( final ModeListParameter p,
                     final SunEarth sunEarth,
                     final SunEarthMoon sunEarthMoon,
                     final EarthMoon earthMoon,
                     final EarthSpaceStation earthSpaceStation ) {
        sunEarth.center();
        sunEarthMoon.center();
        earthMoon.center();
        earthSpaceStation.center();
        this.p = p;
        Function2<BodyNode, Property<Boolean>, PNode> readoutInEarthMasses = new Function2<BodyNode, Property<Boolean>, PNode>() {
            public PNode apply( BodyNode bodyNode, Property<Boolean> visible ) {
                return new EarthMassReadoutNode( bodyNode, visible );
            }
        };

        //Create the modes.
        int SEC_PER_YEAR = 365 * 24 * 60 * 60;
        final double SUN_MODES_VELOCITY_SCALE = 4.48E6;
        add( new GravityAndOrbitsMode( GAOStrings.SUN_AND_PLANET,
                                       sunEarth.forceScale,
                                       false,
                                       sunEarth.dt,
                                       days,
                                       createIconImage( true, true, false, false ),
                                       SEC_PER_YEAR,
                                       SUN_MODES_VELOCITY_SCALE,
                                       readoutInEarthMasses,
                                       sunEarth.initialMeasuringTapeLocation,
                                       sunEarth.zoom,
                                       new ImmutableVector2D( 0, 0 ),
                                       sunEarth.earth.x / 2,
                                       new Point2D.Double( 0, 0 ),
                                       p ) {{
            addBody( createSun( getMaxPathLength(), sunEarth.sun ) );
            addBody( createEarth( getMaxPathLength(), sunEarth.earth ) );
        }} );
        add( new GravityAndOrbitsMode( GAOStrings.SUN_PLANET_AND_MOON,
                                       sunEarthMoon.forceScale,
                                       false,
                                       sunEarthMoon.dt,
                                       days,
                                       createIconImage( true, true, true, false ),
                                       SEC_PER_YEAR,
                                       SUN_MODES_VELOCITY_SCALE,
                                       readoutInEarthMasses,
                                       sunEarthMoon.initialMeasuringTapeLocation,
                                       sunEarthMoon.zoom,
                                       new ImmutableVector2D( 0, 0 ),
                                       sunEarthMoon.earth.x / 2,
                                       new Point2D.Double( 0, 0 ),
                                       p ) {{
            addBody( createSun( getMaxPathLength(), sunEarthMoon.sun ) );
            addBody( createEarth( getMaxPathLength(), sunEarthMoon.earth ) );
            addBody( createMoon( false,//no room for the slider
                                 getMaxPathLength(),
                                 false, sunEarthMoon.moon ) );//so it doesn't intersect with earth mass readout
        }} );
        int SEC_PER_MOON_ORBIT = 28 * 24 * 60 * 60;
        add( new GravityAndOrbitsMode( GAOStrings.PLANET_AND_MOON,
                                       earthMoon.forceScale,
                                       false,
                                       GravityAndOrbitsClock.DEFAULT_DT / 3,
                                       days,
                                       createIconImage( false, true, true, false ),
                                       SEC_PER_MOON_ORBIT,
                                       SUN_MODES_VELOCITY_SCALE * 0.06,
                                       readoutInEarthMasses,
                                       earthMoon.initialMeasuringTapeLocation,
                                       earthMoon.zoom,
                                       new ImmutableVector2D( earthMoon.earth.x, 0 ),
                                       earthMoon.moon.y / 2,
                                       new Point2D.Double( earthMoon.earth.x, 0 ),
                                       p ) {{
            //scale so it is a similar size to other modes
            addBody( createEarth( getMaxPathLength(), earthMoon.earth ) );
            addBody( createMoon( true, getMaxPathLength(), true, earthMoon.moon ) );
        }} );
        Function2<BodyNode, Property<Boolean>, PNode> spaceStationMassReadoutFactory = new Function2<BodyNode, Property<Boolean>, PNode>() {
            public PNode apply( BodyNode bodyNode, Property<Boolean> visible ) {
                return new SpaceStationMassReadoutNode( bodyNode, visible );
            }
        };
        add( new GravityAndOrbitsMode( GAOStrings.PLANET_AND_SPACE_STATION,
                                       earthSpaceStation.forceScale,
                                       false,
                                       GravityAndOrbitsClock.DEFAULT_DT * 9E-4,
                                       minutes,
                                       createIconImage( false, true, false, true ),
                                       5400,
                                       SUN_MODES_VELOCITY_SCALE / 10000,
                                       spaceStationMassReadoutFactory,
                                       earthSpaceStation.initialMeasuringTapeLocation,
                                       earthSpaceStation.zoom,
                                       new ImmutableVector2D( earthSpaceStation.earth.x, 0 ),
                                       ( earthSpaceStation.spaceStation.x - earthSpaceStation.earth.x ) * 15,
                                       new Point2D.Double( earthSpaceStation.earth.x, 0 ),
                                       p ) {{
            addBody( createEarth( getMaxPathLength(), earthSpaceStation.earth ) );

            addBody( new Body( GAOStrings.SATELLITE, earthSpaceStation.spaceStation.x, earthSpaceStation.spaceStation.y,
                               earthSpaceStation.spaceStation.radius * 2000,
                               earthSpaceStation.spaceStation.vx, earthSpaceStation.spaceStation.vy, earthSpaceStation.spaceStation.mass, Color.gray, Color.white,
                               getImageRenderer( "space-station.png" ), -Math.PI / 4, true, getMaxPathLength(), true,
                               earthSpaceStation.spaceStation.mass, GAOStrings.SPACE_STATION, p.clockPausedProperty, p.stepping, p.rewinding ) );
        }} );
    }

    //Creates an image that can be used for the mode icon, showing the nodes of each body in the mode.
    private Image createIconImage( final boolean sun, final boolean earth, final boolean moon, final boolean spaceStation ) {
        return new PNode() {
            {
                int inset = 5;//distance between icons
                addChild( new PhetPPath( new Rectangle2D.Double( 20, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ) );
                addIcon( inset, new PImage( new BodyRenderer.SphereRenderer( Color.yellow, Color.white, 30 ).toImage() ), sun );
                addIcon( inset, new PImage( multiScaleToWidth( RESOURCES.getImage( "earth_satellite.gif" ), 30 ) ), earth );
                addIcon( inset, new PImage( multiScaleToWidth( RESOURCES.getImage( "moon.png" ), 30 ) ), moon );
                addIcon( inset, new PImage( multiScaleToWidth( RESOURCES.getImage( "space-station.png" ), 30 ) ), spaceStation );
            }

            private void addIcon( int inset, PNode icon, boolean visible ) {
                addChild( icon );
                icon.setOffset( getFullBounds().getMaxX() + inset + icon.getFullBounds().getWidth() / 2, 0 );
                icon.setVisible( visible );
            }
        }.toImage();
    }

    private Body createMoon( boolean massSettable, int maxPathLength, final boolean massReadoutBelow, BodyPrototype body ) {
        return new Body( GAOStrings.MOON, body.x, body.y, body.radius * 2, body.vx, body.vy, body.mass, Color.magenta, Color.white,
                         //putting this number too large makes a kink or curly-q in the moon trajectory, which should be avoided
                         getRenderer( "moon.png", body.mass ), -3 * Math.PI / 4, massSettable, maxPathLength,
                         massReadoutBelow, body.mass, GAOStrings.OUR_MOON, p.clockPausedProperty, p.stepping, p.rewinding ) {
            @Override
            protected void doReturnBody( GravityAndOrbitsModel model ) {
                super.doReturnBody( model );
                Body earth = model.getBody( "Planet" );
                //Restore the moon near the earth and with the same relative velocity vector
                if ( earth != null ) {
                    ImmutableVector2D relativePosition = getPositionProperty().getInitialValue().minus( earth.getPositionProperty().getInitialValue() );
                    getPositionProperty().setValue( earth.getPosition().plus( relativePosition ) );

                    ImmutableVector2D relativeVelocity = getVelocityProperty().getInitialValue().minus( earth.getVelocityProperty().getInitialValue() );
                    getVelocityProperty().setValue( earth.getVelocity().plus( relativeVelocity ) );
                }
                else {
                    throw new RuntimeException( "Couldn't find planet." );
                }
            }
        };
    }

    private Body createEarth( int maxPathLength, BodyPrototype body ) {
        return new Body( GAOStrings.PLANET, body.x, body.y, body.radius * 2, body.vx, body.vy, body.mass, Color.gray, Color.lightGray,
                         getRenderer( "earth_satellite.gif", body.mass ), -Math.PI / 4, true,
                         maxPathLength, true, body.mass, GAOStrings.EARTH, p.clockPausedProperty, p.stepping, p.rewinding );
    }

    private Body createSun( int maxPathLength, BodyPrototype body ) {
        return new Body( GAOStrings.SUN, body.x, body.y, body.radius * 2, body.vx, body.vy, body.mass, Color.yellow, Color.white,
                         SUN_RENDERER, -Math.PI / 4, true, maxPathLength, true, body.mass, GAOStrings.OUR_SUN, p.clockPausedProperty, p.stepping, p.rewinding );
    }

    public static Function2<Body, Double, BodyRenderer> getImageRenderer( final String image ) {
        return new Function2<Body, Double, BodyRenderer>() {
            public BodyRenderer apply( Body body, Double viewDiameter ) {
                return new BodyRenderer.ImageRenderer( body, viewDiameter, image );
            }
        };
    }

    public static Function2<Body, Double, BodyRenderer> getRenderer( final String image, final double targetMass ) {//the mass for which to use the image
        return new Function2<Body, Double, BodyRenderer>() {
            public BodyRenderer apply( Body body, Double viewDiameter ) {
                return new BodyRenderer.SwitchableBodyRenderer( body, targetMass, new BodyRenderer.ImageRenderer( body, viewDiameter, image ),
                                                                new BodyRenderer.SphereRenderer( body, viewDiameter ) );
            }
        };
    }

    private final Function2<Body, Double, BodyRenderer> SUN_RENDERER = new Function2<Body, Double, BodyRenderer>() {
        public BodyRenderer apply( Body body, Double viewDiameter ) {
            return new BodyRenderer.SphereRenderer( body, viewDiameter );
        }
    };

    private static final Function1<Double, String> days = new Function1<Double, String>() {
        public String apply( Double time ) {
            int value = (int) ( time / GravityAndOrbitsClock.SECONDS_PER_DAY );
            String units = ( value == 1 ) ? GAOStrings.EARTH_DAY : GAOStrings.EARTH_DAYS;
            return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
        }
    };

    private static final Function1<Double, String> minutes = new Function1<Double, String>() {
        final double SECONDS_PER_MINUTE = 60;

        public String apply( Double time ) {
            int value = (int) ( time / SECONDS_PER_MINUTE );
            String units = ( value == 1 ) ? GAOStrings.EARTH_MINUTE : GAOStrings.EARTH_MINUTES;
            return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
        }
    };
}
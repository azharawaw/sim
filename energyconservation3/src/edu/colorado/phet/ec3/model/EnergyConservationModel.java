/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SplineSurface;

import java.awt.geom.Area;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:16 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EnergyConservationModel {
    public static final double G_EARTH = -9.81;
    public static final double G_MOON = -1.62;
    public static final double G_JUPITER = -25.95;

    private double time = 0;
    private ArrayList history = new ArrayList();
    private ArrayList bodies = new ArrayList();
    private ArrayList floors = new ArrayList();
    private ArrayList splineSurfaces = new ArrayList();
    private double gravity = G_EARTH;
    private double zeroPointPotentialY;
    private ArrayList listeners = new ArrayList();
    private boolean recordPath = false;
    private double initZeroPointPotentialY;
    private PotentialEnergyMetric potentialEnergyMetric;

    public EnergyConservationModel( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
        this.initZeroPointPotentialY = zeroPointPotentialY;
        potentialEnergyMetric = new PotentialEnergyMetric() {
            public double getPotentialEnergy( Body body ) {
                return copy().getPotentialEnergy( body );
            }

            public double getGravity() {
                return gravity;
            }

            public PotentialEnergyMetric copy() {
                return new ImmutablePotentialEnergyMetric( EnergyConservationModel.this.zeroPointPotentialY, gravity );
            }
        };
    }

    public int numSplineSurfaces() {
        return splineSurfaces.size();
    }

    public double getTime() {
        return time;
    }

    public void setRecordPath( boolean selected ) {
        this.recordPath = selected;
    }

    public boolean isRecordPath() {
        return recordPath;
    }

    public boolean containsBody( Body body ) {
        return bodies.contains( body );
    }

    public void clearPaths() {
        history.clear();
    }

    public void clearHeat() {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.clearHeat();
        }
    }

    public void setGravity( double value ) {
        if( this.gravity != value ) {
            this.gravity = value;
            for( int i = 0; i < listeners.size(); i++ ) {
                EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
                energyModelListener.gravityChanged();
            }
        }
    }

    public void removeEnergyModelListener( EnergyModelListener energyModelListener ) {
        listeners.remove( energyModelListener );
    }

    public boolean isSplineUserControlled() {
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            if( splineSurface.isUserControlled() ) {
                return true;
            }
        }
        return false;
    }

    public void splineTranslated( SplineSurface splineGraphic, double dx, double dy ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( splineGraphic ) ) {
                body.translate( dx, dy );
                body.notifyObservers();
            }
        }
    }

    public PotentialEnergyMetric getPotentialEnergyMetric() {
        return potentialEnergyMetric;
    }

    static interface EnergyConservationModelListener {
        public void numBodiesChanged();

        public void numFloorsChanged();

        public void numSplinesChanged();

        public void paramChanged();
    }

    public EnergyConservationModel copyState() {
        EnergyConservationModel copy = new EnergyConservationModel( zeroPointPotentialY );
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            copy.bodies.add( body.copyState() );
        }
        for( int i = 0; i < floors.size(); i++ ) {
            Floor floor = (Floor)floors.get( i );
            copy.floors.add( floor.copyState() );
        }
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface surface = splineSurfaceAt( i );
            copy.splineSurfaces.add( surface.copy() );
        }
        copy.history = new ArrayList( history );
        copy.time = time;
        copy.gravity = gravity;
        return copy;
    }

    public void setState( EnergyConservationModel model ) {
        bodies.clear();
        floors.clear();
        splineSurfaces.clear();
        for( int i = 0; i < model.bodies.size(); i++ ) {
            bodies.add( model.bodyAt( i ).copyState() );
        }
        for( int i = 0; i < model.splineSurfaces.size(); i++ ) {
            splineSurfaces.add( model.splineSurfaceAt( i ).copy() );
        }
        for( int i = 0; i < model.floors.size(); i++ ) {
            floors.add( model.floorAt( i ).copyState() );
        }
        this.history.clear();
        this.history.addAll( model.history );
        this.time = model.time;
        this.gravity = model.gravity;
    }

    public double timeSinceLastHistory() {
        if( history.size() == 0 ) {
            return time;
        }
        return time - historyPointAt( history.size() - 1 ).getTime();
    }

    public void stepInTime( double dt ) {
//        System.out.println( "numSplineSurfaces() = " + numSplineSurfaces() );
        time += dt;
//        System.out.println( "time = " + time );

        if( recordPath && numBodies() > 0 && timeSinceLastHistory() > 0.1 ) {
            history.add( new HistoryPoint( this, bodyAt( 0 ) ) );
        }
//        System.out.println( "EnergyConservationModel.stepInTime" );
        for( int i = 0; i < listeners.size(); i++ ) {
            EnergyModelListener energyModelListener = (EnergyModelListener)listeners.get( i );
            energyModelListener.preStep( dt );
        }
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.stepInTime( dt );
        }
        for( int i = 0; i < floors.size(); i++ ) {
            floorAt( i ).stepInTime( dt );
        }
        doGrabs();
    }

    private void doGrabs() {
//        System.out.println( "bodies.size() = " + bodies.size() );
        for( int i = 0; i < bodies.size(); i++ ) {
            if( bodyAt( i ).isFreeFallMode() ) {
                doGrab( bodyAt( i ) );
            }
        }
    }

    private ArrayList getAllSplines() {
        ArrayList list = new ArrayList();
        for( int i = 0; i < splineSurfaces.size(); i++ ) {
            SplineSurface splineSurface = (SplineSurface)splineSurfaces.get( i );
            list.add( splineSurface.getTop() );
            list.add( splineSurface.getBottom() );
        }
        return list;
    }

    private void doGrab( Body body ) {
        double bestScore = Double.POSITIVE_INFINITY;
        AbstractSpline bestSpline = null;
        ArrayList allSplines = getAllSplines();
//        System.out.println( "allSplines.size() = " + allSplines.size() );
        for( int i = 0; i < allSplines.size(); i++ ) {
            AbstractSpline splineSurface = (AbstractSpline)allSplines.get( i );
            double score = getGrabScore( splineSurface, body );
            if( !Double.isInfinite( score ) ) {
//                System.out.println( "score[" + i + "] = " + score );
            }
            if( score < bestScore ) {
                bestScore = score;
                bestSpline = splineSurface;
            }
        }
//        System.out.println( "set spline mode: "+bestSpline );
        if( bestSpline != null ) {
            body.setSplineMode( this, bestSpline );
        }
    }

    boolean intersectsOrig( AbstractSpline spline, Body body ) {
        Area area = new Area( body.getShape() );
        area.intersect( spline.getArea() );
        return !area.isEmpty();
    }

    private double getGrabScore( AbstractSpline spline, Body body ) {
        if( spline.intersects( body.getShape() ) ) {
//            System.out.println( "intersected" );
            double position = 0;
            try {
                position = new SplineLogic( body ).guessPositionAlongSpline( spline );
            }
            catch( NullIntersectionException e ) {
//                e.printStackTrace();
                return Double.POSITIVE_INFINITY;
            }
            Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.

            double bodyYPerp = segment.getUnitNormalVector().dot( body.getPositionVector() );
            double segmentYPerp = segment.getUnitNormalVector().dot( new ImmutableVector2D.Double( segment.getCenter2D() ) );

            double y = ( bodyYPerp - segmentYPerp - body.getHeight() / 2.0 );
//            System.out.println( "y = " + y );
            if( y > -60 ) {//todo what is this magic number?  -20 was too low for the new graphic rectangle (man fell through track)
                return -y;
            }
            else {
                return Double.POSITIVE_INFINITY;
            }

        }
        return Double.POSITIVE_INFINITY;
    }

    public SplineSurface splineSurfaceAt( int i ) {
        return (SplineSurface)splineSurfaces.get( i );
    }

    public Floor floorAt( int i ) {
        return (Floor)floors.get( i );
    }

    public void addSplineSurface( SplineSurface splineSurface ) {
        splineSurfaces.add( splineSurface );
    }

    public void addBody( Body body ) {
        bodies.add( body );
        if( bodies.size() == 1 ) {//The zero point potential now occurs at the center of mass of the skater.
            zeroPointPotentialY = body.getShape().getBounds2D().getHeight() / 2;
            initZeroPointPotentialY = zeroPointPotentialY;
        }
    }

    public int numBodies() {
        return bodies.size();
    }

    public Body bodyAt( int i ) {
        return (Body)bodies.get( i );
    }

    public void addFloor( Floor floor ) {
        floors.add( floor );
    }

    public double getGravity() {
        return gravity;
    }

    public void removeSplineSurface( SplineSurface splineSurface ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body.isOnSpline( splineSurface ) ) {
                body.setFreeFallMode();
            }
        }
        notifyBodiesSplineRemoved( splineSurface.getTop() );
        notifyBodiesSplineRemoved( splineSurface.getBottom() );
        splineSurfaces.remove( splineSurface );
    }

    private void notifyBodiesSplineRemoved( AbstractSpline spline ) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.splineRemoved( spline );
        }
    }

    public double getZeroPointPotentialY() {
        return zeroPointPotentialY;
    }

    public void setZeroPointPotentialY( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
    }

    public void translateZeroPointPotentialY( double dy ) {
        setZeroPointPotentialY( getZeroPointPotentialY() + dy );
    }

    public void reset() {
        bodies.clear();
        splineSurfaces.clear();
        history.clear();
        gravity = G_EARTH;
        zeroPointPotentialY = initZeroPointPotentialY;
    }

    public static class EnergyModelListenerAdapter implements EnergyModelListener {

        public void preStep( double dt ) {
        }

        public void gravityChanged() {
        }
    }

    public static interface EnergyModelListener {
        void preStep( double dt );

        void gravityChanged();
    }

    public void addEnergyModelListener( EnergyModelListener listener ) {
        listeners.add( listener );
    }

    public int numHistoryPoints() {
        return history.size();
    }

    public HistoryPoint historyPointAt( int i ) {
        return (HistoryPoint)history.get( i );
    }
}

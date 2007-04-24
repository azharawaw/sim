package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import junit.framework.TestCase;

import java.awt.geom.Line2D;
import edu.colorado.phet.energyskatepark.model.SPoint2D;

/**
 * Author: Sam Reid
 * Mar 31, 2007, 7:44:36 PM
 */
public class _TestPassThrough extends TestCase {

    public ParticleTestState getDefaultTestState( double lineX, double speedX ) {
        return new ParticleTestState( new SPoint2D[]{new SPoint2D( lineX, -1 ), new SPoint2D( lineX, +1 )},
                                      new SPoint2D( 0, 0 ), new Vector2D.Double( speedX, 0.0 ) );
    }

    public void runTest( ParticleTestState testState, double dt, int iterations ) {
        System.out.println( "Starting t=0, top=" + testState.getSide() + ", testState.getParticle().getPosition() = " + testState.getParticle().getPosition() );
        boolean origSide = testState.getSide();
        double t = 0;
        for( int i = 0; i < 100; i++ ) {
            testState.stepInTime( dt );
            t += dt;
            boolean side = testState.getSide();
            assertEquals( "Particle passed through at iteration: " + i, origSide, side );
            System.out.println( "t=" + t + ", top=" + testState.getSide() + ", testState.getParticle().getPosition() = " + testState.getParticle().getPosition() );
        }
    }

    public void testFastPassThrough2() {
        ParticleTestState testState = getDefaultTestState( 500, 1000 );
        runTest( testState, 1.0, 5 );
    }

    public void testFastPassThrough() {
        ParticleTestState testState = getDefaultTestState( 1, 1.5 );
        runTest( testState, 1.0, 100 );
    }

    public void testSlowPassThrough() {
        ParticleTestState testState = getDefaultTestState( 1, 0.1 );
        runTest( testState, 1.0, 100 );
    }

//    public void testPointSegmentDistance2() {
//        double dist = pointSegmentDistance( new SPoint2D( 1.0, 0 ), new Line2D.Double( 0.0, 0.0, 2.0, 0.0 ) );
//        System.out.println( "dist = " + dist );
//        assertEquals( "point line distance should be almost zero", 0.0, dist, 1E-4 );
//    }

    public void testPointSegmentDistance() {
        double dist = Particle.pointSegmentDistance( new SPoint2D( 1.0, 0 ), new Line2D.Double( 0.0, 0.0, 2.0, 0.0 ) );
        System.out.println( "dist = " + dist );
        assertEquals( "point line distance should be almost zero", 0.0, dist, 1E-4 );
    }

    public static class ParticleTestState {
        private ParametricFunction2D parametricFunction2D;
        private Particle particle;

        public ParticleTestState( SPoint2D[] controlPoints, SPoint2D position, AbstractVector2D velocity ) {
            parametricFunction2D = new CubicSpline2D( controlPoints );
            particle = new Particle( parametricFunction2D );
            particle.setGravity( 0.0 );
            particle.setPosition( position );
            particle.setVelocity( velocity );
        }

        public boolean getSide() {
            return particle.isAboveSpline( 0 );
        }

        public void stepInTime( double dt ) {
            particle.stepInTime( dt );
        }

        public Particle getParticle() {
            return particle;
        }
    }
}

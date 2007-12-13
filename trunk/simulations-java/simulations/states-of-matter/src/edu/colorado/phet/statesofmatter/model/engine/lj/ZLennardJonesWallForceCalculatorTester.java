package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.awt.geom.Line2D;

public class ZLennardJonesWallForceCalculatorTester extends TestCase {
    private static final double RMIN    = StatesOfMatterConfig.RMIN;
    private static final double EPSILON = StatesOfMatterConfig.EPSILON;
    private static final double EQUALITY_EPS = 0.0000001;

    private static final Line2D.Double     WALL  = new Line2D.Double(1, 1, -1, -1);
    private static final LennardJonesForce FORCE = new LennardJonesForce(EPSILON, RMIN);

    private volatile StatesOfMatterParticle p;
    private double[] forces = new double[2];


    public void setUp() {
        p = new StatesOfMatterParticle(0, 1, 1, 1);

        for (int i = 0; i < forces.length; i++) {
            forces[i] = 0.0;
        }
    }

    public void testForceIsZeroAtRMinFromWall() {
        calcForceAtDist(RMIN);

        assertEquals(forces[0], 0.0, EQUALITY_EPS);
        assertEquals(forces[1], 0.0, EQUALITY_EPS);
    }


    public void testForceIsRepulsiveAtLessThanRMinFromWall() {
        calcForceAtDist(0.5 * RMIN);

        assertTrue(forces[0] > 0);
        assertTrue(forces[1] < 0);
    }

    public void testForceIsZeroAtGreaterThanRMinFromWall() {
        calcForceAtDist(1.1 * RMIN);

        assertEquals(0, forces[0], EQUALITY_EPS);
        assertEquals(0, forces[1], EQUALITY_EPS);
    }

    public void testForceIsRepulsiveBehindWall() {
        calcForceAtDist(-1.0 * RMIN);

        assertTrue(forces[0] > 0);
        assertTrue(forces[1] < 0);
    }

    private void calcForceAtDist(double dist) {
        double delta = dist / Math.sqrt(2);

        double t = Math.random() * Math.sqrt(8) - Math.sqrt(8)/2.0;

        p.setX(t + delta);
        p.setY(t - delta);

        LennardJonesWallForceCalculator calculator = new LennardJonesWallForceCalculator(FORCE, WALL);

        assertEquals(WALL.ptLineDist(p.getX(), p.getY()) * MathUtil.signum(dist), dist, EQUALITY_EPS);

        calculator.calculate(p, forces);
    }

}

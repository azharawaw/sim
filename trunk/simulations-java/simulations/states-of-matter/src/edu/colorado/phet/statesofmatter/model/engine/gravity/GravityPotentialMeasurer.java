package edu.colorado.phet.statesofmatter.model.engine.gravity;

import edu.colorado.phet.statesofmatter.model.engine.Measurable;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.util.Iterator;
import java.util.List;

public class GravityPotentialMeasurer implements Measurable {
    private final List particles;
    private final double floor;
    private final double g;

    public GravityPotentialMeasurer(List particles, double floor, double g) {
        this.particles = particles;
        this.floor     = floor;
        this.g         = Math.abs(g);
    }

    public double measure() {
        double potential = 0.0;

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle)iterator.next();

            potential += (floor - particle.getY()) * g * particle.getMass();
        }

        return potential;
    }
}

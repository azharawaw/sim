package edu.colorado.phet.statesofmatter.model;

import junit.framework.TestCase;

import java.util.ArrayList;

public class ZRandomParticleCreationStrategyTester extends TestCase {
    private volatile ParticleCreationStrategy strategy;

    public void setUp() {
        this.strategy = new RandomParticleCreationStrategy();
    }
    
    public void testNewParticleNonNull() {
        assertNotNull(strategy.createNewParticle(new ArrayList()));
    }
}

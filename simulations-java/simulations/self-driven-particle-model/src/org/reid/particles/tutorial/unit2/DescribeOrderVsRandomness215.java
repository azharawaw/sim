/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import org.reid.particles.tutorial.BasicTutorialCanvas;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 1:36:08 AM
 * Copyright (c) Aug 26, 2005 by Sam Reid
 */

public class DescribeOrderVsRandomness215 extends DescribeOrderVsRandomness200 {

    public DescribeOrderVsRandomness215( BasicTutorialCanvas page ) {
        super( page );
        setText( "The phase transition in water from solid to liquid at 0 degrees Celsius is a " +
                 "first order transition, " +
                 "and comes with a latent heat.  The phase transition in the Self-Driven " +
                 "Particle Model is 2nd order-" +
                 "there is no latent heat, but the system exhibits power law behavior at the critical point.\n\t" +
                 " In the next page, we search for the 'critical point', the " +
                 "value of the randomness parameter at which the order parameter drastically drops." );
    }

}

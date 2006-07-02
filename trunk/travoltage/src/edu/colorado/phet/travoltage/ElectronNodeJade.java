/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:07:31 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class ElectronNodeJade extends PNode {
    private Vector2D.Double velocity = new Vector2D.Double();
    private CircleParticleForElectron circleParticleForElectron;

    public ElectronNodeJade( Point2D location ) {
        this.circleParticleForElectron = new CircleParticleForElectron( this );
        circleParticleForElectron.setLocation( location );
        setOffset( location );
        addChild( PImageFactory.create( "images/Electron3.GIF" ) );
        setPickable( false );
        setChildrenPickable( false );
    }

    public void stepInTime( AbstractVector2D vdt, double dt ) {
        this.velocity.add( vdt );
        translate( velocity.getX() * dt, velocity.getY() * dt );
    }

    public double getRadius() {
        return getFullBounds().getWidth() / 2.0;
    }

    public CircleParticleForElectron getCircleParticleForElectron() {
        return circleParticleForElectron;
    }
}

package edu.colorado.phet.electron.wire1d;

import edu.colorado.phet.paint.particle.ParticlePainter;

public class WireParticle {
    double x;
    double v;
    double m;
    Propagator1d p;
    ParticlePainter pp;

    public WireParticle( ParticlePainter pp, Propagator1d p ) {
        this.p = p;
        this.pp = pp;
        m = 1;
    }

    public void setPainter( ParticlePainter pp ) {
        this.pp = pp;
    }

    public ParticlePainter getPainter() {
        return pp;
    }

    public void propagate( double dt ) {
        p.propagate( this, dt );
    }

    public double getPosition() {
        return x;
    }

    public double getVelocity() {
        return v;
    }

    public void setPosition( double x ) {
        this.x = x;
    }

    public void setVelocity( double v ) {
        this.v = v;
        //edu.colorado.phet.util.Debug.traceln("vel="+v);
    }

    public void setAcceleration( double a ) {
    }

    public double getMass() {
        return m;
    }
}

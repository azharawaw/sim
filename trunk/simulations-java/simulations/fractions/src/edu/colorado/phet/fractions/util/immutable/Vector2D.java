// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.util.immutable;

import lombok.Data;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Immutable vector 2D.  Provides a convenient and consistent way of accessing x & y, for use in the immutable models in this sim.
 * Uses Lombok to generate equals/hash code for use in other immutable objects.
 *
 * @author Sam Reid
 */
@Data public class Vector2D {
    public final double x;
    public final double y;

    //Have to provide required args constructor because we provide auxiliary constructors
    public Vector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public Vector2D( Point2D p ) { this( p.getX(), p.getY() ); }

    public Vector2D( ImmutableVector2D p ) { this( p.getX(), p.getY() ); }

    public Vector2D( Point2D end, Point2D start ) { this( end.getX() - start.getX(), end.getY() - start.getY() ); }

    public Vector2D times( double s ) { return new Vector2D( x * s, y * s ); }

    public ImmutableVector2D toImmutableVector2D() { return new ImmutableVector2D( x, y ); }

    public Vector2D plus( double dx, double dy ) { return new Vector2D( x + dx, y + dy ); }

    public Vector2D plus( Vector2D v ) { return new Vector2D( x + v.x, y + v.y ); }

    public Vector2D minus( Vector2D v ) { return new Vector2D( x - v.x, y - v.y ); }

    public Vector2D getInstanceOfMagnitude( double magnitude ) { return times( magnitude / getMagnitude() ); }

    public double getMagnitude() { return Math.sqrt( x * x + y * y ); }

    public double distance( Vector2D v ) {
        double dx = this.x - v.x;
        double dy = this.y - v.y;
        return Math.sqrt( dx * dx + dy * dy );
    }

    public Vector2D plus( Dimension2D delta ) { return new Vector2D( x + delta.getWidth(), y + delta.getHeight() ); }
}
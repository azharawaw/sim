package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SaltLattice;

/**
 * This lattice for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SaltCrystal extends Particle implements Iterable<LatticeConstituent> {
    private ArrayList<LatticeConstituent> latticeConstituents = new ArrayList<LatticeConstituent>();

    //The time the lattice entered the water, if any
    private Option<Double> underwaterTime = new None<Double>();

    //The fractional scale by which to multiply the sizes to make them look a good size on the screen
    private double sizeScale;

    public SaltCrystal( ImmutableVector2D position, SaltLattice lattice, double sizeScale ) {
        super( position );
        this.sizeScale = sizeScale;

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Put the vectors at the same angle so all crystals don't come out at right angles
    private final double angle = Math.random() * 2 * Math.PI;

    //Recursive method to traverse the graph and create particles
    private void fill( SaltLattice lattice, Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition ) {
        final double chlorideRadius = Units.picometersToMeters( 181 ) * sizeScale;
        final double sodiumRadius = Units.picometersToMeters( 102 ) * sizeScale;
        final double spacing = chlorideRadius + sodiumRadius;
        if ( component instanceof SodiumIon ) {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( sodiumRadius, new ImmutableVector2D( 0, 0 ), Color.red ), relativePosition ) );
        }
        else {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( chlorideRadius, new ImmutableVector2D( 0, 0 ), Color.blue ), relativePosition ) );
        }
        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        for ( Bond bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ) );
            }
        }
    }

    private ImmutableVector2D getDelta( double spacing, Bond bond ) {
        if ( bond.type == BondType.LEFT ) { return new ImmutableVector2D( -spacing, 0 ); }
        else if ( bond.type == BondType.RIGHT ) { return new ImmutableVector2D( spacing, 0 ); }
        else if ( bond.type == BondType.UP ) { return new ImmutableVector2D( 0, spacing ); }
        else if ( bond.type == BondType.DOWN ) { return new ImmutableVector2D( 0, -spacing ); }
        else { throw new RuntimeException( "Unknown bond type: " + bond ); }
    }

    public boolean contains( SphericalParticle particle ) {
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            if ( latticeConstituent.particle == particle ) {
                return true;
            }
        }
        return false;
    }

    @Override public void stepInTime( ImmutableVector2D acceleration, double dt ) {
        super.stepInTime( acceleration, dt );
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            latticeConstituent.particle.position.set( position.get().plus( latticeConstituent.location ) );
        }
    }

    //The shape of a lattice is the combined area of its constituents, using bounding rectangles to improve performance
    @Override public Shape getShape() {
        final Rectangle2D bounds2D = latticeConstituents.get( 0 ).particle.getShape().getBounds2D();
        Rectangle2D rect = new Rectangle2D.Double( bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth(), bounds2D.getHeight() );
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            rect = rect.createUnion( latticeConstituent.particle.getShape().getBounds2D() );
        }
        return rect;
    }

    public Iterator<LatticeConstituent> iterator() {
        return latticeConstituents.iterator();
    }

    public boolean isUnderwater() {
        return underwaterTime.isSome();
    }

    public void setUnderwater( double time ) {
        this.underwaterTime = new Some<Double>( time );
    }

    public double getUnderWaterTime() {
        return underwaterTime.get();
    }
}
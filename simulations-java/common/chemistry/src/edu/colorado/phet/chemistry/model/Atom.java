// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Base class for atoms.
 * Inner classes for each specific atom.
 * <p/>
 * Reference for atom radii:
 * Chemistry: The Molecular Nature of Matter and Change, 5th Edition, Silberberg.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Atom {

    private final String symbol;
    private final double radius; // picometers
    private final double electronegativity; // dimensionless, see https://secure.wikimedia.org/wikipedia/en/wiki/Electronegativity
    private final Color color; // color used in visual representations

    public Atom( String symbol, double radius, double electronegativity, Color color ) {
        this.symbol = symbol;
        this.radius = radius;
        this.electronegativity = electronegativity;
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getRadius() {
        return radius;
    }

    public double getElectronegativity() {
        return electronegativity;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return symbol;
    }

    // TODO: should we have common strings for molecules?

    public static class B extends Atom {
        public B() {
            // TODO: grab this color from a model kit (avogadro?)
            super( "B", 85, 2.04, new Color( 255, 160, 122 ) );// peach/salmon colored
        }
    }

    public static class Br extends Atom {
        public Br() {
            super( "Br", 114, 2.96, new Color( 190, 30, 20 ) );// brown
        }
    }

    public static class C extends Atom {
        public C() {
            super( "C", 77, 2.55, new Color( 178, 178, 178 ) );
        }
    }

    public static class Cl extends Atom {
        public Cl() {
            super( "Cl", 100, 3.16, new Color( 153, 242, 57 ) );
        }
    }

    public static class F extends Atom {
        public F() {
            super( "F", 72, 3.98, new Color( 247, 255, 74 ) );
        }
    }

    public static class H extends Atom {
        public H() {
            super( "H", 37, 2.20, Color.WHITE );
        }
    }

    public static class I extends Atom {
        public I() {
            super( "I", 133, 2.66, new Color( 150, 0, 150 ) );
        }
    }

    public static class N extends Atom {
        public N() {
            super( "N", 75, 3.04, Color.BLUE );
        }
    }

    public static class O extends Atom {
        public O() {
            super( "O", 73, 3.44, new Color( 255, 85, 0 ) );
        }
    }

    public static class P extends Atom {
        public P() {
            super( "P", 110, 2.19, new Color( 255, 128, 0 ) );
        }
    }

    public static class S extends Atom {
        public S() {
            super( "S", 103, 2.58, new Color( 212, 181, 59 ) );
        }
    }

    public static class Si extends Atom {
        public Si() {
            // TODO: grab this color from a model kit (avogadro?)
            super( "Si", 118, 1.90, new Color( 100, 100, 150 ) );
        }
    }

    public boolean isSameTypeOfAtom( Atom atom ) {
        return atom.getSymbol().equals( this.getSymbol() ) && atom.getRadius() == this.getRadius() && atom.getColor().equals( this.getColor() );
    }
}

// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;
import edu.colorado.phet.balancingchemicalequations.model.Atom.F;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.N;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.view.molecules.*;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {


    private final Atom[] atoms;
    private final Image image;
    private final String symbol;

    /**
     * Constructor.
     * Order of atoms determines their display order and format of symbol.
     * Image must be provided because there is no general method for
     * creating a visual representation based on a list of atoms.
     *
     * @param atoms
     * @param image
     */
    public Molecule( Atom[] atoms, Image image ) {
        this.symbol = createSymbol( atoms );
        this.image = image;
        this.atoms = atoms;
    }

    protected Molecule( Atom[] atoms, PNode node ) {
        this( atoms, node.toImage() );
    }

    public String getSymbol() {
        return symbol;
    }

    public Image getImage() {
        return image;
    }

    public Atom[] getAtoms() {
        return atoms;
    }

    /*
     * Creates an HTML symbol based on the list of atoms in the molecule.
     * The atoms must be specified in order of appearance in the symbol.
     * For example [C,C,H,H,H] becomes C2H4.
     */
    private static final String createSymbol( Atom[] atoms ) {
        StringBuffer b = new StringBuffer();
        int atomCount = 1;
        for ( int i = 0; i < atoms.length; i++ ) {
            if ( i == 0 ) {
                b.append( atoms[i].getSymbol() );
            }
            else if ( atoms[i].getClass().equals( atoms[i-1].getClass() ) ) {
                atomCount++;
            }
            else {
                if ( atomCount > 1 ) {
                    b.append( String.valueOf( atomCount ) );
                }
                atomCount = 1;
                b.append( atoms[i].getSymbol() );
            }
        }
        if ( atomCount > 1 ) {
            b.append( String.valueOf( atomCount ) );
        }
        return toSubscript( b.toString() );
    }

    /*
     * Handles HTML subscript formatting of molecule symbols.
     * All numbers in a string are assumed to be part of a subscript, and will be enclosed in a <sub> tag.
     * For example, "C2H4" is converted to "C<sub>2</sub>H<sub>4</sub>".
     */
    private static final String toSubscript( String inString ) {
        String outString = "";
        boolean sub = false; // are we in a <sub> tag?
        for ( int i = 0; i < inString.length(); i++ ) {
            final char c = inString.charAt( i );
            if ( !sub && Character.isDigit( c ) ) {
                // start the subscript tag when a digit is found
                outString += "<sub>";
                sub = true;
            }
            else if ( sub && !Character.isDigit( c ) ) {
                // end the subscript tag when a non-digit is found
                outString += "</sub>";
                sub = false;
            }
            outString += c;
        }
        // end the subscript tag if the string ends with a digit
        if ( sub ) {
            outString += "</sub>";
            sub = false;
        }
        return outString;
    }

    public static class CH4 extends Molecule {

        public CH4() {
            super( new Atom[] { new C(), new H(), new H(), new H(), new H() }, new CH4Node() );
        }
    }

    public static class Cl2 extends Molecule {

        public Cl2() {
            super( new Atom[] { new Cl(), new Cl() }, new Cl2Node() );
        }
    }

    public static class CO2 extends Molecule {

        public CO2() {
            super( new Atom[] { new C(), new O(), new O() }, new CO2Node() );
        }
    }

    public static class F2 extends Molecule {

        public F2() {
            super( new Atom[] { new F(), new F() }, new F2Node() );
        }
    }

    public static class H2 extends Molecule {

        public H2() {
            super( new Atom[] { new H(), new H() }, new H2Node() );
        }
    }

    public static class H2O extends Molecule {

        public H2O() {
            super( new Atom[] { new H(), new H(), new O() }, new H2ONode() );
        }
    }

    public static class HCl extends Molecule {

        public HCl() {
            super( new Atom[] { new H(), new Cl() }, new HClNode() );
        }
    }

    public static class HF extends Molecule {

        public HF() {
            super( new Atom[] { new H(), new F() }, new HFNode() );
        }
    }

    public static class N2 extends Molecule {

        public N2() {
            super( new Atom[] { new N(), new N() }, new N2Node() );
        }
    }

    public static class NH3 extends Molecule {

        public NH3() {
            super( new Atom[] { new N(), new H(), new H(), new H() }, new NH3Node() );
        }
    }

    public static class O2 extends Molecule {

        public O2() {
            super( new Atom[] { new O(), new O() }, new O2Node() );
        }
    }
}

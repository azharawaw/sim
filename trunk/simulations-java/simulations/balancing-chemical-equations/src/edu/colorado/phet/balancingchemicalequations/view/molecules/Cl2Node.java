// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.Cl;

/**
 * CL2 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Cl2Node extends TwoAtomNode {

    public Cl2Node() {
        super( new Cl(), new Cl() );
    }
}
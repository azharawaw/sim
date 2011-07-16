// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.OpenSite;

/**
 * Model for growing a calcium chloride lattice, using the same 2d crystal structure as in Soluble Salts.
 *
 * @author Sam Reid
 */
public class OpenCalciumChlorideSite extends OpenSite {
    public OpenCalciumChlorideSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice grow( Lattice lattice ) {
        Component newIon = ( component instanceof Component.ChlorideIon ) ? new Component.CalciumIon() : new Component.ChlorideIon();
        return new CalciumChlorideLattice( new ImmutableList<Component>( lattice.components, newIon ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newIon, type ) ) );
    }
}

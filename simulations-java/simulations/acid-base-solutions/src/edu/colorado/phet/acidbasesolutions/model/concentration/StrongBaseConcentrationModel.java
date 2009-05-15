package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Water;


public class StrongBaseConcentrationModel extends BaseConcentrationModel {
    
    protected StrongBaseConcentrationModel( Solute solute ) {
        super( solute );
    }
    
    // [MOH] = 0
    public double getBaseConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getMetalConcentration() {
        return getSolute().getConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getH3OConcentration() {
        return Water.getEquilibriumConstant() / getOHConcentration();
    }
    
    // [OH-] = c
    public double getOHConcentration() {
        return getSolute().getConcentration();
    }
    
    // [H2O] = W
    public double getH2OConcentration() {
        return Water.getConcentration();
    }
    
    public int getMetalMoleculeCount() {
        return getMoleculeCount( getMetalConcentration() );
    }
}

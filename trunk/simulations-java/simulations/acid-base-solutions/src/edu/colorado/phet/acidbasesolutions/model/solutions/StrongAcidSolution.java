package edu.colorado.phet.acidbasesolutions.model.solutions;

import java.util.ArrayList;

import edu.colorado.phet.acidbasesolutions.model.PureWater;
import edu.colorado.phet.acidbasesolutions.model.acids.IStrongAcid;


public class StrongAcidSolution extends AbstractAcidSolution {
    
    private final IStrongAcid _acid;
    
    public StrongAcidSolution( IStrongAcid acid, double c ) {
        super( acid, c );
        _acid = acid;
    }
    
    public IStrongAcid getStrongAcid() {
        return _acid;
    }
    
    // [HA] = 0
    public double getAcidConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getConjugateBaseConcentration() {
        return getInitialAcidConcentration();
    }
    
    // [H3O+] = c
    public double getHydroniumConcentration() {
        return getInitialAcidConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getHydroxideConcentration() {
        return PureWater.getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [H2O] = W - c
    public double getWaterConcentration() {
        return PureWater.getConcentration() - getInitialAcidConcentration();
    }
}

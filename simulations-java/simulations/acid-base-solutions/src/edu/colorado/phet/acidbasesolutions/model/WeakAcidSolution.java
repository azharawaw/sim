package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.model.Acid.WeakAcid;



public class WeakAcidSolution {

    private final WeakAcid acid;
    private double initialConcentration;
    
    public WeakAcidSolution( WeakAcid acid, double initialConcentration ) {
        this.acid = acid;
        this.initialConcentration = initialConcentration;
    }
    
    public WeakAcid getAcid() {
        return acid;
    }
    
    // c
    public void setInitialAcidConcentration( double initialConcentration ) {
        if ( initialConcentration != this.initialConcentration ) {
            this.initialConcentration = initialConcentration;
        }
    }
    
    public double getInitialConcentration() {
        return initialConcentration;
    }
    
    // [HA] = c - [A-]
    public double getAcidConcentration() {
        return getInitialConcentration() - getConjugateBaseConcentration();
    }
    
    // [A-]
    public double getConjugateBaseConcentration() {
        final double Ka = acid.getStrength();
        final double c = getInitialConcentration();
        return -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) );
    }
    
    // [H3O+] = [A-]
    public double getH3OConcentration() {
        return getConjugateBaseConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return PureWater.getInstance().getEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getH2OConcentration() {
        return PureWater.getInstance().getConcentration() - getConjugateBaseConcentration();
    }
}

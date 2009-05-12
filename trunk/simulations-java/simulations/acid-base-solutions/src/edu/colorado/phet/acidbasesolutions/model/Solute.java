package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class Solute extends Molecule {
    
    private double strength;
    private final ArrayList<SoluteListener> listeners;
    
    protected Solute( String name, String symbol, double strength ) {
        super( name, symbol );
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        this.strength = strength;
        listeners = new ArrayList<SoluteListener>();
    }
    
    protected abstract boolean isValidStrength( double strength );
    
    protected void setStrength( double strength ) {
        if ( !isValidStrength( strength ) ) {
            throw new IllegalArgumentException( "strength is invalid: " + strength );
        }
        if ( strength != this.strength ) {
            this.strength = strength;
            notifyStrengthChanged();
        }
    }
    
    public double getStrength() {
        return strength;
    }
    
    public interface SoluteListener {
        public void strengthChanged();
    }
    
    public void addSoluteListener( SoluteListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSoluteListener( SoluteListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator<SoluteListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().strengthChanged();
        }
    }
}

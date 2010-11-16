package edu.colorado.phet.buildanatom.modules.game.view;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.PeriodicTableNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * An atom that simply tracks the quantities of the various subatomic
 * particles, but doesn't actually create them.  This can be used in many
 * cases where no subatomic particles are needed.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class SimpleAtom extends SimpleObservable implements IAtom {

    private int numProtons = 0;
    private int numNeutrons = 0;
    private int numElectrons = 0;

    /**
     * Default constructor.
     */
    public SimpleAtom(){
        // Use the default values.
    }

    /**
     * Constructor.
     */
    public SimpleAtom( int numProtons, int numNeutrons, int numElectrons ) {
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.numElectrons = numElectrons;
    }

    /**
     * Structure that contains both the chemical symbol and textual name for
     * an atom.
     * TODO: What's with this?  A wrapper for a string?  Fix it!
     */
    public static class AtomName{
        public final String name;
        public AtomName( String name ) {
            this.name = name;
        }
    }

    private static class Isotope {
            public final int massNumber;
            public final int neutronNumber;
            //Regenerate equals and hashcode if you change the contents of the isotope

            public Isotope( int massNumber, int neutronNumber ) {
                this.massNumber = massNumber;
                this.neutronNumber = neutronNumber;
            }

            //Autogenerated

            @Override
            public boolean equals( Object o ) {
                if ( this == o ) {
                    return true;
                }
                if ( o == null || getClass() != o.getClass() ) {
                    return false;
                }

                Isotope isotope = (Isotope) o;

                if ( massNumber != isotope.massNumber ) {
                    return false;
                }
                if ( neutronNumber != isotope.neutronNumber ) {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode() {
                int result = massNumber;
                result = 31 * result + neutronNumber;
                return result;
            }
        }

    private static final HashMap<Integer, AtomName> mapNumProtonsToName = new HashMap<Integer, AtomName>(){{
            put(0, new AtomName( BuildAnAtomStrings.ELEMENT_NONE_NAME));//for an unbuilt or empty atom
            put(1, new AtomName( BuildAnAtomStrings.ELEMENT_HYDROGEN_NAME));
            put(2, new AtomName( BuildAnAtomStrings.ELEMENT_HELIUM_NAME));
            put(3, new AtomName( BuildAnAtomStrings.ELEMENT_LITHIUM_NAME));
            put(4, new AtomName( BuildAnAtomStrings.ELEMENT_BERYLLIUM_NAME));
            put(5, new AtomName( BuildAnAtomStrings.ELEMENT_BORON_NAME));
            put(6, new AtomName( BuildAnAtomStrings.ELEMENT_CARBON_NAME));
            put(7, new AtomName( BuildAnAtomStrings.ELEMENT_NITROGEN_NAME));
            put(8, new AtomName( BuildAnAtomStrings.ELEMENT_OXYGEN_NAME));
            put(9, new AtomName( BuildAnAtomStrings.ELEMENT_FLUORINE_NAME));
            put(10, new AtomName(BuildAnAtomStrings.ELEMENT_NEON_NAME));
            put(11, new AtomName(BuildAnAtomStrings.ELEMENT_SODIUM_NAME));
            put(12, new AtomName(BuildAnAtomStrings.ELEMENT_MAGNESIUM_NAME));
            put(13, new AtomName(BuildAnAtomStrings.ELEMENT_ALUMINUM_NAME));
            put(14, new AtomName(BuildAnAtomStrings.ELEMENT_SILICON_NAME));
            put(15, new AtomName(BuildAnAtomStrings.ELEMENT_PHOSPHORUS_NAME));
            put(16, new AtomName(BuildAnAtomStrings.ELEMENT_SULPHER_NAME));
            put(17, new AtomName(BuildAnAtomStrings.ELEMENT_CHLORINE_NAME));
            put(18, new AtomName(BuildAnAtomStrings.ELEMENT_ARGON_NAME));
            put(19, new AtomName(BuildAnAtomStrings.ELEMENT_POTASSIUM_NAME));
            put(20, new AtomName(BuildAnAtomStrings.ELEMENT_CALCIUM_NAME));
        }};

    private static final ArrayList<Isotope> stableIsotopes = new ArrayList<Isotope>() {{
            //H
            add( new Isotope( 1, 0 ) );
            add( new Isotope( 2, 1 ) );
            //He
            add( new Isotope( 3, 1 ) );
            add( new Isotope( 4, 2 ) );
            //Li
            add( new Isotope( 6, 3 ) );
            add( new Isotope( 7, 4 ) );
            //Be
            add( new Isotope( 9, 5 ) );
            //B
            add( new Isotope( 10, 5 ) );
            add( new Isotope( 11, 6 ) );
            //C
            add( new Isotope( 12, 6 ) );
            add( new Isotope( 13, 7 ) );
            //N
            add( new Isotope( 14, 7 ) );
            add( new Isotope( 15, 8 ) );
            //O
            add( new Isotope( 16, 8 ) );
            add( new Isotope( 17, 9 ) );
            add( new Isotope( 18, 10 ) );
            //F
            add( new Isotope( 19, 10 ) );
            //Ne
            add( new Isotope( 20, 10 ) );
            add( new Isotope( 21, 11 ) );
            add( new Isotope( 22, 12 ) );
        }};

    public static String getSymbol( int protonCount ) {
        if ( protonCount == 0 ) {
            return BuildAnAtomStrings.ELEMENT_NONE_SYMBOL;
        }
        else {
            return PeriodicTableNode.getElementAbbreviation( protonCount );
        }
    }

    public static String getName( int protonCount ) {
        return mapNumProtonsToName.get( protonCount ).name;
    }

    public int getNumNeutrons() {
        return numNeutrons;
    }

    public void setNumNeutrons( int numNeutrons ) {
        if ( this.numNeutrons != numNeutrons ){
            this.numNeutrons = numNeutrons;
            notifyObservers();
        }
    }

    public int getNumElectrons() {
        return numElectrons;
    }

    public void setNumElectrons( int numElectrons ) {
        if ( this.numElectrons != numElectrons ){
            this.numElectrons = numElectrons;
            notifyObservers();
        }
    }

    public int getNumProtons() {
        return numProtons;
    }

    public void setNumProtons( int numProtons ) {
        if ( this.numProtons != numProtons ){
            this.numProtons = numProtons;
            notifyObservers();
        }
    }

    public int getAtomicMassNumber() {
        return getNumProtons() + getNumNeutrons();
    }

    public int getCharge() {
        return getNumProtons() - getNumElectrons();
    }

    public String getFormattedCharge() {
        if (getCharge() <= 0){
            return "" + getCharge();
        }
        else{
            return "+" + getCharge();
        }
    }

    public String getSymbol(){
        return getSymbol( getNumProtons() );
    }

    public String getName() {
        assert mapNumProtonsToName.containsKey( getNumProtons() );
        return getName( getNumProtons() );
    }

    public boolean isStable() {
        //taken from the table at https://docs.google.com/document/edit?id=1VGGhLUetiwijbDneU-U6BPrjRlkI0rt939zk8Y4AuA4&authkey=CMLM4ZUC&hl=en#
        return getAtomicMassNumber() == 0 || stableIsotopes.contains( new Isotope( getAtomicMassNumber(), getNumNeutrons() ) );
    }

    public int getNumParticles() {
        return getAtomicMassNumber()+getNumElectrons();
    }

    public AtomValue toAtomValue() {
        return new AtomValue(getNumProtons(), getNumNeutrons(),getNumElectrons() );
    }
}
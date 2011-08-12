// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

/**
 * Enum pattern for Salt and Sugar dispensers, to keep track of which one the user is using.
 *
 * @author Sam Reid
 */
public class DispenserType {

    //Name of the solute
    private final String name;

    //List of elements comprising the solute
    private final Integer[] elements;

    public static final DispenserType SALT = new DispenserType( "Salt", 11, 17 );
    public static final DispenserType SUGAR = new DispenserType( "Sugar", 6, 1, 8 );
    public static final DispenserType GLUCOSE = new DispenserType( "Glucose", 6, 1, 8 );
    public static final DispenserType SODIUM_NITRATE = new DispenserType( "Sodium Nitrate", 11, 7, 8 );
    public static final DispenserType CALCIUM_CHLORIDE = new DispenserType( "Calcium Chloride", 20, 17 );

    //Enum pattern, so no other instances should be created
    private DispenserType( String name, Integer... elements ) {
        this.name = name;
        this.elements = elements;
    }

    @Override public String toString() {
        return name;
    }

    public Integer[] getElementAtomicMasses() {
        return elements;
    }
}

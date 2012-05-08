package edu.colorado.phet.fractionsintro.buildafraction.model;

import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public class FractionID extends ObjectID {
    public FractionID( final int id ) { super( id ); }

    public static int count = 0;

    public static FractionID nextID() { return new FractionID( count++ ); }
}
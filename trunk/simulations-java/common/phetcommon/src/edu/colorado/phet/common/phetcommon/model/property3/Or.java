// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Returns a boolean OR over its arguments, and signifying changes when its value changes.  This provides read-only access to the computed value.
 *
 * @author Sam Reid
 */
public class Or
        <T extends Gettable<Boolean> & Observable0>//parents are gettable and observable but not settable
        extends OperatorBoolean<T> {

    public Or( T a, T b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean a, Boolean b ) {
                return a || b;
            }
        } );
    }
}
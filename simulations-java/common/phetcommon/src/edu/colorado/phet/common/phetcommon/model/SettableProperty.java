// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model;

/**
 * In addition to being Observable, this SettableProperty<T> is also settable, that is, it adds the method setValue().
 * It does not declare the data storage type, and is thus compatible with combined properties such as And and Or.
 *
 * @author Sam Reid
 */
public abstract class SettableProperty<T> extends ObservableProperty<T> {
    public abstract void setValue( T value );
}

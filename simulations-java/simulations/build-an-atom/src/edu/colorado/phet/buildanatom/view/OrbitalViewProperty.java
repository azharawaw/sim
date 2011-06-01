package edu.colorado.phet.buildanatom.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;

//REVIEW This seems unnecessary, like having DoubleProperty extends Property<Double>.

/**
 * Property class based on an enumeration of the different representations
 * that can be used to represent the electrons in this atom.
 */
public class OrbitalViewProperty extends Property<OrbitalView> {
    /**
     * Constructor.
     */
    public OrbitalViewProperty( OrbitalView value ) {
        super( value );
    }

    ;
}
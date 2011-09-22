// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * An atom that includes additional 3D position
 */
public class Atom3D extends Atom {
    public final Property<ImmutableVector3D> position; // model coordinates in angstroms

    public Atom3D( Element element, ImmutableVector3D position ) {
        super( element );

        this.position = new Property<ImmutableVector3D>( position );
    }
}

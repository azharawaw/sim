// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Contains multiple buckets of different types of atoms
 */
public class Kit {
    private final List<Bucket> buckets;
    private final List<AtomModel> atoms = new LinkedList<AtomModel>();
    private KitCollectionModel model;

    public static final double BUCKET_PADDING = 50;

    public Kit( KitCollectionModel model, List<Bucket> buckets ) {
        this.model = model;
        this.buckets = buckets;

        // keep track of all atoms in our kit
        for ( Bucket bucket : buckets ) {
            atoms.addAll( bucket.getAtoms() );
        }

        /*---------------------------------------------------------------------------*
        * bucket layout
        *----------------------------------------------------------------------------*/

        double kitY = model.getAvailableKitBounds().getCenterY();
        double kitXCenter = model.getAvailableKitBounds().getCenterX();

        double usedWidth = 0;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < buckets.size(); i++ ) {
            Bucket bucket = buckets.get( i );
            if ( i != 0 ) {
                usedWidth += BUCKET_PADDING;
            }
            bucket.setPosition( new ImmutableVector2D( usedWidth, kitY ) );
            usedWidth += bucket.getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( Bucket bucket : buckets ) {
            bucket.setPosition( new ImmutableVector2D( bucket.getPosition().getX() - usedWidth / 2 + kitXCenter + bucket.getWidth() / 2, kitY ) );
        }
    }

    public boolean isContainedInBucket( AtomModel atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.containsAtom( atom ) ) {
                return true;
            }
        }
        return false;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public Bucket getBucketForAtomType( Atom atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.getAtomType().isSameTypeOfAtom( atom ) ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for atom type: " + atom );//oh noes
    }

    public PBounds getAvailableKitBounds() {
        return model.getAvailableKitBounds();
    }
}

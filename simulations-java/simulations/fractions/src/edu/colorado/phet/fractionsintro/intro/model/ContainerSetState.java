// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import edu.colorado.phet.fractionsintro.intro.view.PieSetNode;

import static fj.Function.curry;

/**
 * The entire state (immutable) of what is filled and empty for multiple containers.
 *
 * @author Sam Reid
 */
@Data public class ContainerSetState {
    public final List<Container> containers;
    public final int denominator;
    public final int numContainers;  //Number of containers to show
    public final int numerator;

    public ContainerSetState( int denominator, Container[] containers ) {
        this( denominator, Arrays.asList( containers ) );
    }

    public ContainerSetState( int denominator, List<Container> containers ) {
        this( denominator, containers.toCollection() );
    }

    public ContainerSetState( int denominator, Collection<Container> containers ) {
        this.containers = List.iterableList( containers );
        this.denominator = denominator;
        this.numContainers = containers.size();
        int count = 0;
        for ( Container container : containers ) {
            count += container.numFilledCells;
        }
        this.numerator = count;
    }

    @Override public String toString() {
        return containers.toString();
    }

    public ContainerSetState addPieces( int delta ) {
        if ( delta == 0 ) {
            return this;
        }
        else if ( delta > 0 ) {
            ContainerSetState cs = isFull() ? addEmptyContainer() : this;
            return cs.toggle( cs.getFirstEmptyCell() ).addPieces( delta - 1 ).padAndTrim();
        }
        else {
            return toggle( getLastFullCell() ).addPieces( delta + 1 ).padAndTrim();
        }
    }

    //Add an empty container if this one is all full, but don't go past 6 (would be off the screen)
    public ContainerSetState padAndTrim() {
        ContainerSetState cs = trim();
        while ( cs.numContainers < 6 ) {
            cs = cs.addEmptyContainer();
        }
        return cs;
    }

    //Remove any trailing containers that are completely empty
    public ContainerSetState trim() {
        final List<Container> reversed = containers.reverse();
        final boolean[] foundNonEmpty = { false };

        final ArrayList<Container> all = new ArrayList<Container>() {{
            for ( Container container : reversed ) {
                if ( container.isEmpty() ) {

                }
                else {
                    //as soon as finding a non-empty container, add all after that
                    foundNonEmpty[0] = true;
                }
                if ( foundNonEmpty[0] ) {
                    add( container );
                }
            }
        }};
        Collections.reverse( all );
        return new ContainerSetState( denominator, all );
    }

    public ContainerSetState addEmptyContainer() {
        return new ContainerSetState( denominator, new ArrayList<Container>( containers.toCollection() ) {{
            add( new Container( denominator, new int[0] ) );
        }} );
    }

    private boolean isFull() {
        for ( Container container : containers ) {
            if ( !container.isFull() ) { return false; }
        }
        return true;
    }

    public ContainerSetState toggle( final CellPointer pointer ) {
        return new ContainerSetState( denominator, containers.map( new F<Container, Container>() {
            @Override public Container f( Container container ) {
                int containerIndex = containers.elementIndex( PieSetNode.<Container>refEqual(), container ).some();
                if ( pointer.container == containerIndex ) {
                    return container.toggle( pointer.cell );
                }
                else {
                    return container;
                }
            }
        } ) );
    }

    private CellPointer getLastFullCell() {
        return getAllCellPointers().reverse().find( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer cellPointer ) {
                return !isEmpty( cellPointer );
            }
        } ).some();
    }

    public List<CellPointer> getAllCellPointers() {
        return List.iterableList( new ArrayList<CellPointer>() {{
            for ( int i = 0; i < numContainers; i++ ) {
                for ( int k = 0; k < containers.index( i ).numCells; k++ ) {
                    add( new CellPointer( i, k ) );
                }
            }
        }} );
    }

    private CellPointer getFirstEmptyCell() {
        return getAllCellPointers().find( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer cellPointer ) {
                return isEmpty( cellPointer );
            }
        } ).some();
    }

    public Boolean isEmpty( CellPointer cellPointer ) {
        return containers.index( cellPointer.container ).isEmpty( cellPointer.cell );
    }

    public boolean isFilled( CellPointer cp ) {
        return !isEmpty( cp );
    }

    public Container getContainer( int container ) {
        return containers.index( container );
    }

    public ContainerSetState removeContainer( final int container ) {
        return new ContainerSetState( denominator, new ArrayList<Container>() {{
            for ( int i = 0; i < numContainers; i++ ) {
                if ( i != container ) {
                    add( getContainer( i ) );
                }
            }
        }} );
    }

    //When converting denominator, try to keep pieces close to where they were.  This requires computing the closest unoccupied
    public CellPointer getClosestUnoccupiedLocation( final CellPointer cellPointer ) {
        List<CellPointer> emptyCells = getEmptyCells();
        if ( emptyCells.isEmpty() ) {
            return null;
        }
        return emptyCells.minimum( Ord.ord( curry( new F2<CellPointer, CellPointer, Ordering>() {
            public Ordering f( final CellPointer u1, final CellPointer u2 ) {
                return Ord.<Comparable>comparableOrd().compare( cellPointer.distance( u1 ), cellPointer.distance( u2 ) );
            }
        } ) ) );
    }

    public List<CellPointer> getFilledCells() {
        return getAllCellPointers().filter( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer c ) {
                return isFilled( c );
            }
        } );
    }

    public List<CellPointer> getEmptyCells() {
        return getAllCellPointers().filter( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer c ) {
                return !isFilled( c );
            }
        } );
    }

    public ContainerSetState denominator( Integer denominator ) {
        ContainerSetState newState = new ContainerSetState( denominator, new Container[] { new Container( denominator, new int[0] ) } ).padAndTrim();

        //for each piece in oldState, find the closest unoccupied location in newState and add it there
        for ( CellPointer cellPointer : getFilledCells() ) {

            //Find closest unoccupied location
            CellPointer cp = newState.getClosestUnoccupiedLocation( cellPointer );
            if ( cp != null ) {
                newState = newState.toggle( cp );
            }
            else {
                System.out.println( "Null CP!" );
            }
        }
        return newState;
    }
}
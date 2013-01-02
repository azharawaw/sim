// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;

/**
 * Base class for challenge factories. These factories handle generation of challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ChallengeFactory {

    protected final Random random;

    public ChallengeFactory() {
        this.random = new Random();
    }

    // Converts an integer range to a list of values that are in that range.
    protected static ArrayList<Integer> rangeToList( IntegerRange range ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int i = range.getMin(); i <= range.getMax(); i++ ) {
            list.add( i );
        }
        assert ( list.size() > 0 );
        return list;
    }

    // Gets a random index for a specified list.
    protected int randomIndex( List list ) {
        return random.nextInt( list.size() );
    }

    // Picks a manipulation mode, removes it from the list.
    protected ManipulationMode pickManipulationMode( ArrayList<ManipulationMode> list ) {
        int index = randomIndex( list );
        final ManipulationMode manipulationMode = list.get( index );
        list.remove( index );
        return manipulationMode;
    }

    // Picks an integer, removes it from the bin.
    protected int pickInteger( ArrayList<ArrayList<Integer>> bins ) {
        return pickInteger( bins, rangeToList( new IntegerRange( 0, bins.size() - 1 ) ) );
    }

    // Picks an integer, removes it from the bin, removes the bin from the binIndices.
    protected int pickInteger( ArrayList<ArrayList<Integer>> bins, ArrayList<Integer> binIndices ) {
        int index = randomIndex( binIndices );
        ArrayList<Integer> bin = bins.get( binIndices.get( index ) );
        binIndices.remove( index );
        index = randomIndex( bin );
        final int value = bin.get( index );
        bin.remove( index );
        return value;
    }

    // Picks a fraction, removes it from the bin.
    protected Fraction pickFraction( ArrayList<ArrayList<Fraction>> bins ) {
        return pickFraction( bins, rangeToList( new IntegerRange( 0, bins.size() - 1 ) ) );
    }

    // Picks a fraction, removes it from the bin, removes the bin from the binIndices.
    protected Fraction pickFraction( ArrayList<ArrayList<Fraction>> bins, ArrayList<Integer> binIndices ) {
        int index = randomIndex( binIndices );
        ArrayList<Fraction> bin = bins.get( binIndices.get( index ) );
        binIndices.remove( index );
        index = randomIndex( bin );
        final Fraction value = bin.get( index );
        bin.remove( index );
        return value;
    }

    // Picks a point, removes it from the bin.
    protected Point2D pickPoint( ArrayList<ArrayList<Point2D>> pointBins ) {
        return pickPoint( pointBins, rangeToList( new IntegerRange( 0, pointBins.size() - 1 ) ) );
    }

    // Picks a point, removes it from the bin, removes the bin from the binIndices.
    protected Point2D pickPoint( ArrayList<ArrayList<Point2D>> bins, ArrayList<Integer> binIndices ) {
        int index = randomIndex( binIndices );
        ArrayList<Point2D> bin = bins.get( binIndices.get( index ) );
        binIndices.remove( index );
        index = randomIndex( bin );
        final Point2D point = bin.get( index );
        bin.remove( index );
        return point;
    }

    // Shuffles a list of challenges.
    protected ArrayList<IChallenge> shuffle( ArrayList<IChallenge> list ) {
        ArrayList<IChallenge> shuffledList = new ArrayList<IChallenge>();
        while ( list.size() != 0 ) {
            int index = randomIndex( list );
            shuffledList.add( list.get( index ) );
            list.remove( index );
        }
        return shuffledList;
    }
}

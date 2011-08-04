// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.UpdateStrategy.FREE_PARTICLE_SPEED;
import static java.util.Collections.sort;

/**
 * This class handles incremental crystallization of particles when the concentration surpasses the saturation point.
 * I originally tried just specifying the "T extends particle" generic type and using Crystal
 *
 * @author Sam Reid
 */
public abstract class IncrementalGrowth<T extends Particle, U extends Crystal<T>> {

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNewCrystalFormationTime;

    //The main model for timing and adding/removing particles and crystals
    protected final MicroModel model;

    //The list of all crystals of the appropriate type
    private final ItemList<U> crystals;

    //Randomness for crystal formation times and which crystals to bond to
    private final Random random = new Random();

    public IncrementalGrowth( MicroModel model, ItemList<U> crystals ) {
        this.model = model;
        this.crystals = crystals;
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    public void allowCrystalGrowth( double dt, ObservableProperty<Boolean> saturated ) {
        double timeSinceLast = model.getTime() - lastNewCrystalFormationTime;

        //Make sure at least 1 second has passed, then convert to crystals
        if ( saturated.get() && timeSinceLast > 1 && crystals.size() == 0 ) {

            //Create a crystal if there weren't any
            System.out.println( "No crystals, starting a new one" );
            towardNewCrystal( dt );
        }

        //If the solution is saturated, try adding on to an existing crystal
        else if ( saturated.get() ) {

            //Randomly choose an existing crystal to possibly bond to
            Crystal<T> crystal = crystals.get( random.nextInt( crystals.size() ) );

            //Enumerate all particles and distances from crystal sites, but only look for sites that are underwater, otherwise particles would try to fly out of the solution (and get stuck at the boundary)
            ArrayList<CrystallizationMatch<T>> matches = getAllCrystallizationMatchesSorted( crystal );
            if ( matches.size() > 0 ) {

                //Find a matching particle nearby one of the sites and join it together
                CrystallizationMatch<T> match = matches.get( 0 );

                //With 1% chance, form a new crystal anyways (if there aren't too many crystals)
                if ( random.nextDouble() > 0.99 && crystals.size() <= 2 ) {
                    System.out.println( "Random choice to form new crystal instead of joining another" );
                    towardNewCrystal( dt );
                }

                //If close enough, join the lattice
                else if ( match.distance <= FREE_PARTICLE_SPEED * dt ) {

                    //Remove the particle from the list of free particles
                    model.freeParticles.remove( match.particle );

                    //Add it as a constituent of the crystal
                    crystal.addConstituent( new Constituent<T>( match.particle, match.site.relativePosition ) );
                }

                //Otherwise, move closest particle toward the lattice
                else if ( match.distance <= model.beaker.getWidth() / 8 ) {
                    match.particle.velocity.set( match.site.absolutePosition.minus( match.particle.getPosition() ).getInstanceOfMagnitude( FREE_PARTICLE_SPEED ) );
                }

                else {
                    System.out.println( "Best match was too far away (" + match.distance / model.beaker.getWidth() + " beaker widths, so starting a new crystal with a random particle" );
                    towardNewCrystal( dt );
                }
            }

            //No matches, so start a new crystal
            else {
                System.out.println( "No matches, starting a new crystal" );
                towardNewCrystal( dt );
            }
        }
    }

    //Look for all open site on its lattice and sort by distance to available participants
    public ArrayList<CrystallizationMatch<T>> getAllCrystallizationMatchesSorted( Crystal<T> crystal ) {
        ArrayList<CrystallizationMatch<T>> matches = new ArrayList<CrystallizationMatch<T>>();
        for ( Particle freeParticle : model.freeParticles ) {
            for ( OpenSite<T> openSite : crystal.getOpenSites() ) {
                if ( model.solution.shape.get().contains( openSite.shape.getBounds2D() ) && openSite.matches( freeParticle ) ) {
                    matches.add( new CrystallizationMatch<T>( (T) freeParticle, openSite ) );
                }
            }
        }

        //Find the best site
        sort( matches, new Comparator<CrystallizationMatch>() {
            public int compare( CrystallizationMatch o1, CrystallizationMatch o2 ) {
                return Double.compare( o1.distance, o2.distance );
            }
        } );
        return matches;
    }

    //Move nearby matching particles closer together, or, if close enough, form a 2-particle crystal with them
    private void towardNewCrystal( double dt ) {

        //Find the pair of particles closest to each other, and move them even closer to each other.  When they are close enough, form the crystal
        ArrayList<ParticlePair> pairs = getAllPairs();
        sort( pairs, new Comparator<ParticlePair>() {
            public int compare( ParticlePair o1, ParticlePair o2 ) {
                return Double.compare( o1.getDistance(), o2.getDistance() );
            }
        } );

        //If there was a match, move the closest particles even closer together
        //If they are close enough, convert them into a crystal
        if ( pairs.size() > 0 ) {
            ParticlePair closestPair = pairs.get( 0 );
            closestPair.moveTogether( dt );
            if ( closestPair.getDistance() <= dt * UpdateStrategy.FREE_PARTICLE_SPEED ) {
                convertToCrystal( (T) closestPair._1, (T) closestPair._2 );

                //Record the crystal formation time so new crystals don't form too often
                lastNewCrystalFormationTime = model.getTime();
            }
        }
    }

    //Crystal-specific code to generate a list of all matching pairs of particles, these are particles that could form a new crystal together, if they are close enough together
    protected abstract ArrayList<ParticlePair> getAllPairs();

    //Convert the specified particles to a crystal and add the crystal to the model
    private void convertToCrystal( T a, T b ) {

        //Create a crystal based on the 'a' particle, then add the 'b' particle as the second constituent
        U crystal = newCrystal( a.getPosition() );
        crystal.addConstituent( new Constituent<T>( a, ZERO ) );

        //Choose a site that matches the first particle
        ArrayList<OpenSite<T>> sites = crystal.getOpenSites();
        Collections.shuffle( sites );
        OpenSite<T> selectedSite = null;
        for ( OpenSite<T> site : sites ) {
            if ( site.matches( b ) ) {
                selectedSite = site;
                break;
            }
        }

        //Add the second particle as the second constituent of the crystal
        if ( selectedSite == null ) {
            System.out.println( "No available sites to bind to, this probably shouldn't have happened." );
        }
        else {
            crystal.addConstituent( new Constituent<T>( b, selectedSite.relativePosition ) );

            model.freeParticles.remove( a );
            model.freeParticles.remove( b );
            crystals.add( crystal );
        }
    }

    //Create the right subtype of crystal at the specified location.  It will be populated by the convertToCrystal method
    protected abstract U newCrystal( ImmutableVector2D position );

    public ArrayList<ParticlePair> generateAllPairs( Class<? extends Particle> typeA, Class<? extends Particle> typeB ) {
        ArrayList<Particle> aList = model.freeParticles.filter( typeA );
        ArrayList<Particle> bList = model.freeParticles.filter( typeB );
        ArrayList<ParticlePair> pairs = new ArrayList<ParticlePair>();
        for ( Particle a : bList ) {
            for ( Particle b : aList ) {

                //Check for equality in case typeA==typeB, as in the case of Sucrose
                if ( a != b ) {
                    pairs.add( new ParticlePair( a, b ) );
                }
            }
        }
        return pairs;
    }
}
/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.SpriteHandler;

/**
 * Represents a bunny in the Natural Selection model
 *
 * @author Jonathan Olson
 */
public class Bunny extends ClockAdapter {
    // parents (although currently the gender of bunnies is ignored)
    private Bunny father;
    private Bunny mother;

    // genetic data for each trait
    private Genotype colorGenotype;
    private Genotype teethGenotype;
    private Genotype tailGenotype;

    /**
     * Currently bunnies only mate with their siblings for a simple hierarchical layout. This is the only other
     * possible bunny that this one can mate with. If either dies, the other cannot reproduce
     */
    private Bunny potentialMate;

    /**
     * Whether or not this bunny has reproduced
     */
    private boolean mated = false;

    /**
     * Whether on not the bunny has mutated
     */
    private boolean mutated = false;

    /**
     * Whether the bunny is alive or not. Instances should not disappear after bunnies die, but should stay around
     * so that the family tree can be viewed or analyzed.
     */
    private boolean alive;

    /**
     * A list of the bunny's children. Currently only 0 or 4 children.
     */
    private ArrayList<Bunny> children;

    /**
     * The bunny's age (in years). starts at 0, counts up 1 each generation
     */
    private int age;

    /**
     * The generation of the bunny (will not change)
     */
    private final int generation;

    /**
     * Unique identifier for this bunny, used for ordering and simplifies handling couples (plus good for debugging)
     */
    public int bunnyId;
    public static int bunnyCount = 0;

    // old genetic data. If the new data is different, listeners will be notified of the changes
    private Allele colorPhenotype;
    private Allele teethPhenotype;
    private Allele tailPhenotype;

    // random number generator
    private static final Random random = new Random( System.currentTimeMillis() );

    // 3d coordinates
    private double x, y, z;

    // time since the last hop
    private int sinceHopTime = 0;

    private boolean movingRight;

    public static final int BETWEEN_HOP_TIME = 70;
    public static final int HOP_TIME = 10;
    public static final int HOP_HEIGHT = 50;
    public static final double HOP_HORIZONTAL_STEP = 2.0;

    private NaturalSelectionModel model;

    private ArrayList<Listener> listeners;

    /**
     * Constructor
     *
     * @param model      The natural selection model
     * @param father     The father of the bunny (or null if there is none)
     * @param mother     The mother of the bunny (or null if there is none)
     * @param generation The generation the bunny is being born into
     */
    public Bunny( NaturalSelectionModel model, Bunny father, Bunny mother, int generation ) {
        this.model = model;

        bunnyId = bunnyCount++;

        sinceHopTime = random.nextInt( BETWEEN_HOP_TIME + HOP_TIME );

        movingRight = true;
        if ( random.nextInt( 2 ) == 0 ) {
            movingRight = false;
        }

        this.father = father;
        this.mother = mother;
        this.generation = generation;

        alive = true;
        children = new ArrayList<Bunny>();
        listeners = new ArrayList<Listener>();

        setAge( 0 );

        if ( this.father == null || this.mother == null ) {
            // one parent is null, so this bunny must be one of the "root" generation 0 bunnies.
            // in general, just make this a vanilla bunny
            colorGenotype = new Genotype( ColorGene.getInstance(), ColorGene.WHITE_ALLELE, ColorGene.WHITE_ALLELE );
            teethGenotype = new Genotype( TeethGene.getInstance(), TeethGene.TEETH_REGULAR_ALLELE, TeethGene.TEETH_REGULAR_ALLELE );
            tailGenotype = new Genotype( TailGene.getInstance(), TailGene.TAIL_SHORT_ALLELE, TailGene.TAIL_SHORT_ALLELE );
        }
        else {
            // we have both parents. do the usual genetic process (including possible mutations)
            colorGenotype = combineGenotypes( this.father.getColorGenotype(), this.mother.getColorGenotype() );
            teethGenotype = combineGenotypes( this.father.getTeethGenotype(), this.mother.getTeethGenotype() );
            tailGenotype = combineGenotypes( this.father.getTailGenotype(), this.mother.getTailGenotype() );
        }

        // set the "cached" phenotypes so that if they change we can notify listeners
        colorPhenotype = colorGenotype.getPhenotype();
        teethPhenotype = teethGenotype.getPhenotype();
        tailPhenotype = tailGenotype.getPhenotype();

        // let each Gene keep track of this bunny for trait distribution purposes
        addListener( ColorGene.getInstance() );
        addListener( TeethGene.getInstance() );
        addListener( TailGene.getInstance() );

        setInitialPosition();

        model.getClock().addClockListener( this );

        // bunny is set up, notify various things that the bunny has been created and is ready to use
        //notifyInit();

    }

    public boolean isMovingRight() {
        return movingRight;
    }

    /**
     * Sets the initial bunny position (3d coordinates)
     */
    private void setInitialPosition() {
        x = Math.random() * ( SpriteHandler.MAX_X - SpriteHandler.MIN_X ) + SpriteHandler.MIN_X;

        // start on the ground
        y = SpriteHandler.MIN_Y;

        z = Math.random() * ( SpriteHandler.MAX_Z - SpriteHandler.MIN_Z ) + SpriteHandler.MIN_Z;
    }

    public boolean isAlive() {
        return alive;
    }

    /**
     * Called to kill the bunny. Will notify listeners if the bunny goes from alive to dead.
     */
    public void die() {
        if ( !isAlive() ) {
            return;
        }

        alive = false;

        notifyDeath();
    }

    public int getAge() {
        return age;
    }

    /**
     * Sets the bunny's age. If too old, the bunny will die of old age.
     *
     * @param age The age you want the bunny to be!
     */
    public void setAge( int age ) {
        this.age = age;
        if ( isAlive() && this.age >= NaturalSelectionConstants.BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD ) {
            if ( model.isFriendAdded() && model.getGeneration() == 0 ) {
                // in this case, we don't want the bunny to die! just want them to get older
                return;
            }
            die();
        }
    }

    public int getGeneration() {
        return generation;
    }

    public ArrayList<Bunny> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Age this bunny
     */
    public void ageMe() {
        setAge( getAge() + 1 );

        if ( isAlive() ) {
            notifyAging();
        }
    }

    // get genetic information

    public Genotype getColorGenotype() {
        return colorGenotype;
    }

    public Genotype getTeethGenotype() {
        return teethGenotype;
    }

    public Genotype getTailGenotype() {
        return tailGenotype;
    }

    public Allele getColorPhenotype() {
        return colorPhenotype;
    }

    public Allele getTeethPhenotype() {
        return teethPhenotype;
    }

    public Allele getTailPhenotype() {
        return tailPhenotype;
    }

    public Bunny getPotentialMate() {
        return potentialMate;
    }

    public void setPotentialMate( Bunny bunny ) {
        if ( bunny == null ) {
            System.out.println( "WARNING: setting potential mate to null on " + this );
        }
        potentialMate = bunny;
    }

    public boolean hasMated() {
        return mated;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        double old = this.x;
        this.x = x;
        if ( old != x ) {
            notifyChangePosition();
        }
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        double old = this.y;
        this.y = y;
        if ( old != y ) {
            notifyChangePosition();
        }
    }

    public double getZ() {
        return z;
    }

    public void setZ( double z ) {
        double old = this.z;
        this.z = z;
        if ( old != z ) {
            notifyChangePosition();
        }
    }

    public boolean isMutated() {
        return mutated;
    }

    /**
     * Determines whether this bunny can reproduce or not.
     *
     * @return Whether or not this bunny can currently mate with its possible partner
     */
    public boolean canMate() {
        // potential mate not set yet, bad things!!!
        if ( potentialMate == null ) {
            System.out.println( "WARNING: potentialMate == null on " + this );
            return false;
        }

        if ( !isAlive() || !potentialMate.isAlive() ) {
            // just no, one or both are dead!
            return false;
        }

        if ( hasMated() || potentialMate.hasMated() ) {
            // for now, if we mated already, we can't again
            return false;
        }

        if ( this != model.getRootFather() && this != model.getRootMother() && model.getGeneration() != 0 ) {
            if ( getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD || potentialMate.getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD ) {
                // too old
                return false;
            }
        }

        return true;
    }

    public int getId() {
        return bunnyId;
    }

    public NaturalSelectionModel getModel() {
        return model;
    }

    /**
     * Helper function that signals that this bunny has reproduced, and adds children at the same time.
     *
     * @param bunnyArray The array of this bunny's new children
     */
    public void reproduce( Bunny[] bunnyArray ) {
        for ( int i = 0; i < bunnyArray.length; i++ ) {
            children.add( bunnyArray[i] );
        }
        mated = true;
        notifyReproduces();
    }

    /**
     * Causes the bunny to move around physically
     */
    private void moveAround() {
        // TODO: add randomness to inbetween-jumping time?
        sinceHopTime++;
        if ( sinceHopTime > BETWEEN_HOP_TIME + HOP_TIME ) {
            sinceHopTime = 0;
        }
        if ( sinceHopTime > BETWEEN_HOP_TIME ) {
            // move in the "hop"
            int hopProgress = sinceHopTime - BETWEEN_HOP_TIME;
            double hopFraction = ( (double) hopProgress ) / ( (double) HOP_TIME );

            setY( HOP_HEIGHT * 2 * ( -hopFraction * hopFraction + hopFraction ) );

            if ( movingRight ) {
                setX( getX() + HOP_HORIZONTAL_STEP );
                if ( getX() >= SpriteHandler.MAX_X ) {
                    movingRight = false;
                }
            }
            else {
                setX( getX() - HOP_HORIZONTAL_STEP );
                if ( getX() <= SpriteHandler.MIN_X ) {
                    movingRight = true;
                }
            }
        }

    }


    /**
     * Cause a bunny to mutate for all mutatable genes
     */
    public void mutateMe() {
        if ( ColorGene.getInstance().getMutatable() ) {
            mutated = true;
            colorPhenotype = ColorGene.BROWN_ALLELE;
            colorGenotype = new Genotype( ColorGene.getInstance(), ColorGene.BROWN_ALLELE, ColorGene.BROWN_ALLELE );
        }
        if ( TailGene.getInstance().getMutatable() ) {
            mutated = true;
            tailPhenotype = TailGene.TAIL_LONG_ALLELE;
            tailGenotype = new Genotype( TailGene.getInstance(), TailGene.TAIL_LONG_ALLELE, TailGene.TAIL_LONG_ALLELE );
        }
        if ( TeethGene.getInstance().getMutatable() ) {
            mutated = true;
            teethPhenotype = TeethGene.TEETH_HUGE_ALLELE;
            teethGenotype = new Genotype( TeethGene.getInstance(), TeethGene.TEETH_HUGE_ALLELE, TeethGene.TEETH_HUGE_ALLELE );
        }
    }

    /**
     * Cause a bunny to mutate with the specified gene and allele
     *
     * @param gene   The gene to mutate
     * @param allele The allele to mutate to
     */
    public void mutateGene( Gene gene, Allele allele ) {
        mutated = true;

        if ( gene == ColorGene.getInstance() ) {
            colorPhenotype = allele;
            colorGenotype = new Genotype( gene, allele, allele );
        }
        else if ( gene == TailGene.getInstance() ) {
            tailPhenotype = allele;
            tailGenotype = new Genotype( gene, allele, allele );
        }
        else if ( gene == TeethGene.getInstance() ) {
            teethPhenotype = allele;
            teethGenotype = new Genotype( gene, allele, allele );
        }


    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        if ( isAlive() ) {
            moveAround();
        }
        else {
            // stop listening
            clockEvent.getClock().removeClockListener( this );
        }
    }

    //----------------------------------------------------------------------------
    // Static methods
    //----------------------------------------------------------------------------

    /**
     * Combines the DNA of each parent to get the DNA for the child. getRandomAllele should introduce mutations if
     * possible
     *
     * @param fatherGenotype The father's genotype
     * @param motherGenotype The mother's genotype
     * @return A new genotype for a child bunny
     */
    public static Genotype combineGenotypes( Genotype fatherGenotype, Genotype motherGenotype ) {

        if ( fatherGenotype.getGene() != motherGenotype.getGene() ) {
            throw new RuntimeException( "Genotype mismatch" );
        }

        return new Genotype( fatherGenotype.getGene(), fatherGenotype.getNextChildAllele(), motherGenotype.getNextChildAllele() );
    }

    /**
     * Causes two bunnies to have children, with the correct genetics.
     *
     * @param father The father
     * @param mother The mother
     * @return An array of the children
     */
    public static Bunny[] mateBunnies( Bunny father, Bunny mother ) {
        //System.out.println( "Mating " + father + " and " + mother );

        // create the bunnies
        Bunny a = new Bunny( father.getModel(), father, mother, father.getGeneration() + 1 );
        Bunny b = new Bunny( father.getModel(), father, mother, father.getGeneration() + 1 );
        Bunny c = new Bunny( father.getModel(), father, mother, father.getGeneration() + 1 );
        Bunny d = new Bunny( father.getModel(), father, mother, father.getGeneration() + 1 );

        // pair them up with their potential mates
        a.setPotentialMate( b );
        b.setPotentialMate( a );
        c.setPotentialMate( d );
        d.setPotentialMate( c );

        Bunny[] bunnyArray = new Bunny[]{a, b, c, d};
        father.reproduce( bunnyArray );
        mother.reproduce( bunnyArray );

        return bunnyArray;
    }


    //----------------------------------------------------------------------------
    // Notifications
    //----------------------------------------------------------------------------

    public void notifyInit() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_INIT ) );
    }

    private void notifyDeath() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_DIED ) );
    }

    private void notifyReproduces() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_REPRODUCED ) );
    }

    private void notifyAging() {
        notifyListenersOfEvent( new Event( this, Event.TYPE_AGE_CHANGED ) );
    }

    private void notifyChangePosition() {
        Event event = new Event( this, Event.TYPE_POSITION_CHANGED );
        event.setPosition( getX(), getY(), getZ() );
        notifyListenersOfEvent( event );
    }

    private void notifyListenersOfEvent( Event event ) {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onEvent( event );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    /**
     * Interface for objects that want to get events from a bunny
     */
    public interface Listener {
        public void onEvent( Event event );
    }

    /**
     * Events sent to bunny listeners
     */
    public class Event {
        public static final int TYPE_INIT = 0;
        public static final int TYPE_DIED = 1;
        public static final int TYPE_REPRODUCED = 2;
        public static final int TYPE_AGE_CHANGED = 3;
        public static final int TYPE_POSITION_CHANGED = 4;

        public final int type;
        public final Bunny bunny;
        private boolean targeted;
        private Point3D position;

        public Event( Bunny bunny, int type ) {
            this.bunny = bunny;
            this.type = type;
        }

        //----------------------------------------------------------------------------
        // Getters
        //----------------------------------------------------------------------------

        public int getType() {
            return type;
        }

        public Bunny getBunny() {
            return bunny;
        }

        public boolean isTargeted() {
            return targeted;
        }

        public Point3D getPosition() {
            return position;
        }

        //----------------------------------------------------------------------------
        // Setters
        //----------------------------------------------------------------------------

        private void setTargeted( boolean targeted ) {
            this.targeted = targeted;
        }

        private void setPosition( double x, double y, double z ) {
            position = new Point3D.Double( x, y, z );
        }
    }


    /**
     * Show the genetics of the bunny
     *
     * @return A string containing: the bunny ID, in brackets the color, teeth and tail genotypes, and in parenthesis the
     *         phenotypes in the same order
     */
    public String toString() {
        String ret = "#" + String.valueOf( bunnyId ) + "[ " + colorGenotype + " " + teethGenotype + " " + tailGenotype + " (";
        ret += colorPhenotype.toString();
        ret += teethPhenotype.toString();
        ret += tailPhenotype.toString();
        ret += ")]";

        return ret;
    }

}

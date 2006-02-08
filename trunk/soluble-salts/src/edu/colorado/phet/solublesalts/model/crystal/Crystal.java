/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.Atom;
import edu.colorado.phet.solublesalts.model.Binder;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Lattice
 * <p/>
 * A lattice is a crystal of ions. It has a Form that defines how it is constructed.
 * <p/>
 * The lattice has a seed ion. The lattice's position is that of it's seed ion.
 * <p/>
 * At each time step, a crystal determines if it should release an ion. The determination is
 * based on the following factors
 * <ul>
 * <li>Is the crystal in water?
 * <li>Is a random number between 0 and 1 less than the crystal's dissociationLikelihood?
 * </ul>
 * <p/>
 * To know if and how it can release an ion, the crystal must know whether or not it is in
 * the water. Currently, the crystal has knowledge of the vessel. This is not a very good
 * design, and should be changed.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Crystal extends Body implements Binder {

    //================================================================
    // Class data and methods
    //================================================================

    private static EventChannel instanceLifetimeEventChannel = new EventChannel( InstanceLifetimeListener.class );
    protected static InstanceLifetimeListener instanceLifetimeListenerProxy =
            (InstanceLifetimeListener)instanceLifetimeEventChannel.getListenerProxy();
    private static Random random = new Random( System.currentTimeMillis() );
    private static double dissociationLikelihood;

    public static void setDissociationLikelihood( double dissociationLikelihood ) {
        Crystal.dissociationLikelihood = dissociationLikelihood;
    }

    public static class InstanceLifetimeEvent extends EventObject {
        public InstanceLifetimeEvent( Object source ) {
            super( source );
        }

        public Crystal getInstance() {
            return (Crystal)getSource();
        }
    }

    public static interface InstanceLifetimeListener extends EventListener {
        void instanceCreated( InstanceLifetimeEvent event );

        void instanceDestroyed( InstanceLifetimeEvent event );
    }

    public static void addInstanceLifetimeListener( InstanceLifetimeListener listener ) {
        instanceLifetimeEventChannel.addListener( listener );
    }

    public static void removeInstanceLifetimeListener( InstanceLifetimeListener listener ) {
        instanceLifetimeEventChannel.removeListener( listener );
    }

    //================================================================
    // Instance data and methods
    //================================================================

    private Point2D cm = new Point2D.Double();
    private ArrayList ions = new ArrayList();
    // The angle that the lattice is oriented at, relative to the x axis
    private double orientation;
    private Lattice lattice;
    // The list of ions that cannot be bound to this lattice at this time
    private Vector noBindList = new Vector();
    private Rectangle2D waterBounds;


    //----------------------------------------------------------------
    // Lifecycle
    //----------------------------------------------------------------

    /**
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice ) {
        this( model, lattice, new ArrayList() );
    }

    /**
     * Creates a crystal from a list of ions. The first ion in the list is the seed
     *
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     * @param ions
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice, List ions ) {
        this.lattice = (Lattice)lattice.clone();

        // Open up the bounds to include the whole model so we can make lattice
        this.lattice.setBounds( model.getBounds() );

        // We need to interleave the positive and negative ions in the list, as much as possible, so the
        // lattice can be built out without any of them not finding an ion of the opposite polarity to
        // stick to
        ArrayList anions = new ArrayList();
        ArrayList cations = new ArrayList();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            if( ion.getCharge() < 0 ) {
                cations.add( ion );
            }
            else if( ion.getCharge() > 0 ) {
                anions.add( ion );
            }
            else {
                throw new RuntimeException( "ion with 0 charge" );
            }
        }
        ArrayList ions2 = new ArrayList();
        int n = Math.max( anions.size(), cations.size() );
        for( int i = 0; i < n; i++ ) {
            if( i < anions.size() ) {
                ions2.add( anions.get( i ) );
            }
            if( i < cations.size() ) {
                ions2.add( cations.get( i ) );
            }
        }

        for( int i = 0; i < ions2.size(); i++ ) {
            Ion ion = (Ion)ions2.get( i );
            addIon( ion );
            if( this.ions.size() != i + 1 ) {
                System.out.println( "Crystal.Crystal: i = " + i + "\tsize = " + this.ions.size() );
            }
        }
//
//        for( int i = 0; i < ions.size(); i++ ) {
//            Ion ion = (Ion)ions.get( i );
//            addIon( ion );
//            if( this.ions.size() != i + 1 ) {
//                System.out.println( "Crystal.Crystal: i = " + i + "\tsize = " + this.ions.size() );
//            }
//        }

        model.getVessel().addChangeListener( new Vessel.ChangeListener() {
            public void stateChanged( Vessel.ChangeEvent event ) {
                setWaterBounds( event.getVessel() );
            }
        } );
        setWaterBounds( model.getVessel() );

        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
    }

    /**
     * Sets the seed to be the ion closest to the bottom of the vessel
     * todo: This is the wrong way to to this!!!!
     *
     * @param model
     * @param lattice
     * @param ions
     * @param seed
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice, List ions, Ion seed ) {
        this( model, lattice, ions );
        seed = null;
        double maxY = Double.MIN_VALUE;
        for( int i = 0; i < ions.size(); i++ ) {
            Ion testIon = (Ion)ions.get( i );
            if( testIon.getPosition().getY() + testIon.getRadius() > maxY ) {
                maxY = testIon.getPosition().getY() + testIon.getRadius();
                seed = testIon;
            }
        }
        if( seed == null ) {
            throw new RuntimeException( "seed == null" );
        }
        setSeed( seed );
    }

    void setWaterBounds( Vessel vessel ) {
        waterBounds = vessel.getWater().getBounds();
        lattice.setBounds( waterBounds );
    }

    public Point2D getPosition() {
        return lattice.getSeed().getPosition();
    }

    public void leaveModel() {
        instanceLifetimeListenerProxy.instanceDestroyed( new InstanceLifetimeEvent( this ) );
    }

    public Point2D getCM() {
        return cm;
    }

    public double getMomentOfInertia() {
        throw new RuntimeException( "not implemented " );
    }

    private void updateCm() {
        cm.setLocation( 0, 0 );
        for( int i = 0; i < ions.size(); i++ ) {
            Atom atom = (Atom)ions.get( i );
            cm.setLocation( cm.getX() + atom.getPosition().getX(),
                            cm.getY() + atom.getPosition().getY() );
        }
        cm.setLocation( cm.getX() / ions.size(),
                        cm.getY() / ions.size() );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            String s2 = new String( "ion: type = " + ion.getClass() + "  position = " + ion.getPosition() + "\n" );
            sb.append( s2 );
        }
        return sb.toString();
    }

    //----------------------------------------------------------------
    // Lattice building
    //----------------------------------------------------------------

    /**
     * Add and ion to the crystal in a site adjacent to another ion
     *
     * @param ionA The ion to be added
     * @param ionB The ion next to which the other ion should be placed
     * @return
     */
    public boolean addIon( Ion ionA, Ion ionB ) {

        if( noBindList.contains( ionB ) ) {
            return false;
        }

        double orientation = 0;
        Point2D placeToPutIon = null;
        switch( getIons().size() ) {
            case 0:
                throw new RuntimeException( "Crystal contains no ions" );
            case 1:
                orientation = Math.atan2( ionA.getPosition().getY() - lattice.getSeed().getPosition().getY(),
                                          ionA.getPosition().getX() - lattice.getSeed().getPosition().getX() );
                placeToPutIon = lattice.getNearestOpenSite( ionA, ions, orientation );
                break;
            default:
                placeToPutIon = lattice.getNearestOpenSite( ionA, ionB, ions, orientation );

                orientation = Math.atan2( ionA.getPosition().getY() - ionB.getPosition().getY(),
                                          ionA.getPosition().getX() - ionB.getPosition().getX() );
        }

        if( placeToPutIon != null ) {
            ionA.setPosition( placeToPutIon );

            // Sanity check
            for( int i = 0; i < ions.size(); i++ ) {
                Ion testIon = (Ion)ions.get( i );
                if( testIon.getPosition() == placeToPutIon ) {
                    throw new RuntimeException( "duplicate location" );
                }
            }
            ions.add( ionA );
//            ions.add( new BoundIon( ionA, orientation ) );
//            ions.add( ionA );
            ionA.bindTo( this );
            updateCm();
        }

        return placeToPutIon != null;
    }


    /**
     * Returns true if the ion was added. If there wasn't a place to put it in
     * the lattice, returns false
     */
    public boolean addIon( Ion ion ) {

        if( noBindList.contains( ion ) ) {
            return false;
        }

        Point2D placeToPutIon = null;
        switch( getIons().size() ) {
            case 0:
                lattice.setSeed( ion );
                // Give the crystal an orientation that will allow the seed ion to move if the crystal
                // dissolves before another ion attaches.
                orientation = ion.getVelocity().getAngle();
                placeToPutIon = ion.getPosition();
                setPosition( placeToPutIon );
                break;
            case 1:
                orientation = Math.atan2( ion.getPosition().getY() - lattice.getSeed().getPosition().getY(),
                                          ion.getPosition().getX() - lattice.getSeed().getPosition().getX() );
                placeToPutIon = lattice.getNearestOpenSite( ion, ions, orientation );
                break;
            default:
                placeToPutIon = lattice.getNearestOpenSite( ion, ions, orientation );
        }

        if( placeToPutIon != null ) {
            ion.setPosition( placeToPutIon );

            // Sanity check
            for( int i = 0; i < ions.size(); i++ ) {
                Ion testIon = (Ion)ions.get( i );
                if( testIon.getPosition() == placeToPutIon ) {
                    throw new RuntimeException( "duplicate location" );
                }
            }
            ions.add( ion );
            ion.bindTo( this );
            updateCm();
        }

        return placeToPutIon != null;
    }

    /**
     * This method is only to be used when a client remves an ion from the
     * crystal. It is not to be used when the crystal itself releases the ion
     *
     * @param ion
     */
    public void removeIon( Ion ion ) {
        getIons().remove( ion );

        // If there aren't any ions left in the crystal, the crystal should be removed
        // from the model
        if( getIons().size() == 0 ) {
            instanceLifetimeListenerProxy.instanceDestroyed( new InstanceLifetimeEvent( this ) );
        }
    }

    /**
     * Releases an ion from the lattice.
     *
     * @param dt
     */
    public void releaseIon( double dt ) {
        Ion ionToRelease = lattice.getLeastBoundIon( getIons(), orientation );

        // Sanity check
        if( ionToRelease == getSeed() && ions.size() > 1 ) {
            throw new RuntimeException( "ionToRelease == getSeed() && ions.size() > 1" );
        }

        // Sanity check
        if( ionToRelease == null ) {
            throw new RuntimeException( "no ion found to release" );
        }

        Thread t = new Thread( new NoBindTimer( ionToRelease ) );
        t.start();

        Vector2D v = determineReleaseVelocity( ionToRelease );
        ionToRelease.setVelocity( v );
        removeIon( ionToRelease );
        ionToRelease.unbindFrom( this );

        // Give the ion a step so that it isn't in contact with the crystal
        ionToRelease.stepInTime( dt );

        // If there aren't any ions left in the crystal, remove it from the model
        if( getIons().size() == 0 ) {
            leaveModel();
        }
    }


    public void releaseIonDebug( double dt ) {
        releaseIon( dt );
    }

    /**
     * Determine the velocity an ion that is about to be release should have
     *
     * @param ionToRelease
     * @return
     */
    private Vector2D determineReleaseVelocity( Ion ionToRelease ) {

        // If this is the seed ion, then just send it off with the velocity it had before it seeded the
        // crystal. This prevents odd looking behavior if the ion is released soon after it nucleates.
        if( ionToRelease == this.getSeed() ) {
            return ionToRelease.getVelocity();
        }

        // Get the unoccupied sites around the ion being release
        List openSites = lattice.getOpenNeighboringSites( ionToRelease, ions, orientation );
        double maxAngle = Double.MIN_VALUE;
        double minAngle = Double.MAX_VALUE;

        if( openSites.size() > 1 ) {
            for( int i = 0; i < openSites.size() - 1; i++ ) {
                Point2D p1 = (Point2D)openSites.get( i );
                Vector2D.Double v1 = new Vector2D.Double( p1.getX() - ionToRelease.getPosition().getX(),
                                                          p1.getY() - ionToRelease.getPosition().getY() );
                double angle1 = ( v1.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                for( int j = i + 1; j < openSites.size(); j++ ) {

                    // If the two open sites we're now looking at are adjacent, set the velocity to point between them
                    Point2D p2 = (Point2D)openSites.get( j );
                    Vector2D.Double v2 = new Vector2D.Double( p2.getX() - ionToRelease.getPosition().getX(),
                                                              p2.getY() - ionToRelease.getPosition().getY() );
                    double angle2 = ( v2.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                    if( Math.abs( angle2 - angle1 ) < Math.PI ) {
                        double angle = random.nextDouble() * ( angle2 - angle1 ) + angle1;
                        Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
                        return releaseVelocity;
                    }
                }
            }

            Point2D p1 = (Point2D)openSites.get( 0 );
            Vector2D.Double v1 = new Vector2D.Double( p1.getX() - ionToRelease.getPosition().getX(),
                                                      p1.getY() - ionToRelease.getPosition().getY() );
            double angle1 = ( v1.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
            Point2D p2 = (Point2D)openSites.get( 1 );
            Vector2D.Double v2 = new Vector2D.Double( p2.getX() - ionToRelease.getPosition().getX(),
                                                      p2.getY() - ionToRelease.getPosition().getY() );
            double angle2 = ( v2.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
            double angle = random.nextDouble() * ( angle2 - angle1 ) + angle1;
            Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
            return releaseVelocity;
        }

        // If we get here, there is only one open site adjacent to the ion being released
        System.out.println( "openSites.size() = " + openSites.size() );
        Point2D point2D = (Point2D)openSites.get( 0 );
        Vector2D.Double v = new Vector2D.Double( point2D.getX() - ionToRelease.getPosition().getX(),
                                                 point2D.getY() - ionToRelease.getPosition().getY() );
        double angle = ( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
        maxAngle = angle > maxAngle ? angle : maxAngle;
        minAngle = angle < minAngle ? angle : minAngle;

        angle = random.nextDouble() * ( maxAngle - minAngle ) + minAngle;
        Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
        if( releaseVelocity.getMagnitude() < 0.001 ) {
            throw new RuntimeException( "releaseVelocity.getMagnitude() < 0.001" );
        }
        return releaseVelocity;
    }

    /**
     * If the crystal is translated, all its ions must be translated, too
     *
     * @param dx
     * @param dy
     */
    public void translate( double dx, double dy ) {
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            ion.translate( dx, dy );
        }
        super.translate( dx, dy );
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public Atom getSeed() {
        return lattice.getSeed();
    }

    public void setSeed( Ion ion ) {
        lattice.setSeed( ion );
    }

    public ArrayList getIons() {
        return ions;
    }

    public double getDissociationLikelihood() {
        return dissociationLikelihood;
    }

    public List getOccupiedSites() {
        List l = new ArrayList();
        for( int i = 0; i < ions.size(); i++ ) {
            Atom atom = (Atom)ions.get( i );
            l.add( atom.getPosition() );
        }
        return l;
    }

    /**
     * Sets the bounds of the model. I don't know if this is used anymore or not
     *
     * @param bounds
     */
    public void setBounds( Rectangle2D bounds ) {
        lattice.setBounds( bounds );
    }

    //----------------------------------------------------------------
    // Time-dependent behavior
    //----------------------------------------------------------------

    /**
     * If the lattice choses to dissociate, it releases its bound ions and removes
     * itself from the model
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        // Only dissociate if the lattice is in the water
        if( waterBounds.contains( getPosition() ) && random.nextDouble() < dissociationLikelihood ) {
            releaseIon( dt );
        }
        super.stepInTime( dt );
    }

    //================================================================
    // Inner classes
    //================================================================

    /**
     * An agent that keeps an ion from being bound to this lattice for a
     * specified period of time
     */
    private class NoBindTimer implements Runnable {
        private Ion ion;

        public NoBindTimer( Ion ion ) {
            this.ion = ion;
            noBindList.add( ion );
        }

        public void run() {
            try {
                Thread.sleep( SolubleSaltsConfig.RELEASE_ESCAPE_TIME );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            noBindList.remove( ion );
        }
    }


    private class BoundIon extends Ion {
        private double orientation;
        private Ion ion;

        public BoundIon( Ion ion, double orientation ) {
            super( ion.getIonProperties() );
            this.orientation = orientation;
            this.ion = ion;
        }

        public void stepInTime( double dt ) {
            ion.stepInTime( dt );
        }

        public void bindTo( Binder binder ) {
            ion.bindTo( binder );
        }

        public void unbindFrom( Binder binder ) {
            ion.unbindFrom( binder );
        }

        public boolean isBound() {
            return ion.isBound();
        }

        public double getCharge() {
            return ion.getCharge();
        }

        public Crystal getBindingCrystal() {
            return ion.getBindingCrystal();
        }

        public void addChangeListener( ChangeListener listener ) {
            ion.addChangeListener( listener );
        }

        public void removeChangeListener( ChangeListener listener ) {
            ion.removeChangeListener( listener );
        }

        public double getOrientation() {
            return orientation;
        }

        public void setOrientation( double orientation ) {
            this.orientation = orientation;
        }
    }
}

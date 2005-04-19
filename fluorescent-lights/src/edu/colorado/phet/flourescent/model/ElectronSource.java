/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;

import java.util.EventObject;
import java.util.EventListener;
import java.util.Random;
import java.awt.geom.Point2D;

/**
 * ElectronSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronSource extends Electrode {
//public class ElectronSource extends Body {

    private Random random = new Random( System.currentTimeMillis() );

    private double electronsPerSecond;
    private double timeSincelastElectronEmitted;

    private Point2D p1;
    private Point2D p2;

    private BaseModel model;
    private double sinkPotential;

    /**
     * Emits electrons along a line between two points
     *
     * @param model
     * @param p1    One endpoint of the line
     * @param p2    The other endpoint of the line
     */
    public ElectronSource( BaseModel model, Point2D p1, Point2D p2 ) {
        this.model = model;
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Produces electrons when the time is right
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        timeSincelastElectronEmitted += dt;

        // Note that we only produce one electron at a time. Otherwise, we get a bunch of
        // electrons produced if the electronsPerSecond is suddently increased, especially
        // if it had been 0.
        if( 1 / timeSincelastElectronEmitted < electronsPerSecond ) {
            timeSincelastElectronEmitted = 0;
            produceElectron();
        }
    }

    /**
     * Produce a single electron, and notify all listeners that it has happened
     */
    public Electron produceElectron() {
        Electron electron = null;
        if( this.getPotential() - sinkPotential > 0 ) {
            electron = new Electron();

            // Determine where the electron will be emitted from
            double x = random.nextDouble() * ( p2.getX() - p1.getX() ) + p1.getX();
            double y = random.nextDouble() * ( p2.getY() - p1.getY() ) + p1.getY();
            electron.setPosition( x, y );
            model.addModelElement( electron );
            electronProductionListenerProxy.electronProduced( new ElectronProductionEvent( this, electron ) );
        }
        return electron;
    }

    //-----------------------------------------------------------------
    // Getters and setters
    //-----------------------------------------------------------------
    public double getElectronsPerSecond() {
        return electronsPerSecond;
    }

    public void setElectronsPerSecond( double electronsPerSecond ) {
        this.electronsPerSecond = electronsPerSecond;
    }

    public void setSinkPotential( double sinkPotential ) {
        this.sinkPotential = sinkPotential;
//        setElectronsPerSecond( this.getPotential() - sinkPotential );
    }

    public void setCurrent( double current ) {
        setElectronsPerSecond( current );
    }


    /**
     * Sets the length of the electrode. Fields p1 and p2 are modified
     *
     * @param newLength
     */
    public void setLength( double newLength ) {
        double currLength = p1.distance( p2 );
        double ratio = newLength / currLength;
        p1.setLocation( getPosition().getX() + ( p1.getX() - getPosition().getX() ) * ratio,
                        getPosition().getY() + ( p1.getY() - getPosition().getY() ) * ratio );
        p2.setLocation( getPosition().getX() + ( p2.getX() - getPosition().getX() ) * ratio,
                        getPosition().getY() + ( p2.getY() - getPosition().getY() ) * ratio );
    }


    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( ElectronProductionListener.class );
    private ElectronProductionListener electronProductionListenerProxy = (ElectronProductionListener)listenerChannel.getListenerProxy();

    public interface ElectronProductionListener extends EventListener {
        void electronProduced( ElectronProductionEvent event );
    }

    /**
     * Event class for the production of electrons
     */
    public class ElectronProductionEvent extends EventObject {
        private Electron electron;

        public ElectronProductionEvent( Object source, Electron electron ) {
            super( source );
            this.electron = electron;
        }

        public Electron getElectron() {
            return electron;
        }
    }

    public void addListener( ElectronProductionListener electronProductionListener ) {
        listenerChannel.addListener( electronProductionListener );
    }

    public void removeListener( ElectronProductionListener electronProductionListener ) {
        listenerChannel.removeListener( electronProductionListener );
    }
}

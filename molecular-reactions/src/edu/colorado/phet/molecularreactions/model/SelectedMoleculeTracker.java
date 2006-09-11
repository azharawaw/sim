/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.util.List;
import java.util.EventListener;

/**
 * SelectedMoleculeTracker
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectedMoleculeTracker implements ModelElement,
                                                PublishingModel.ModelListener,
                                                SimpleMolecule.Listener{
    private MRModel model;
    private SimpleMolecule moleculeTracked;
    private SimpleMolecule closestMolecule;

    public SelectedMoleculeTracker( MRModel model ) {
        this.model = model;
        model.addListener( this );
    }

    private void setMoleculeTracked( SimpleMolecule moleculeTracked ) {
        SimpleMolecule prevMolecule = this.moleculeTracked;
        if( prevMolecule != null ) {
            prevMolecule.setSelectionStatus( Selectable.NOT_SELECTED );
        }
        this.moleculeTracked = moleculeTracked;
        listenerProxy.moleculeBeingTrackedChanged( moleculeTracked, prevMolecule );
    }

    public SimpleMolecule getMoleculeTracked() {
        return moleculeTracked;
    }

    public void stepInTime( double dt ) {
        List modelElements = model.getModelElements();

        // Look for the closest molecule to the one being tracked that isn't of the
        // same type
        if( moleculeTracked != null /*&& modelElements.contains( moleculeTracked )*/ ) {

            SimpleMolecule prevClosetMolecule = closestMolecule;

            double closestDistSq = Double.POSITIVE_INFINITY;
            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( o instanceof SimpleMolecule && o != moleculeTracked ) {
                    SimpleMolecule testMolecule = (SimpleMolecule)o;
                    double distSq = moleculeTracked.getPosition().distanceSq( testMolecule.getPosition() );
                    if( distSq < closestDistSq ) {
                        closestDistSq = distSq;
                        closestMolecule = testMolecule;
                    }
                }
            }

            if( closestMolecule != null && closestMolecule != prevClosetMolecule ) {
                if( prevClosetMolecule != null ) {
                    prevClosetMolecule.setSelectionStatus( Selectable.NOT_SELECTED);
                }
                closestMolecule.setSelectionStatus( Selectable.NEAREST_TO_SELECTED );
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ((SimpleMolecule)element).addListener( this );
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ((SimpleMolecule)element).removeListener( this );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            setMoleculeTracked( molecule );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface Listener extends EventListener {
        void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule, SimpleMolecule prevTrackedMolecule);
    }

    private EventChannel listenerEventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerEventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        listenerEventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerEventChannel.removeListener( listener );
    }
}

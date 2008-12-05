/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayModelListener;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Polonium211CompositeNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.AlphaDecayNucleusTypeControl;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class SingleNucleusAlphaDecayModel implements AlphaDecayNucleusTypeControl {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	public int DEFAULT_NUCLEUS_TYPE_ID = NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private AlphaDecayCompositeNucleus _atomicNucleus;
    private AlphaParticle _tunneledAlpha;
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners = new ArrayList();
    private int _nucleusID;
    private AtomicNucleus.Adapter _atomicNucleusAdapter;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public SingleNucleusAlphaDecayModel(NuclearPhysicsClock clock)
    {
        _clock = clock;
        _nucleusID = DEFAULT_NUCLEUS_TYPE_ID;

        // Register as a listener to the clock.
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
            	if (_nucleusID != DEFAULT_NUCLEUS_TYPE_ID){
            		_nucleusID = DEFAULT_NUCLEUS_TYPE_ID;
            		addOrReplaceNucleus();
            	}
            	else{
            		resetNucleus();
            	}
            }
        });

        // Create the listener that will be registered for model events.
        _atomicNucleusAdapter = new AtomicNucleus.Adapter(){
            
            public void atomicWeightChanged(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                
                if (byProducts != null){
                    // There are some byproducts of this event that need to be
                    // managed by this object.
                    for (int i = 0; i < byProducts.size(); i++){
                        Object byProduct = byProducts.get( i );
                        if (byProduct instanceof AlphaParticle){
                            _tunneledAlpha = (AlphaParticle)byProduct;
                        }
                        else {
                            // We should never get here, debug it if it does.
                            System.err.println("Error: Unexpected byproduct of decay event.");
                            assert false;
                        }
                    }
                }
            }
        };
        
        addOrReplaceNucleus(); 
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    /**
     * Get a reference to the nucleus, of which there is only one in this
     * model.
     * 
     * @return - Reference to the nucleus model element.
     */
    public AlphaDecayCompositeNucleus getAtomNucleus()
    {
        return _atomicNucleus;
    }
    
    public ConstantDtClock getClock(){
        return _clock;
    }
    
	public boolean getEnergyChartShowing() {
		// The energy chart is always showing for this class.
		return true;
	}

	public void setNucleusType(int nucleusId) {
		_nucleusID = nucleusId;
		addOrReplaceNucleus();
	}
	
	public int getNucleusType(){
		return _nucleusID;
	}
	
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

	/**
	 * Reset the currently active nucleus.
	 */
	public void resetNucleus(){
        // Reset the nucleus, including passing the alpha particle back to it.
        _atomicNucleus.reset( _tunneledAlpha );
        if (_tunneledAlpha != null){
        	notifyModelElementRemoved(_tunneledAlpha);
        }
        _tunneledAlpha = null;
	}
	
    /**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(AlphaDecayModelListener listener)
    {
        assert !_listeners.contains( listener );
        
        if (!_listeners.contains( listener )){
            _listeners.add( listener );
        }
    }
    
    // TODO: JPB TBD - Make this real.
    public double getHalfLife(){
    	return 0;
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
        if (_tunneledAlpha != null){
            // We have a particle that has tunneled and needs to be moved.
            _tunneledAlpha.moveOut();
        }
    }
    
    /**
     * Add a new nucleus of the currently selected type.  If the selected type
     * of nucleus already exists, this does nothing.  If the nucleus is not of
     * the desired type, then replace it.
     */
	private void addOrReplaceNucleus() {

		// First check if the requested nucleus already exists.
		if (_atomicNucleus != null){
			switch (_nucleusID){
			case NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM:
		        if (_atomicNucleus instanceof Polonium211CompositeNucleus){
		        	// Nothing to do, since we already have the nucleus we need.
		        	return;
		        }
		        break;
				
			case NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM:
		        if (_atomicNucleus instanceof AdjustableHalfLifeCompositeNucleus){
		        	// Nothing to do, since we already have the nucleus we need.
		        	return;
		        }
		        break;
			}

		    // Remove listener from current nucleus.
			_atomicNucleus.removeListener(_atomicNucleusAdapter);
			
			// Remove the nucleus itself and inform any listeners of its demise.
			_atomicNucleus.removedFromModel();
			AlphaDecayCompositeNucleus tempNucleus = _atomicNucleus;
			_atomicNucleus = null;
			_tunneledAlpha = null;
			notifyModelElementRemoved(tempNucleus);
		}
		
		// Create the nucleus that will demonstrate alpha decay.
		switch (_nucleusID){
		case NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM:
	        _atomicNucleus = new Polonium211CompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
			
		case NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM:
	        _atomicNucleus = new AdjustableHalfLifeCompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
		}
        
        // Register as a listener for the nucleus so we can handle the
        // particles thrown off by alpha decay.
        _atomicNucleus.addListener( _atomicNucleusAdapter );
        
        // Inform any listeners of the changes.
        notifyModelElementAdded(_atomicNucleus);
        notifyNucleusTypeChanged();
	}
	
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void notifyModelElementRemoved(Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void notifyModelElementAdded(Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).modelElementAdded( addedElement );
        }
    }

    /**
     * Notify listeners about the change of nucleus type.
     * @param addedElement
     */
    private void notifyNucleusTypeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).nucleusTypeChanged();
        }
    }

    /**
     * Notify listeners about the change of nucleus type.
     * @param addedElement
     */
    private void notifyHalfLifeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).halfLifeChanged();
        }
    }
    

}

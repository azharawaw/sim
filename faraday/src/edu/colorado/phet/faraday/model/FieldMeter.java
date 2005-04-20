/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * FieldMeter is the model of a B-field meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FieldMeter extends FaradayObservable implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Magnet that the field meter is observing.
    private AbstractMagnet _magnetModel;
    // B-field vector at the field meter's location.
    private Vector2D _fieldVector;
    // The field meter's location.
    private Point2D _location;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public FieldMeter( AbstractMagnet magnetModel ) {
        super();
        
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _fieldVector = new Vector2D();
        _location = new Point2D.Double();
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
  
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the strength at the field meter's location.
     * 
     * @param vector strength value is copied here
     */
    public void getStrength( Vector2D vector /* output */ ) {
        assert( vector != null );
        vector.copy( _fieldVector );
    }
    
    //----------------------------------------------------------------------------
    // FaradayObservable overrides
    //----------------------------------------------------------------------------
    
    /**
     * Respond to changes in superclass attributes (eg, location).
     */
    public void notifySelf() {
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        if ( isEnabled() ) {
            getLocation( _location /* output */);
            _magnetModel.getStrength( _location, _fieldVector /* output */);
            notifyObservers();
        }
    }
}

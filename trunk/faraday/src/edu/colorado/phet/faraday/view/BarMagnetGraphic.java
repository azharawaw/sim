/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;


/**
 * BarMagnetGraphic is the graphical representation of a bar magnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double TRANSPARENT_LAYER = 1;
    private static final double OPAQUE_LAYER = 2;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AbstractMagnet _magnetModel;
    private PhetImageGraphic _opaqueMagnetGraphic, _transparentMagnetGraphic;
    private boolean _transparencyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param barMagnetModel model of the bar magnet
     */
    public BarMagnetGraphic( Component component, AbstractMagnet magnetModel ) {
        super( component );
        assert( component != null );
        assert( magnetModel != null );
        
        // Save a reference to the model.
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        // Create the images.
        _opaqueMagnetGraphic = new PhetImageGraphic( component, FaradayConfig.BAR_MAGNET_IMAGE );
        _transparentMagnetGraphic = new PhetImageGraphic( component, FaradayConfig.BAR_MAGNET_TRANSPARENT_IMAGE );
        
        // WORKAROUND:
        // Swapping graphics doesn't seem to work correctly.
        // So we'll add both graphics, then toggle the visibility of the opaque layer.
        addGraphic( _transparentMagnetGraphic, TRANSPARENT_LAYER );
        addGraphic( _opaqueMagnetGraphic, OPAQUE_LAYER );
        
        // Registration point is the center of the image.
        _opaqueMagnetGraphic.setRegistrationPoint( 
                _opaqueMagnetGraphic.getImage().getWidth() / 2, _opaqueMagnetGraphic.getImage().getHeight() / 2 );
        _transparentMagnetGraphic.setRegistrationPoint( 
                _transparentMagnetGraphic.getImage().getWidth() / 2, _transparentMagnetGraphic.getImage().getHeight() / 2 );

        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                double x = _magnetModel.getX() + e.getDx();
                double y = _magnetModel.getY() + e.getDy();
                _magnetModel.setLocation( x, y );
            }
        } );
        
        // Use the opaque image by default.
        setTransparencyEnabled( false );
        
        // Synchronize view with model.
        update();
    }

    public void finalize() {
        _magnetModel.removeObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    /** 
     * Enabled and disables transparency of the magnet graphic.
     * 
     * @param enabled true for transparency, false for opaque
     */
    public void setTransparencyEnabled( boolean enabled ) {
        _transparencyEnabled = enabled;
        _opaqueMagnetGraphic.setVisible( !enabled );
    }
    
    /**
     * Gets the current state of the magnet graphic transparency.
     * 
     * @return true if transparency, false if opaque
     */
    public boolean getTransparencyEnabled() {
        return _transparencyEnabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        if ( isVisible() ) {
            
            clearTransform();

            // Rotation
            rotate( Math.toRadians( _magnetModel.getDirection() ) );
            
            // Scale
            double scaleX = _magnetModel.getWidth() / _opaqueMagnetGraphic.getImage().getWidth();
            double scaleY = _magnetModel.getHeight() / _opaqueMagnetGraphic.getImage().getHeight();
            scale( scaleX, scaleY );
            
            // Location
            setLocation( (int) _magnetModel.getX(), (int) _magnetModel.getY() );
        }
    }
}

package edu.colorado.phet.testlocation.view;

import java.awt.Component;


import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.testlocation.model.CarModelElement;

/**
 * CarGraphic is the graphical representation of a car that moves horizontally.
 * The origin is at the bottom-center of the car, at its starting point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CarGraphic extends PhetImageGraphic implements SimpleObserver {

    private CarModelElement _carModelElement;
     
    public CarGraphic( Component component, CarModelElement carModelElement ) {
        super( component, "images/car.png" );
        _carModelElement = carModelElement;
        _carModelElement.addObserver( this );
        
        // Registration point is bottom center of the car.
        int rx = getImage().getWidth() / 2;
        int ry = getImage().getHeight();
        setRegistrationPoint( rx, ry );
    }

    public void finalize() {
        _carModelElement.removeObserver( this );
    }
    
    public void update() {
        
        int x = (int) _carModelElement.getLocation();
        double direction = _carModelElement.getDirection();
        
        clearTransform();
        if ( direction == 0 ) {
            scale( 1.0, 1.0 );
        }
        else {
            scale( -1.0, 1.0 );
        }
        translate( x, 0 );
    }
}

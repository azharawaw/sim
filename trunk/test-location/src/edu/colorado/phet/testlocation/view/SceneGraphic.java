package edu.colorado.phet.testlocation.view;

import java.awt.Component;


import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.testlocation.model.CarModelElement;
import edu.colorado.phet.testlocation.model.WindmillModelElement;

/**
 * SceneGraphic demostrates creation of a complex scene involving 
 * composite and non-composite graphics. The scene's origin is at
 * the upper left corner of the background image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SceneGraphic extends CompositePhetGraphic {

    public SceneGraphic( Component component, WindmillModelElement windmillModelElement, CarModelElement carModelElement ) {
        super( component );

        // Graphics components
        PhetImageGraphic background = new PhetImageGraphic( component, "images/landscape.png" );
        WindmillGraphic windmill = new WindmillGraphic( component, windmillModelElement );
        CarOnRoadGraphic carOnRoad = new CarOnRoadGraphic( component, carModelElement );
        
        windmill.setLocation( 150, 70 );
        windmill.scale( 0.25 );
        
        carOnRoad.setLocation( 0, 128 );
        carOnRoad.scale( 0.25 );
        
        addGraphic( background );
        addGraphic( windmill );
        addGraphic( carOnRoad );
        
        // Interactivity
        InteractivityHandler.register( this );
    }
}

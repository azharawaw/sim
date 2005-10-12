/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.view.IonGraphic;

import java.util.HashMap;

/**
 * IonGraphicManager
 * <p/>
 * Creates graphics for ions when they are added to a model and adds the graphics to a canvas,
 * and removes the graphics from the canvas when the ions leave the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonGraphicManager implements SolubleSaltsModel.IonListener {

    private PhetPCanvas graphicContainer;
    private HashMap ionToGraphicMap = new HashMap();

    public IonGraphicManager( PhetPCanvas graphicContainer ) {
        this.graphicContainer = graphicContainer;
    }

    public void ionAdded( SolubleSaltsModel.IonEvent event ) {
        IonGraphic ig = new IonGraphic( event.getIon(), SolubleSaltsConfig.BLUE_ION_IMAGE_NAME );
        graphicContainer.addWorldChild( ig );
        ionToGraphicMap.put( event.getIon(), ig );
    }

    public void ionRemoved( SolubleSaltsModel.IonEvent event ) {
        IonGraphic ig = (IonGraphic)ionToGraphicMap.get( event.getIon() );
        graphicContainer.removeWorldChild( ig );
        ionToGraphicMap.remove( event.getIon() );
    }
}

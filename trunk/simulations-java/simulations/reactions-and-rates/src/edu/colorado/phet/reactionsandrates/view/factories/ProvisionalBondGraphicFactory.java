// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.factories;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.reactionsandrates.model.ProvisionalBond;
import edu.colorado.phet.reactionsandrates.util.ModelElementGraphicManager;
import edu.colorado.phet.reactionsandrates.view.ProvisionalBondGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * ProvisionalBondFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

    public ProvisionalBondGraphicFactory( PNode layer ) {
        super( ProvisionalBond.class, layer );
    }

    public PNode createGraphic( ModelElement modelElement ) {
        return new ProvisionalBondGraphic( (ProvisionalBond)modelElement );
    }
}


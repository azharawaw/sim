/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.module.DNAModule;

/**
 * DNAControlPanel is the control panel for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAModule _module;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public DNAControlPanel( DNAModule module ) {
        super( module );

        _module = module;

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Layout
        {
            addResetButton();
        }
    }

}

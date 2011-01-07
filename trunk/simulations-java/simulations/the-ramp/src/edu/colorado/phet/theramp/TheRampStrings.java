// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.theramp;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * User: Sam Reid
 * Date: Aug 21, 2006
 * Time: 2:21:41 PM
 */

public class TheRampStrings {
    
    private static final PhetResources RESOURCES = new PhetResources( TheRampConstants.PROJECT_NAME );

    /* not intended for instantiation */
    private TheRampStrings() {}

    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }

    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }

    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }

    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }

}

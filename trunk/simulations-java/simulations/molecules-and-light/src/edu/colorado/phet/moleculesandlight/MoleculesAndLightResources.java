// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculesandlight;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class MoleculesAndLightResources {

    private static final PhetResources RESOURCES = new PhetResources( MoleculesAndLightConfig.PROJECT_NAME );

    /* not intended for instantiation */
    private MoleculesAndLightResources() {
    }

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
    
    public static final int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }
}

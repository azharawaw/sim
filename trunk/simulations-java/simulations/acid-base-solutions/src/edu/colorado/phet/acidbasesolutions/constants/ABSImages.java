/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.image.BufferedImage;

/**
 * Images used throughout the simulation.
 * Loaded statically to make it easy to debug missing images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSImages {

    /* not intended for instantiation */
    private ABSImages() {}
    
    public static final BufferedImage A_MINUS_MOLECULE = getBufferedImage( "molecules/A_minus.png" );
    public static final BufferedImage B_MOLECULE = getBufferedImage( "molecules/B.png" );
    public static final BufferedImage BH_PLUS_MOLECULE = getBufferedImage( "molecules/BH_plus.png" );
    public static final BufferedImage H2O_MOLECULE = getBufferedImage( "molecules/H2O.png" );
    public static final BufferedImage H3O_PLUS_MOLECULE = getBufferedImage( "molecules/H3O_plus.png" );
    public static final BufferedImage HA_MOLECULE = getBufferedImage( "molecules/HA.png" );
    public static final BufferedImage M_PLUS_MOLECULE = getBufferedImage( "molecules/M_plus.png" );
    public static final BufferedImage MOH_MOLECULE = getBufferedImage( "molecules/MOH.png" );
    public static final BufferedImage OH_MINUS_MOLECULE = getBufferedImage( "molecules/OH_minus.png" );
    
    public static final BufferedImage ARROW_DOUBLE = getBufferedImage( "arrow_double.png" );
    public static final BufferedImage ARROW_SINGLE = getBufferedImage( "arrow_single.png" );
    
    public static final BufferedImage BATTERY= getBufferedImage( "battery.png" );
    public static final BufferedImage LIGHT_BULB= getBufferedImage( "lightbulb.png" );
    
    private static final BufferedImage getBufferedImage( String resourceName ) {
        return ABSResources.getBufferedImage( resourceName );
    }
}

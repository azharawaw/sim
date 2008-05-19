/* Copyright 2006-2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;


/**
 * ColorUtils is a collection of utilities related to Color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorUtils {

    /* Not intended for instantiation */
    private ColorUtils() {
    }

    /**
     * Creates a new color by taking an existing color and changing its alpha channel.
     * Useful when you need to modulate the alpha channel.
     *
     * @param c
     * @param alpha
     * @return Color
     */
    public static Color createColor( Color c, int alpha ) {
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
    }

    /**
     * Interpolates between 2 colors in RGBA space.
     * When distance is 0, color1 is returned.
     * When distance is 1, color2 is returned.
     * Other values of distance return a color somewhere between color1 and color2.
     * Each color component is interpolated separately.
     *
     * @param color1
     * @param color2
     * @param distance distance between color1 and color2, 0 <= distance <= 1
     * @return Color
     * @throws IllegalArgumentException if distance is out of range
     */
    public static Color interpolateRBGA( Color color1, Color color2, double distance ) {
        if ( distance < 0 || distance > 1 ) {
            throw new IllegalArgumentException( "distance out of range: " + distance );
        }
        int r = (int) interpolate( color1.getRed(), color2.getRed(), distance );
        int g = (int) interpolate( color1.getGreen(), color2.getGreen(), distance );
        int b = (int) interpolate( color1.getBlue(), color2.getBlue(), distance );
        int a = (int) interpolate( color1.getAlpha(), color2.getAlpha(), distance );
        return new Color( r, g, b, a );
    }

    /*
    * Interpolates between 2 values.
    * @param value1
    * @param value2
    * @param distance distance between value1 and value2, 0 <= distance <= 1
    * @return double, such that value1 <= double <= value2
    */
    private static double interpolate( double value1, double value2, double distance ) {
        assert ( distance >= 0 && distance <= 1 );
        return value1 + ( distance * ( value2 - value1 ) );
    }

    /**
     * Is this a visible wavelength?
     *
     * @param wavelength
     * @return true or false
     */
    public static final boolean isVisible( double wavelength ) {
        return ( wavelength >= VisibleColor.MIN_WAVELENGTH && wavelength <= VisibleColor.MAX_WAVELENGTH );
    }

    /**
     * Is this a UV (ultraviolet) wavelength?
     *
     * @param wavelength
     * @return true or false
     */
    public static final boolean isUV( double wavelength ) {
        return ( wavelength < VisibleColor.MIN_WAVELENGTH );
    }

    /**
     * Is this an IR (infrared) wavelength?
     *
     * @param wavelength
     * @return true or false
     */
    public static final boolean isIR( double wavelength ) {
        return ( wavelength > VisibleColor.MAX_WAVELENGTH );
    }
}

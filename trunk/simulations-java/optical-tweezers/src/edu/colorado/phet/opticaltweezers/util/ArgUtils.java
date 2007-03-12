/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.util;

/**
 * ArgUtils is a set of utilites for processing command line arguments.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ArgUtils {

    private ArgUtils() {}
    
    /**
     * Does the set of command line args contain a specific arg?
     * 
     * @param args
     * @param arg
     * @return true or false
     */
    public static final boolean contains( String[] args, String arg ) {
        boolean found = false;
        if ( args != null && arg != null ) {
            for ( int i = 0; i < args.length; i++ ) {
                if ( arg.equals( args[i] ) ) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }
}

/*  */
package edu.colorado.phet.cck.common;

import edu.colorado.phet.cck.CCKResources;

/**
 * User: Sam Reid
 * Date: Jun 24, 2006
 * Time: 10:11:33 PM
 */

public class CCKStrings {
    public static String getString( String s ) {
        return CCKResources.getString( s );
    }

    public static String toHTML( String key ) {
        String text = getString( key );
        return "<html>" + text.replaceAll( "\n", "<br>" ) + "</html>";
    }
}

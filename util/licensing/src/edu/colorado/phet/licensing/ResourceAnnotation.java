package edu.colorado.phet.licensing;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.AnnotationParser;

/**
 * Object-oriented representation of one line in a resource annotation file. 
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResourceAnnotation implements ResourceAnnotationElement {
    
    /**
     * A resource annotation consists of key-value pairs.
     * These are the supported keys.
     * To add a new key, add a constants here, add the constant to KEYS, and create a get method.
     */
    private static final String SOURCE_KEY = "source"; // where we found the resource (organization, URL, etc.)
    private static final String AUTHOR_KEY = "author"; // person or organization that created the resource
    private static final String LICENSE_KEY = "license"; // the type of license, see PhetRuleSet for a list of recognized licenses
    private static final String NOTES_KEY = "notes"; // any misc notes that you want to include
    private static final String SAME_KEY = "same";  //if this resource is a copy of another, you can reference the original here
    private static final String LICENSEFILE_KEY = "licensefile";  // file that contains the actual license text
    
    private static final String[] KEYS = { SOURCE_KEY, AUTHOR_KEY, LICENSE_KEY, NOTES_KEY, SAME_KEY, LICENSEFILE_KEY };
    
    private String name;
    private HashMap<String,String> values; // maps keys to values, for those keys that have values

    public ResourceAnnotation( String line ) {
        values = new HashMap<String,String>();
        parse( line );
    }
    
    public String getName() {
        return name;
    }

    public String getSource() {
        return values.get( SOURCE_KEY );
    }
    
    /**
     * @deprecated This is being misused in ResourceAnnotationList as a hack for documenting things that aren't annotated. This is a bad design.
     */
    public void setSource( String value ) {
        values.put( SOURCE_KEY, value );
    }

    public String getAuthor() {
        return values.get( AUTHOR_KEY );
    }

    public String getLicense() {
        return values.get( LICENSE_KEY );
    }

    public String getSame() {
        return values.get( SAME_KEY );
    }

    public String getNotes() {
        return values.get( NOTES_KEY );
    }

    public String getLicensefile() {
        return values.get( LICENSEFILE_KEY );
    }
    
    public String toText() {
        String s = name;
        for ( String key : values.keySet() ) {
            s += " " + key + "=" + values.get( key );
        }
        return s;
    }

    private void parse( String line ) {
        try {
            AnnotationParser.Annotation a = AnnotationParser.parse( line.trim() );
            name = a.getId();
            for ( String key : KEYS ) {
                String value = a.get( key );
                if ( value != null ) {
                    values.put( key, a.get( key ) );
                }
            }
        }
        catch ( Exception e ) {
            System.err.println( "Error on line=" + line );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}

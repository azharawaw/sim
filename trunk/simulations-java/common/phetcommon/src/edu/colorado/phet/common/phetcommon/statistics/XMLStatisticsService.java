package edu.colorado.phet.common.phetcommon.statistics;

import java.io.*;
import java.net.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.*;

import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 * Statistics service that uses PHP to deliver a statistics message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class XMLStatisticsService implements IStatisticsService {
    
    private static final boolean ENABLE_DEBUG_OUTPUT = true;

    /**
     * Delivers a statistics message to PhET.
     *
     * @param message
     */
    public void postMessage( StatisticsMessage message ) throws IOException {
        postXML( getStatisticsURL( message ), toXML( message ) );
    }

    private String toXML( StatisticsMessage message ) {
        try {
            return toXMLFromDom( message );
        }
        catch( Exception e ) {
            e.printStackTrace();
            return toXMLFromStringConcatenation( message );
        }
    }

    private String toXMLFromDom( StatisticsMessage message ) throws ParserConfigurationException, TransformerException {

        //see: http://www.genedavis.com/library/xml/java_dom_xml_creation.jsp
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement( "statistics" );
        for (int i=0;i<message.getFieldCount();i++){
            root.setAttribute( message.getField(i ).getName(),message.getField( i ).getValue() );
        }
        doc.appendChild( root );

        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
        trans.setOutputProperty( OutputKeys.INDENT, "yes" );

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult( sw );
        DOMSource source = new DOMSource( doc );
        trans.transform( source, result );
        String xmlString = sw.toString();

        //print xml
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( getClass().getName() + ": xmlString=\n" + xmlString );
        }
        return xmlString;
    }

    private String toXMLFromStringConcatenation( StatisticsMessage message ) {
        String xml = "<statistics ";
        for ( int i = 0; i < message.getFieldCount(); i++ ) {
            xml += message.getField( i ).getName() + "=\"" + encode( message.getField( i ).getValue() + "\"" ) + " ";
        }
        xml += "/>";
        return xml;
    }

    private String encode( String value ) {
        //TODO: turn - into %2D as in flash?
        return value;
    }

    /*
     * The URL points to a PHP script, with name/value pairs appended to the URL.
     */
    private String getStatisticsURL( StatisticsMessage message ) {
        return "http://phet.colorado.edu/statistics/submit_message.php";
    }

    public static void postXML( String url, String xml ) throws IOException {
        // open connection
        URL urlObject = new URL( url );
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
        connection.setDoOutput( true );

        // post
        if ( ENABLE_DEBUG_OUTPUT ) {
        System.out.println( XMLStatisticsService.class.getName() + ": posting to " + url + " ..." );
        }
        OutputStreamWriter outStream = new OutputStreamWriter( connection.getOutputStream(), "UTF-8" );
        outStream.write( xml );
        outStream.close();

        // Get the response
        if ( ENABLE_DEBUG_OUTPUT ) {
            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            System.out.println( XMLStatisticsService.class.getName() + ": reading response ..." );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                System.out.println( line );
            }
            reader.close();
            System.out.println( "done." );
        }
    }


    public static void main( String[] args ) throws IOException {
        String URL_STRING = "http://phet.colorado.edu/statistics/submit_message.php";
        String XML_STRING = "<xml>hello stats1234</xml>";
        postXML( URL_STRING, XML_STRING );
    }
}
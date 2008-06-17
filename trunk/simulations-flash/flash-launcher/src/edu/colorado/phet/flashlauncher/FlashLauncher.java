package edu.colorado.phet.flashlauncher;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.colorado.phet.flashlauncher.util.BareBonesBrowserLaunch;
import edu.colorado.phet.flashlauncher.util.FileUtils;

/**
 * FlashLauncher is the mechanism for launching Flash simulations.
 * It is bundled into a JAR file as the mainclass.
 * An args.txt file in the JAR identifies the simulation name and language code,
 * which are used to fill in an HTML template.
 * A web browser is launched, and pointed at the HTML.
 * 
 * Created by: Sam
 * May 29, 2008 at 7:53:28 AM
 */
public class FlashLauncher {
    
    private String sim;
    private String language;
    private static JTextArea jTextArea;

    public FlashLauncher() throws IOException {
        
        // read sim and language from args.txt, a JAR resource file
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "args.txt" );
        BufferedReader bu = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bu.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        println( "line = " + line );
        this.sim = stringTokenizer.nextToken();
        this.language = stringTokenizer.nextToken();
        
        // if the developer flag is specified in args.txt, open a window to show debug output
        if ( stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equals( "-dev" ) ) {
            println( "FlashLauncher.FlashLauncher dev" );
            JFrame frame = new JFrame( "Text" );
            jTextArea = new JTextArea( 10, 50 );
            frame.setContentPane( new JScrollPane( jTextArea ) );
            frame.setVisible( true );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setSize( 800, 600 );
        }
    }

    /**
     * Prints to the debug window, if it exists.
     */
    public static void println( String string ) {
        System.out.println( string );
        if ( jTextArea != null ) {
            jTextArea.append( string + "\n" );
        }
    }

    public static void main( String[] args ) throws IOException {
//        JOptionPane.showMessageDialog( null, System.getProperty( "java.class.path" ) );
        new FlashLauncher().start();
    }

    /*
     * Launches the simulation in a web browser.
     */
    private void start() throws IOException {
        
        println( "FlashLauncher.start" );
        println( "System.getProperty( \"user.dir\" ) = " + System.getProperty( "user.dir" ) );
        File currentDir = new File( System.getProperty( "user.dir" ) );
        File tempDir = new File( currentDir, "temp-"+sim+"-phet" );
        
        // unzip the JAR into temp directory
        File jarfile = getJARFile();
        println( "jarfile = " + jarfile );
        println( "Starting unzip jarfile=" + jarfile + ", tempDir=" + tempDir );
        FileUtils.unzip( jarfile, tempDir );
        println( "Finished unzip" );
       
        // dynamically generate an HTML file
        String html = generateHTML( sim, language );
        File htmlFile = new File( tempDir, sim + "_" + language + ".html" );
        FileOutputStream outputStream = new FileOutputStream( htmlFile );
        outputStream.write( html.getBytes() );
        outputStream.close();

        // open the browser, point it at the HTML file
        println( "Starting openurl" );
        BareBonesBrowserLaunch.openURL( "file://" + htmlFile.getAbsolutePath() );
    }
    
    /*
     * Reads the HTML template and fills in the blanks for sim and language.
     */
    private static String generateHTML( String sim, String language ) throws IOException {
        String s = "";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "flash-template.html" );
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bufferedReader.readLine();
        while ( line != null ) {
            s += line;
            line = bufferedReader.readLine();
            if ( line != null ) {
                s += System.getProperty( "line.separator" );
            }
        }
        s = s.replaceAll( "@SIM@", sim );
        s = s.replaceAll( "@LANGUAGE@", language );
        return s;
    }

    /*
     * Gets the JAR file that this class was launched from.
     */
    private File getJARFile() {
        URL url = FlashLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URI uri = new URI( url.toString() );
            return new File( uri.getPath() );
        }
        catch( URISyntaxException e ) {
            println( e.getMessage() );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}

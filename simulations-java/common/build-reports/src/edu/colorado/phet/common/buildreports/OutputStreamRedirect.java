package edu.colorado.phet.common.buildreports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OutputStreamRedirect extends Thread {
    private InputStream inputStream;
    private String sourceName;

    public OutputStreamRedirect( InputStream inputStream, String sourceName ) {
        this.inputStream = inputStream;
        this.sourceName = sourceName;
    }

    public void run() {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
        try {
            String line = bufferedReader.readLine();
            while ( line != null ) {
                System.out.println( sourceName + ": " + line );
                line = bufferedReader.readLine();
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
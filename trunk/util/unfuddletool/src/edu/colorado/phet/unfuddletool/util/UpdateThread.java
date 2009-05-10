package edu.colorado.phet.unfuddletool.util;

import java.util.Date;

import javax.swing.*;

public class UpdateThread extends Thread {
    public void run() {
        try {
            while ( true ) {
                // every minute
                Thread.sleep( 1000 * 60 );

                System.out.println( "Requesting latest activity from the server at " + ( new Date() ) );

                //SwingUtilities.invokeLater( new Runnable() {
                //    public void run() {
                        Activity.requestRecentActivity( 3 );
                //    }
                //} );
            }
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}

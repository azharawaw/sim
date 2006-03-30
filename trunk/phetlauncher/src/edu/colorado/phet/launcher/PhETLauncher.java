/* Copyright 2004, Sam Reid */
package edu.colorado.phet.launcher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 29, 2006
 * Time: 9:17:08 PM
 * Copyright (c) Mar 29, 2006 by Sam Reid
 */

public class PhETLauncher {
    private JFrame frame;
    private ArrayList launchers = new ArrayList();
    private JLabel webAvailableLabel;

    public PhETLauncher() throws IOException {
        frame = new JFrame( "PhET Launcher" );
        frame.setSize( 600, 600 );
        URL location = new URL( "http://www.colorado.edu/physics/phet/dev/waveinterference/0.03/waveinterference.jnlp" );
        ApplicationComponent applicationComponent = new ApplicationComponent( location );
        launchers.add( applicationComponent );

        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        frame.setContentPane( contentPane );
        webAvailableLabel = new JLabel();
        contentPane.add( webAvailableLabel );
        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    refresh();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        contentPane.add( refresh );
        contentPane.add( applicationComponent );
        refresh();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private void refresh() throws IOException {
        for( int i = 0; i < launchers.size(); i++ ) {
            ApplicationComponent applicationComponent = (ApplicationComponent)launchers.get( i );
            applicationComponent.refresh();
        }
        webAvailableLabel.setText( isWebAvailable() ? "Web is Available" : "You are off-line" );
    }

    private boolean isWebAvailable() {
        try {
            URL google = new URL( "http://www.google.com" );
            URLConnection conn = google.openConnection();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            bufferedReader.readLine();
            return true;
        }
        catch( MalformedURLException e ) {
        }
        catch( IOException e ) {
        }
        return false;
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) throws IOException {
        new PhETLauncher().start();
    }
}

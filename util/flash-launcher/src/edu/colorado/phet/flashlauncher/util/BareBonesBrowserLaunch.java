package edu.colorado.phet.flashlauncher.util;

//See http://www.centerkey.com/java/browser/

/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5 (December 10, 2005)                    //
//  By Dem Pilafian                                    //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import java.lang.reflect.Method;

import javax.swing.*;

public class BareBonesBrowserLaunch {

    private static final String errMsg = "Error attempting to launch web browser";

    public static void openURL( String url ) {
        String osName = System.getProperty( "os.name" );
        System.out.println( "osName = " + osName );
        try {
            if ( osName.startsWith( "Mac OS" ) ) {
                System.out.println( "Mac" );
                Class fileMgr = Class.forName( "com.apple.eio.FileManager" );
                System.out.println( "fileMgr = " + fileMgr );
                Method openURL = fileMgr.getDeclaredMethod( "openURL",
                                                            new Class[]{String.class} );
                System.out.println( "openURL = " + openURL );
                System.out.println( "url = " + url );
                openURL.invoke( null, new Object[]{url} );
                System.out.println( "invocation finished" );
            }
            else if ( osName.startsWith( "Windows" ) ) {
                Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + url );
            }
            else { //assume Unix or Linux
                String[] browsers = {
                        "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for ( int count = 0; count < browsers.length && browser == null; count++ ) {
                    if ( Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]} ).waitFor() == 0 ) {
                        browser = browsers[count];
                    }
                }
                if ( browser == null ) {
                    throw new Exception( "Could not find web browser" );
                }
                else {
                    Runtime.getRuntime().exec( new String[]{browser, url} );
                }
            }
        }
        catch( Exception e ) {
            JOptionPane.showMessageDialog( null, errMsg + ":\n" + e.getLocalizedMessage() );
        }
    }

}


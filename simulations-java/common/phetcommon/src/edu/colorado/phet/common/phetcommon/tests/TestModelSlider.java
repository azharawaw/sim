/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14677 $
 * Date modified : $Date:2007-04-17 03:40:29 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.phetcommon.tests;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 8:32:49 PM
 *
 */

public class TestModelSlider {
    public static void main( String[] args ) {
        final ModelSlider ms = new ModelSlider( "Model Slider", "Newtons", 2, 4, 3 );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( ms );
        frame.pack();
        frame.setVisible( true );
        ms.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = ms.getValue();
                System.out.println( "value = " + value );
            }
        } );
    }
}
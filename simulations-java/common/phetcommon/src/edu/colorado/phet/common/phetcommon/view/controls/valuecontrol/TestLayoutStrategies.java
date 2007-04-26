/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Test harness for the ILayoutStrategy hierarchy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestLayoutStrategies extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 500, 500 );
    
    public TestLayoutStrategies() {
        
        // Default layout
        final LinearValueControl protonsControl;
        {
            int value = 79;
            int min = 20;
            int max = 100;
            String valuePattern = "0";
            String label = "Number of protons";
            String units = "";
            int columns = 3;
            protonsControl = new LinearValueControl( min, max, label, valuePattern, units );
            protonsControl.setValue( value );
            protonsControl.setUpDownArrowDelta( 1 );
            protonsControl.setTextFieldEditable( true );
            protonsControl.setTextFieldColumns( columns );
            protonsControl.setMinorTickSpacing( 10 );
            protonsControl.setMinorTickLabelsVisible( false );
            protonsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "protonsControl.stateChanged " + protonsControl.getValue() );
                }
            } );
        }
        
        // Default layout, but centered
        final LinearValueControl speedControl;
        {
            double min = 0;
            double max = 1000;
            double value = 0;
            String label = "Speed:";
            String valuePattern = "######0";
            String units = "mph";
            speedControl = new LinearValueControl( min, max, label, valuePattern, units, new DefaultLayoutStrategy( SwingConstants.CENTER ) );
            speedControl.setValue( value );
            speedControl.setMajorTickSpacing( 500 );
            speedControl.setMinorTickSpacing( 100 );
            speedControl.setMinorTickLabelsVisible( false );
            speedControl.setUpDownArrowDelta( 1 );
            speedControl.setTextFieldEditable( true );
            speedControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "speedControl.stateChanged " + speedControl.getValue() );
                }
            } );
        }
        
        // ModelSlider layout
        final LinearValueControl directionControl;
        {
            double min = 0;
            double max = 360;
            double value = 0;
            String label = "Direction";
            String valuePattern = "######0";
            String units = "degrees";
            directionControl = new LinearValueControl( min, max, label, valuePattern, units, new ModelSliderLayoutStrategy() );
            directionControl.setValue( value );
            directionControl.setMajorTickSpacing( 90 );
            directionControl.setMinorTickLabelsVisible( false );
            directionControl.setUpDownArrowDelta( 1 );
            directionControl.setTextFieldEditable( true );
            directionControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    System.out.println( "directionControl.stateChanged " + speedControl.getValue() );
                }
            } );
        }
        
        
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout( panel, BoxLayout.Y_AXIS );
        panel.setLayout( layout );
        panel.add( protonsControl );
        panel.add( speedControl );
        panel.add( directionControl );
        
        setContentPane( panel );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestLayoutStrategies test = new TestLayoutStrategies();
        test.show();
    }
}

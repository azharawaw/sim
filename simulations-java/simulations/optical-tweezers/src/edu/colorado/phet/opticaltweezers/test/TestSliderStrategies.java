/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import edu.colorado.phet.opticaltweezers.control.slider.AbstractSliderStrategy;
import edu.colorado.phet.opticaltweezers.control.slider.ISliderStrategy;
import edu.colorado.phet.opticaltweezers.control.slider.LinearSliderStrategy;
import edu.colorado.phet.opticaltweezers.control.slider.LogarithmicSliderStrategy;

/**
 * TestSliderStrategies is the test harness for the ISliderStrategy hierarchy.
 * <p>
 * These tests are difficult to automate because there will be some error due
 * to integer-double conversions. And the amount of error that's acceptable 
 * is based on the resolution of the slider and the range of the model values.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSliderStrategies {

    /* not intended for instantiation */
    private TestSliderStrategies() {}
    
    /*
     * Test harness.
     */
    public static void main( String[] args ) {
        TestSliderStrategies.runTests();
    }
    
    /*
     * Tests a strategy for a specified number of equally-spaced intervals.
     */
    private static void testStrategy( AbstractSliderStrategy strategy, int numberOfTests ) {
        
        int sliderMin = strategy.getSliderMin();
        int sliderMax = strategy.getSliderMax();
        double modelMin = strategy.getModelMin();
        double modelMax = strategy.getModelMax();
        System.out.println( "test: " + strategy.toString() );
        
        int sliderStep = ( sliderMax - sliderMin ) / ( numberOfTests - 1 );
        double modelStep = ( modelMax - modelMin ) / ( numberOfTests - 1 );
        
        System.out.println( "LinearStrategy.sliderToModel tests..." );
        for ( int i = 0; i < numberOfTests; i++ ) {
            int sliderValue = sliderMin + ( i * sliderStep );
            double modelValue = modelMin + ( i * modelStep );
            testStrategy( strategy, sliderValue, modelValue );
        }
    }
    
    /*
     * Tests a strategy for specific slider and model value.
     */
    private static void testStrategy( ISliderStrategy strategy, int sliderValue, double modelValue ) {
        System.out.println( sliderValue + " -> " + strategy.sliderToModel( sliderValue ) + " (" + modelValue + ")" );
        System.out.println( modelValue  + " -> " + strategy.modelToSlider( modelValue )  + " (" + sliderValue + ")" );
    }
    
    /*
     * Run a bunch of tests for each strategty type.
     */
    public static void runTests() {
        
        // Linear test
        {
            int numberOfTests = 10;
            System.out.println( "------------------------" );
            testStrategy( new LinearSliderStrategy( -1.0, 1.0, -100, 100 ), numberOfTests );
            System.out.println( "------------------------" );
            testStrategy( new LinearSliderStrategy( 1000.0, 2000.0, 0, 1000 ), numberOfTests );
            System.out.println( "------------------------" );
            testStrategy( new LinearSliderStrategy( 0, 1000, -1000, 0 ), numberOfTests );
        }
        
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            ISliderStrategy logarithmicStrategy = new LogarithmicSliderStrategy( 2E1, 2E5, -100, 100 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy, -100, 2E1 );
            testStrategy( logarithmicStrategy,  -50, 2E2 );
            testStrategy( logarithmicStrategy,    0, 2E3 );
            testStrategy( logarithmicStrategy,   50, 2E4 );
            testStrategy( logarithmicStrategy,  100, 2E5 );
        }
        
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            ISliderStrategy logarithmicStrategy = new LogarithmicSliderStrategy( 2E1, 2E5, 0, 1000 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy,    0, 2E1 );
            testStrategy( logarithmicStrategy,  250, 2E2 );
            testStrategy( logarithmicStrategy,  500, 2E3 );
            testStrategy( logarithmicStrategy,  750, 2E4 );
            testStrategy( logarithmicStrategy, 1000, 2E5 );
        }
        
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            ISliderStrategy logarithmicStrategy = new LogarithmicSliderStrategy( -2E5, -2E1, 0, 1000 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy,    0, -2E5 );
            testStrategy( logarithmicStrategy,  250, -2E4 );
            testStrategy( logarithmicStrategy,  500, -2E3 );
            testStrategy( logarithmicStrategy,  750, -2E2 );
            testStrategy( logarithmicStrategy, 1000, -2E1 );
        }
        
        //XXX This test case fails!
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            ISliderStrategy logarithmicStrategy = new LogarithmicSliderStrategy( .001, .01, 0, 1000 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy,    0, .001 );
            testStrategy( logarithmicStrategy,  500, .01 );
            testStrategy( logarithmicStrategy, 1000, .1 );
        }
    }
}

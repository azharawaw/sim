/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.phetcommon.view.ClockTimePanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * GlaciersClockControlPanel is a custom clock control panel.
 * It has a time display, speed control, and control buttons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClockControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlaciersClock _clock;
    
    private ClockControlPanel _clockControlPanel;
    private ClockTimePanel _timePanel;
    private LinearValueControl _speedControl;
    private JButton _restartButton;

    public class SliderOnlyLayoutStrategy implements ILayoutStrategy {

        public SliderOnlyLayoutStrategy() {}

        public void doLayout( AbstractValueControl valueControl ) {
            valueControl.add( valueControl.getSlider() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public GlaciersClockControlPanel( GlaciersClock clock ) {
        super();
        
        // Clock
        _clock = clock;
        
        // common clock controls
        _clockControlPanel = new ClockControlPanel( clock );
        
        // Restart button
        {
            String restartLabel = GlaciersResources.getCommonString( ClockControlPanelWithTimeDisplay.PROPERTY_RESTART );
            Icon restartIcon = new ImageIcon( GlaciersResources.getCommonImage( PhetCommonResources.IMAGE_REWIND ) );
            _restartButton = new JButton( restartLabel, restartIcon );
            _clockControlPanel.addControlToLeft( _restartButton );
        }

        // Time display
        {
            String units = clock.getUnits();
            DecimalFormat format = new DecimalFormat( "0" );
            int columns = 6;
            _timePanel = new ClockTimePanel( clock, units, format, columns );
        }
        
        // Speed control
        {
            double min = _clock.getDtRange().getMin();
            double max = _clock.getDtRange().getMax();
            String label = "";
            String textFieldPattern = "";
            String units = "";
            _speedControl = new LinearValueControl( min, max, label, textFieldPattern, units, new SliderOnlyLayoutStrategy() );
            _speedControl.setValue( _clock.getDt() );
            _speedControl.setMinorTicksVisible( false );
            
            // Label the min and max
            String normalString = GlaciersResources.getString( "label.clockSpeed.slow" );
            String fastString = GlaciersResources.getString( "label.clockSpeed.fast" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( normalString ) );
            labelTable.put( new Double( max ), new JLabel( fastString ) );
            _speedControl.setTickLabels( labelTable );
            Dictionary d = _speedControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setFont( new PhetDefaultFont( 10 ) );
            }
            
            // Slider width
            _speedControl.setSliderWidth( 125 );
        }
        
        // Layout
        {
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( new Insets( 0, 0, 0, 0 ) );
            layout.addComponent( _timePanel, 0, 0 );
            layout.addComponent( _speedControl, 0, 1 );
            layout.addComponent( _clockControlPanel, 0, 2 );

            FlowLayout flowLayout = new FlowLayout( FlowLayout.LEFT, 0, 0 );
            setLayout( flowLayout );
            add( innerPanel );
            System.out.println( "insets=" + getInsets().toString() );
        }
        
        // Interactivity
        _speedControl.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleDtChange();
            }
        } );
        _restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRestart();
            }
        } );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _clockControlPanel.cleanup();
        _clock = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the "Restart" component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getRestartComponent() {
        return _restartButton;
    }
    
    /**
     * Gets the clock index (speed) component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getClockIndexComponent() {
        return _speedControl;
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleDtChange() {
        _clock.setDt( _speedControl.getValue() );
    }
    
    private void handleRestart() {
        _clock.resetSimulationTime();
    }
}

/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSSquareWells;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSSquareDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSSquareWells _potential;
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    private SliderControl _spacingSlider;
    private JButton _closeButton;
    private EventListener _eventListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSquareDialog( Frame parent, BSSquareWells potential ) {
        super( parent );
        setModal( false );
        setResizable( false );
        setTitle( SimStrings.get( "BSSquareDialog.title" ) );
        
        _potential = potential;
        _potential.addObserver( this );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        createUI( parent );
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
            _potential = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Private initializers
    //----------------------------------------------------------------------------

    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        String positionUnits = SimStrings.get( "units.position" );
        String energyUnits = SimStrings.get( "units.energy" );
        
        // Width
        {
            double value = _potential.getWidth();
            double min = BSConstants.MIN_WELL_WIDTH;
            double max = BSConstants.MAX_WELL_WIDTH;
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String widthLabel = SimStrings.get( "label.wellWidth" );
            _widthSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, widthLabel, positionUnits, 4, SLIDER_INSETS );
            _widthSlider.setTextEditable( true );
        }
        
        // Depth
        {
            double value = _potential.getDepth();
            double min = BSConstants.MIN_WELL_DEPTH;
            double max = BSConstants.MAX_WELL_DEPTH;
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String depthLabel = SimStrings.get( "label.wellDepth" );
            _depthSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, depthLabel, energyUnits, 4, SLIDER_INSETS );
            _depthSlider.setTextEditable( true );
        }

        // Offset
        {
            double value = _potential.getOffset();
            double min = BSConstants.MIN_WELL_OFFSET;
            double max = BSConstants.MAX_WELL_OFFSET;
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String offsetLabel = SimStrings.get( "label.wellOffset" );
            _offsetSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, offsetLabel, energyUnits, 4, SLIDER_INSETS );
            _offsetSlider.setTextEditable( true );
        }

        // Spacing
        {
            double value = _potential.getSpacing();
            double min = BSConstants.MIN_WELL_SPACING;
            double max = BSConstants.MAX_WELL_SPACING;
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String spacingLabel = SimStrings.get( "label.wellSpacing" );
            _spacingSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, spacingLabel, positionUnits, 4, SLIDER_INSETS );
            _spacingSlider.setTextEditable( true );
        }
        
        updateControls();
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _offsetSlider, row, col );
        row++;
        layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
        row++;
        layout.addComponent( _widthSlider, row, col );
        row++;
        layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
        row++;
        layout.addComponent( _depthSlider, row, col );
        row++;
        layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
        row++;
        layout.addComponent( _spacingSlider, row, col );
        row++;

        // Interction
        setEventHandlingEnabled( true );
        
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _eventListener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 1 ) );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update( Observable o, Object arg ) {
        updateControls();
    }
    
    private void updateControls() {

        // Sync values
        _widthSlider.setValue( _potential.getWidth() );
        _depthSlider.setValue( _potential.getDepth() );
        _offsetSlider.setValue( _potential.getOffset() );
        _spacingSlider.setValue( _potential.getSpacing() );
    
        // Visibility
        _spacingSlider.setEnabled( _potential.getNumberOfWells() > 1 );
    }
    
    private void setEventHandlingEnabled( boolean enabled ) {
        if ( enabled ) {
            _widthSlider.addChangeListener( _eventListener );
            _depthSlider.addChangeListener( _eventListener );
            _offsetSlider.addChangeListener( _eventListener );
            _spacingSlider.addChangeListener( _eventListener );
        }
        else {
            _widthSlider.removeChangeListener( _eventListener );
            _depthSlider.removeChangeListener( _eventListener );
            _offsetSlider.removeChangeListener( _eventListener );
            _spacingSlider.removeChangeListener( _eventListener );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Dispatches events to the appropriate handler method.
     */
    private class EventListener extends WindowAdapter implements ActionListener, ChangeListener {

        public void windowClosing( WindowEvent event ) {
            handleCloseAction();
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _closeButton ) {
                handleCloseAction();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _widthSlider ) {
                handleWidthChange();
            }
            else if ( event.getSource() == _depthSlider ) {
                handleDepthChange();
            }
            else if ( event.getSource() == _offsetSlider ) {
                handleOffsetChange();
            }
            else if ( event.getSource() == _spacingSlider ) {
                handleSpacingChange();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    private void handleCloseAction() {
        cleanup();
        dispose();
    }
    
    private void handleWidthChange() {
        final double width = _widthSlider.getValue();
        _potential.setWidth( width );
    }
    
    private void handleDepthChange() {
        final double depth = _depthSlider.getValue();
        _potential.setDepth( depth );
    }
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        _potential.setOffset( offset );
    }
    
    private void handleSpacingChange() {
        final double spacing = _spacingSlider.getValue();
        _potential.setSpacing( spacing );
    }

}

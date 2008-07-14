/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.beaker;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.phscale.PHScaleApplication;
import edu.colorado.phet.phscale.beaker.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.CustomLiquidDescriptor;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorAdapter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * LiquidControlNode contains the combo box and on/off faucet for controlling
 * the type and amount of liquid in the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean CONFIRM_CHANGE_LIQUID_ENABLED = false; // feature disabled by request
    private static final PDimension LIQUID_COLUMN_SIZE = new PDimension( 20, 490 );
    private static final LiquidDescriptor WATER = LiquidDescriptor.getWater();
    private static final CustomLiquidDescriptor CUSTOM_LIQUID = LiquidDescriptor.getCustom();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final LiquidComboBox _comboBox;
    private final PPath _waterColumnNode; // put water behind liquid so that it looks the same as in beaker
    private final PPath _liquidColumnNode;
    private final FaucetControlNode _faucetControlNode;
    
    private LiquidDescriptor _selectedLiquidDescriptor;
    private boolean _confirmChangeLiquid;
    private boolean _notifyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LiquidControlNode( PSwingCanvas canvas, Liquid liquid ) {
        super();
        
        _notifyEnabled = true;
        
        _confirmChangeLiquid = CONFIRM_CHANGE_LIQUID_ENABLED;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        WATER.addLiquidDescriptorListener( new LiquidDescriptorAdapter() {
            public void colorChanged( Color color ) {
                _waterColumnNode.setPaint( WATER.getColor() );
            }
        } );
        
        _comboBox = new LiquidComboBox();
        _comboBox.setChoice( _liquid.getLiquidDescriptor() );
        _selectedLiquidDescriptor = _comboBox.getChoice();
        _comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED && _notifyEnabled ) {
                    handleLiquidSelection( _confirmChangeLiquid );
                }
            }
        } );
        PSwing comboBoxWrapper = new PSwing( _comboBox );
        comboBoxWrapper.addInputEventListener( new CursorHandler() );
        _comboBox.setEnvironment( comboBoxWrapper, canvas ); // hack required by PComboBox
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_RIGHT );
        _faucetControlNode.setOn( false );
        _faucetControlNode.setEnabled( _comboBox.getChoice() != null ); // disabled until a liquid is chosen
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                handleFaucetOnOff( on );
            }
        });
        
        Shape liquidColumnShape = new Rectangle2D.Double( 0, 0, LIQUID_COLUMN_SIZE.getWidth(), LIQUID_COLUMN_SIZE.getHeight() );
        _liquidColumnNode = new PPath( liquidColumnShape );
        _liquidColumnNode.setStroke( null );
        _liquidColumnNode.setVisible( _faucetControlNode.isOn() );
        _liquidColumnNode.setPickable( false );
        _liquidColumnNode.setChildrenPickable( false );
        
        _waterColumnNode = new PPath( liquidColumnShape );
        _waterColumnNode.setPaint( WATER.getColor() );
        _waterColumnNode.setStroke( null );
        _waterColumnNode.setVisible( _faucetControlNode.isOn() );
        _waterColumnNode.setPickable( false );
        _waterColumnNode.setChildrenPickable( false );
        
        addChild( comboBoxWrapper );
        addChild( _waterColumnNode );
        addChild( _liquidColumnNode );
        addChild( _faucetControlNode );
        
        comboBoxWrapper.setOffset( 0, 0 );
        PBounds cb = comboBoxWrapper.getFullBoundsReference();
        _faucetControlNode.setOffset( cb.getX(), cb.getMaxY() + 5 );
        _liquidColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMaxX() - _liquidColumnNode.getFullBoundsReference().getWidth() - 8, _faucetControlNode.getFullBoundsReference().getMaxY() );
        _waterColumnNode.setOffset( _liquidColumnNode.getOffset() );
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setLiquidDescriptor( LiquidDescriptor liquidDescriptor ) {
        _comboBox.setChoice( liquidDescriptor );
        handleLiquidSelection( false /* confirm */ );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        _notifyEnabled = false;
        _selectedLiquidDescriptor = _liquid.getLiquidDescriptor();
        _faucetControlNode.setOn( _liquid.isFillingLiquid() );
        _liquidColumnNode.setPaint( _liquid.getLiquidDescriptor().getColor() );
        _liquidColumnNode.setVisible( _liquid.isFillingLiquid() );
        _waterColumnNode.setVisible( _liquid.isFillingLiquid() );
        _comboBox.setChoice( _liquid.getLiquidDescriptor() );
        _notifyEnabled = true;
    }
    
    private void handleLiquidSelection( boolean confirm ) {
        LiquidDescriptor liquidDescriptor = _comboBox.getChoice();
        if ( !liquidDescriptor.equals( _selectedLiquidDescriptor ) ) {
            
            if ( liquidDescriptor.equals( CUSTOM_LIQUID ) ) {
                CUSTOM_LIQUID.resetPH();
            }
            
            boolean confirmed = true;
            if ( !_liquid.isEmpty() && confirm ) {
                confirmed = confirmChangeLiquid();
            }
            
            if ( confirmed ) {
                _selectedLiquidDescriptor = liquidDescriptor;
                _liquid.setLiquidDescriptor( liquidDescriptor );
            }
            else {
                _comboBox.setChoice( _selectedLiquidDescriptor );
            }
        }
    }
    
    private void handleFaucetOnOff( boolean on ) {
        if ( on ) {
            _liquid.startFillingLiquid( Liquid.SLOW_FILL_RATE );
        }
        else {
            _liquid.stopFillingLiquid();
        }
    }
    
    /*
     * Opens a dialog to confirm whether the user wants to change the liquid.
     * As a side effect, the flag is set that determines whether this dialog is shown again.
     * 
     * @return true or false
     */
    private boolean confirmChangeLiquid() {
        Frame parent = PHScaleApplication.instance().getPhetFrame();
        ConfirmChangeLiquidDialog dialog = new ConfirmChangeLiquidDialog( parent );
        SwingUtils.centerDialogInParent( dialog );
        dialog.setVisible( true );
        boolean confirmed = dialog.isConfirmed();
        _confirmChangeLiquid = !dialog.getDontAskAgain();
        return confirmed;
    }
}

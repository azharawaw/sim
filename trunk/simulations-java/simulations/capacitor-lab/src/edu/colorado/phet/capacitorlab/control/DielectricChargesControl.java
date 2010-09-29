/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.util.GridPanel.Anchor;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.DielectricNode.DielectricChargeView;

/**
 * Controls related to the dielectric's charges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricChargesControl extends JPanel {
    
    private final JRadioButton hideAllRadioButton, showAllRadioButton, showExcessRadioButton;

    public DielectricChargesControl( final CapacitorNode capacitorNode ) {
        
        JLabel chargesLabel = new JLabel( CLStrings.LABEL_DIELECTRIC_CHARGES );
        
        hideAllRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_HIDE_ALL_CHARGES );
        hideAllRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                capacitorNode.setDielectricChargeView( DielectricChargeView.NONE );
            }
        } );
         
        showAllRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_SHOW_ALL_CHARGES );
        showAllRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                capacitorNode.setDielectricChargeView( DielectricChargeView.ALL );
            }
        } );
        
        showExcessRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_SHOW_EXCESS_CHARGES );
        showExcessRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                capacitorNode.setDielectricChargeView( DielectricChargeView.EXCESS );
            }
        } );
        
        ButtonGroup group = new ButtonGroup();
        group.add( hideAllRadioButton );
        group.add( showAllRadioButton );
        group.add( showExcessRadioButton );

        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( chargesLabel );
        innerPanel.add( hideAllRadioButton );
        innerPanel.add( showAllRadioButton );
        innerPanel.add( showExcessRadioButton );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        hideAllRadioButton.setSelected( capacitorNode.getDielectricChargeView() == DielectricChargeView.NONE );
        showAllRadioButton.setSelected( capacitorNode.getDielectricChargeView() == DielectricChargeView.ALL );
        showExcessRadioButton.setSelected( capacitorNode.getDielectricChargeView() == DielectricChargeView.EXCESS );
    }
}

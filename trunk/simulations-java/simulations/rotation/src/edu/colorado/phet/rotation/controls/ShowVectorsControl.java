package edu.colorado.phet.rotation.controls;

import edu.colorado.phet.rotation.RotationLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:10:46 PM
 *
 */

public class ShowVectorsControl extends JPanel {
    VectorViewModel vectorViewModel;

    public ShowVectorsControl( final VectorViewModel vectorViewModel ) {
        this.vectorViewModel = vectorViewModel;
        setLayout( new GridBagLayout() );

        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        final JCheckBox velocityCheckBox = new JCheckBox( "velocity", vectorViewModel.isVelocityVisible() );
        velocityCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vectorViewModel.setVelocityVisible( velocityCheckBox.isSelected() );
            }
        } );

        final JCheckBox accelerationCheckBox = new JCheckBox( "acceleration", vectorViewModel.isAccelerationVisible() );
        accelerationCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vectorViewModel.setAccelerationVisible( accelerationCheckBox.isSelected() );
            }
        } );

        vectorViewModel.addListener( new VectorViewModel.Listener() {
            public void visibilityChanged() {
                velocityCheckBox.setSelected( vectorViewModel.isVelocityVisible() );
                accelerationCheckBox.setSelected( vectorViewModel.isAccelerationVisible() );
            }
        } );

        JLabel showVectorLabel = new JLabel( "Show Vectors:" );
        showVectorLabel.setFont( RotationLookAndFeel.getControlPanelTitleFont() );
        velocityCheckBox.setFont( RotationLookAndFeel.getCheckBoxFont() );
        accelerationCheckBox.setFont( RotationLookAndFeel.getCheckBoxFont() );

        add( showVectorLabel, gridBagConstraints );
        add( velocityCheckBox, gridBagConstraints );
        add( accelerationCheckBox, gridBagConstraints );
    }
}

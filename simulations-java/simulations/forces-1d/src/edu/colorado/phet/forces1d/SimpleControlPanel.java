/*  */
package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

/**
 * User: Sam Reid
 * Date: Jan 11, 2005
 * Time: 8:15:29 PM
 */

public class SimpleControlPanel extends IForceControl {
    private FreeBodyDiagramSuite fbdSuite;
    private JCheckBox frictionCheckBox;
    private BarrierCheckBox barriers;
    private Force1DApplication simpleForceModule;

    public SimpleControlPanel( final Force1DApplication simpleForceModule ) {
        super( simpleForceModule );
        this.simpleForceModule = simpleForceModule;

//        AdvancedPanel advancedPanel = new AdvancedPanel( "More Controls", "Hide" );
        JButton moreControls = new JButton( SimStrings.get( "SimpleControlPanel.moreControls" ) );
        moreControls.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                simpleForceModule.setAdvancedControlPanel();
            }
        } );
        add( moreControls );
        frictionCheckBox = new JCheckBox( SimStrings.get( "SimpleControlPanel.friction" ), true );
        frictionCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                simpleForceModule.setFrictionEnabled( frictionCheckBox.isSelected() );
            }
        } );

        fbdSuite = new FreeBodyDiagramSuite( simpleForceModule );
        fbdSuite.setControlPanel( this );
        addControl( fbdSuite.getCheckBox() );
        addControl( fbdSuite.getFBDPanel() );

        if ( Toolkit.getDefaultToolkit().getScreenSize().width < 1200 ) {
            super.removeTitle();
        }


        addControl( frictionCheckBox );
        barriers = new BarrierCheckBox( simpleForceModule );
        addControl( barriers );
        super.setHelpPanelEnabled( true );
        simpleForceModule.getForceModel().getPlotDeviceModel().addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                setChangesEnabled( true );
            }

            public void playbackStarted() {
                setChangesEnabled( false );
            }

            public void playbackPaused() {
                setChangesEnabled( true );
            }

            public void playbackFinished() {
                setChangesEnabled( true );
            }
        } );
        ObjectSelectionPanel osp = new ObjectSelectionPanel( simpleForceModule.getImageElements(), this );
        addControl( osp );
    }

    private void setChangesEnabled( boolean enabled ) {
        barriers.setEnabled( enabled );
        frictionCheckBox.setEnabled( enabled );
    }

    public void updateGraphics() {
        fbdSuite.updateGraphics();
    }

    public void reset() {
        fbdSuite.reset();
    }

    public void setup( Force1dObject imageElement ) {
        simpleForceModule.setObject( imageElement );
    }

    public void handleUserInput() {
        fbdSuite.handleUserInput();
    }

    public FreeBodyDiagramSuite getFreeBodyDiagramSuite() {
        return fbdSuite;
    }
}

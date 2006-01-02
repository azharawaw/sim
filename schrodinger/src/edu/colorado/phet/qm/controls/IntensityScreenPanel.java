/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 9:17:04 AM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class IntensityScreenPanel extends VerticalLayoutPanel {
    private SchrodingerControlPanel schrodingerControlPanel;

    public IntensityScreenPanel( SchrodingerControlPanel schrodingerControlPanel ) {
        this.schrodingerControlPanel = schrodingerControlPanel;
        setBorder( BorderFactory.createTitledBorder( "Intensity Screen" ) );

        final IntensityManager intensityManager = schrodingerControlPanel.getModule().getIntensityDisplay();
        JPanel inflationPanel = new HorizontalLayoutPanel();
        final JSpinner probabilityInflation = new JSpinner( new SpinnerNumberModel( intensityManager.getProbabilityScaleFudgeFactor(), 0.0, 1000, 0.1 ) );
        probabilityInflation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)probabilityInflation.getValue() ).doubleValue();
                intensityManager.setProbabilityScaleFudgeFactor( val );
            }
        } );
        inflationPanel.add( new JLabel( "Probability Inflation" ) );
        inflationPanel.add( probabilityInflation );
        super.addFullWidth( inflationPanel );

        JPanel pan = new HorizontalLayoutPanel();
        pan.add( new JLabel( "Waveform Decrement" ) );
        final JSpinner waveformDec = new JSpinner( new SpinnerNumberModel( intensityManager.getNormDecrement(), 0, 1.0, 0.1 ) );
//        waveformDec.setBorder( BorderFactory.createTitledBorder( "Waveform Decrement" ) );
        waveformDec.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)waveformDec.getValue() ).doubleValue();
                intensityManager.setNormDecrement( val );
            }
        } );
        pan.add( waveformDec );
        super.addFullWidth( pan );

        JPanel p3 = new HorizontalLayoutPanel();
        p3.add( new JLabel( "Multiplier" ) );
        final JSpinner mult = new JSpinner( new SpinnerNumberModel( intensityManager.getMultiplier(), 0, 1000, 5 ) );
        mult.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                intensityManager.setMultiplier( ( (Number)mult.getValue() ).intValue() );
            }
        } );
        p3.add( mult );
        super.addFullWidth( p3 );

        JPanel p4 = new HorizontalLayoutPanel();
        p4.add( new JLabel( "Opacity" ) );
        final JSpinner transparency = new JSpinner( new SpinnerNumberModel( getDetectorSheetPNode().getOpacity(), 0, 255, 1 ) );
        transparency.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Number)transparency.getValue() ).intValue();
                getDetectorSheetPNode().setOpacity( val );
            }
        } );
        p4.add( transparency );
        addFullWidth( p4 );
    }

    private DetectorSheetPNode getDetectorSheetPNode() {
        return schrodingerControlPanel.getSchrodingerPanel().getDetectorSheetPNode();
    }
}

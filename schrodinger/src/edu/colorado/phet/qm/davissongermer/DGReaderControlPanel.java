/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:23:59 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class DGReaderControlPanel extends VerticalLayoutPanel {
    private DGModule dgModule;

    public DGReaderControlPanel( final DGModule dgModule ) {
        this.dgModule = dgModule;
        setBorder( BorderFactory.createTitledBorder( "Intensity Reader (testing only)" ) );
        JRadioButton edge = new JRadioButton( "Edge", dgModule.getPlotPanel().isIntensityReaderEdge() );
        JRadioButton circ = new JRadioButton( "Radial", dgModule.getPlotPanel().isIntensityReaderRadial() );
        add( edge );
        add( circ );
        edge.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.getPlotPanel().setEdgeIntensityReader();
            }
        } );
        circ.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.getPlotPanel().setRadialIntensityReader();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( edge );
        buttonGroup.add( circ );
    }
}

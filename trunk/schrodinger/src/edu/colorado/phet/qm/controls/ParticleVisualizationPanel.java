/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.GrayscaleColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.VisualColorMap3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 17, 2005
 * Time: 7:40:43 PM
 * Copyright (c) Dec 17, 2005 by Sam Reid
 */

public class ParticleVisualizationPanel extends VerticalLayoutPanel implements IVisualizationPanel {
    private QWIPanel QWIPanel;
    private VisButton phaseColorRadioButton;
    private ButtonGroup buttonGroup;
    private VisButton grayMag;
    private VisButton realGray;
    private VisButton complexGray;
    private VisButton[] v;
    private VisButton lastUserSelected;

    public ParticleVisualizationPanel( QWIPanel QWIPanel ) {
        this.QWIPanel = QWIPanel;

        setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "wave.function.display" ) ) );
        buttonGroup = new ButtonGroup();

        grayMag = createVisualizationButton( QWIStrings.getString( "magnitude" ), new MagnitudeColorMap(), new WaveValueAccessor.Magnitude(), true, buttonGroup );
        addFullWidth( grayMag );

        realGray = createVisualizationButton( QWIStrings.getString( "real.part" ), new GrayscaleColorMap.Real(), new WaveValueAccessor.Real(), false, buttonGroup );
        addFullWidth( realGray );

        complexGray = createVisualizationButton( QWIStrings.getString( "imaginary.part" ), new GrayscaleColorMap.Imaginary(), new WaveValueAccessor.Imag(), false, buttonGroup );
        addFullWidth( complexGray );

        phaseColorRadioButton = createVisualizationButton( QWIStrings.getString( "phase.color" ), new VisualColorMap3(), new WaveValueAccessor.Magnitude(), false, buttonGroup );
        addFullWidth( phaseColorRadioButton );
        v = new VisButton[]{grayMag, realGray, complexGray, phaseColorRadioButton};
    }

    private VisButton createVisualizationButton( String s, final ComplexColorMap colorMap, final WaveValueAccessor waveValueAccessor, boolean b, ButtonGroup buttonGroup ) {
        final VisButton radioButton = new VisButton( s, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIPanel.setVisualizationStyle( colorMap, waveValueAccessor );
            }
        } );
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                lastUserSelected = radioButton;
            }
        } );
        buttonGroup.add( radioButton );
        radioButton.setSelected( b );
        return radioButton;
    }

    public void setPhaseColorEnabled( boolean b ) {
        phaseColorRadioButton.setEnabled( b );
    }

    public Component getPanel() {
        return this;
    }

    public void applyChanges() {
        for( int i = 0; i < v.length; i++ ) {
            VisButton visButton = v[i];
            if( visButton.isSelected() ) {
                visButton.fireEvent();
            }
        }
    }

    public void setMagnitudeMode() {
        grayMag.setSelected( true );
        applyChanges();
    }

    public void setLastUserSelected() {
        if( lastUserSelected != null ) {
            lastUserSelected.setSelected( true );
            applyChanges();
        }
    }
}

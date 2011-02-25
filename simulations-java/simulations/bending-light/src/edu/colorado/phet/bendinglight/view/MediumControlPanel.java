// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.Medium;
import edu.colorado.phet.bendinglight.model.MediumState;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.view.BendingLightCanvas.labelFont;

/**
 * @author Sam Reid
 */
public class MediumControlPanel extends PNode {

    MediumState CUSTOM = new MediumState( "Custom", BendingLightModel.MYSTERY_B.index + 1.2 ) {
        public boolean isCustom() {
            return true;
        }
    };
    private final Property<Medium> medium;
    private final Property<Function1<Double, Color>> colorMappingFunction;
    private static final int MIN = 1;
    private static final double MAX = 1.6;

    public MediumControlPanel( final PhetPCanvas phetPCanvas, final Property<Medium> medium, final Property<Function1<Double, Color>> colorMappingFunction, final String name, boolean textFieldVisible ) {
        this.medium = medium;
        this.colorMappingFunction = colorMappingFunction;
        final MediumState initialMediumState = medium.getValue().getMediumState();
        final PNode topLabel = new PNode() {{
            final PText materialLabel = new PText( name ) {{
                setFont( new PhetFont( labelFont.getSize(), true ) );
            }};
            final Object[] mediumStates = new Object[] {
                    BendingLightModel.AIR,
                    BendingLightModel.WATER,
                    BendingLightModel.GLASS,
                    BendingLightModel.MYSTERY_A,
                    BendingLightModel.MYSTERY_B,
                    CUSTOM,
            };
            final PComboBox comboBox = new PComboBox( mediumStates ) {
                {
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            MediumState selected = (MediumState) getSelectedItem();
                            if ( !selected.isCustom() ) {
                                setMediumState( selected, medium, colorMappingFunction );
                            }
                        }
                    } );
                    updateComboBox();
                    medium.addObserver( new SimpleObserver() {
                        public void update() {
                            updateComboBox();
                        }
                    } );
                    setFont( labelFont );
                    setMediumState( initialMediumState, medium, colorMappingFunction );
                }

                private void updateComboBox() {
                    int selected = -1;
                    for ( int i = 0; i < mediumStates.length; i++ ) {
                        MediumState mediumState = (MediumState) mediumStates[i];
                        if ( mediumState.index == medium.getValue().getIndexOfRefraction() ) {
                            selected = i;
                        }
                    }
                    if ( selected != -1 ) {
                        setSelectedIndex( selected );
                    }
                    else {
                        setSelectedItem( CUSTOM );
                    }
                }
            };
            final PSwing comboBoxPSwing = new PSwing( comboBox ) {{
                comboBox.setEnvironment( this, phetPCanvas );
                setOffset( materialLabel.getFullBounds().getMaxX() + 10, materialLabel.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 + 1 );
            }};
            addChild( materialLabel );
            addChild( comboBoxPSwing );
        }};
        addChild( topLabel );

        //Many efforts were made to make this control work with LinearValueControl, including writing custom layouts.
        //However, for unknown reasons, some text was always clipped off, and we decided to proceed by doing the layout in Piccolo, which resolved the problem.
        final PNode slider = new PNode() {{
            final PNode topComponent = new PNode() {{
                final PText label = new PText( "Index of Refraction (n):" ) {{setFont( BendingLightCanvas.labelFont );}};
                addChild( label );
                addChild( new PSwing( new JTextField( new DecimalFormat( "0.00" ).format( medium.getValue().getIndexOfRefraction() ), 4 ) {{
                    setFont( BendingLightCanvas.labelFont );
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            double value = Double.parseDouble( getText() );
                            if ( value > MIN && value < MAX ) {
                                setIndexOfRefraction( value );
                            }
                        }
                    } );
                    medium.addObserver( new SimpleObserver() {
                        public void update() {
                            setText( new DecimalFormat( "0.00" ).format( medium.getValue().getIndexOfRefraction() ) );
                        }
                    } );
                }} ) {{
                    setOffset( label.getFullBounds().getMaxX() + 10, label.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }} );
            }};
            addChild( topComponent );

            addChild( new PSwing( new JSlider( 0, 10000 ) {{
                final Function.LinearFunction mapping = new Function.LinearFunction( getMinimum(), getMaximum(), MIN, MAX );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        if ( isFocusOwner() ) {//Only send events if caused by user, otherwise selecting "mystery b" causes buggy behavior
                            final double indexOfRefraction = mapping.evaluate( getValue() );
                            setIndexOfRefraction( indexOfRefraction );
                        }
                    }
                } );
                medium.addObserver( new SimpleObserver() {
                    public void update() {
                        setValue( (int) mapping.createInverse().evaluate( medium.getValue().getIndexOfRefraction() ) );
                    }
                } );
                setPaintTicks( true );
                setPaintLabels( true );
                setLabelTable( new Hashtable<Object, Object>() {{
                    put( (int) mapping.createInverse().evaluate( BendingLightModel.AIR.index ), new TickLabel( "Air" ) );
                    put( (int) mapping.createInverse().evaluate( BendingLightModel.WATER.index ), new TickLabel( "Water" ) );
                    put( (int) mapping.createInverse().evaluate( BendingLightModel.GLASS.index ), new TickLabel( "Glass" ) );
//            put( LRRModel.N_DIAMOND, new TickLabel( "Diamond" ) );//commented out while we determine how to handle overlapping labels
                }} );
                setPreferredSize( new Dimension( (int) topComponent.getFullBounds().getWidth(), getPreferredSize().height ) );
            }} ) {{
                setOffset( 0, topComponent.getFullBounds().getMaxY() );
            }} );
            setOffset( 0, topLabel.getFullBounds().getMaxY() + 10 );
        }};

        medium.addObserver( new SimpleObserver() {
            public void update() {
                slider.setVisible( !medium.getValue().isMystery() );
            }
        } );

        addChild( slider );

        final PText unknown = new PText( "n=?" ) {{
            setFont( labelFont );
            centerFullBoundsOnPoint( slider.getFullBounds().getCenterX(), slider.getFullBounds().getCenterY() );
            medium.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( medium.getValue().isMystery() );
                }
            } );
        }};
        addChild( unknown );
        topLabel.setOffset( getFullBounds().getCenterX() - topLabel.getFullBounds().getWidth() / 2, 0 );
    }

    private void setIndexOfRefraction( double indexOfRefraction ) {
        medium.setValue( new Medium( medium.getValue().getShape(), new MediumState( "Custom", indexOfRefraction ), colorMappingFunction.getValue().apply( indexOfRefraction ) ) );
    }

    private void setMediumState( MediumState mediumState, Property<Medium> medium, Property<Function1<Double, Color>> colorMappingFunction ) {
        medium.setValue( new Medium( medium.getValue().getShape(), mediumState, colorMappingFunction.getValue().apply( mediumState.index ) ) );
    }
}
/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Test harness for plate charge layout in Capacitor Lab simulation.
 * Charges are arranged in a grid.
 * Strategy used for grid size is determined by GridSizeStrategyFactory.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPlateChargeLayout extends JFrame {

    private static final Dimension CANVAS_SIZE = new Dimension( 1024, 768 );
    private static final IntegerRange NUMBER_OF_CHARGES_RANGE = new IntegerRange( 0, 625, 0 );
    private static final IntegerRange PLATE_WIDTH_RANGE = new IntegerRange( 5, 500, 200 );
    private static final IntegerRange PLATE_HEIGHT_RANGE = new IntegerRange( 5, 500, 200 );
    private static final double PLUS_MINUS_WIDTH = 7;
    private static final double PLUS_MINUS_HEIGHT = 1;
    
    //==============================================================================
    // Charge layout strategies
    //==============================================================================
    
    /**
     * Change the strategy here.
     */
    public static class GridSizeStrategyFactory {
        public static IGridSizeStrategy createStrategy() {
            return new CCKGridSizeStrategy();
        }
    }
    
    /**
     * Interface for all grid size strategies.
     */
    public interface IGridSizeStrategy {
        public Dimension getGridSize( int numberOfObjects, double width, double height );
    }
    
    /**
     * Strategy borrowed from CCK's CapacitorNode.
     * When the plate's aspect ration gets large, this strategy creates grid sizes 
     * where one of the dimensions is zero (eg, 8x0, 0x14).
     */
    public static class CCKGridSizeStrategy implements IGridSizeStrategy {
        
        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            double alpha = Math.sqrt( numberOfObjects / width / height );
            // casting here may result in some charges being thrown out, but that's OK
            int columns = (int)( width * alpha );
            int rows = (int)( height * alpha );
            return new Dimension( columns, rows );
        }
    }
    
    /**
     * Workaround for one of the known issues with CCKGridSizeStrategy.
     * Ensures that we don't have a grid size where exactly one of the dimensions is zero.
     * This introduces a new problem: If numberOfCharges is kept constant, a plate with smaller
     * area but larger aspect ratio will display more charges.
     * For example, if charges=7, a 5x200mm plate will display 7 charges,
     * while a 200x200mm plate will only display 4 charges.
     */
    public static class ModifiedCCKGridSizeStrategy extends CCKGridSizeStrategy {
        
        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            Dimension gridSize = super.getGridSize( numberOfObjects, width, height );
            if ( gridSize.width == 0 && gridSize.height != 0 ) {
                gridSize.setSize( 1, numberOfObjects );
            }
            else if ( gridSize.width != 0 && gridSize.height == 0 ) {
                gridSize.setSize( numberOfObjects, 1 );
            }
            return gridSize;
        }
    }
    
    //==============================================================================
    // Model
    //==============================================================================
    
    public interface ModelChangeListener extends EventListener {
        public void numberOfChargesChanged();
        public void plateSizeChanged();
    }
    
    public static class TestModel {
        
        private final EventListenerList listeners;
        private int numberOfCharges;
        private int plateWidth, plateHeight;
        
        public TestModel() {
            listeners = new EventListenerList();
            numberOfCharges = NUMBER_OF_CHARGES_RANGE.getDefault();
            plateWidth = PLATE_WIDTH_RANGE.getDefault();
            plateHeight = PLATE_HEIGHT_RANGE.getDefault();
        }
        
        public void setNumberOfCharges( int numberOfCharges ) {
            if ( numberOfCharges != this.numberOfCharges ) {
                this.numberOfCharges = numberOfCharges;
                fireNumberOfChargesChanged();
            }
        }
        
        public int getNumberOfCharges() {
            return numberOfCharges;
        }
        
        public void setPlateWidth( int plateWidth ) {
            if ( plateWidth != this.plateWidth ) {
                this.plateWidth = plateWidth;
                firePlateSizeChanged();
            }
        }
        
        public void setPlateHeight( int plateHeight ) {
            if ( plateHeight != this.plateHeight ) {
                this.plateHeight = plateHeight;
                firePlateSizeChanged();
            }
        }
        
        public int getPlateWidth() {
            return plateWidth;
        }
        
        public int getPlateHeight() {
            return plateHeight;
        }
        
        public void addModelChangeListener( ModelChangeListener listener ) {
            listeners.add( ModelChangeListener.class, listener );
        }
        
        public void removeModelChangeListener( ModelChangeListener listener ) {
            listeners.remove( ModelChangeListener.class, listener );
        }
        
        private void fireNumberOfChargesChanged() {
            for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
                listener.numberOfChargesChanged();
            }
        }
        
        private void firePlateSizeChanged() {
            for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
                listener.plateSizeChanged();
            }
        }
    }
    
    //==============================================================================
    // View
    //==============================================================================

    public static class TestCanvas extends PCanvas {
        
        private final TestModel model;
        private final PPath plateNode;
        private final PComposite parentChargesNode;
        private final HTMLNode debugNode;
        private final IGridSizeStrategy gridSizeStrategy;
        
        public TestCanvas( final TestModel model ) {
            setPreferredSize( CANVAS_SIZE );
            
            gridSizeStrategy = GridSizeStrategyFactory.createStrategy();
            
            // plate
            plateNode = new PPath();
            plateNode.setPaint( Color.LIGHT_GRAY );
            plateNode.setStroke( new BasicStroke( 1f ) );
            plateNode.setStrokePaint( Color.BLACK );

            // parent node for charges on the plate
            parentChargesNode = new PComposite();
            
            // debug output
            debugNode = new HTMLNode();
            debugNode.setFont( new PhetFont( 18 ) );
            
            // rendering order
            addChild( plateNode );
            addChild( parentChargesNode );
            addChild( debugNode );
            
            // layout
            plateNode.setOffset( ( PLATE_WIDTH_RANGE.getMax() / 2 ) + 100, ( PLATE_HEIGHT_RANGE.getMax() / 2 ) + 100 );
            parentChargesNode.setOffset( plateNode.getOffset() );
            debugNode.setOffset( plateNode.getXOffset() + ( PLATE_WIDTH_RANGE.getMax() / 2 ) + 50, plateNode.getYOffset() );
            
            // model change listener
            this.model = model;
            model.addModelChangeListener( new ModelChangeListener() {

                public void plateSizeChanged() {
                    update();
                }
                
                public void numberOfChargesChanged() {
                    update();
                }
            });
            
            update();
        }
        
        // convenience method for adding nodes to the canvas
        public void addChild( PNode child ) {
            getLayer().addChild( child );
        }
        
        private void update() {
            updatePlate();
            updateCharges();
        }
        
        /*
         * Updates the plate geometry to match the model.
         * Origin is at the geometric center.
         */
        private void updatePlate() {
            double width = model.getPlateWidth();
            double height = model.getPlateHeight();
            plateNode.setPathTo( new Rectangle2D.Double( -width / 2, -height / 2, width, height ) );
        }
        
        /*
         * Updates the charges to match the model.
         */
        private void updateCharges() {
            
            // get model values
            final int numberOfCharges = model.getNumberOfCharges();
            final double plateWidth = model.getPlateWidth(); // use double in grid computations!
            final double plateHeight = model.getPlateHeight(); // use double in grid computations!
            
            // clear the grid of existing charges
            parentChargesNode.removeAllChildren();
            
            // clear the debug output node
            debugNode.setHTML( "" );
            
            if ( numberOfCharges != 0 ) {
                // compute the grid dimensions
                Dimension gridSize = gridSizeStrategy.getGridSize( numberOfCharges, plateWidth, plateHeight );
                final int rows = gridSize.height;
                final int columns = gridSize.width;

                // populate the grid with charges
                double dx = plateWidth / columns;
                double dy = plateHeight / rows;
                double xOffset = dx / 2;
                double yOffset = dy / 2;
                for ( int row = 0; row < rows; row++ ) {
                    for ( int column = 0; column < columns; column++ ) {
                        // add a charge
                        PNode chargeNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, Color.RED );
                        parentChargesNode.addChild( chargeNode );

                        // position the charge in cell in the grid
                        double x = -( plateWidth / 2 ) + xOffset + ( column * dx );
                        double y = -( plateHeight / 2 ) + yOffset + ( row * dy );
                        chargeNode.setOffset( x, y );
                    }
                }
                
                // debug output
                debugNode.setHTML( "grid=" + rows + "x" + columns + 
                        "<br>computed charges=" + numberOfCharges + 
                        "<br>displayed charges=" + ( rows * columns ) );
            }
        }
    }
    
    //==============================================================================
    // Controls
    //==============================================================================

    public static class TestControlPanel extends GridPanel {
        
        public TestControlPanel( final TestModel model ) {
            setBorder( new LineBorder( Color.BLACK ) );
            
            // number of charges
            final IntegerValueControl numberOfChargesControl = new IntegerValueControl( "# charges:", NUMBER_OF_CHARGES_RANGE.getMin(), NUMBER_OF_CHARGES_RANGE.getMax(), model.getNumberOfCharges(), "" );
            numberOfChargesControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setNumberOfCharges( numberOfChargesControl.getValue() );
                }
            });
            
            // plate width
            final IntegerValueControl plateWidthControl = new IntegerValueControl( "plate width:", PLATE_WIDTH_RANGE.getMin(), PLATE_WIDTH_RANGE.getMax(), model.getPlateWidth(), "mm" );
            plateWidthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setPlateWidth( plateWidthControl.getValue() );
                }
            });
            
            // plate height
            final IntegerValueControl plateHeightControl = new IntegerValueControl( "plate height:", PLATE_HEIGHT_RANGE.getMin(), PLATE_HEIGHT_RANGE.getMax(), model.getPlateHeight(), "mm" );
            plateHeightControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setPlateHeight( plateHeightControl.getValue() );
                }
            });
            
            // layout
            int row = 0;
            int column = 0;
            setAnchor( Anchor.WEST );
            setInsets( new Insets( 5, 5, 5, 5 ) );
            add( numberOfChargesControl, row++, column );
            add( plateWidthControl, row++, column );
            add( plateHeightControl, row++, column );
            
            // model change listener
            model.addModelChangeListener( new ModelChangeListener() {

                public void numberOfChargesChanged() {
                    numberOfChargesControl.setValue( model.getNumberOfCharges() );
                }

                public void plateSizeChanged() {
                    plateWidthControl.setValue( model.getPlateWidth() );
                    plateHeightControl.setValue( model.getPlateHeight() );
                }
            } );
        }
        
        /*
         * A slider with integrated title, value display and units.
         * Layout => title: slider value units
         */
        public static class IntegerValueControl extends JPanel {
            
            private final JSlider slider;
            private final JLabel valueLabel;
            
            public IntegerValueControl( String title, int min, int max, int value, String units ) {
                // components
                JLabel titleLabel = new JLabel( title );
                slider = new JSlider( min, max, value );
                valueLabel = new JLabel( String.valueOf( slider.getValue() ) );
                JLabel unitsLabel = new JLabel( units );
                // layout
                add( titleLabel );
                add( slider );
                add( valueLabel );
                add( unitsLabel );
                // keep value display in sync with slider
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        valueLabel.setText( String.valueOf( slider.getValue() ) );
                    }
                } );
            }
            
            public int getValue() {
                return slider.getValue();
            }
            
            public void setValue( int value ) {
                slider.setValue( value );
            }
            
            public void addChangeListener( ChangeListener listener ) {
                // warning: ChangeEvent.getSource will return the JSlider, not the IntegerValueControl
                slider.addChangeListener( listener );
            }
            
            public void removeChangeListener( ChangeListener listener ) {
                slider.addChangeListener( listener );
            }
        }
    }
    
    //==============================================================================
    // Main frame
    //==============================================================================

    public TestPlateChargeLayout() {
        
        // MVC
        TestModel model = new TestModel();
        TestCanvas canvas = new TestCanvas( model );
        TestControlPanel controlPanel = new TestControlPanel( model );
        
        // layout like a simulation
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        setContentPane( panel );
        
        pack();
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestPlateChargeLayout();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

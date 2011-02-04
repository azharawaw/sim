// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.model.AtomCount;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of an equation as a pair of bar charts, for left and right side of equation.
 * An indicator between the charts (equals or not equals) indicates whether they are balanced.
 * <p>
 * This implementation is very brute force, just about everything is recreated each time
 * a coefficient is changed in the equations.  But we have a small number of coefficients,
 * and nothing else is happening in the sim.  So we're trading efficiency for simplcity of
 * implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarChartsNode extends PComposite {

    private final HorizontalAligner aligner;
    private final EqualsSignNode equalsSignNode;
    private final NotEqualsSignNode notEqualsSignNode;
    private final PNode reactantsChartParent, productsChartParent;
    private final SimpleObserver coefficientsObserver;

    private Equation equation;

    public BarChartsNode( final Property<Equation> equationProperty, HorizontalAligner aligner ) {

        this.aligner = aligner;

        reactantsChartParent = new PComposite();
        addChild( reactantsChartParent );

        productsChartParent = new PComposite();
        addChild( productsChartParent );

        equalsSignNode = new EqualsSignNode();
        addChild( equalsSignNode );

        notEqualsSignNode = new NotEqualsSignNode();
        addChild( notEqualsSignNode );

        coefficientsObserver = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };

        this.equation = equationProperty.getValue();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                BarChartsNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                BarChartsNode.this.equation = equationProperty.getValue();
                BarChartsNode.this.equation.addCoefficientsObserver( coefficientsObserver );
            }
        } );
    }

    /*
     * Updates this node's entire geometry and layout
     */
    private void updateNode() {
        updateChart( reactantsChartParent, equation.getReactants(), true /* isReactants */ );
        updateChart( productsChartParent, equation.getProducts(), false /* isReactants */ );
        updateEqualitySign();
        updateLayout();
    }

    /*
     * Creates a bar chart under a parent node.
     */
    private void updateChart( PNode parent, EquationTerm[] terms, boolean isReactants ) {
        parent.removeAllChildren();
        double x = 0;
        ArrayList<AtomCount> atomCounts = equation.getAtomCounts();
        for ( AtomCount atomCount : atomCounts ) {
            int count = ( isReactants ? atomCount.getReactantsCount() : atomCount.getProductsCount() );
            BarNode barNode = new BarNode( atomCount.getAtom(), count );
            barNode.setOffset( x, 0 );
            parent.addChild( barNode );
            x = barNode.getFullBoundsReference().getMaxX() + 60;
        }
    }

    /*
     * Updates the equality sign.
     */
    private void updateEqualitySign() {
        // visibility
        equalsSignNode.setVisible( equation.isAllCoefficientsZero() || equation.isBalanced() );
        notEqualsSignNode.setVisible( !equalsSignNode.getVisible() );
        // color
        equalsSignNode.setPaint( equation.isBalanced() ? BCEColors.BALANCED_HIGHLIGHT_COLOR : BCEColors.UNBALANCED_COLOR );
    }

    /*
     * Updates the layout.
     */
    private void updateLayout() {

        final double xSpacing = 80;

        // equals sign at center
        double x = aligner.getCenterXOffset() - ( equalsSignNode.getFullBoundsReference().getWidth() / 2 );
        double y = -equalsSignNode.getFullBoundsReference().getHeight() / 2;
        equalsSignNode.setOffset( x, y );
        notEqualsSignNode.setOffset( x, y );

        // reactants chart to the left
        x = equalsSignNode.getFullBoundsReference().getMinX() - reactantsChartParent.getFullBoundsReference().getWidth() - PNodeLayoutUtils.getOriginXOffset( reactantsChartParent ) - xSpacing;
        y = 0;
        reactantsChartParent.setOffset( x, y );

        // products chart to the right
        x = equalsSignNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( productsChartParent ) + xSpacing;
        y = 0;
        productsChartParent.setOffset( x, y );
    }

    /*
     * Equals sign, drawn using Piccolo nodes so that we can put a stroke around it.
     * This will prevent it from disappearing on light-colored background when it "lights up" to indicate "balanaced".
     */
    private static class EqualsSignNode extends PComposite {

        private static final double BAR_WIDTH = 35;
        private static final double BAR_HEIGHT = 6;
        private static final double BAR_Y_SPACING = 6;

        private final PPath topBarNode, bottomBarNode;

        public EqualsSignNode() {

            Rectangle2D shape = new Rectangle2D.Double( 0, 0, BAR_WIDTH, BAR_HEIGHT );
            Stroke stroke = new BasicStroke( 1f );
            Color strokeColor = Color.BLACK;

            topBarNode = new PPath( shape );
            topBarNode.setStroke( stroke );
            topBarNode.setStrokePaint( strokeColor );
            addChild( topBarNode );

            bottomBarNode = new PPath( shape );
            bottomBarNode.setStroke( stroke );
            bottomBarNode.setStrokePaint( strokeColor );
            addChild( bottomBarNode );

            // layout
            topBarNode.setOffset( 0, 0 );
            bottomBarNode.setOffset( 0, BAR_HEIGHT + BAR_Y_SPACING );
        }

        public void setPaint( Paint paint ) {
            super.setPaint( paint );
            topBarNode.setPaint( paint );
            bottomBarNode.setPaint( paint );
        }
    }

    /*
     * Not-equals sign, drawn using constructive-area geometry so that we can put a stroke around it.
     */
    private static class NotEqualsSignNode extends PPath {
        public NotEqualsSignNode() {
            super();
            setStroke( new BasicStroke( 1f ) );
            setStrokePaint( Color.BLACK );
            setPaint( BCEColors.UNBALANCED_COLOR );

            Shape topBarShape = new Rectangle2D.Double( 0, 0, EqualsSignNode.BAR_WIDTH, EqualsSignNode.BAR_HEIGHT );
            Shape bottomBarShape = new Rectangle2D.Double( 0, EqualsSignNode.BAR_HEIGHT + EqualsSignNode.BAR_Y_SPACING, EqualsSignNode.BAR_WIDTH, EqualsSignNode.BAR_HEIGHT );

            Rectangle2D r = new Rectangle2D.Double( 0, EqualsSignNode.BAR_HEIGHT + ( ( EqualsSignNode.BAR_Y_SPACING - EqualsSignNode.BAR_HEIGHT ) / 2 ), EqualsSignNode.BAR_WIDTH, EqualsSignNode.BAR_HEIGHT );
            AffineTransform t2 = AffineTransform.getRotateInstance( Math.toRadians( -75 ), EqualsSignNode.BAR_WIDTH / 2, EqualsSignNode.BAR_HEIGHT + ( EqualsSignNode.BAR_Y_SPACING / 2 ) );
            Shape slashShape = t2.createTransformedShape( r );

            Area area = new Area( topBarShape );
            area.add( new Area( slashShape ) );
            area.add( new Area( bottomBarShape ) );
            setPathTo( area );
        }
    }
}

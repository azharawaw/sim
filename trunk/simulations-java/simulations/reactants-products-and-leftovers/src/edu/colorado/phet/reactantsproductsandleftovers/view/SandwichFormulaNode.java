package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.IntegerSpinnerNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product.ProductChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * Node that displays the formula for a sandwich, with editable coefficients.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichFormulaNode extends PhetPNode {
    
    private static final PhetFont TITLE_FONT = new PhetFont( 28 );
    private static final PhetFont COEFFICIENT_FONT = new PhetFont( 24 );
    private static final PhetFont NO_REACTION_FONT = new PhetFont( 24 );
    
    private static final int IMAGE_X_SPACING = 6;
    private static final int TERM_X_SPACING = 20;
    private static final int Y_SPACING = 30;
    
    private static final double IMAGE_SCALE = 0.5; //XXX
    
    private final SandwichShopModel model;
    private final PText titleNode;
    private final PNode arrowNode;
    private final ArrayList<PNode> lhsCoefficientNodes, lhsImageNodes, lhsPlusNodes, rhsCoefficientNodes, rhsImageNodes, rhsPlusNodes;
    private final PText noReactionNode;
    
    public SandwichFormulaNode( final SandwichShopModel model ) {
        super();
        
        this.model = model;
        
        // title
        titleNode = new PText( model.getReaction().getName() );
        titleNode.setFont( TITLE_FONT );
        addChild( titleNode );
        
        // left side (reactants)
        lhsCoefficientNodes = new ArrayList<PNode>();
        lhsImageNodes = new ArrayList<PNode>();
        lhsPlusNodes = new ArrayList<PNode>();
        ArrayList<Reactant> reactants = model.getReaction().getReactantsReference();
        for ( int i = 0; i < reactants.size(); i++ ) {
            
            final Reactant reactant = reactants.get( i );
            
            // coefficient spinner
            final IntegerSpinnerNode spinnerNode = new IntegerSpinnerNode( model.getCoefficientRange() );
            spinnerNode.scale( 2 ); //XXX
            spinnerNode.setValue( reactant.getCoefficient() );
            spinnerNode.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    reactant.setCoefficient( spinnerNode.getValue() );
                }
            });
            reactant.addReactantChangeListener( new ReactantChangeAdapter() {
                public void coefficientChanged() {
                    spinnerNode.setValue( reactant.getCoefficient() );
                }
            });
            addChild( spinnerNode );
            lhsCoefficientNodes.add( spinnerNode );
            
            // image
            PNode imageNode = new SubstanceNode( reactant );
            imageNode.scale( IMAGE_SCALE );
            addChild( imageNode );
            lhsImageNodes.add( imageNode );
            
            // plus sign
            if ( i < reactants.size() - 1 ) {
                PNode plusNode = new PlusNode();
                addChild( plusNode );
                lhsPlusNodes.add( plusNode );
            }
        }
        
        // arrow
        arrowNode = new RPALArrowNode();
        addChild( arrowNode );

        // right side (products)
        rhsCoefficientNodes = new ArrayList<PNode>();
        rhsImageNodes = new ArrayList<PNode>();
        rhsPlusNodes = new ArrayList<PNode>();
        ArrayList<Product> products = model.getReaction().getProductsReference();
        for ( int i = 0; i < products.size(); i++ ) {
            
            final Product product = products.get( i );
            
            // coefficient display
            final PText coefficientNode = new PText( String.valueOf( product.getCoefficient() ) );
            coefficientNode.setFont( COEFFICIENT_FONT );
            product.addProductChangeListener( new ProductChangeAdapter() {
                public void coefficientChanged() {
                    coefficientNode.setText( String.valueOf( product.getCoefficient() ) );
                }
            } );
            addChild( coefficientNode );
            rhsCoefficientNodes.add( coefficientNode );
            
            // image
            PNode imageNode = new SubstanceNode( product );
            imageNode.scale( IMAGE_SCALE );
            addChild( imageNode );
            rhsImageNodes.add( imageNode );
            
             // plus sign
            if ( i < products.size() - 1 ) {
                PNode plusNode = new PlusNode();
                addChild( plusNode );
                rhsPlusNodes.add( plusNode );
            }
        }
        
        noReactionNode = new PText( RPALStrings.LABEL_NO_REACTION );
        noReactionNode.setFont( NO_REACTION_FONT );
        addChild( noReactionNode );
        
        this.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
        
        model.getReaction().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
               updateVisibility();
            }
        });
     
        updateVisibility();
        updateLayout();
    }
    
    private void updateVisibility() {
        boolean isReaction = model.getReaction().isReaction();
        noReactionNode.setVisible( !isReaction );
        for ( PNode node : rhsCoefficientNodes ) {
            node.setVisible( isReaction );
        }
        for ( PNode node : rhsImageNodes ) {
            node.setVisible( isReaction );
        }
        for ( PNode node : rhsPlusNodes ) {
            node.setVisible( isReaction );
        }
        updateLayout();
    }

    private void updateLayout() {
        
        PNode previousNode = null;
        double spinnerYOffset = 0;
        
        // title
        double x = 0;
        double y = 0;
        titleNode.setOffset( x, y );
        
        // left side
        for ( int i = 0; i < lhsCoefficientNodes.size(); i++ ) {
            
            // coefficient
            PNode coefficientNode = lhsCoefficientNodes.get( i );
            if ( i == 0 ) {
                // below title
                x = titleNode.getXOffset();
                y = titleNode.getFullBoundsReference().getMaxY() + Y_SPACING - PNodeLayoutUtils.getOriginYOffset( coefficientNode );
                coefficientNode.setOffset( x, y );
                spinnerYOffset = y;
            }
            else {
                // to right of previous term
                x = previousNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
                coefficientNode.setOffset( x, spinnerYOffset );
            }
            
            // image
            PNode imageNode = lhsImageNodes.get( i );
            x = coefficientNode.getFullBoundsReference().getMaxX() + IMAGE_X_SPACING;
            y = coefficientNode.getFullBoundsReference().getCenterY() - ( imageNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( imageNode );
            imageNode.setOffset( x, y );
            
            // plus sign 
            if ( i < lhsCoefficientNodes.size() - 1 ) {
                PNode plusNode = lhsPlusNodes.get( i );
                x = imageNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
                y = imageNode.getFullBoundsReference().getCenterY() - ( plusNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plusNode );
                plusNode.setOffset( x, y );
                previousNode = plusNode;
            }
            else {
                previousNode = imageNode;
            }
        }
        
        // arrow
        x = previousNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = previousNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( arrowNode );
        arrowNode.setOffset( x, y );
        previousNode = arrowNode;
        
        // right side
        for ( int i = 0; i < rhsCoefficientNodes.size(); i++ ) {
            
            // coefficient
            PNode coefficientNode = rhsCoefficientNodes.get( i );
            x = previousNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
            y = previousNode.getFullBoundsReference().getCenterY() - ( coefficientNode.getFullBoundsReference().getHeight() / 2 ) - - PNodeLayoutUtils.getOriginYOffset( coefficientNode );
            coefficientNode.setOffset( x, y );
            
            // "no reaction" label
            if ( i == 0 ) {
                y = previousNode.getFullBoundsReference().getCenterY() - ( noReactionNode.getFullBoundsReference().getHeight() / 2 ) - - PNodeLayoutUtils.getOriginYOffset( noReactionNode );
                noReactionNode.setOffset( x, y );
            }
            
            // image
            PNode imageNode = rhsImageNodes.get( i );
            x = coefficientNode.getFullBoundsReference().getMaxX() + IMAGE_X_SPACING - PNodeLayoutUtils.getOriginXOffset( imageNode );
            y = coefficientNode.getFullBoundsReference().getCenterY() - ( imageNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( imageNode );
            imageNode.setOffset( x, y );
            
            // plus
            if ( i < rhsCoefficientNodes.size() - 1 ) {
                PNode plusNode = rhsPlusNodes.get( i );
                x = imageNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
                y = imageNode.getFullBoundsReference().getCenterY() - ( plusNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plusNode );
                plusNode.setOffset( x, y );
                previousNode = plusNode;
            }
            else {
                previousNode = imageNode;
            }
        }
    }
}

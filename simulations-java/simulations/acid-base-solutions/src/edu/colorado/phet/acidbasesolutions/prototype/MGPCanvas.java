/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.geom.Dimension2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;

/**
 * Piccolo canvas for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPCanvas extends PhetPCanvas {
    
    private final PNode rootNode;
    private final MagnifyingGlassNode magnifyingGlassNode;
    private final BeakerNode beakerNode;
    private final PNode reactionEquationNode;
    private final MoleculeCountsNode countsNode;

    public MGPCanvas( MGPModel model, boolean dev ) {
        super( MGPConstants.CANVAS_SIZE );
        setBackground( MGPConstants.CANVAS_COLOR );
        
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass(), model.getSolution(), dev );
        magnifyingGlassNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
        
        beakerNode = new BeakerNode( model.getBeaker(), model.getSolution(), magnifyingGlassNode, dev );
        beakerNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
        
        reactionEquationNode = new WeakAcidReactionEquationNode();
        reactionEquationNode.scale( 1.2 );
        
        countsNode = new MoleculeCountsNode( model.getSolution(), magnifyingGlassNode, dev );
        countsNode.setOffset( 20, 20 );
        countsNode.scale( 1.5 );
        
        // draggable only in dev version
        if ( dev ) {
            magnifyingGlassNode.addInputEventListener( new PDragEventHandler() );
            magnifyingGlassNode.addInputEventListener( new CursorHandler() );

            beakerNode.addInputEventListener( new PDragEventHandler() );
            beakerNode.addInputEventListener( new CursorHandler() );
        }
        
        // rendering order
        addChild( beakerNode );
        addChild( magnifyingGlassNode );
        addChild( reactionEquationNode );
        if ( dev ) {
            addChild( countsNode );
        }
    }
    
    public MagnifyingGlassNode getMagnifyingGlassNode() {
        return magnifyingGlassNode;
    }
    
    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
    
    @Override
    public void updateLayout() {
        super.updateLayout();
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            return;
        }
        double x = ( worldSize.getWidth() / 2 ) - ( reactionEquationNode.getFullBoundsReference().getWidth() / 2 );
        double y = worldSize.getHeight() - reactionEquationNode.getFullBoundsReference().getHeight() - 30;
        reactionEquationNode.setOffset( x, y );
        x = reactionEquationNode.getFullBoundsReference().getCenterX() - ( beakerNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( beakerNode );
        y = reactionEquationNode.getFullBoundsReference().getMinY() - beakerNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( beakerNode ) - 20;
        beakerNode.setOffset( x, y );
        magnifyingGlassNode.setOffset( x, y );
    }
}

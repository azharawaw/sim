/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.defaults.GlaciersDefaults;
import edu.colorado.phet.glaciers.view.BirdsEyeViewNode;
import edu.colorado.phet.glaciers.view.PenguinNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * GlaciersCanvas
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private GlaciersModel _model;
    
    // View 
    private PNode _rootNode;
    private BirdsEyeViewNode _birdsEyeViewNode;
    private PenguinNode _penguinNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersCanvas( GlaciersModel model ) {
        super( GlaciersDefaults.WORLD_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addScreenChild( _rootNode );
        
        _birdsEyeViewNode = new BirdsEyeViewNode();
        _rootNode.addChild( _birdsEyeViewNode );
        _birdsEyeViewNode.setOffset( 0, 0 ); // upper left
        
        _penguinNode = new PenguinNode();
        _rootNode.addChild( _penguinNode );
        PBounds b = _birdsEyeViewNode.getFullBoundsReference();
        double x = b.getX() + ( b.getWidth() / 2 );
        double y = b.getY() + b.getHeight() - _penguinNode.getFullBoundsReference().getHeight();
        _penguinNode.setOffset( x, y );
    }
    

    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public BirdsEyeViewNode getBirdsEyeViewNode() {
        return _birdsEyeViewNode;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
    }
}

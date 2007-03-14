/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.event.BoundedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.RulerNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class OTRulerNode extends RulerNode implements Observer {
    
    private static final Dimension2D DEFAULT_WORLD_SIZE = new PDimension( 600, 600 );
    private static final double HEIGHT = 40;
    private static final int MAJOR_TICK_INTERVAL = 100; // nm
    private static final int MINOR_TICKS_BETWEEN_MAJORS = 1; // 
    private static final int FONT_SIZE = 12;
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private PPath _dragBoundsNode;
    private Dimension2D _worldSize;
    
    public OTRulerNode( Laser laser, ModelViewTransform modelWorldTransform, PPath dragBoundsNode ) {
        super( DEFAULT_WORLD_SIZE.getWidth(), HEIGHT, null, SimStrings.get( "units.position" ), MINOR_TICKS_BETWEEN_MAJORS, FONT_SIZE );
        
        setUnits( "" );//XXX
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelWorldTransform;
        _dragBoundsNode = dragBoundsNode;
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
        
        _worldSize = new PDimension();
        setWorldSize( DEFAULT_WORLD_SIZE );
        updatePosition();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }

    public void setWorldSize( Dimension2D worldSize ) {
        
        _worldSize.setSize( worldSize );
        
        // Convert canvas width to model coordinates
        double modelCanvasWidth = _modelViewTransform.viewToModel( worldSize.getWidth() );
        
        // Calculate the number of ticks on the ruler
        int numMajorTicks = (int)( 3 * modelCanvasWidth / MAJOR_TICK_INTERVAL );
        if ( numMajorTicks % 2 == 0 ) {
            numMajorTicks++; // must be an odd number, with 0 at middle
        }
        
        // Distance between first and last tick
        double viewDistance = ( numMajorTicks - 1 ) * _modelViewTransform.modelToView( MAJOR_TICK_INTERVAL );
        setDistanceBetweenFirstAndLastTick( viewDistance );
        
        // Create the tick labels
        String[] majorTickLabels = new String[ numMajorTicks ];
        int majorTick = -MAJOR_TICK_INTERVAL * ( numMajorTicks - 1 ) / 2;
        for ( int i = 0; i < majorTickLabels.length; i++ ) {
            majorTickLabels[i] = String.valueOf( majorTick );
            majorTick += MAJOR_TICK_INTERVAL;
        }
        setMajorTickLabels( majorTickLabels );
        
        // Ruler may have changed size, so update position
        updatePosition();
    }

    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePosition();
            }
        }
    }
    
    private void updatePosition() {
        double xModel = _laser.getPositionRef().getX();
        double xView = _modelViewTransform.modelToView( xModel ) - ( getFullBounds().getWidth() / 2 );
        double yView = getOffset().getY();
        setOffset( xView, yView );
        updateDragBounds();
    }
    
    private void updateDragBounds() {
        Rectangle2D dragBounds = new Rectangle2D.Double( getFullBounds().getX(), 0, getFullBounds().getWidth(), _worldSize.getHeight() );
        _dragBoundsNode.setPathTo( dragBounds );
    }
}

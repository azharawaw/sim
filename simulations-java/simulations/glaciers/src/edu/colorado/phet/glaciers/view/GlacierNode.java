/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * GlacierNode is the visual representation of a glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierNode extends PComposite {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private ModelViewTransform _mvt;
    
    private GeneralPath _icePath;
    private PPath _icePathNode;
    private Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacierNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _glacier = glacier;
        _glacierListener = new GlacierListener() {

            public void lengthChanged() {
                //XXX
            }
            
            public void iceThicknessChanged() {
                updateIceThickness();
            }

            public void iceVelocityChanged() {
                //XXX
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _mvt = mvt;
        
        _icePath = new GeneralPath();
        _icePathNode = new PPath( _icePath );
        _icePathNode.setStroke( null );
        _icePathNode.setPaint( GlaciersConstants.ICE_COLOR );
        addChild( _icePathNode );
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // initialization
        updateIceThickness();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    private void updateIceThickness() {
        
        final double x0 = Glacier.getX0();
        final double xTerminus = _glacier.getTerminusX();
        final double dx = Glacier.getDx();
        Valley valley = _glacier.getValley();
        double elevation = 0;
        
        // reset the reusable path
        _icePath.reset();
        
        // move upvalley, draw ice-rock boundary
        for ( double x = xTerminus; x >= x0; x -= dx ) {
            elevation = valley.getElevation( x );
            _pModel.setLocation( x, elevation );
            _mvt.modelToView( _pModel, _pView );
            if ( x == xTerminus ) {
                _icePath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
            }
            else {
                _icePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
            }
        }
        
        // move downvalley, draw ice-air boundary
        double[] iceThicknessSamples = _glacier.getIceThicknessSamples();
        double thickness = 0;
        double iceSurface = 0;
        double x = x0;
        for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
            thickness = iceThicknessSamples[i];
            iceSurface = valley.getElevation( x ) + thickness;
            _pModel.setLocation( x, iceSurface );
            _mvt.modelToView( _pModel, _pView );
            _icePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
            x += dx;
        }
        
        // close the path
        _icePath.closePath();
        
        _icePathNode.setPathTo( _icePath );
    }
}

/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.capacitorlab.model.Box;
import edu.colorado.phet.capacitorlab.model.Box.BoxChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Pseudo-3D representation of a box, using parallelograms.
 * The top and side faces are foreshortened to give the illusion of distance between front and back planes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BoxNode extends PhetPNode {
    
    private static final double PARALLELOGRAM_ANGLE = Math.toRadians( 35 ); // radians
    private static final double FORESHORTENING_FACTOR = 0.5; // how much lines going away from the viewer should be shortened
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;

    private final Box box;
    private final TopNode topNode;
    private final FrontNode frontNode;
    private final SideNode sideNode;
    
    public BoxNode( Box box, Paint topPaint, Paint frontPaint, Paint sidePaint ) {
        
        this.box = box;
        box.addBoxChangeListener( new BoxChangeListener() {
            public void shapeChanged( double oldWidth, double oldHeight, double oldDepth, double newWidth, double newHeight, double newDepth ) {
                updateNode();
            }
        } );
        
        topNode = new TopNode( topPaint );
        frontNode = new FrontNode( frontPaint );
        sideNode = new SideNode( sidePaint );
        
        addChild( topNode );
        addChild( frontNode );
        addChild( sideNode );
        
        updateNode();
    }
    
    private void updateNode() {
        
        // geometry
        topNode.setWidthAndDepth( box.getWidth(), box.getDepth() );
        frontNode.setWidthAndHeight( box.getWidth(), box.getHeight() );
        sideNode.setDepthAndHeight( box.getDepth(), box.getHeight() );
        
        // layout
        double x = 0;
        double y = 0;
        frontNode.setOffset( x, y );
        x = frontNode.getFullBoundsReference().getWidth();
        y = frontNode.getYOffset();
        sideNode.setOffset( x, y );
        x = frontNode.getXOffset();
        y = frontNode.getYOffset();
        topNode.setOffset( x, y );
    }
    
    public void setTopPaint( Paint paint ) {
        topNode.setPaint( paint );
    }
    
    public void setFrontPaint( Paint paint ) {
        frontNode.setPaint( paint );
    }
    
    public void setSidePaint( Paint paint ) {
        sideNode.setPaint( paint );
    }
    
    private abstract static class FaceNode extends PPath {
        
        private final GeneralPath path;
        
        public FaceNode( Paint paint ) {
            path = new GeneralPath();
            setPaint( paint );
            setStroke( STROKE );
            setStrokePaint( STROKE_COLOR );
        }
        
        public GeneralPath getPath() {
            return path;
        }
    }
    
    private static class TopNode extends FaceNode {
        
        public TopNode( Paint paint ) {
            super( paint );
        }
        
        // parallelogram, origin at lower-left point
        public void setWidthAndDepth( double width, double depth ) {
            double xOffset = FORESHORTENING_FACTOR * depth * Math.cos( PARALLELOGRAM_ANGLE );
            double yOffset = FORESHORTENING_FACTOR * depth * Math.sin( PARALLELOGRAM_ANGLE );
            GeneralPath path = getPath();
            path.moveTo( 0, 0 );
            path.lineTo( xOffset, -yOffset );
            path.lineTo( width + xOffset, -yOffset );
            path.lineTo( width, 0 );
            path.closePath();
            setPathTo( path );
        }
    }
    
    private static class FrontNode extends FaceNode {
        
        public FrontNode( Paint paint ) {
            super( paint );
        }
        
        // rectangle, origin at upper-left point
        public void setWidthAndHeight( double width, double height ) {
            GeneralPath path = getPath();
            path.moveTo( 0, 0 );
            path.lineTo( width, 0 );
            path.lineTo( width, height );
            path.lineTo( 0, height );
            path.closePath();
            setPathTo( path );
        }
    }
    
    private static class SideNode extends FaceNode {
        
        public SideNode( Paint paint ) {
            super( paint );
        }
        
        // original at upper-left point
        public void setDepthAndHeight( double depth, double height ) {
            double xOffset = FORESHORTENING_FACTOR * depth * Math.cos( PARALLELOGRAM_ANGLE );
            double yOffset = FORESHORTENING_FACTOR * depth * Math.sin( PARALLELOGRAM_ANGLE );
            GeneralPath path = getPath();
            path.moveTo( 0, 0 );
            path.lineTo( xOffset, -yOffset );
            path.lineTo( xOffset, -yOffset + height );
            path.lineTo( 0, height );
            path.closePath();
            setPathTo( path );
        }
    }
}

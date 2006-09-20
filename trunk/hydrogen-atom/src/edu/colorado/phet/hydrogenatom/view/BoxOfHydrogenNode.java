/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;


public class BoxOfHydrogenNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Paint BOX_FRONT_PAINT = Color.GRAY;

    private static final Color TOP_COLOR_FRONT = Color.GRAY;
    private static final Color TOP_COLOR_BACK = Color.DARK_GRAY;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final float BACK_DEPTH = 10f;
    private static final float BACK_OFFSET = 0.15f;
    
    private static final Font LABEL_FONT = new Font( HAConstants.FONT_NAME, Font.PLAIN, 16 );
    private static final double Y_SPACING = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _tinyBoxNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoxOfHydrogenNode( Dimension boxSize, Dimension tinyBoxSize, Color tinyBoxColor ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );

        // Box, origin in upper-left corner of bounds
        PNode boxNode = new PNode();
        {
            final float w = (float)boxSize.width;
            GeneralPath topPath = new GeneralPath();
            topPath.moveTo( BACK_OFFSET * w, 0 );
            topPath.lineTo( ( 1 - BACK_OFFSET ) * w, 0 );
            topPath.lineTo( w, BACK_DEPTH );
            topPath.lineTo( 0, BACK_DEPTH ); 
            topPath.closePath();
            PPath topNode = new PPath();
            topNode.setPathTo( topPath );
            topNode.setPaint( new GradientPaint( 0f, 0f, TOP_COLOR_BACK, 0f, BACK_DEPTH, TOP_COLOR_FRONT ) );
            topNode.setStroke( STROKE );
            topNode.setStrokePaint( STROKE_COLOR );
            
            PPath frontNode = new PPath( new Rectangle2D.Double( 0, BACK_DEPTH, boxSize.width, boxSize.height ) );
            frontNode.setPaint( BOX_FRONT_PAINT );
            frontNode.setStroke( STROKE );
            frontNode.setStrokePaint( STROKE_COLOR );
            
            boxNode.addChild( frontNode );
            boxNode.addChild( topNode );
        }

        // Tiny box
        _tinyBoxNode = new AnimationRegionNode( tinyBoxSize, tinyBoxColor );
        
        // Label, origin in upper-left corner of bounds
        HTMLNode labelNode = new HTMLNode();
        labelNode.setHTML( SimStrings.get( "label.boxOfHydrogen" ) );
//        labelNode.setHTML( "<html>Really<br>really<br>tall<br>label on the box of hydrogen" );
        labelNode.setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
        labelNode.setFont( LABEL_FONT );
        
        // Layering
        addChild( boxNode );
        addChild( _tinyBoxNode );
        addChild( labelNode );
        OriginNode originNode = new OriginNode( Color.RED );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        // Label centered above box, orgin in upper-left corner of bounds
        final double labelWidth = labelNode.getFullBounds().getWidth();
        final double boxWidth = boxNode.getFullBounds().getWidth();
        if ( boxWidth > labelWidth ) {
            labelNode.setOffset( ( boxWidth - labelWidth ) / 2, 0 );
            boxNode.setOffset( 0, labelNode.getFullBounds().getHeight() + Y_SPACING );
        }
        else {
            labelNode.setOffset( 0, 0 );
            boxNode.setOffset( ( labelWidth - boxWidth ) / 2, labelNode.getFullBounds().getHeight() + Y_SPACING );
        }
        
        // Tiny box in upper right quadrant of box
        double x = boxNode.getFullBounds().getX() + ( 0.6 * boxNode.getFullBounds().getWidth() );
        double y = boxNode.getFullBounds().getY() + ( 0.3 * boxNode.getFullBounds().getHeight() );
        _tinyBoxNode.setOffset( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public PBounds getTinyBoxGlobalBounds() {
        PBounds fb = _tinyBoxNode.getFullBounds();
        Point2D gp = localToGlobal( fb.getOrigin() );
        Dimension2D gd = localToGlobal( fb.getSize() );
        PBounds gb = new PBounds( gp.getX(), gp.getY(), gd.getWidth(), gd.getHeight() );
        return gb;
    }
}

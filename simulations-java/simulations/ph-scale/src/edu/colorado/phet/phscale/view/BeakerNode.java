/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class BeakerNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 6f );

    private final GeneralPath _beakerPath;
    private final PPath _beakerNode;
    
    public BeakerNode( PDimension size ) {
        super();
        
        final float width = (float) size.getWidth();
        final float height = (float) size.getHeight();
        
        _beakerPath = new GeneralPath();
        _beakerPath.reset();
        _beakerPath.moveTo( 0f, 0f );
        _beakerPath.lineTo( 0f, height );
        _beakerPath.lineTo( width, height );
        _beakerPath.lineTo( width, 0f );
        
        _beakerNode = new PPath( _beakerPath );
        _beakerNode.setPaint( null );
        _beakerNode.setStroke( STROKE );
        addChild( _beakerNode );
    }
}

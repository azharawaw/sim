// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class ImageButtonNodeWithText extends PNode {
    public ImageButtonNodeWithText( final BufferedImage up, final BufferedImage hover, final BufferedImage pressed, final String text, final VoidFunction0 effect ) {
        final PImage imageNode = new PImage( up );
        addChild( imageNode );
        final PhetPText textNode = new PhetPText( text );
        textNode.scale( up.getWidth() / textNode.getFullWidth() * 0.65 );
        addChild( textNode );

        textNode.centerFullBoundsOnPoint( imageNode.getFullBounds().getCenter2D() );

        //account for shadow
        textNode.translate( -4 / textNode.getScale(), -4 / textNode.getScale() );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( final PInputEvent event ) {
                super.mouseEntered( event );
                imageNode.setImage( hover );
            }

            @Override public void mouseExited( final PInputEvent event ) {
                imageNode.setImage( up );
            }

            @Override public void mousePressed( final PInputEvent event ) {
                super.mousePressed( event );
                imageNode.setImage( pressed );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );
                effect.apply();
                imageNode.setImage( up );
            }
        } );
    }
}
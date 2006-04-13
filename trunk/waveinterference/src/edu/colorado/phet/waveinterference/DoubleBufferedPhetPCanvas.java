/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 12:28:18 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class DoubleBufferedPhetPCanvas extends PhetPCanvas {
    private BufferedImage buffer;
    private boolean pressed;

    public DoubleBufferedPhetPCanvas() {
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                pressed = true;
            }

            public void mouseReleased( MouseEvent e ) {
                pressed = false;
            }
        } );
    }

    public void paintComponent( Graphics g ) {
//        if( pressed ) {
//            paintNormal( g );
//        }
//        else {
        paintBuffered( g );
//        }
    }

    protected void paintNormal( Graphics g ) {
        super.paintComponent( g );
    }

    protected void superPaint( Graphics g ) {
        super.paintComponent( g );
    }

    protected void paintBuffered( final Graphics g ) {
        synchronizeImage();
        superPaint( buffer.createGraphics() );
        g.drawImage( buffer, 0, 0, null );
    }

    private void synchronizeImage() {
        if( buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight() ) {
            buffer = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        }
    }

}

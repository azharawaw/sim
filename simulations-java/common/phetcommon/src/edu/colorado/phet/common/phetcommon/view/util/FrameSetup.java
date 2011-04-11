// Copyright 2002-2011, University of Colorado

/*
 * Strategies for setting up the size and location of a frame
 * @author Sam Reid
 */
package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;

import javax.swing.*;

/**
 * FrameSetup
 *
 * @author ?
 * @version $Revision:14677 $
 */
public interface FrameSetup {
    public void initialize( JFrame frame );

    public static class CenteredWithInsets implements FrameSetup {
        private int insetX;
        private int insetY;

        public CenteredWithInsets( int insetX, int insetY ) {
            this.insetX = insetX;
            this.insetY = insetY;
        }

        public static void setup( Window w, int insetX, int insetY ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int width = d.width - insetX * 2;
            int height = d.height - insetY * 2;
            w.setSize( width, height );
            w.setLocation( insetX, insetY );
        }

        public void initialize( JFrame frame ) {
            setup( frame, insetX, insetY );
        }
    }

    public static class CenteredWithSize implements FrameSetup {
        private int width;
        private int height;

        public CenteredWithSize( int width, int height ) {
            this.width = width;
            this.height = height;
        }

        public void initialize( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - width ) / 2;
            int y = ( d.height - height ) / 2;
            frame.setLocation( x, y );
            frame.setSize( width, height );
        }
    }

    public static class Full {
        public Full() {
        }

        public void initialize( JFrame frame ) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation( 0, 0 );
            frame.setSize( d );
        }
    }

    public static class MaxExtent implements FrameSetup {
        FrameSetup pre = null;

        public MaxExtent() {
        }

        public MaxExtent( FrameSetup pre ) {
            this.pre = pre;
        }

        public void initialize( JFrame frame ) {
            if ( pre != null ) {
                pre.initialize( frame );
            }
            int state = frame.getExtendedState();
            // Set the maximized bits
            state |= Frame.MAXIMIZED_BOTH;
            // Maximize the frame
            frame.setExtendedState( state );
        }
    }

    /**
     * This FrameSetup centers the JFrame at the top of the screen, with the specified dimensions.
     */
    public static class TopCenter implements FrameSetup {
        private int width;
        private int height;

        public TopCenter( int width, int height ) {
            this.width = width;
            this.height = height;
        }

        /**
         * centers the JFrame at the top of the screen, with the specified dimensions.
         *
         * @param frame the frame to initialize.
         */
        // TODO: add test to see that the requested dimensions aren't bigger than the screen
        public void initialize( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - width ) / 2;
            int y = 0;
            frame.setLocation( x, y );
            frame.setSize( width, height );
        }
    }

    /**
     * This class does nothing to a JFrame, provided to support older applications until they are standardized.
     */
    public static class NoOp implements FrameSetup {
        public void initialize( JFrame frame ) {
        }
    }
}
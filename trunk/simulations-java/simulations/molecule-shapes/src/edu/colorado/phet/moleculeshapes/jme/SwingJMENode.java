// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import edu.colorado.phet.moleculeshapes.util.VoidNotifier;

import com.jme3.app.Application;
import com.jme3.scene.Node;

/**
 * Creates a JME node that displays a Swing component, keeps it up-to-date, and
 * can resize its underlying 3D representation when the component resizes
 */
public class SwingJMENode extends Node {
    private final JComponent component;
    private final Application app;

    private Dimension size = new Dimension(); // our current size
    private HUDNode hudNode; // the current node displaying our component

    public final VoidNotifier onResize = new VoidNotifier(); // notifier that fires when this node is resized

    public SwingJMENode( final JComponent component, final Application app ) {
        this.component = component;
        this.app = app;

        // ensure that we have it at its preferred size before sizing and painting
        component.setSize( component.getPreferredSize() );

        component.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)

        // by default, the components should usually be transparent
        component.setOpaque( false );

        // when our component resizes, we need to handle it!
        component.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                synchronized ( app ) {
                    onResize();
                }
            }
        } );
        onResize();
    }

    // flags the HUD node as needing a full repaint
    public void repaint() {
        if ( hudNode != null ) {
            hudNode.repaint();
        }
    }

    // if necessary, creates a new HUD node of a different size to display our component
    public void onResize() {
        // verify that it is actually a size change. don't do the extra work!
        if ( !component.getPreferredSize().equals( size ) ) {
            // get rid of the old HUD node
            if ( hudNode != null ) {
                detachChild( hudNode );
                hudNode.dispose();
            }

            // record our new size
            size = component.getPreferredSize();

            // construct our new HUD node
            hudNode = new HUDNode( component, size.width, size.height, app );
            attachChild( hudNode );

            // notify that we resized
            onResize.fire();
        }
    }

    public int getWidth() {
        return hudNode.getWidth();
    }

    public int getHeight() {
        return hudNode.getHeight();
    }

    public JComponent getComponent() {
        return component;
    }
}

package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:22:10 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphComponent extends PNode {
    private String label;
    private boolean minimized = false;
    private PNode graphChild;
    private PNode stubChild;

    private ArrayList listeners = new ArrayList();
    private boolean dummy = false;
    private ControlGraph controlGraph;
    private PSwing closeButton;
//    private boolean dummy = true;

    public GraphComponent( PSwingCanvas pSwingCanvas, String label ) {
        this.label = label;

        graphChild = new PNode();
        stubChild = new PNode();

        if( dummy ) {
            PPath pPath = new PhetPPath( new Rectangle( 0, 0, 100, 50 ), Color.white, new BasicStroke( 1 ), Color.blue );
            graphChild.addChild( pPath );

            PText text = new PText( label );
            graphChild.addChild( text );
        }
        else {
            controlGraph = new ControlGraph( pSwingCanvas, new SimulationVariable(), label );
            graphChild.addChild( controlGraph );
        }

        JButton minimizeButton = new JButton();
        try {
            minimizeButton.setIcon( new ImageIcon( ImageLoader.loadBufferedImage( "images/minimizeButton.png" ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        minimizeButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        minimizeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( true );
            }
        } );
        closeButton = new PSwing( pSwingCanvas, minimizeButton );

        graphChild.addChild( closeButton );

        addChild( graphChild );

        JButton maximizeButton = new JButton( "Maximize " + label + " graph" );
        maximizeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMinimized( false );
            }
        } );
        try {
            maximizeButton.setIcon( new ImageIcon( ImageLoader.loadBufferedImage( "images/maximizeButton.png" ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        PSwing maxButton = new PSwing( pSwingCanvas, maximizeButton );
        stubChild.addChild( maxButton );
        relayout();
    }

    private void updateCloseButton() {
        int buttonInsetX = 3;
        int buttonInsetY = 3;

        closeButton.setOffset( graphChild.getFullBounds().getMaxX() - closeButton.getFullBounds().getWidth() - buttonInsetX, buttonInsetY );
    }

    private void setMinimized( boolean b ) {
        if( this.minimized != b ) {
            this.minimized = b;
            if( minimized ) {
                removeChild( graphChild );
                addChild( stubChild );
            }
            else {
                removeChild( stubChild );
                addChild( graphChild );
            }
            notifyListeners();
        }
    }

    public String getLabel() {
        return label;
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setAvailableBounds( double width, double height ) {
        controlGraph.setBounds( 0, 0, width, height );
        relayout();
    }

    private void relayout() {
        updateCloseButton();
    }

    public static interface Listener {
        void minimizeStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.minimizeStateChanged();
        }
    }
}

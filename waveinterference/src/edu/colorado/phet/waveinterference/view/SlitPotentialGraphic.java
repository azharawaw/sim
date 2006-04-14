/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:11:44 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitPotentialGraphic extends PNode {
    private SlitPotential slitPotential;
    private LatticeScreenCoordinates latticeScreenCoordinates;

    //todo remove assumption that all bars are distinct.

    public SlitPotentialGraphic( final SlitPotential slitPotential, final LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.slitPotential = slitPotential;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        slitPotential.addListener( new SlitPotential.Listener() {
            public void slitsChanged() {
                update();
            }
        } );
        update();
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            private Point2D dragStartPt;
            int origLocation;

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                this.dragStartPt = event.getCanvasPosition();
                origLocation = slitPotential.getLocation();
//                System.out.println( "origLocation = " + origLocation );
            }

            protected void drag( PInputEvent event ) {
//                super.drag( event );
//                System.out.println( "SlitPotentialGraphic.drag" );
                Point2D pos = event.getCanvasPosition();
                double dx = pos.getX() - dragStartPt.getX();
//                System.out.println( "dx = " + dx );
                double latticeDX = latticeScreenCoordinates.toLatticeCoordinatesDifferentialX( dx );
//                System.out.println( "latticeDX = " + latticeDX );
                slitPotential.setLocation( (int)( origLocation + latticeDX ) );
            }
        } );
    }

    private void update() {
        removeAllChildren();
        Rectangle[]r = slitPotential.getBarrierRectangles();
        for( int i = 0; i < r.length; i++ ) {
            Rectangle rectangle = r[i];
            if( !rectangle.isEmpty() ) {
                Rectangle2D screenRect = latticeScreenCoordinates.toScreenRect( rectangle );

                PPath path = new PPath( screenRect );
                path.setPaint( new Color( 241, 216, 148 ) );
                path.setStroke( new BasicStroke( 2 ) );
                path.setStrokePaint( Color.black );
                addChild( path );
            }
        }
    }
}

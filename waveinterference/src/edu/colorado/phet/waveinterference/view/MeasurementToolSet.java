/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.MeasuringTape;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:24:31 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MeasurementToolSet extends PNode {
    private MeasuringTape measuringTape;
    private PNode stopwatchGraphic;
    private PSwingCanvas pSwingCanvas;

    public MeasurementToolSet( PSwingCanvas pSwingCanvas, IClock clock ) {
        this.pSwingCanvas = pSwingCanvas;
        measuringTape = new MeasuringTape( new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 100, 100 ), new Rectangle2D.Double( 0, 0, 100, 100 ) ), new Point2D.Double( 0, 0 ) );
        measuringTape.setVisible( false );
        measuringTape.setOffset( 100, 100 );
        addChild( measuringTape );

        stopwatchGraphic = new PhetPNode( new PSwing( pSwingCanvas, new StopwatchPanelDectorator( clock ) ) );
        stopwatchGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatchGraphic.addInputEventListener( new PDragEventHandler() );
        stopwatchGraphic.setVisible( false );
        addChild( stopwatchGraphic );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
    }

    public boolean isStopwatchVisible() {
        return stopwatchGraphic.getVisible();
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchGraphic.setVisible( selected );
    }
}

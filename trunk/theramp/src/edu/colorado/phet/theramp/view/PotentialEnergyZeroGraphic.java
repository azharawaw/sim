/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:59:25 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */

public class PotentialEnergyZeroGraphic extends PNode {
    private RampPhysicalModel rampPhysicalModel;
    private RampWorld rampWorld;
    private RampPanel rampPanel;
    private PPath phetShapeGraphic;
    private PText label;

    public PotentialEnergyZeroGraphic( RampPanel component, final RampPhysicalModel rampPhysicalModel, final RampWorld rampWorld ) {
        super();
        this.rampPanel = component;
        this.rampPhysicalModel = rampPhysicalModel;
        this.rampWorld = rampWorld;
//        phetShapeGraphic = new PhetShapeGraphic( component, new Line2D.Double( 0, 0, 1000, 0 ),new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{20, 20}, 0 ), Color.black );
        phetShapeGraphic = new PPath( new Line2D.Double( 0, 0, 1000, 0 ), new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{20, 20}, 0 ) );
        phetShapeGraphic.setPaint( Color.black );
        addChild( phetShapeGraphic );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                changeZeroPoint( event );
            }
        } );

        RampPhysicalModel.Listener listener = new RampPhysicalModel.Listener() {
            public void appliedForceChanged() {
            }

            public void zeroPointChanged() {
                setOffset( 0, rampWorld.getRampGraphic().getScreenTransform().modelToViewY( rampPhysicalModel.getZeroPointY() ) );
                updateLabel();
            }

            public void stepFinished() {
            }
        };
        rampPhysicalModel.addListener( listener );

        //setCursorHand();
        label = new PText( "h=???" );
        addChild( label );
        label.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
//        label.setLocation( 10, -label.getHeight() - 4 );
//        label.setLocation( 10, 0);//-label.getHeight() - 4 );
//        label.setLocation( 10, (int)( label.getHeight()*.075 ) );//-label.getHeight() - 4 );
        label.setOffset( 10, label.getHeight() );//-label.getHeight() - 4 );
//        listener.zeroPointChanged();
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        updateLabel();
        rampPhysicalModel.setZeroPointY( 0.0 );
    }

    private void updateLabel() {
        String str = new DecimalFormat( "0.0" ).format( rampPhysicalModel.getZeroPointY() );
        label.setText( "h=0.0 @ y=" + str );
//        label.setText( "y=0.0" );
    }

    private void changeZeroPoint( PInputEvent pEvent ) {
//        pEvent.getca
//        Point pt = rampWorld.convertToWorld( pEvent.getPosition() );
        Point2D pt = pEvent.getPositionRelativeTo( rampWorld );
        double zeroPointY = rampPanel.getRampGraphic().getScreenTransform().viewToModelY( pt.getY() );
        rampPhysicalModel.setZeroPointY( zeroPointY );
    }
}

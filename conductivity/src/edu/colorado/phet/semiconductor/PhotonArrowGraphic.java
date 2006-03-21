// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ImageGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

// Referenced classes of package edu.colorado.phet.semiconductor.photons:
//            Photon

public class PhotonArrowGraphic
        implements Graphic {

    public PhotonArrowGraphic( Photon photon1, ModelViewTransform2D modelviewtransform2d ) {
        photon = photon1;
        transform = modelviewtransform2d;
//        shapeGraphic = new ShapeGraphic( null, Color.red, Color.black, new BasicStroke( 1.0F, 2, 0 ) );
        try {
            shapeGraphic = new ImageGraphic( ImageLoader.loadBufferedImage( "images/photon-comet-42.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                doUpdate();
            }

        } );
        photon1.addObserver( new SimpleObserver() {

            public void update() {
                doUpdate();
            }

        } );
        doUpdate();
    }

    private void doUpdate() {
        PhetVector position = new PhetVector( photon.getPosition() );
        PhetVector velocity = photon.getVelocity();
        Point viewVelocity = transform.modelToViewDifferential( velocity.getX(), velocity.getY() );
        PhetVector viewVelocityVector = new PhetVector( viewVelocity );
        PhetVector positionViewVector = new PhetVector( transform.modelToView( position ) );
//        PhetVector src = positionViewVector.getSubtractedInstance( viewVelocityVector.getInstanceForMagnitude( 25D ) );
//        ArrowShape arrowShape = new ArrowShape( src, positionViewVector, 10D, 10D, 3D );
        AffineTransform at = new AffineTransform();
        at.translate( positionViewVector.getX(), positionViewVector.getY() );
        double fudgeAngle = -Math.PI / 32;
        double theta = viewVelocityVector.getAngle() - Math.PI / 2 + fudgeAngle;
//        System.out.println( "theta = " + theta );
        at.rotate( -theta );
        shapeGraphic.setTransform( at );
//        shapeGraphic.setShape( arrowShape.getArrowPath() );
    }

    public void paint( Graphics2D graphics2d ) {
        shapeGraphic.paint( graphics2d );
    }

    Photon photon;
    ModelViewTransform2D transform;
    ImageGraphic shapeGraphic;

}

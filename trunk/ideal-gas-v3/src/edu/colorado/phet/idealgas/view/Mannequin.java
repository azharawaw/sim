/**
 * Class: Mannequin
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 14, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.Animation;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.*;
import java.io.IOException;

public class Mannequin extends PhetGraphic implements SimpleObserver {
    private static float s_leaningManStateChangeScaleFactor = 1.75F;

    private Animation pusher;
    private Animation leaner;
    private Image currPusherFrame;
    private Image currLeanerFrame;
    private IdealGasModel model;
    private PressureSensingBox box;
    private Point location = new Point();
    private double lastPressure;
    private Image currFrame;

    public Mannequin( Component component, IdealGasModel model, PressureSensingBox box ) {
        super( component );
        this.model = model;
        this.box = box;
        try {
            pusher = new Animation( IdealGasConfig.PUSHER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_PUSHER_ANIMATION_FRAMES );
            leaner = new Animation( IdealGasConfig.LEANER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_LEANER_ANIMATION_FRAMES );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        currPusherFrame = pusher.getCurrFrame();
        currLeanerFrame = leaner.getCurrFrame();


        // todo: When we get everything in one thread, we can observe the box rather
        // than using a separate thread to update the mannequin
//        box.addObserver( this );
//        update();
        try {
            new Thread( new Updater()).start();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    protected Rectangle determineBounds() {
        return new Rectangle( location.x, location.y,
                              currPusherFrame.getWidth( null ),
                              currPusherFrame.getHeight( null ) );
    }

    public void paint( Graphics2D g ) {
        g.drawImage( currFrame, location.x, location.y, null );
    }

    public void update() {
        int offsetX = -2 * (int)Box2DGraphic.s_thickness;
        int offsetY = 3 * (int)Box2DGraphic.s_thickness;

        int nextLocationX = (int)box.getMinX() - currPusherFrame.getHeight( null ) + offsetX;
        boolean wallMoving = nextLocationX != location.x;
        if( wallMoving ) {
            int dir = nextLocationX - location.x;
            location.setLocation( nextLocationX, box.getMaxY() - currPusherFrame.getWidth( null ) + offsetY );
            // Update the pusher
            currPusherFrame = dir > 0 ? pusher.getNextFrame() : pusher.getPrevFrame();
            currFrame = currPusherFrame;
            setBoundsDirty();
            repaint();
        }
        else {
            double newPressure = box.getPressure();
            if( newPressure != lastPressure ) {
                int dir = newPressure == lastPressure ? 0 :
                          ( newPressure > lastPressure * s_leaningManStateChangeScaleFactor ? 1 : -1 );
                lastPressure = newPressure;
                // Update the leaner
                if( dir > 0 && leaner.getCurrFrameNum() + 1 < leaner.getNumFrames() ) {
                    currLeanerFrame = leaner.getNextFrame();
                }
                else if( dir < 0 && leaner.getCurrFrameNum() > 0 ) {
                    currLeanerFrame = leaner.getPrevFrame();
                }
                // todo: replace hard-coded number here
                int frameNum = (int)Math.min( ( newPressure / 120 ) * leaner.getNumFrames(), leaner.getNumFrames() - 1 );
                currLeanerFrame = leaner.getFrame( frameNum );
                if( model.isConstantVolume() ) {
                    currFrame = currLeanerFrame;
                    setBoundsDirty();
                    repaint();
                }
            }
        }
    }

    private class Updater implements Runnable {
        public void run() {
            while( true ) {
                try {
                    Thread.sleep( 20 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                update();
            }
        }
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.jmephet.JMECursorHandler;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.jmephet.hud.SwingJMENode;
import edu.colorado.phet.platetectonics.util.JMEModelViewTransform;

import com.jme3.renderer.queue.RenderQueue.Bucket;

/**
 * Displays a ruler in the 3D play area space
 */
public class RulerNode3D extends PiccoloJMENode {

    // how much we subsample the piccolo ruler in texture construction
    private static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    private static final float RULER_PIXEL_SCALE = 3f;

    public RulerNode3D( final JMEModelViewTransform transform, final JMEModule module ) {
        super( new RulerNode2D( transform.modelToViewDeltaX( 1000 ) ), module.getInputHandler(), module, SwingJMENode.getDefaultTransform() );

        // scale the node to handle the subsampling
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // allow antialiasing for a cleaner look
        antialiased.set( true );

        // allow parts to see through
        setQueueBucket( Bucket.Transparent );

        // don't forward mouse events!
        ignoreInput();

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public static class RulerNode2D extends RulerNode {

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public RulerNode2D( float kmToViewUnit ) {
            // TODO: i18n
            super( 100 * RulerNode3D.RULER_PIXEL_SCALE, 10 * RulerNode3D.RULER_PIXEL_SCALE, new String[] { "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" }, "km", 1, 9 );

            // make it vertical
            rotate( -Math.PI / 2 );

            // scale it so that we achieve adherence to the model scale
            scale( PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / RULER_PIXEL_SCALE );

            // don't show things below the "0" mark
            setInsetWidth( 0 );

            // give it the "Hand" cursor
            addInputEventListener( new JMECursorHandler() );
        }
    }
}

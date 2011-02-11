// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LightWaveNode extends PNode {
    int phase = 0;

    public LightWaveNode( final ModelViewTransform transform, final LightRay lightRay ) {
        addChild( new PhetPPath( new BasicStroke( 80, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ),
                                 createPaint( transform, lightRay ) ) {{
            lightRay.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( new Line2D.Double( lightRay.tip.getValue().toPoint2D(), lightRay.tail.getValue().toPoint2D() ) ) );
                }
            } );
            new Timer( 30, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    phase = phase + 2;
                    setStrokePaint( createPaint( transform, lightRay ) );
                }
            } ) {{
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        stop();
                    }
                } );
            }}.start();
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    private GradientPaint createPaint( ModelViewTransform transform, LightRay lightRay ) {
        double powerFraction = lightRay.getPowerFraction();
        double viewWavelength = transform.modelToViewDeltaX( lightRay.getWavelength() );
        final ImmutableVector2D vec = transform.modelToView( lightRay.toVector2D() ).getNormalizedInstance().getScaledInstance( viewWavelength );
        return new GradientPaint( phase, 0, new Color( 1f, 0, 0, (float) Math.sqrt( powerFraction ) ), phase + (float) vec.getX(), 0 + (float) vec.getY(), new Color( 0, 0, 0, (float) Math.sqrt( powerFraction ) ), true );
    }

    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new JLabel( new ImageIcon( new BufferedImage( 200, 100, BufferedImage.TYPE_INT_ARGB_PRE ) {{
                Graphics2D g2 = createGraphics();
                g2.setPaint( new GradientPaint( 0, 0, Color.red, 100, 0, Color.black, true ) );
                g2.fillRect( 0, 0, 200, 100 );
                g2.dispose();
            }} ) ) );
            pack();
        }}.setVisible( true );
    }

}

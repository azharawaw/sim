/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether the region is dirty.
 * Testing.
 */
public abstract class PhetGraphic implements BoundedGraphic {
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
//    private boolean visible = false;
    protected boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private GraphicsState graphicsState;

    protected PhetGraphic( Component component ) {
        this.component = component;
    }

    public Rectangle getBounds() {
        syncBounds();
        return bounds;
    }

    protected void saveGraphicsState( Graphics2D graphics2D ) {
        graphicsState = new GraphicsState( graphics2D );
    }

    protected void restoreGraphicsState() {
        graphicsState.restoreGraphics();
    }

    protected void pushRenderingHints( Graphics2D g ) {
        savedRenderingHints = g.getRenderingHints();
    }

    protected void popRenderingHints( Graphics2D g ) {
        if( savedRenderingHints != null ) {
            g.setRenderingHints( savedRenderingHints );
        }
    }

    protected void syncBounds() {
        if( boundsDirty ) {
            rebuildBounds();
            boundsDirty = false;
        }
    }

    protected void setBoundsDirty() {
        boundsDirty = true;
    }

    public Component getComponent() {
        return component;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        if( visible != this.visible ) {
            this.visible = visible;
            forceRepaint();//if we just turned invisible, we need to paint over ourselves, and vice versa.
        }
    }

    public boolean contains( int x, int y ) {
        if( visible ) {
            syncBounds();
            return bounds.contains( x, y );
        }
        else {
            return false;
        }
    }

    private void rebuildBounds() {
        Rectangle newBounds = determineBounds();
        if( newBounds != null ) {
            if( this.bounds == null ) {
                this.bounds = new Rectangle( newBounds );
            }
            else {
                this.bounds.setBounds( newBounds );
            }
            if( lastBounds == null ) {
                lastBounds = new Rectangle( bounds );
            }
        }
    }

    public void repaint() {
        if( visible ) {
            forceRepaint();
        }
    }

    private void forceRepaint() {
        syncBounds();
        if( lastBounds != null ) {
            component.repaint( lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height );
        }
        if( bounds != null ) {
            component.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
        }
        if( bounds != null ) {
            lastBounds.setBounds( bounds );
        }
    }

    protected abstract Rectangle determineBounds();


    //
    // Inner Classes
    //

    /**
     * A utilitye class for saving and restoring the state of Graphics2D objects
     */
    private class GraphicsState {
        private Graphics2D graphics2D;
        private RenderingHints renderingHints;
        private Paint paint;
        private Color color;
        private Stroke stroke;
        private Composite composite;
        private AffineTransform transform;
        private Font font;
        private Shape clip;
        private Color background;

        GraphicsState( Graphics2D graphics2D ) {
            this.graphics2D = graphics2D;
            renderingHints = graphics2D.getRenderingHints();
            paint = graphics2D.getPaint();
            color = graphics2D.getColor();
            stroke = graphics2D.getStroke();
            composite = graphics2D.getComposite();
            transform = graphics2D.getTransform();
            font = graphics2D.getFont();
            clip = graphics2D.getClip();
            background = graphics2D.getBackground();
        }

        void restoreGraphics() {
            graphics2D.setRenderingHints( renderingHints );
            graphics2D.setPaint( paint );
            graphics2D.setColor( color );
            graphics2D.setStroke( stroke );
            graphics2D.setComposite( composite);
            graphics2D.setTransform( transform );
            graphics2D.setFont( font );
            graphics2D.setClip( clip );
            graphics2D.setBackground( background );
        }
    }
}

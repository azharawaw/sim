/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockStateEvent;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p/>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn is undefined.
 * <p/>
 * The differences between this class and ApparatusPanel are:
 * <ul>
 * <li>The graphic objects in the panel setReferenceSize when the panel is resized
 * <li>Mouse events are handled in the model loop, not the Swing event dispatch thread
 * <li>An option allows drawing to be done to an offscreen buffer, then the whole buffer
 * written at one time to the graphics card
 * </ul>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ApparatusPanel2 extends ApparatusPanel {

    private static final boolean DEBUG_OUTPUT_ENABLED = false;

    private TransformManager transformManager;
    private PaintStrategy paintStrategy;

    private ArrayList rectangles = new ArrayList();
    private Rectangle repaintArea;

    private ScaledComponentLayout scaledComponentLayout;
    protected ModelElement paintModelElement;
    protected PanelResizeHandler panelResizeHandler;

    /**
     * This constructor adds a feature that allows PhetGraphics to get mouse events
     * when the model clock is paused.
     *
     * @param model
     * @param clock
     */
    public ApparatusPanel2( BaseModel model, AbstractClock clock ) {
        super( null );
        init( model, clock );
    }

    /**
     * @param model
     * @deprecated
     */
    public ApparatusPanel2( BaseModel model ) {
        super( null );
        init( model, null );
    }

    protected void init( BaseModel model, AbstractClock clock ) {
        // The following lines use a mouse processor in the model loop
        MouseProcessor mouseProcessor = new MouseProcessor( getGraphic(), clock );
        model.addModelElement( mouseProcessor );
        this.addMouseListener( mouseProcessor );
        this.addMouseMotionListener( mouseProcessor );
        this.addKeyListener( getGraphic().getKeyAdapter() );//TODO key events should go in processing thread as well.

        // Add a model element that paints the panile
        paintModelElement = new ModelElement() {
            public void stepInTime( double dt ) {
                paint();
            }
        };
        model.addModelElement( paintModelElement );

        // Add a listener what will adjust things if the size of the panel changes
        panelResizeHandler = new PanelResizeHandler();
        this.addComponentListener( panelResizeHandler );
        transformManager = new TransformManager( this );
        paintStrategy = new DefaultPaintStrategy( this );
        scaledComponentLayout = new ScaledComponentLayout( this );
    }

    /**
     * Paints the panel. Exactly how this is depends on if an offscreen buffer is being used,
     * or the union of dirty rectangles.
     */
    public void paint() {
        paintStrategy.paintImmediately();
        // Clear the rectangles so they get garbage collectged
        rectangles.clear();
    }

    /**
     * Sets the reference size for this panel. If the panel resizes after this, it will scale its graphicsTx using
     * its current size in relation to the reference size
     */
    public void setReferenceSize() {
        transformManager.setReferenceSize();
        scaledComponentLayout.saveSwingComponentCoordinates( 1.0 );
        setScale( 1.0 );
        paintImmediately( 0, 0, getWidth(), getHeight() );

        // Set the canvas size
        determineCanvasSize();

        if( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "ApparatusPanel2.setReferenceBounds: referenceBounds=" + transformManager.getReferenceBounds() );
        }
    }

    /**
     * Tells if we are using an offscreen buffer or dirty rectangles
     *
     * @return
     */
    public boolean isUseOffscreenBuffer() {
        return paintStrategy instanceof OffscreenBufferStrategy;
    }

    /**
     * Specifies whether the panel is to paint to an offscreen buffer, then paint the buffer,
     * or paint using dirty rectangles.
     *
     * @param useOffscreenBuffer
     */
    public void setUseOffscreenBuffer( boolean useOffscreenBuffer ) {
        this.paintStrategy = useOffscreenBuffer ? new OffscreenBufferStrategy( this ) : (PaintStrategy)new DefaultPaintStrategy( this );
        // Todo: Determine if the following two lines help or not
//        setOpaque( useOffscreenBuffer );
        setDoubleBuffered( !useOffscreenBuffer );
    }

    /**
     * Returns the AffineTransform used by the apparatus panel to size and place graphics
     *
     * @return
     */
    public AffineTransform getGraphicTx() {
        return transformManager.getGraphicTx();
    }

    private void saveLocation( Component comp ) {
        scaledComponentLayout.saveLocation( comp );
    }

    public Component add( Component comp ) {
        saveLocation( comp );
        return super.add( comp );
    }

    public void add( Component comp, Object constraints ) {
        saveLocation( comp );
        super.add( comp, constraints );
    }

    public Component add( Component comp, int index ) {
        saveLocation( comp );
        return super.add( comp, index );
    }

    public Component add( String name, Component comp ) {
        saveLocation( comp );
        return super.add( name, comp );
    }

    //-------------------------------------------------------------------------
    // Rendering
    //-------------------------------------------------------------------------

    public void paintImmediately() {
        paintDirtyRectanglesImmediately();
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param tm
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void repaint( long tm, int x, int y, int width, int height ) {
        repaint( x, y, width, height );
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param r
     */
    public void repaint( Rectangle r ) {
        repaint( r.x, r.y, r.width, r.height );
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void repaint( int x, int y, int width, int height ) {
        addRectangleToRepaintList( x, y, width, height );
    }

    /**
     * Overriden as a noop so that nothing happens if a child component calls repaint(). The actions
     * taken by our superclasss' repaint() should only happen in the model loop.
     */
    public void repaint() {
    }

    /**
     * Provided for backward compatibility
     *
     * @deprecated
     */
    public void megarepaintImmediately() {
        paintDirtyRectanglesImmediately();
    }

    /**
     * Paints immediately the union of dirty rectangles
     */
    private void paintDirtyRectanglesImmediately() {
        if( rectangles.size() > 0 ) {
            this.repaintArea = RectangleUtils.union( rectangles );
            paintImmediately( repaintArea );
        }
    }

    private void addRectangleToRepaintList( int x, int y, int width, int height ) {
        Rectangle r = new Rectangle( x, y, width, height );
        r = transformManager.transform( r );
        rectangles.add( r );
    }

    /**
     * Overriden as a noop so that nothing happens if a child component calls repaint(). The actions
     * taken by our superclasss' repaint( long tm) should only happen in the model loop.
     */
    public void repaint( long tm ) {
    }

    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        if( repaintArea == null ) {
            repaintArea = this.getBounds();
        }
        g2.setBackground( super.getBackground() );
//        g2.clearRect( 0, 0, this.getWidth(), this.getHeight() );
        Rectangle clipBounds = g2.getClipBounds();
        g2.clearRect( clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height );
//        g2.clearRect( repaintArea.x, repaintArea.y, repaintArea.width, repaintArea.height );
        for( int i = 0; i < getGraphicsSetups().size(); i++ ) {
            GraphicsSetup graphicsSetup = (GraphicsSetup)getGraphicsSetups().get( i );
            graphicsSetup.setup( g2 );
        }

        GraphicsState gs = new GraphicsState( g2 );
        g2.transform( transformManager.getGraphicTx() );
        paintStrategy.render( g2 );

        //remove the affine transform.
        gs.restoreGraphics();
        super.drawBorder( g2 );
        //restore color and stroke.
        gs.restoreGraphics();
    }

    /**
     * todo: is this used anywhere?
     *
     * @return
     */
    protected Rectangle getReferenceBounds() {
        return transformManager.getReferenceBounds();
    }

    /**
     * Gets the size of the drawing area (the canvas) that is available to clients.
     * This is the size of the apparatus panel, adjusted for scaling.
     * This method is intended for use by clients who need to know how big
     * an area is available for drawing.
     * <p/>
     * An example: The client is a "grid" that needs to cover all visible space in
     * the apparatus panel.  The apparatus panel's size is currently 500x250, and its
     * scaling is 0.5.  If the grid uses 500x250, it will only 25% of the
     * apparatus panel after scaling.  Using getCanvasSize adjusts for
     * scaling and returns 1000x500 (ie, 500/0.5 x 250/0.5).
     *
     * @return the size
     */
    public Dimension getCanvasSize() {
        return transformManager.getCanvasSize();
    }

    //-----------------------------------------------------------------
    // Resizing and scaling
    //-----------------------------------------------------------------

    private class PanelResizeHandler extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            if( !transformManager.isReferenceSizeSet() ) {
                setReferenceSize();
            }
            else {
                // Setup the affine transforms for graphics and mouse events
                Rectangle referenceBounds = transformManager.getReferenceBounds();
                double sx = getWidth() / referenceBounds.getWidth();
                double sy = getHeight() / referenceBounds.getHeight();
                // Using a single scale factor keeps the aspect ratio constant
                double s = Math.min( sx, sy );
                setScale( s );
                determineCanvasSize();
            }
            paintStrategy.componentResized();
        }
    }

    /**
     * Computes the size of the canvas on which PhetGraphics attached to this panel are drawn.
     * If the size changed, an canvasSizeChanged is called on all ChangeListeners
     */
    private void determineCanvasSize() {
        boolean changed = transformManager.determineCanvasSize();
        if( changed ) {
            changeListenerProxy.canvasSizeChanged( new ApparatusPanel2.ChangeEvent( ApparatusPanel2.this ) );
        }
    }

    public double getScale() {
        return transformManager.getScale();
    }

    public void setScale( double scale ) {
        transformManager.setScale( scale );
        scaledComponentLayout.layoutSwingComponents( scale );
    }



    //-------------------------------------------------------------------------
    // Inner classes
    //-------------------------------------------------------------------------

    /**
     * Handles mouse events in the model loop
     */
    private class MouseProcessor implements ModelElement, MouseListener, MouseMotionListener {
        private LinkedList mouseEventList;
        private LinkedList mouseMotionEventList;
        private AbstractClock clock;
        private GraphicLayerSet handler;
        private boolean modelPaused;

        // The following Runnable is used to process mouse events when the model clock is paused
        private Runnable pausedEventListProcessor = new Runnable() {
            public void run() {
                stepInTime( 0 );
                ApparatusPanel2.this.paintDirtyRectanglesImmediately();
            }
        };


        public MouseProcessor( GraphicLayerSet mouseDelegator, final AbstractClock clock ) {
            this.clock = clock;
            mouseEventList = new LinkedList();
            mouseMotionEventList = new LinkedList();
            this.handler = mouseDelegator;
            
            // Add a listener that keeps track of when the clock is paused and unpaused. this
            // is needed so the mouse will still work if the clock is paused.
            this.clock.addClockStateListener( new ClockStateListener() {
                public void stateChanged( ClockStateEvent event ) {
                    modelPaused = clock.isPaused();
                }
            } );
            this.modelPaused = clock.isPaused();
        }

        public void stepInTime( double dt ) {
            processMouseEventList();
            processMouseMotionEventList();
        }

        private void xformEventPt( MouseEvent event ) {
            Point2D.Double p = new Point2D.Double( event.getPoint().getX(), event.getPoint().getY() );
            AffineTransform mouseTx = transformManager.getMouseTx();
            mouseTx.transform( p, p );
            int dx = (int)( p.getX() - event.getPoint().getX() );
            int dy = (int)( p.getY() - event.getPoint().getY() );
            event.translatePoint( dx, dy );
        }

        private void addMouseEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseEventList ) {
                mouseEventList.add( event );
            }

            // If the clock is paused, then process mouse events
            // in the Swing thread
            if( modelPaused ) {
                SwingUtilities.invokeLater( pausedEventListProcessor );
            }
        }

        private void addMouseMotionEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseMotionEventList ) {
                mouseMotionEventList.add( event );
            }
            // If the clock is paused, then process mouse events
            // in the Swing thread
            if( modelPaused ) {
                SwingUtilities.invokeLater( pausedEventListProcessor );
            }
        }

        private void processMouseEventList() {
            MouseEvent event;
            while( mouseEventList.size() > 0 ) {
                synchronized( mouseEventList ) {
                    event = (MouseEvent)mouseEventList.removeFirst();
                }
                handleMouseEvent( event );
            }
        }

        public void processMouseMotionEventList() {
            MouseEvent event;
            while( mouseMotionEventList.size() > 0 ) {
                synchronized( mouseMotionEventList ) {
                    event = (MouseEvent)mouseMotionEventList.removeFirst();
                }
                handleMouseEvent( event );
            }
        }

        private void handleMouseEvent( MouseEvent event ) {
            switch( event.getID() ) {
                case MouseEvent.MOUSE_CLICKED:
                    handler.getMouseHandler().mouseClicked( event );
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    handler.getMouseHandler().mouseDragged( event );
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    handler.getMouseHandler().mouseEntered( event );
                    break;
                case MouseEvent.MOUSE_EXITED:
                    handler.getMouseHandler().mouseExited( event );
                    break;
                case MouseEvent.MOUSE_MOVED:
                    handler.getMouseHandler().mouseMoved( event );
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    handler.getMouseHandler().mousePressed( event );
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    handler.getMouseHandler().mouseReleased( event );
                    break;
            }
        }

        public void mouseClicked( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseEntered( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseExited( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mousePressed( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseReleased( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseDragged( MouseEvent e ) {
            this.addMouseMotionEvent( e );
        }

        public void mouseMoved( MouseEvent e ) {
            this.addMouseMotionEvent( e );
        }
    }

    //-----------------------------------------------------------------
    // Event-related classes
    //-----------------------------------------------------------------
    public class ChangeEvent extends EventObject {
        public ChangeEvent( ApparatusPanel2 source ) {
            super( source );
        }

        public Dimension getCanvasSize() {
            return transformManager.getCanvasSize();
        }
    }

    public interface ChangeListener extends EventListener {
        void canvasSizeChanged( ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    static interface PaintStrategy {

        void paintImmediately();

        void render( Graphics2D g2 );

        void componentResized();
    }

    static class OffscreenBufferStrategy implements PaintStrategy {
        private BufferedImage bImg;
        private ApparatusPanel2 apparatusPanel2;

        public OffscreenBufferStrategy( ApparatusPanel2 apparatusPanel2 ) {
            this.apparatusPanel2 = apparatusPanel2;
        }

        public void paintImmediately() {
            //TODO: even if we use an offscreen buffer, we could still just throw the changed part to the screen.
            Rectangle region = new Rectangle( 0, 0, apparatusPanel2.getWidth(), apparatusPanel2.getHeight() );
            apparatusPanel2.paintImmediately( region );
        }

        public void render( Graphics2D g2 ) {
            if( bImg != null ) {
                Graphics2D bImgGraphics = (Graphics2D)bImg.getGraphics();
                bImgGraphics.setColor( apparatusPanel2.getBackground() );
                bImgGraphics.fillRect( bImg.getMinX(), bImg.getMinY(), bImg.getWidth(), bImg.getHeight() );
                apparatusPanel2.getGraphic().paint( bImgGraphics );
                g2.drawImage( bImg, new AffineTransform(), null );
                bImgGraphics.dispose();
            }
        }

        public void componentResized() {
            bImg = new BufferedImage( apparatusPanel2.getWidth(), apparatusPanel2.getHeight(), BufferedImage.TYPE_INT_RGB );
        }
    }

    static class DefaultPaintStrategy implements PaintStrategy {
        ApparatusPanel2 apparatusPanel2;

        public DefaultPaintStrategy( ApparatusPanel2 apparatusPanel2 ) {
            this.apparatusPanel2 = apparatusPanel2;
        }

        public void paintImmediately() {
            apparatusPanel2.paintDirtyRectanglesImmediately();
        }

        public void render( Graphics2D g2 ) {
            apparatusPanel2.getGraphic().paint( g2 );
        }

        public void componentResized() {
        }
    }

    static class ScaledComponentLayout {
        private HashMap componentOrgLocationsMap = new HashMap();
        JComponent component;

        public ScaledComponentLayout( JComponent component ) {
            this.component = component;
        }

        private void saveSwingComponentCoordinates( double scale ) {
            Component[] components = component.getComponents();
            for( int i = 0; i < components.length; i++ ) {
                Component component = components[i];
                Point location = component.getLocation();
                //factor out the old scale, if any.
                componentOrgLocationsMap.put( component, new Point( (int)( location.x / scale ), (int)( location.y / scale ) ) );
            }
        }

        public void saveLocation( Component comp ) {
            componentOrgLocationsMap.put( comp, new Point( comp.getLocation() ) );
        }

        /**
         * Adjust the locations of Swing components based on the current scale
         */
        private void layoutSwingComponents( double scale ) {
            Component[] components = component.getComponents();
            for( int i = 0; i < components.length; i++ ) {
                Component component = components[i];
                Point origLocation = (Point)componentOrgLocationsMap.get( component );
                if( origLocation != null ) {
//                    double scale = transformManager.getScale();
                    Point newLocation = new Point( (int)( origLocation.getX() * scale ), (int)( origLocation.getY() * scale ) );
                    component.setLocation( newLocation );
                }
            }
        }

    }


}


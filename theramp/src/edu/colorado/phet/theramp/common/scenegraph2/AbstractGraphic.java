package edu.colorado.phet.theramp.common.scenegraph2;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:06:31 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */
public abstract class AbstractGraphic {
    private Shape clip;
    private RenderingHints renderingHints;
    private AffineTransform transform;
    private Paint paint;
    private Stroke stroke;
    private Font font;
    private ArrayList mouseHandlers = new ArrayList();
    private boolean visible = true;
    private String name;
    private boolean ignoreMouse = false;

    private boolean mousePressed = false;
    private boolean mouseEntered = false;
    private Cursor cursor;

    private Point2D registrationPoint = new Point2D.Double( 0, 0 );
    private Point2D location = new Point2D.Double( 0, 0 );
    private SceneGraphPanel sceneGraphPanel;
    private boolean drawBorderDebug = false;
    private ArrayList keyListeners = new ArrayList();
    private ArrayList focusListeners = new ArrayList();
    private boolean hasKeyFocus = false;
    private static int focusEventID = 0;
    private boolean regionDirty = false;
    private ArrayList savedStates = new ArrayList();

    public abstract void render( RenderEvent renderEvent );

    /**
     * Save state & modify graphics.
     */
    protected void setup( RenderEvent graphics2D ) {
        GraphicsState state = new GraphicsState( graphics2D.getGraphics2D() );
        savedStates.add( state );

//        if( transform != null ) {
        AffineTransform transform = getTransform();
//        System.out.println( "name=" + name + ", transform = " + transform );
//        System.out.println( "PRE graphics2D.getTransform() = " + graphics2D.getTransform() );
        AffineTransform tot = graphics2D.getTransform();
        tot.concatenate( getTransform() );
        graphics2D.getGraphics2D().setTransform( tot );
//        graphics2D.transform( transform );//todo could account for no-op transform smartly.
//        graphics2D.transform( transform );//todo could account for no-op transform smartly.
//        graphics2D.getTransform().concatenate( transform );//todo could account for no-op transform smartly.
//        System.out.println( "POST graphics2D.getTransform() = " + graphics2D.getTransform() );
//        }
        if( paint != null ) {
            graphics2D.setPaint( paint );
        }
        if( font != null ) {
            graphics2D.setFont( font );
        }
        if( clip != null ) {
            graphics2D.clip( clip );
        }
        if( stroke != null ) {
            graphics2D.setStroke( stroke );
        }
        if( renderingHints != null ) {
            graphics2D.setRenderingHints( renderingHints );
        }

    }

    protected void restore( RenderEvent renderEvent ) {
//        if( isDrawBorderDebug() ) {
//            drawBorderDebug( renderEvent );
//        }
        GraphicsState state = (GraphicsState)savedStates.remove( savedStates.size() - 1 );
        state.restoreGraphics();
    }

    protected void drawBorderDebug( RenderEvent graphics2D ) {
//        GraphicsState state = new GraphicsState( graphics2D );
//        graphics2D.setStroke( new BasicStroke( 1 ) );
//        graphics2D.setColor( Color.black );
//        graphics2D.draw( getLocalBounds() );
//        state.restoreGraphics();
    }

    public void translate( double dx, double dy ) {
        setRegionDirty();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.translate( dx, dy );
        setRegionDirty();
    }

    public void setClip( Shape clip ) {
        this.clip = clip;
    }

    public void setRenderingHints( RenderingHints renderingHints ) {
        this.renderingHints = renderingHints;
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
    }

    public void setColor( Color color ) {
        setPaint( color );
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public void setRenderingHint( RenderingHints.Key key, Object value ) {
        if( renderingHints == null ) {
            renderingHints = new RenderingHints( key, value );
        }
        else {
            renderingHints.put( key, value );
        }
    }

    public void setAntialias( boolean b ) {
        setRenderingHint( RenderingHints.KEY_ANTIALIASING, b ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
    }

    public AffineTransform getTransform() {
        AffineTransform net = new AffineTransform();

        // Apply local transform
        if( transform != null ) {
            net.preConcatenate( transform );
        }
        net.preConcatenate( AffineTransform.getTranslateInstance( -registrationPoint.getX(), -registrationPoint.getY() ) );
        // Translate to location
        net.preConcatenate( AffineTransform.getTranslateInstance( location.getX(), location.getY() ) );
        return net;
    }

    public void scale( double sx, double sy ) {
        setRegionDirty();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.scale( sx, sy );
        setRegionDirty();
    }

    public void rotate( double theta, double x, double y ) {
        setRegionDirty();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.rotate( theta, x, y );
        setRegionDirty();
    }

    public void rotate( double theta ) {
        setRegionDirty();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.rotate( theta );
        setRegionDirty();
    }

    public void addMouseListener( SceneGraphMouseHandler mouseHandler ) {
        mouseHandlers.add( mouseHandler );
    }

    public boolean containsMousePointLocal( double x, double y ) {
        return isVisible() && getLocalBounds().contains( x, y );
    }

    public void mouseDragged( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        if( mousePressed && !event.isConsumed() ) {//only drag if we're the right guy.
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseDragged( event );
            }
        }
        event.restore( orig );
    }

    public void mouseEntered( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        setMouseEntered( true );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < mouseHandlers.size(); i++ ) {
            SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
            sceneGraphMouseHandler.mouseEntered( event );
        }
        event.restore( orig );
    }

    private void setMouseEntered( boolean mouseEntered ) {
        this.mouseEntered = mouseEntered;
    }

    public boolean isMouseEntered() {
        return mouseEntered;
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        setMouseEntered( false );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < mouseHandlers.size(); i++ ) {
            SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
            sceneGraphMouseHandler.mouseExited( event );
        }
        event.restore( orig );
    }

    public void mouseClicked( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        if( event.isConsumed() ) {
            return;
        }
        if( containsLocal( event ) ) {
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseClicked( event );
            }
        }
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        if( event.isConsumed() ) {
            setMousePressed( false );
            return;
        }
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        if( containsLocal( event ) ) {
            event.consume();
            setMousePressed( true );
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mousePressed( event );
            }
            requestKeyFocus();
        }
        event.restore( orig );
    }

    private void setMousePressed( boolean mousePressed ) {
        this.mousePressed = mousePressed;
//        System.out.println( "Set: pressed = " + mousePressed );
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        if( containsLocal( event ) && !event.isConsumed() ) {
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseReleased( event );
            }
        }
        setMousePressed( false );
        event.restore( orig );
    }

    public void handleEntranceAndExit( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        if( !mouseEntered && containsLocal( event ) ) {
            event.restore( orig );
            mouseEntered( event );
        }
        else if( mouseEntered && !containsLocal( event ) ) {
            event.restore( orig );
            mouseExited( event );
        }
    }

    protected boolean containsLocal( SceneGraphMouseEvent event ) {
        return containsMousePointLocal( event.getX(), event.getY() );
    }

    public double getLocalWidth() {
        return getLocalBounds().getWidth();
    }

    public double getLocalHeight() {
        return getLocalBounds().getHeight();
    }

    public abstract Rectangle2D getLocalBounds();

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void setCursorHand() {
        this.cursor = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String toString() {
        return getClass().getName() + "@" + hashCode() + ", name=" + name;
    }

    public void setFontLucidaSansPlain( int size ) {
        setFont( new Font( "Lucida Sans", Font.PLAIN, size ) );
    }

    public void setFontLucidaSansBold( int size ) {
        setFont( new Font( "Lucida Sans", Font.BOLD, size ) );
    }

    public AbstractGraphic getHandler( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return null;
        }
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        AbstractGraphic handler = containsLocal( event ) ? this : null;
        event.restore( orig );
        return handler;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public Cursor determineCursor( SceneGraphMouseEvent event ) {
        AbstractGraphic handler = getHandler( event );
        return handler == null ? null : handler.getCursor();
    }

    public Point2D push( Point2D src ) {
        AffineTransform transform = getTransform();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        try {
            AffineTransform inverse = transform.createInverse();

            Point2D pt = inverse.transform( src, null );
            return pt;
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public Point2D getRegistrationPoint() {
        return new Point2D.Double( registrationPoint.getX(), registrationPoint.getY() );
    }

    public void setRegistrationPoint( Point2D registrationPoint ) {
        this.registrationPoint = registrationPoint;
    }

    public Point2D getLocation() {
        return new Point2D.Double( location.getX(), location.getY() );
    }

    public void setLocation( Point2D location ) {
        if( !this.location.equals( location ) ) {
            setRegionDirty();
            this.location = location;
            setRegionDirty();
        }
    }

    public void setLocation( double x, double y ) {
        setLocation( new Point2D.Double( x, y ) );
    }

    public void setSceneGraphPanel( SceneGraphPanel sceneGraphPanel ) {
        this.sceneGraphPanel = sceneGraphPanel;
    }

    public SceneGraphPanel getSceneGraphPanel() {
//        if( getParent() != null ) {
//            return getParent().getSceneGraphPanel();
//        }
//        else {
        return sceneGraphPanel;
//        }
    }

//    public GraphicNode getParent() {
//        return parent;
//    }
//
//    public GraphicNode getRoot() {
//        if( parent == null ) {
//            if( this instanceof GraphicNode ) {
//                return (GraphicNode)this;
//            }
//            else {
//                return null;
//            }
//        }
//        else {
//            return parent.getRoot();
//        }
//    }
//
//    public void setParent( GraphicNode parent ) {
//        this.parent = parent;
//    }

    public Stroke getStroke() {
        return stroke;
    }

    public Font getFont() {
        return font;
    }

    public void setTransformViewport( Rectangle screenLocation, Rectangle2D modelViewPort ) {
        AffineTransform tx = new ModelViewTransform2D( modelViewPort, screenLocation, false ).getAffineTransform();
        setTransform( tx );
    }
//    public void setTransformViewport(Rectangle screenLocation, Rectangle2D.Double modelViewPort) {
//        AffineTransform tx = new ModelViewTransform2D(modelViewPort, screenLocation, false).getAffineTransform();
//        setTransform(tx);
//    }

    public void repaint() {
        System.out.println( "AbstractGraphic.repaint" );
//        if( getSceneGraphPanel() != null ) {
//            getSceneGraphPanel().repaint();
//        }
    }

    public void setTransformBounds( Rectangle2D bounds ) {
        setTransformBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }

    public void setTransformBounds( double x, double y, double width, double height ) {
        double sx = width / getLocalWidth();
        double sy = height / getLocalHeight();
        AffineTransform transform = new AffineTransform();
        transform.translate( x, y );
        transform.scale( sx, sy );

        setTransform( transform );
    }

//    public Shape getBoundsIn( GraphicNode ancestor ) {
//        return getTransform( ancestor ).createTransformedShape( getLocalBounds() );
//    }

//    public Shape getBoundsIn2( GraphicNode ancestor ) {
//        Shape shape = getBoundsIn( ancestor );
//        shape = ancestor.getTransform().createTransformedShape( shape );
//        return shape;
//    }

//    /**
//     * Get the affineTransform that takes one from our frame to the frame of an ancestor.
//     *
//     * @param ancestor
//     * @return
//     */
//    public AffineTransform getTransform( GraphicNode ancestor ) {
//        if( ancestor == this ) {
//            return new AffineTransform();
//        }
//        else {
//            AffineTransform net = new AffineTransform();
//            net.concatenate( getParent().getTransform( ancestor ) );
//            net.concatenate( getTransform() );
//            return net;
//        }
//    }
//
    public void setRegionDirty() {
        this.regionDirty = true;
//        if( getSceneGraphPanel() != null ) {
//            getSceneGraphPanel().addDirtyRegion( this );//changes all references of this thing.
////            getSceneGraphPanel().addDirtyRegion( getBoundsIn2( getSceneGraphPanel().getGraphic() ) );
//        }
    }

    public boolean isDrawBorderDebug() {
        return drawBorderDebug;
    }

    public void setDrawBorderDebug( boolean drawBorderDebug ) {
        this.drawBorderDebug = drawBorderDebug;
    }

    public boolean isIgnoreMouse() {
        return ignoreMouse;
    }

    public void setIgnoreMouse( boolean ignoreMouse ) {
        this.ignoreMouse = ignoreMouse;
    }

    public void concatenate( AffineTransform affineTransform ) {
        AffineTransform t = new AffineTransform( transform );
        t.concatenate( affineTransform );
        setTransform( t );
    }

    public void preConcatenate( AffineTransform affineTransform ) {
        AffineTransform t = new AffineTransform( transform );
        t.preConcatenate( affineTransform );
        setTransform( t );
    }

    public void addKeyListener( KeyListener keyListener ) {
        keyListeners.add( keyListener );
    }

    public void requestKeyFocus() {
//        if( !hasKeyFocus ) {
//            getRoot().disableKeyFocusTree();
//            this.hasKeyFocus = true;
//            notifyFocusGained();
//        }
    }

    private boolean hasKeyFocus() {
        return hasKeyFocus;
    }

    public void keyPressed( KeyEvent e ) {
        if( e.isConsumed() ) {
            return;
        }
        if( hasKeyFocus() ) {
            e.consume();
            for( int i = 0; i < keyListeners.size(); i++ ) {
                ( (KeyListener)keyListeners.get( i ) ).keyPressed( e );
            }
        }
    }

    public void keyTyped( KeyEvent e ) {
        if( e.isConsumed() ) {
            return;
        }
        if( hasKeyFocus() ) {
            e.consume();
            for( int i = 0; i < keyListeners.size(); i++ ) {
                ( (KeyListener)keyListeners.get( i ) ).keyTyped( e );
            }
        }
    }

    public void keyReleased( KeyEvent e ) {
        if( e.isConsumed() ) {
            return;
        }
        if( hasKeyFocus() ) {
            e.consume();
            for( int i = 0; i < keyListeners.size(); i++ ) {
                ( (KeyListener)keyListeners.get( i ) ).keyReleased( e );
            }
        }
    }

    public void disableKeyFocusTree() {
        //To change body of created methods use File | Settings | File Templates.
        if( hasKeyFocus ) {
            hasKeyFocus = false;
            notifyFocusLost();
        }
    }

    private void notifyFocusLost() {
        FocusEvent focusEvent = new FocusEvent( getSceneGraphPanel(), focusEventID++ );
        for( int i = 0; i < focusListeners.size(); i++ ) {
            FocusListener focusListener = (FocusListener)focusListeners.get( i );
            focusListener.focusLost( focusEvent );
        }
    }

    private void notifyFocusGained() {
        FocusEvent focusEvent = new FocusEvent( getSceneGraphPanel(), focusEventID++ );
        for( int i = 0; i < focusListeners.size(); i++ ) {
            FocusListener focusListener = (FocusListener)focusListeners.get( i );
            focusListener.focusGained( focusEvent );
        }
    }

    public void addFocusListener( FocusListener focusListener ) {
        focusListeners.add( focusListener );
    }

    public void collectDirtyRegions( DirtyRegionSet dirty ) {
        if( regionDirty ) {
            dirty.setRegionDirty();
            regionDirty = false;
        }
        dirty.concatenate( getTransform() );
    }

}

/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.model.FourierComponent;


/**
 * SinWaveGraphic is the graphical representation of a sine wave.
 * <p>
 * A set of line segments is draw to approximate the curve.
 * The curve is constrained to be drawn within some viewport.
 * The amplitude determines the height of the curve, while
 * the frequency determines how many cycles of the curve will
 * be drawn.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SineWaveGraphic extends PhetShapeGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int WAVE_TYPE_SINE = 0;
    public static final int WAVE_TYPE_COSINE = 1;
    
    // Defaults
    private static final Dimension DEFAULT_VIEWPORT_SIZE = new Dimension( 200, 50 );
    private static final double DEFAULT_PHASE_ANGLE = 0.0;
    private static final int DEFAULT_WAVE_TYPE = WAVE_TYPE_SINE;
    private static final Color DEFAULT_WAVE_COLOR = Color.BLACK;
    private static final float DEFAULT_WAVE_LINE_WIDTH = 2f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model we're viewing
    private FourierComponent _fourierComponent;
    // Wave must be constrained to this viewport.
    private Dimension _viewportSize;
    // Paths that describes the wave.
    private GeneralPath _path;
    // The phase angle at the origin
    private double _phaseAngle;
    // Type of wave (sines or cosines)
    private int _waveType;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public SineWaveGraphic( Component component, FourierComponent fourierComponent ) {
        super( component );
        
        assert( fourierComponent != null );
        _fourierComponent = fourierComponent;
        _fourierComponent.addObserver( this );
        
        _viewportSize = DEFAULT_VIEWPORT_SIZE;
        _phaseAngle = DEFAULT_PHASE_ANGLE;
        _waveType = DEFAULT_WAVE_TYPE;
        
        _path = new GeneralPath();
        setShape( _path );
        setBorderColor( DEFAULT_WAVE_COLOR );
        setStroke( new BasicStroke( DEFAULT_WAVE_LINE_WIDTH ) );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
    }
    
    /**
     * Finalizes an instance of this type..
     */
    public void finalize() {
        _fourierComponent.removeObserver( this );
        _fourierComponent = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wave type (sine or cosine).
     * 
     * @param waveType WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public void setWaveType( int waveType ) {
        if ( waveType != _waveType ) {
            _waveType = waveType;
            update();
        }
    }
    
    /**
     * Gets the wave type.
     * 
     * @return WAVE_TYPE_SINES or WAVE_TYPE_COSINE
     */
    public int getWaveType() {
        return _waveType;
    }
    
    /**
     * Sets the phase angle at the origin.
     * 
     * @param phaseAngle the phase angle, in radians
     */
    public void setPhaseAngle( double phaseAngle ) {
        if ( phaseAngle != phaseAngle ) {
            _phaseAngle = phaseAngle;
            update();
        }
    }
    
    /**
     * Gets the phase angle at the origin.
     * 
     * @return the phase angle, in radians
     */
    public double getPhaseAngle() {
        return _phaseAngle;
    }

    /**
     * Sets the color used to draw the wave.
     * 
     * @param color the color
     */
    public void setColor( Color color ) {
        setBorderColor( color );
        repaint();
    }

    /**
     * Sets the line width used to draw the wave.
     * 
     * @param viewportSize
     */
    public void setLineWidth( float width ) {
        setStroke( new BasicStroke( width ) );
        repaint();
    }
    
    /**
     * Sets the viewport size.
     * 
     * @param viewportSize the viewport size
     */
    public void setViewportSize( Dimension viewportSize ) {
        assert( viewportSize != null );
        setViewportSize( viewportSize.width, viewportSize.height );
    }
    
    /**
     * Sets the viewport size.
     * 
     * @param width the viewport width
     * @param height the viewport height
     */
    public void setViewportSize( int width, int height ) {
        if ( width != _viewportSize.width || height != _viewportSize.height ) {
            _viewportSize.setSize( width, height );
            update();
        }
    }
    
    /**
     * Gets the viewport size.
     * 
     * @return the viewport size
     */
    public Dimension getViewportSize() {
        return new Dimension( _viewportSize );
    }
    
    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current parameter settings.
     * The wave is approximated using a set of line segments.
     * The origin is at the center of the viewport.
     * <p>
     * NOTE! As a performance optimization, you must call this
     * method explicitly after changing parameter values.
     */
    public void update() {

        if ( isVisible() ) {
            
            int numberOfCycles = _fourierComponent.getOrder() + 1;
            double amplitude = _fourierComponent.getAmplitude();
            
            // Change in angle per change in x coordinate
            final double deltaAngle = ( 2.0 * Math.PI * numberOfCycles ) / _viewportSize.width;

            // Start angle
            double startAngle = _phaseAngle - ( deltaAngle * ( _viewportSize.width / 2.0 ) );
            
            // Approximate the wave as a set of line segments.
            _path.reset();
            for ( double i = 0; i <= _viewportSize.width; i++ ) {
                double angle = startAngle + ( i * deltaAngle );
                double radians = ( _waveType == WAVE_TYPE_SINE ) ? Math.sin( angle ): Math.cos( angle );
                double x = -( _viewportSize.width / 2 - i );
                double y = amplitude * radians * ( _viewportSize.height / 2.0 );
                if ( i == 0 ) {
                    _path.moveTo( (float) x, (float) -y );  // +Y is up
                }
                else {
                    _path.lineTo( (float) x, (float) -y );  // +Y is up
                }
            }
            
            setShape( _path );
            repaint();
        }
    }
}
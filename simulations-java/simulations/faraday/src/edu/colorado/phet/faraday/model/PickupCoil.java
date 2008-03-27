/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * PickupCoil is the model of a pickup coil.
 * Its behavior follows Faraday's Law for electromagnetic induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PickupCoil extends AbstractCoil implements ModelElement, SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    // Determines how the magnetic field decreases with the distance from the magnet.
    private final double _distanceExponent;
    
    private double _flux; // in webers
    private double _deltaFlux; // in webers
    private double _emf; // in volts
    private double _biggestEmf; // in volts
    private Point2D _samplePoints[]; // B-field sample points
    private SamplePointsStrategy _samplePointsStrategy;
    
    // Reusable objects
    private AffineTransform _someTransform;
    private Point2D _samplePoint;
    private Vector2D _sampleVector;
    private Vector2D _fieldVector;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a PickupCoil that uses a fixed number of sample points
     * to measure the magnet's B-field.
     * 
     * @param magnetModel
     * @param distanceExponent
     */
    public PickupCoil( AbstractMagnet magnetModel, double distanceExponent ) {
        this( magnetModel, distanceExponent, new ConstantNumberOfSamplePointsStrategy( 9 /* numberOfSamplePoints */ ) );
    }
    
    /**
     * Constructs a PickupCoil that uses a specified strategy for creating sample points
     * to measure the magnet's B-field.
     * 
     * @param magnetModel
     * @param distanceExponent
     * @param samplePointsStrategy
     */
    public PickupCoil( AbstractMagnet magnetModel, double distanceExponent, SamplePointsStrategy samplePointsStrategy ) {
        super();
        
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _distanceExponent = distanceExponent;
        
        _samplePointsStrategy = samplePointsStrategy;
        _samplePoints = null;
        
        _flux = 0.0;
        _deltaFlux = 0.0;
        _emf = 0.0;
        _biggestEmf = 0.0;
        
        // Reusable objects
        _someTransform = new AffineTransform();
        _samplePoint = new Point2D.Double();
        _fieldVector = new Vector2D();
        _sampleVector = new Vector2D();
        
        // loosely packed loops
        setLoopSpacing( 1.5 * getWireWidth() );
        
        updateSamplePoints();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the magnetic flux.
     * 
     * @return the flux, in Webers
     */
    public double getFlux() {
        return _flux;
    }
    
    /**
     * Gets the change in magnetic flux.
     * 
     * @return change in magnetic flux, in Webers
     */
    public double getDeltaFlux() {
        return _deltaFlux;
    }
    
    /**
     * Gets the emf.
     * 
     * @return the emf
     */
    public double getEmf() {
        return _emf;
    }
    
    /**
     * Gets the biggest emf that the pickup coil has seen.
     * 
     * @return the biggest emf
     */
    public double getBiggestEmf() {
        return _biggestEmf;
    }
    
    /**
     * Gets the sample points used to measure the B-field and calculate emf.
     * 
     * @return
     */
    public Point2D[] getSamplePoints() {
        return _samplePoints;
    }
    
    /**
     * When the coil's radius changes, update the sample points.
     */
    public void setRadius( double radius ) {
        super.setRadius( radius );
        updateSamplePoints();
    }
    
    /**
     * Sets the strategy used to compute sample points.
     * 
     * @param samplePointsStrategy
     */
    public void setSamplePointsStrategy( SamplePointsStrategy samplePointsStrategy ) {
        _samplePointsStrategy = samplePointsStrategy;
        updateSamplePoints();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        // Do nothing, handled by stepInTime
    }
    
    //----------------------------------------------------------------------------
    // Sample points
    //----------------------------------------------------------------------------
    
    /*
     * Updates the sample points for the coil.
     * The samples points are used to measure the B-field in the calculation of emf.
     */
    private void updateSamplePoints() {
        _samplePoints = _samplePointsStrategy.createSamplePoints( this );
    }
    
    /**
     * Interface implemented by all strategies for 
     * creating B-field sample points for a pickup coil.
     */
    public interface SamplePointsStrategy {
        public Point2D[] createSamplePoints( PickupCoil pickupCoilModel );
    }
    
    /**
     * A fixed number of points is distributed
     * along a vertical line that goes through the center of a pickup coil.
     * The number of sample points must be odd, so that one point is at the center.
     * The points at the outer edge are guaranteed to be on the coil.
     */
    public static class ConstantNumberOfSamplePointsStrategy implements SamplePointsStrategy {
        
        private final int _numberOfSamplePoints;
        
        public ConstantNumberOfSamplePointsStrategy( final int numberOfSamplePoints ) {
            if ( numberOfSamplePoints < 1 || numberOfSamplePoints % 2 != 1 ) {
                throw new IllegalArgumentException( "numberOfSamplePoints must be > 0 and odd" );
            }
            _numberOfSamplePoints = numberOfSamplePoints;
        }

        public Point2D[] createSamplePoints( PickupCoil pickupCoilModel ) {
            
            Point2D[] samplePoints = new Point2D[_numberOfSamplePoints];
            final double numberOfSamplePointsOnRadius = ( _numberOfSamplePoints - 1 ) / 2;
            final double samplePointsYSpacing = pickupCoilModel.getRadius() / numberOfSamplePointsOnRadius;

            // all sample points share the same x offset
            final double xOffset = 0;

            // Center
            int index = 0;
            samplePoints[index++] = new Point2D.Double( xOffset, 0 );

            // Offsets below & above the center
            double y = 0;
            for ( int i = 0; i < numberOfSamplePointsOnRadius; i++ ) {
                y += samplePointsYSpacing;
                samplePoints[index++] = new Point2D.Double( xOffset, y );
                samplePoints[index++] = new Point2D.Double( xOffset, -y );
            }
            
            return samplePoints;
        }
    }
    
    /**
     * A fixed spacing is used to distribute a variable number of points
     * along a vertical line that goes through the center of a pickup coil.
     * One point is at the center. Points will be on the edge of the coil
     * only if the coil's radius is an integer multiple of the spacing.
     */
    public static class VariableNumberOfSamplePointsStrategy implements SamplePointsStrategy {
        
        private final double _ySpacing;
        
        public VariableNumberOfSamplePointsStrategy( final double ySpacing ) {
            if ( ySpacing <= 0 ) {
                throw new IllegalArgumentException( "ySpacing must be >= 0" );
            }
            _ySpacing = ySpacing;
        }
        
        public Point2D[] createSamplePoints( PickupCoil pickupCoilModel ) {
            
            final int numberOfSamplePointsOnRadius = (int)( pickupCoilModel.getRadius() / _ySpacing );
            
            Point2D[] samplePoints = new Point2D[ 1 + ( 2 * numberOfSamplePointsOnRadius ) ];

            // all sample points share the same x offset
            final double xOffset = 0;

            // Center
            int index = 0;
            samplePoints[index++] = new Point2D.Double( xOffset, 0 );

            // Offsets below & above the center
            double y = 0;
            for ( int i = 0; i < numberOfSamplePointsOnRadius; i++ ) {
                y += _ySpacing;
                samplePoints[index++] = new Point2D.Double( xOffset, y );
                samplePoints[index++] = new Point2D.Double( xOffset, -y );
            }
            
            return samplePoints;
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the simulation clock.
     * Calculates the induced emf using Faraday's Law.
     * Performs median smoothing of data if isSmoothingEnabled.
     * 
     * @param dt time delta
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
            updateEmf( dt );
        }
    }
    
    /**
     * Updates the induced emf, using Faraday's Law.
     */
    private void updateEmf( double dt ) {
        
        // Sum the B-field sample points.
        _fieldVector.setMagnitudeAngle( 0, 0 );
        for ( int i = 0; i < _samplePoints.length; i++ ) {
            
            _samplePoint.setLocation( getX() + _samplePoints[i].getX(), getY() + _samplePoints[i].getY() );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _samplePoint, _samplePoint /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _samplePoint, _sampleVector /* output */, _distanceExponent  );
            
            // Accumulate a sum of the sample points.
            _fieldVector.add( _sampleVector );
        }
        
        // Average the B-field sample points.
        double scale = 1.0 / _samplePoints.length;
        _fieldVector.scale( scale );
        
        // Flux in one loop.
        double A = getLoopArea(); // scaling factor to account for variable loop area 
        double Bx = _fieldVector.getX();
        double loopFlux = A * Bx; 
        
        // Flux in the coil.
        double flux = getNumberOfLoops() * loopFlux;
        
        // Change in flux.
        _deltaFlux = flux - _flux;
        _flux = flux;
        
        // Induced emf.
        double emf = -( _deltaFlux / dt );
        
        // If the emf has changed, set the current in the coil and notify observers.
        if ( emf != _emf ) {
            _emf = emf;
            
            // Current amplitude is proportional to emf amplitude.
            double amplitude = MathUtil.clamp( -1,  emf / FaradayConstants.PICKUP_REFERENCE_EMF, +1 );
            setCurrentAmplitude( amplitude ); // calls notifyObservers
        }
        
        // Keep track of the biggest emf seen by the pickup coil.
        if ( Math.abs( _emf ) > Math.abs( _biggestEmf ) ) {
            _biggestEmf = _emf;
            if ( FaradayConstants.DEBUG_PICKUP_COIL_EMF ) {
                System.out.println( "PickupCoil.updateEmf: biggestEmf=" + _biggestEmf );
            }
        }
    }
}
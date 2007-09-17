/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * DNASegment
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNASegment extends FixedObject implements ModelElement, Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_FORCE = "force"; // force exerted by this segment of the DNA
    public static final String PROPERTY_SHAPE = "shape"; // shape of the strand
    
    // Developer controls
    public static final String PROPERTY_SPRING_CONSTANT = "springConstant";
    public static final String PROPERTY_DRAG_COEFFICIENT = "dragCoefficient";
    public static final String PROPERTY_KICK_CONSTANT = "kickConstant";
    public static final String PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK = "numberOfEvolutionsPerClockTick";
    public static final String PROPERTY_EVOLUTION_DT = "evolutionDtScale";
    public static final String PROPERTY_FLUID_DRAG_COEFFICIENT = "fluidDragCoefficient";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private OTClock _clock;
    private double _referenceClockStep;
    
    private double _contourLength; // nm, length of the DNA strand
    private final double _persistenceLength; // nm, measure of the strand's bending stiffness
    private final double _springLength; // nm, length of a spring when it's fully stretched
    
    private ArrayList _pivots; // array of DNAPivot, first element is closest to pin
    private double _closestSpringLength; // length of spring closest to pin
    
    /*
     * Maximum that the segment can be stretched, expressed as a percentage
     * of the strand's contour length. As this value gets closer to 1, the 
     * DNA force gets closer to infinity, increasing the likelihood that the 
     * bead will rocket off the screen when it is released.
     */
    private final double _maxStretchiness;
    
    private Random _kickRandom; // random number generator for "kick"
    private Vector2D _someVector; // reusable vector
    
    // Developer controls
    private double _springConstant; // actually the spring constant divided by mass
    private DoubleRange _springConstantRange;
    private double _dragCoefficient;
    private DoubleRange _dragCoefficientRange;
    private double _kickConstant;
    private DoubleRange _kickConstantRange;
    private int _numberOfEvolutionsPerClockTick;
    private IntegerRange _numberOfEvolutionsPerClockTickRange;
    private double _evolutionDt;
    private DoubleRange _evolutionDtRange;
    private double _fluidDragCoefficient;
    private DoubleRange _fluidDragCoefficientRange;
    
    //----------------------------------------------------------------------------
    // Constructors & initializers
    //----------------------------------------------------------------------------
    
    public DNASegment( 
            Point2D position,
            double contourLength,
            double persistenceLength,
            double springLength,
            double maxStretchiness,
            Bead bead,
            Fluid fluid,
            OTClock clock,
            double referenceClockStep,
            /* developer controls below here */
            DoubleRange springConstantRange, 
            DoubleRange dragCoefficientRange, 
            DoubleRange kickConstantRange, 
            IntegerRange numberOfEvolutionsPerClockTickRange, 
            DoubleRange evolutionDtRange, 
            DoubleRange fluidDragCoefficientRange ) {
        
        super( position, 0 /* orientation */ );
        
        _contourLength = contourLength;
        _persistenceLength = persistenceLength;
        _springLength = springLength;
        _maxStretchiness = maxStretchiness;
        
        _bead = bead;
        _bead.addObserver( this );

        _fluid = fluid;
        _fluid.addObserver( this );
        
        _clock = clock;
        _referenceClockStep = referenceClockStep;
        
        _kickRandom = new Random();
        _someVector = new Vector2D();
        
        // developer controls
        {
            _springConstantRange = springConstantRange;
            _springConstant = _springConstantRange.getDefault();
            _dragCoefficientRange = dragCoefficientRange;
            _dragCoefficient = _dragCoefficientRange.getDefault();
            _kickConstantRange = kickConstantRange;
            _kickConstant = _kickConstantRange.getDefault();
            _numberOfEvolutionsPerClockTickRange = numberOfEvolutionsPerClockTickRange;
            _numberOfEvolutionsPerClockTick = _numberOfEvolutionsPerClockTickRange.getDefault();
            _evolutionDtRange = evolutionDtRange;
            _evolutionDt = _evolutionDtRange.getDefault();
            _fluidDragCoefficientRange = fluidDragCoefficientRange;
            _fluidDragCoefficient = _fluidDragCoefficientRange.getDefault();
        }
        
        initialize();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    /**
     * Gets the pivot points that define the segment.
     * 
     * @param ArrayList of DNAPivot
     */
    public ArrayList getPivots() {
        return _pivots;
    }
    
    /**
     * Sets the segment's contour length.
     * 
     * @param length
     */
    public void setContourLength( double contourLength ) {
        if ( contourLength < _contourLength ) {
            makeShorter( _contourLength - contourLength );
        }
        else if ( contourLength > _contourLength ) {
            makeLonger( contourLength - _contourLength );
        }
    }
    
    /**
     * Gets the segment's contour length.
     * 
     * @return
     */
    public double getContourLength() {
        return _contourLength;
    }
    
    /**
     * Gets the straight-line distance between the pin and bead.
     * 
     * @return extension (nm)
     */
    public double getExtension() {
        return getExtension( getBeadX(), getBeadY() );
    }
    
    /*
     * Gets the straight-line distance between the pin and some arbitrary point.
     * 
     * @returns extension (nm)
     */
    private double getExtension( double x, double y ) {
        final double dx = x - getX();
        final double dy = y - getY();
        return PolarCartesianConverter.getRadius( dx, dy );
    }
    
    /**
     * Gets the maximum extension that the strand can have.
     * This is a function of the strand's "stretchiness" and segment's contour length.
     * 
     * @return maximum extension (nm)
     */
    public double getMaxExtension() {
        return _maxStretchiness * _contourLength;
    }
    
    /**
     * Gets the max "stretchiness" of the strand.
     * This is expressed as a percentage of the strand's contour length.
     * As this value gets closer to 1, the DNA force gets closer to infinity,
     * increasing the likelihood that the bead will rocket off the screen when 
     * it is released.
     * 
     * @return
     */
    public double getMaxStretchiness() {
        return _maxStretchiness;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters for developer controls
    //----------------------------------------------------------------------------
    
    public void setSpringConstant( double springConstant ) {
        if ( !_springConstantRange.contains( springConstant ) ) {
            throw new IllegalArgumentException( "springConstant out of range: " + springConstant );
        }
        if ( springConstant != _springConstant ) {
            _springConstant = springConstant;
            notifyObservers( PROPERTY_SPRING_CONSTANT );
        }
    }

    public double getSpringConstant() {
        return _springConstant;
    }

    public DoubleRange getSpringConstantRange() {
        return _springConstantRange;
    }

    public void setDragCoefficient( double dragCoefficient ) {
        if ( !_dragCoefficientRange.contains( dragCoefficient ) ) {
            throw new IllegalArgumentException( "dragCoefficient out of range: " + dragCoefficient );
        }
        if ( dragCoefficient != _dragCoefficient ) {
            _dragCoefficient = dragCoefficient;
            notifyObservers( PROPERTY_DRAG_COEFFICIENT );
        }
    }

    public double getDragCoefficient() {
        return _dragCoefficient;
    }

    public DoubleRange getDragCoefficientRange() {
        return _dragCoefficientRange;
    }

    public void setKickConstant( double kickConstant ) {
        if ( !_kickConstantRange.contains( kickConstant ) ) {
            throw new IllegalArgumentException( "kickConstant out of range: " + kickConstant );
        }
        if ( kickConstant != _kickConstant ) {
            _kickConstant = kickConstant;
            notifyObservers( PROPERTY_KICK_CONSTANT );
        }
    }

    public double getKickConstant() {
        return _kickConstant;
    }

    public DoubleRange getKickConstantRange() {
        return _kickConstantRange;
    }

    public void setNumberOfEvolutionsPerClockTick( int numberOfEvolutionsPerClockTick ) {
        if ( !_numberOfEvolutionsPerClockTickRange.contains( numberOfEvolutionsPerClockTick ) ) {
            throw new IllegalArgumentException( "numberOfEvolutionsPerClockTick out of range: " + numberOfEvolutionsPerClockTick );
        }
        if ( numberOfEvolutionsPerClockTick != _numberOfEvolutionsPerClockTick ) {
            _numberOfEvolutionsPerClockTick = numberOfEvolutionsPerClockTick;
            notifyObservers( PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK );
        }
    }

    public int getNumberOfEvolutionsPerClockTick() {
        return _numberOfEvolutionsPerClockTick;
    }

    public IntegerRange getNumberOfEvolutionsPerClockTickRange() {
        return _numberOfEvolutionsPerClockTickRange;
    }

    public void setEvolutionDt( double evolutionDt ) {
        if ( !_evolutionDtRange.contains( evolutionDt ) ) {
            throw new IllegalArgumentException( "evolutionDt out of range: " + evolutionDt );
        }
        if ( evolutionDt != _evolutionDt ) {
            _evolutionDt = evolutionDt;
            notifyObservers( PROPERTY_EVOLUTION_DT );
        }
    }
    
    public double getEvolutionDt() {
        return _evolutionDt;
    }
    
    public DoubleRange getEvolutionDtRange() {
        return _evolutionDtRange;
    }
    
    public void setFluidDragCoefficient( double fluidDragCoefficient ) {
        if ( !_fluidDragCoefficientRange.contains( fluidDragCoefficient  ) ) {
            new IllegalArgumentException( "fluidDragCoefficient out of range: " + fluidDragCoefficient );
        }
        if ( fluidDragCoefficient != _fluidDragCoefficient ) {
            _fluidDragCoefficient = fluidDragCoefficient;
            notifyObservers( PROPERTY_FLUID_DRAG_COEFFICIENT );
        }
    }
    
    public double getFluidDragCoefficient() {
        return _fluidDragCoefficient;
    }
    
    public DoubleRange getFluidDragCoefficientRange() {
        return _fluidDragCoefficientRange;
    }
    
    //----------------------------------------------------------------------------
    // Convenience method for accessing pivots
    //----------------------------------------------------------------------------
    
    public double getBeadX() {
        return getBeadPivot().getX();
    }
    
    public double getBeadY() {
        return getBeadPivot().getY();
    }
    
    private DNAPivot getBeadPivot() {
        DNAPivot pivot = null;
        if ( _pivots.size() > 0 ) {
            pivot = (DNAPivot) _pivots.get( _pivots.size() - 1 );
        }
        return pivot;
    }
    
    public double getPinX() {
        return getPinPivot().getX();
    }

    public double getPinY() {
        return getPinPivot().getY();
    }
    
    private DNAPivot getPinPivot() {
        DNAPivot pivot = null;
        if ( _pivots.size() > 0 ) {
            pivot = (DNAPivot) _pivots.get( 0 );
        }
        return pivot;
    }
    
    //----------------------------------------------------------------------------
    // Force model
    //----------------------------------------------------------------------------

    /**
     * Gets the DNA force at some arbitrary point from the pin.
     * 
     * @param p
     * @return force (pN)
     */
    public Vector2D getForce( Point2D p ) {
        return getForce( p.getX(), p.getY() );
    }
    
    /**
     * Gets the DNA force at some arbitrary point from the pin.
     * 
     * @param x
     * @param y
     * @return force (pN)
     */
    public Vector2D getForce( double x, double y ) {
        
        // angle (radians)
        final double xOffset = getPinX() - x;
        final double yOffset = getPinY() - y;
        final double angle = PolarCartesianConverter.getAngle( xOffset, yOffset );
        
        // magnitude (pN)
        final double extension = getExtension( x, y );
        final double kbT = 4.1 * _fluid.getTemperature() / 293; // kbT is 4.1 pN-nm at temperature=293K
        final double Lp = _persistenceLength;
        final double scale = extension / _contourLength;
        final double magnitude = ( kbT / Lp ) * ( ( 1 / ( 4 * ( 1 - scale ) * ( 1 - scale ) ) ) - ( 0.24 ) + scale );
        
        return new Vector2D.Polar( magnitude, angle );
    }
    
    //----------------------------------------------------------------------------
    // Springs-&-pivots model
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the pivot points that connect the springs.
     * The springs are layed out in a straight line between the pin and the bead.
     * If the contour length is not an integer multiple of the spring length,
     * then the first spring (closest to the pin) will be shorter than the others.
     */
    public void initialize() {
        
        assert( _contourLength >= ( 2 * _springLength ) ); // initialization requires at least 2 springs (3 pivots)
        
        // validate the distance from the pin to the bead
        Point2D pinPosition = getPositionReference();
        Point2D beadPosition = _bead.getPositionReference();
        final double extension = Math.abs( pinPosition.distance( beadPosition ) );
        final double maxExtension = _contourLength * _maxStretchiness;
        if ( extension > maxExtension ) {
            throw new IllegalStateException( "cannot connect DNA strand to bead, bead is too far away from pin" );
        }

        // determine how many pivot points, and the length of the spring closest to the pin
        int numberOfPivots = (int) ( _contourLength / _springLength ) + 2;
        _closestSpringLength = _contourLength % _springLength;
        if ( _closestSpringLength == 0 ) {
            _closestSpringLength = _springLength;
            numberOfPivots--;
        }
        
        final double springLengthScale = extension / _contourLength;
        final double extensionAngle = PolarCartesianConverter.getAngle( beadPosition.getX() - pinPosition.getX(), beadPosition.getY() - pinPosition.getY() );
        
        _pivots = new ArrayList();
        
        // first pivot is at the pin, starts the partial spring
        DNAPivot currentPivot = new DNAPivot( getX(), getY() );
        _pivots.add( currentPivot );
        DNAPivot previousPivot = currentPivot;

        // second pivot, terminates the first spring
        double xDelta = springLengthScale * PolarCartesianConverter.getX( _closestSpringLength, extensionAngle );
        double yDelta = springLengthScale * PolarCartesianConverter.getY( _closestSpringLength, extensionAngle );
        currentPivot = new DNAPivot( currentPivot.getX() + xDelta, currentPivot.getY() + yDelta );
        _pivots.add( currentPivot );
        previousPivot = currentPivot;
        
        // all pivots after the partial spring and before bead
        xDelta = springLengthScale * PolarCartesianConverter.getX( _springLength, extensionAngle );
        yDelta = springLengthScale * PolarCartesianConverter.getY( _springLength, extensionAngle );
        for ( int i = 1; i < numberOfPivots - 3; i++ ) {
            currentPivot = new DNAPivot( previousPivot.getX() + xDelta, previousPivot.getY() + yDelta );
            _pivots.add( currentPivot );
            previousPivot = currentPivot;
        }

        // last pivot is at the bead
        currentPivot = new DNAPivot( beadPosition.getX(), beadPosition.getY() );
        _pivots.add( currentPivot );

        // evolve so that it doesn't look like a straight line
        evolve( _clock.getDt() );
        
        notifyObservers( PROPERTY_SHAPE );
    }
    
    /*
     * Evolves the segment using a "Hollywood" model.
     * The segment is a collection of springs connected at pivot points.
     * This model was provided by Mike Dubson.
     */
    private void evolve( double clockStep ) {
        
        // return unless we have at least 2 springs
        if ( _pivots.size() < 3 ) {
            return;
        }
        
        // scale down the spring's motion as the segment becomes stretched taut
        double stretchFactor = Math.min( 1, getExtension() / _contourLength );
        final double springMotionScale = Math.sqrt( 1 - stretchFactor );
        
        // scale all time dependent parameters based on how the clockStep compares to reference clock step
        final double timeScale = clockStep / _referenceClockStep;
        
        final double dt = _evolutionDt * timeScale;
        
        for ( int i = 0; i < _numberOfEvolutionsPerClockTick; i++ ) {

            final int numberOfPivots = _pivots.size();
            DNAPivot currentPivot, previousPivot, nextPivot; // previousPivot is closer to pin, nextPivot is closer to end
            
            // traverse all pivots starting at pin, skip first and last pivots
            for ( int j = 1; j < numberOfPivots - 1; j++ ) {
                
                currentPivot = (DNAPivot) _pivots.get( j );
                previousPivot = (DNAPivot) _pivots.get( j - 1 );
                nextPivot = (DNAPivot) _pivots.get( j + 1 );
                
                // position
                final double x = currentPivot.getX() + ( currentPivot.getXVelocity() * dt ) + ( 0.5 * currentPivot.getXAcceleration() * dt * dt );
                final double y = currentPivot.getY() + ( currentPivot.getYVelocity() * dt ) + ( 0.5 * currentPivot.getYAcceleration() * dt * dt );
                currentPivot.setPosition( x, y );
                
                // distance to previous and next pivots
                final double dxPrevious = currentPivot.getX() - previousPivot.getX();
                final double dyPrevious = currentPivot.getY() - previousPivot.getY();
                final double dxNext = nextPivot.getX() - currentPivot.getX();
                final double dyNext = nextPivot.getY() - currentPivot.getY();
                final double distanceToPrevious = PolarCartesianConverter.getRadius( dxPrevious, dyPrevious );
                final double distanceToNext = PolarCartesianConverter.getRadius( dxNext, dyNext );
                
                // common terms
                final double termPrevious = 1 - ( springMotionScale * _springLength / distanceToPrevious );
                final double termNext = 1 - ( springMotionScale * _springLength / distanceToNext );
                
                // fluid drag force
                _fluid.getVelocity( _someVector );
                final double xFluidDrag = _fluidDragCoefficient * _someVector.getX();
                final double yFluidDrag = _fluidDragCoefficient * _someVector.getY();
                assert( yFluidDrag == 0 );
                    
                // acceleration
                double springConstant = _springConstant;
                if ( j == 1 ) {
                    // spring constant gets larger as spring gets shorter
                    springConstant = _springConstant * ( _springLength / _closestSpringLength );
                }
                final double xAcceleration = ( springConstant * ( ( dxNext * termNext ) - ( dxPrevious * termPrevious ) ) ) - 
                    ( _dragCoefficient * currentPivot.getXVelocity() ) + xFluidDrag;
                final double yAcceleration = ( springConstant * ( ( dyNext * termNext ) - ( dyPrevious * termPrevious ) ) ) - 
                    ( _dragCoefficient * currentPivot.getYVelocity() ) + yFluidDrag;
                currentPivot.setAcceleration( xAcceleration, yAcceleration );
                
                // velocity
                final double kick = _kickConstant * Math.sqrt( timeScale );
                final double xVelocity = currentPivot.getXVelocity() + ( currentPivot.getXAcceleration() * dt ) + ( kick * ( _kickRandom.nextDouble() - 0.5 ) );
                final double yVelocity = currentPivot.getYVelocity() + ( currentPivot.getYAcceleration() * dt ) + ( kick * ( _kickRandom.nextDouble() - 0.5 ) );
                currentPivot.setVelocity( xVelocity, yVelocity );
            }
        }
        
        notifyObservers( PROPERTY_SHAPE );
    }
    
    /**
     * Makes the segment shorter.
     * This will remove zero or more pivots.
     * 
     * @param amount amount (nm)
     */ 
    private void makeShorter( double amount ) {
        
        if ( amount > _contourLength ) {
            throw new IllegalArgumentException( "amount is > than contour length" );
        }
        
        // remove partial-length spring closest to pin
        double adjustedAmount = amount;
        if ( amount < _closestSpringLength ) {
            _closestSpringLength -= amount;
            adjustedAmount = 0;
        }
        else {
            adjustedAmount -= _closestSpringLength;
            _pivots.remove( 1 );
            _closestSpringLength = _springLength;
        }
        
        if ( adjustedAmount > 0 ) {
            
            // remove full-length springs
            int numberOfPivotsToRemove = (int) ( adjustedAmount / _springLength ) - 1;
            for ( int i = 0; i < numberOfPivotsToRemove; i++ ) {
                _pivots.remove( 1 );
            }

            // adjust length of spring closest to pin
            _closestSpringLength = _springLength - ( adjustedAmount % _springLength );
        }
        
        _contourLength -= amount;
        
        notifyObservers( PROPERTY_SHAPE );
    }
    
    /**
     * Makes the segment longer.
     * This will add zero or more pivots.
     * 
     * @param amount amount (nm)
     */ 
    private void makeLonger( double amount ) {
        
        // adjust length of spring closest to pin
        double adjustedAmount = amount;
        if ( _closestSpringLength != _springLength ) {
            adjustedAmount -= ( _springLength - _closestSpringLength );
        }

        if ( adjustedAmount > 0 ) {
            
            // add full-length springs
            int numberOfPivotsToAdd = (int) ( adjustedAmount / _springLength ) - 1;
            DNAPivot pivot = null;
            for ( int i = 0; i < numberOfPivotsToAdd; i++ ) {
                pivot = new DNAPivot( getPinX(), getPinY() );
                _pivots.add( 1, pivot );
            }

            // add partial-length spring closest to pin
            _closestSpringLength = adjustedAmount % _springLength;
            if ( _closestSpringLength != 0 ) {
                pivot = new DNAPivot( getPinX(), getPinY() );
                _pivots.add( 1, pivot );
            }
            else {
                _closestSpringLength = _springLength;
            }
        }
        
        _contourLength += amount;
        
        notifyObservers( PROPERTY_SHAPE );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the model when something it's observing changes.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                DNAPivot pivot = getBeadPivot();
                pivot.setPosition( _bead.getX(), _bead.getY() );
                if ( !_clock.isRunning() ) {
                    evolve( _clock.getDt() );
                }
                notifyObservers( PROPERTY_SHAPE );
            }
        }
        else if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_TEMPERATURE ) {
                notifyObservers( PROPERTY_FORCE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Evolves the strand each time the simulation clock ticks.
     * 
     * @param clockStep clock step
     */
    public void stepInTime( double clockStep ) {
        evolve( clockStep );
    }
    
}

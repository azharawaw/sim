/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enum.WellType;


/**
 * BSAsymmetricWell is the model of a potential composed on one Asymmetric well.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset
 * <li>width
 * <li>depth
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricWell extends BSAbstractPotential {

    private double _width;
    private double _depth;

    public BSAsymmetricWell() {
        this( BSConstants.DEFAULT_ASYMMETRIC_WIDTH, 
              BSConstants.DEFAULT_ASYMMETRIC_DEPTH, 
              BSConstants.DEFAULT_ASYMMETRIC_OFFSET, 
              BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSAsymmetricWell( double width, double depth, double offset, double center ) {
        super( 1, 0, offset, center );
        setWidth( width );
        setDepth( depth );
    }
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        _width = width;
        notifyObservers();
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        if ( depth > 0 ) {
            throw new IllegalArgumentException( "invalid depth: " + depth );
        }
        _depth = depth;
        notifyObservers();
    }
    
    public WellType getWellType() {
        return WellType.ASYMMETRIC;
    }
    
    public int getStartingIndex() {
        return 1;
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple wells not supported for asymmetric well" );
        }
        else {
            super.setNumberOfWells( numberOfWells );
        }
    }

    //XXX Hack -- creates 20 eigenstates between offset and depth
    public BSEigenstate[] getEigenstates() {
        final int numberOfEigenstates = 20;
        BSEigenstate[] eigenstates = new BSEigenstate[ numberOfEigenstates ];
        final double maxEnergy = getOffset();
        final double minEnergy = getOffset() + getDepth();
        final double deltaEnergy = ( maxEnergy - minEnergy ) / numberOfEigenstates;
        for ( int i = 0; i < eigenstates.length; i++ ) {
            eigenstates[i] = new BSEigenstate( minEnergy + ( i * deltaEnergy ) );
        }
        return eigenstates;
    }
    
    public double solve( double x ) {
        assert( getNumberOfWells() == 1 );
        
        final double offset = getOffset();
        final double c = getCenter();
        final double w = getWidth();
        
        double value = offset;
        if ( Math.abs( x - c ) <= w / 2 ) {
            final double d = Math.abs( getDepth() );
            value = offset - Math.abs( c + w/2 - x ) * d / w;
        }
        return value;
    }
}

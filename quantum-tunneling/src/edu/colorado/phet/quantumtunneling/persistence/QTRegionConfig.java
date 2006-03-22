/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.persistence;

import edu.colorado.phet.quantumtunneling.model.PotentialRegion;


/**
 * QTRegionConfig is a JavaBean-compliant data structure for saving region information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTRegionConfig implements QTSerializable {

    private double _start;
    private double _end;
    private double _energy;

    public QTRegionConfig() {
        this( 0, 0, 0 );
    }

    public QTRegionConfig( double start, double end, double energy ) {
        _start = start;
        _end = end;
        _energy = energy;
    }

    public QTRegionConfig( PotentialRegion region ) {
        this( region.getStart(), region.getEnd(), region.getEnergy() );
    }

    public double getStart() {
        return _start;
    }

    public void setStart( double start ) {
        _start = start;
    }

    public double getEnd() {
        return _end;
    }

    public void setEnd( double end ) {
        _end = end;
    }

    public double getEnergy() {
        return _energy;
    }

    public void setEnergy( double energy ) {
        _energy = energy;
    }

    public PotentialRegion toPotentialRegion() {
        return new PotentialRegion( _start, _end, _energy );
    }
}

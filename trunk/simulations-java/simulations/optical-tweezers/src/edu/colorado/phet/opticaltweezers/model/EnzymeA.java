/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.opticaltweezers.model.IDNASpeedStrategy.TomAbstractDNASpeedStrategy.TomDNASpeedStrategyA;

/**
 * EnzymeA is one type of fictitious enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeA extends AbstractEnzyme {
    
    private static final IDNASpeedStrategy DNA_SPEED_STRATEGY = new TomDNASpeedStrategyA();
    
    public EnzymeA( Point2D position, double outerDiameter, double innerDiameter, 
            DNAStrand dnaStrandBead, DNAStrand dnaStrandFree, Fluid fluid, double maxDt ) {
        super( position, outerDiameter, innerDiameter, dnaStrandBead, dnaStrandFree, fluid, maxDt, DNA_SPEED_STRATEGY );
    }
}

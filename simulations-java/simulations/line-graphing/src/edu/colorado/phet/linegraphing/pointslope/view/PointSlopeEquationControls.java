// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.LineFactory.PointSlopeLineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.view.EquationControls;

/**
 * Control panel for interacting with a line's equation in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationControls extends EquationControls<PointSlopeLine> {

    /**
     * Constructor
     * @param interactiveLine the line that can be manipulated by the user
     * @param savedLines lines that have been saved by the user
     * @param maximized is the control panel maximized (true) or minimized (false)?
     * @param linesVisible are lines visible on the graph?
     * @param riseRange
     * @param runRange
     * @param x1Range
     * @param y1Range
     */
    public PointSlopeEquationControls( final Property<PointSlopeLine> interactiveLine,
                                       final ObservableList<PointSlopeLine> savedLines,
                                       final Property<Boolean> maximized,
                                       final Property<Boolean> linesVisible,
                                       Property<DoubleRange> riseRange,
                                       Property<DoubleRange> runRange,
                                       Property<DoubleRange> x1Range,
                                       Property<DoubleRange> y1Range ) {
        super( MessageFormat.format( "({0} - {1}) = {2}({3} - {4})", /* (y - y1) = m(x - x1) */
                                     Strings.SYMBOL_Y, Strings.SYMBOL_Y1, Strings.SYMBOL_SLOPE, Strings.SYMBOL_X, Strings.SYMBOL_X1 ),
               interactiveLine, savedLines, maximized, linesVisible,
               new PointSlopeInteractiveEquationNode( interactiveLine, riseRange, runRange, x1Range, y1Range ),
               new PointSlopeLineFactory() );
    }
}

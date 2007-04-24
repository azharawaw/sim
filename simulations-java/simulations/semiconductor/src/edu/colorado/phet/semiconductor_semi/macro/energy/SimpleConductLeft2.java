
package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.semiconductor_semi.macro.doping.DopantType;
import edu.colorado.phet.semiconductor_semi.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:24:11 PM
 *
 */
public class SimpleConductLeft2 extends DefaultStateDiagram {
    private ExciteForConduction leftExcite;
    private ExciteForConduction rightExcite;

    public SimpleConductLeft2( EnergySection energySection, DopantType dopantType ) {
        super( energySection );
        leftExcite = excite( dopantType.getDopingBand(), 0, dopantType.getNumFilledLevels() - 1 );
        rightExcite = excite( dopantType.getDopingBand(), 1, dopantType.getNumFilledLevels() - 1 );
        exitLeft( leftExcite.getLeftCell() );
        enter( rightExcite.getRightCell() );
        propagateLeft( rightExcite.getRightCell() );
    }

    public ExciteForConduction getLeftExcite() {
        return leftExcite;
    }

    public ExciteForConduction getRightExcite() {
        return rightExcite;
    }
}

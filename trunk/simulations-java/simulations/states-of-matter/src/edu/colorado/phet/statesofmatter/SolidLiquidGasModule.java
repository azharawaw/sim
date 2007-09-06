package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.MultipleParticleSimulationPanel;

public class SolidLiquidGasModule extends Module {
    private MultipleParticleModel model=new MultipleParticleModel();
    protected SolidLiquidGasModule() {
        super("Solid, Liquid, Gas", new ConstantDtClock(30, 1.0));

        setSimulationPanel(new MultipleParticleSimulationPanel(model));
    }
}

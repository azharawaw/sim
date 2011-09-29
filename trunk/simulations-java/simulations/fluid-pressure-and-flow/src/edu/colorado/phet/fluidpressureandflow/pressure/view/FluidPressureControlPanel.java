// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.RadioButton;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;

/**
 * @author Sam Reid
 */
public class FluidPressureControlPanel extends VerticalLayoutPanel {
    public static final Color BACKGROUND = new Color( 239, 250, 125 );
    public static final Color FOREGROUND = Color.black;

    public FluidPressureControlPanel( final FluidPressureAndFlowModule<FluidPressureModel> module ) {
        super();
        addControlFullWidth( new CheckBox( RULER, module.rulerVisible ) );

        //Units control panel that allows choice between atmospheres, english and metric
        final Property<UnitSet> units = module.model.units;
        addControlFullWidth( new EnglishMetricControlPanel( new RadioButton<UnitSet>( ATMOSPHERES, units, UnitSet.ATMOSPHERES ),
                                                            new RadioButton<UnitSet>( METRIC, units, UnitSet.METRIC ),
                                                            new RadioButton<UnitSet>( ENGLISH, units, UnitSet.ENGLISH ) ) );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }
}
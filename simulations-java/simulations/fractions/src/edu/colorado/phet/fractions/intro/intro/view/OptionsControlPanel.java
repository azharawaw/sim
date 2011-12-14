// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class OptionsControlPanel extends ControlPanelNode {
    public OptionsControlPanel( SettableProperty<Visualization> visualization ) {
        super( new VBox( new PSwing( new PropertyRadioButton<Visualization>( "None", visualization, Visualization.NONE ) {{setFont( FractionsIntroCanvas.CONTROL_FONT );}} ),
                         new PSwing( new PropertyRadioButton<Visualization>( "Improper", visualization, Visualization.FRACTION ) {{setFont( FractionsIntroCanvas.CONTROL_FONT );}} ),
                         new PSwing( new PropertyRadioButton<Visualization>( "Mixed", visualization, Visualization.MIXED ) {{setFont( FractionsIntroCanvas.CONTROL_FONT );}} ) ) );
    }

}

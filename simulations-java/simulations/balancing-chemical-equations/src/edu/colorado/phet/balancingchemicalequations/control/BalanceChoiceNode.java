// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.GridBagConstraints;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Panel of radio buttons for selecting the visual representation for "balanced".
 * "Balanced" is represented as either a bar chart, or a balance scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceChoiceNode extends PhetPNode {

    public static enum BalanceChoice { BAR_CHARTS, BALANCE_SCALES };

    public BalanceChoiceNode( Property<BalanceChoice> balanceChoiceProperty ) {
        addChild( new PSwing( new BalanceChoicePanel( balanceChoiceProperty ) ) );
    }

    /*
     * Swing component.
     */
    private static class BalanceChoicePanel extends GridPanel {
        public BalanceChoicePanel( Property<BalanceChoice> balanceChoiceProperty ) {
            setOpaque( false );
            setAnchor( Anchor.WEST );
            setGridX( GridBagConstraints.RELATIVE ); // horizontal layout
            setGridY( 0 ); // horizontal layout
            add( new PropertyRadioButton<BalanceChoice>( BCEStrings.BAR_CHARTS, balanceChoiceProperty, BalanceChoice.BAR_CHARTS ) );
            add( new PropertyRadioButton<BalanceChoice>( BCEStrings.BALANCE_SCALES, balanceChoiceProperty, BalanceChoice.BALANCE_SCALES ) );
        }
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * A kit the user can choose from, for showing the appropriate bar charts + controls
 *
 * @author Sam Reid
 */
public class Kit {

    //Bars to be shown in the concentration bar chart
    public final BarItem[] barItems;

    //Control node to be shown for removing the solutes when in this kit
    public final PNode removeSoluteControl;

    public Kit( final RemoveSoluteButtonNode[] removeSoluteButtonNodes, BarItem... barItems ) {
        removeSoluteControl = new PNode() {{
            for ( RemoveSoluteButtonNode removeSoluteButtonNode : removeSoluteButtonNodes ) {
                addChild( removeSoluteButtonNode );
            }
        }};
        this.barItems = barItems;
    }
}
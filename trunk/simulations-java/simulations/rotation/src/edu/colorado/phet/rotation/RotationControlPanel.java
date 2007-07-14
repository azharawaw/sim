package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.controls.SymbolKeyButton;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationBody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:51:51 AM
 */

public class RotationControlPanel extends JPanel {
    public RotationControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, VectorViewModel vectorViewModel, JFrame parentFrame, final RotationBody beetle) {
        super( new GridBagLayout() );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        SymbolKeyButton symbolKey = new SymbolKeyButton( parentFrame );
        ShowVectorsControl showVectorsControl = new ShowVectorsControl( vectorViewModel );

        VerticalLayoutPanel box = new VerticalLayoutPanel( );
        RulerButton rulerButton = new RulerButton( rulerNode );
        box.add( symbolKey );

        final JCheckBox beetleGraph = new JCheckBox( "Show Beetle Graph", true );
        beetleGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beetle.setDisplayGraph(beetleGraph.isSelected());
            }
        } );
        box.add( beetleGraph );

        add( graphSelectionControl, getConstraints( 0, 0 ) );
        add( box, getConstraints( 2, 0 ) );
        add( rulerButton, getConstraints( 2, 1 ) );
        add( showVectorsControl, getConstraints( 0, 1 ) );
    }

    private GridBagConstraints getConstraints( int gridX, int gridY ) {
        return new GridBagConstraints( gridX, gridY, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }
}

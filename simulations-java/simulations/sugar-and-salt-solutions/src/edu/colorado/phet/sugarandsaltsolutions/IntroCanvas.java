// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the introductory (first) tab in the Sugar and Salt Solutions Sim
 *
 * @author Sam Reid
 */
public class IntroCanvas extends PhetPCanvas {
    private PNode rootNode;

    public IntroCanvas() {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        //Width of the stage
        final int stageWidth = 1000;

        //Right now this is just the ratio of width/height
        double modelHeight = 0.7;
        double modelWidth = 1.04;

        PDimension stageSize = new PDimension( stageWidth, stageWidth * modelHeight / modelWidth );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, stageSize ) );

        //Allows the user to select a solute
        final ControlPanelNode soluteControlPanelNode = new ControlPanelNode( new VBox() {{
            addChild( new PText( "Solute" ) {{setFont( new PhetFont( 14, true ) );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 10, 10 ), new Color( 0, 0, 0, 0 ) ) );//spacer
            addChild( new PSwing( new JRadioButton( "Salt" ) ) );
            addChild( new PSwing( new JRadioButton( "Sugar" ) ) );
        }} );
        addChild( soluteControlPanelNode );

        //Tools for the user to use
        ControlPanelNode toolsControlPanelNode = new ControlPanelNode( new VBox() {{
            addChild( new PText( "Tools" ) {{setFont( new PhetFont( 14, true ) );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 10, 10 ), new Color( 0, 0, 0, 0 ) ) );//spacer
            addChild( new PSwing( new JCheckBox( "Show concentration" ) ) );
            addChild( new PSwing( new JCheckBox( "Show values" ) ) );
            addChild( new PSwing( new JCheckBox( "Measure conductivity" ) ) );
            addChild( new PSwing( new JCheckBox( "Evaporate water" ) ) );
        }} ) {{
            setOffset( 0, soluteControlPanelNode.getFullBounds().getMaxY() + 10 );
        }};
        addChild( toolsControlPanelNode );

        //Debug for showing stage
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, stageSize.getWidth(), stageSize.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}

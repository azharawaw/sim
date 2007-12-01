package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Dec 1, 2007 at 7:42:12 AM
 */
public class AbstractIntroSimulationPanel extends PhetPCanvas {
    private RotationPlayAreaNode playAreaNode;
    private PNode introSimControlPanelPSwing;
    private RotationPlatform rotationPlatform;

    public AbstractIntroSimulationPanel() {
    }

    protected void init( RotationPlayAreaNode playAreaNode, PNode introSimControlPanelPSwing, RotationPlatform rotationPlatform ) {
        this.playAreaNode = playAreaNode;
        this.introSimControlPanelPSwing = introSimControlPanelPSwing;
        this.rotationPlatform = rotationPlatform;
    }


    protected void updateLayout() {
        int padX = 50;
        int padY = 50;

        playAreaNode.setScale( 1.0 );

        double sx = ( getWidth() - padX * 2 ) / ( playAreaNode.getPlatformNode().getFullBounds().getWidth() );
        double sy = ( getHeight() - padY * 2 ) / ( playAreaNode.getPlatformNode().getFullBounds().getHeight() );
        double scale = Math.min( sx, sy );
        if ( scale > 0 ) {
            playAreaNode.scale( scale );
        }
        playAreaNode.setOffset( getWidth() / 2, scale * getRotationPlatform().getRadius() + padY / 2 );
        introSimControlPanelPSwing.setOffset( 0, getHeight() - introSimControlPanelPSwing.getFullBounds().getHeight() );
    }

    protected RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new JPanel();
    }
}

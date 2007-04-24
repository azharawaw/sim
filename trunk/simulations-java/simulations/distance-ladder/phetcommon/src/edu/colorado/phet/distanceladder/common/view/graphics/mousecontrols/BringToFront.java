/*, 2003.*/
package edu.colorado.phet.distanceladder.common.view.graphics.mousecontrols;

import edu.colorado.phet.distanceladder.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.distanceladder.common.view.graphics.Graphic;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 1:07:02 AM
 *
 */
public class BringToFront implements MouseInputListener {
    CompositeInteractiveGraphic graphicTree;
    Graphic target;

    public BringToFront( CompositeInteractiveGraphic graphicTree, Graphic target ) {
        this.graphicTree = graphicTree;
        this.target = target;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        graphicTree.moveToTop( target );
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }


}

/*, 2003.*/
package edu.colorado.phet.common_1200.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:53:40 AM
 *
 */

/**
 * @deprecated
 */
public class HandCursorControl implements MouseInputListener {
    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
        e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent e ) {
        e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }

}

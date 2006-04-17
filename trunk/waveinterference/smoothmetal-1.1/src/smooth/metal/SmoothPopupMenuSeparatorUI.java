package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalPopupMenuSeparatorUI;
import java.awt.*;

public class SmoothPopupMenuSeparatorUI extends MetalPopupMenuSeparatorUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothPopupMenuSeparatorUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}

package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsDesktopIconUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothDesktopIconUI extends WindowsDesktopIconUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothDesktopIconUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}

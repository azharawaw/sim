package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsSplitPaneUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothSplitPaneUI extends WindowsSplitPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothSplitPaneUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}

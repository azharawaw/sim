/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14677 $
 * Date modified : $Date:2007-04-17 03:40:29 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.phetcommon.tests.utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 6:56:12 PM
 *
 */

public class ImageDebugFrame extends JFrame {
    public ImageDebugFrame( Image im ) {
        setContentPane( new JLabel( new ImageIcon( im ) ) );
        pack();
    }
}
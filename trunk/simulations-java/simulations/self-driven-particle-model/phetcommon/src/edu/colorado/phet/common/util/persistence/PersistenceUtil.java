/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/persistence/PersistenceUtil.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util.persistence;

import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PersistenceUtil
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class PersistenceUtil {

    /**
     * Adds Save and Restore items to the File menu of a PhetApplication
     *
     * @param application
     */
    public static void addMenuItems( PhetApplication application ) {

        JMenuItem mi2 = new JMenuItem( "Restore state" );
        mi2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().getModuleManager().restoreState( "/temp/ttt.xml" );
            }
        } );
        application.getPhetFrame().addFileMenuItem( mi2 );

        JMenuItem mi = new JMenuItem( "Save state" );
        mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().getModuleManager().saveState( "/temp/ttt.xml" );
            }
        } );
        application.getPhetFrame().addFileMenuItem( mi );
        application.getPhetFrame().addFileMenuSeparatorAfter( mi2 );
    }
}

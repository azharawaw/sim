// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJMenu;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ParamKeys.color;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ParamValues.black;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.ParamValues.white;

/**
 * Displays a "Teacher" menu that allows the user to select between normal colors and a "White Background" mode
 */
public class TeachersMenu extends SimSharingJMenu {
    public TeachersMenu() {
        super( SimSharingConstants.User.UserComponents.teacherMenu, PhetCommonResources.getString( "Common.TeacherMenu" ) );
        setMnemonic( PhetCommonResources.getChar( "Common.TeacherMenu.mnemonic", 'T' ) );
        add( new JCheckBoxMenuItem( PhetCommonResources.getString( "Common.WhiteBackground" ) ) {{
            setSelected( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {

                    SimSharingManager.sendUserEvent( MoleculeShapesSimSharing.Components.backgroundColor, SimSharingConstants.User.UserActions.changed, param( color, isSelected() ? white.toString() : black.toString() ) );

                    if ( isSelected() ) {
                        MoleculeShapesColor.PROJECTOR.apply( MoleculeShapesColor.handler );
                    }
                    else {
                        MoleculeShapesColor.DEFAULT.apply( MoleculeShapesColor.handler );
                    }
                }
            } );
            setMnemonic( PhetCommonResources.getChar( "Common.WhiteBackground.mnemonic", 'W' ) );
        }} );
    }
}

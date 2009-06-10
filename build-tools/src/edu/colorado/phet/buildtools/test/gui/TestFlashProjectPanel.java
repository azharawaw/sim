package edu.colorado.phet.buildtools.test.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.VersionIncrement;
import edu.colorado.phet.buildtools.flash.FlashBuildCommand;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class TestFlashProjectPanel extends JPanel {

    private File trunk;

    private FlashSimulationProject project;
    private JCheckBox cleanButton;
    private JCheckBox rebuildHTMLButton;
    private JCheckBox rebuildSWFButton;
    private JCheckBox rebuildJARsButton;
    private TestLocaleListPanel localeList;
    private JRadioButton keepOpenButton;
    private JRadioButton closeFLAButton;
    private JRadioButton closeIDEButton;
    private JRadioButton incrementMinor;
    private JRadioButton incrementMajor;

    public TestFlashProjectPanel( File trunk, FlashSimulationProject project ) {
        super( new BorderLayout() );

        this.trunk = trunk;
        this.project = project;

        JLabel title = new JLabel( project.getName() + " : " + project.getFullVersionString() );
        title.setHorizontalAlignment( SwingConstants.CENTER );
        add( title, BorderLayout.NORTH );

        localeList = new TestLocaleListPanel( project.getLocales() );
        add( localeList, BorderLayout.WEST );
        add( new TestChangesPanel( project ), BorderLayout.CENTER );

        JPanel controlPanel = new JPanel();

        JPanel idePanel = new VerticalLayoutPanel();
        idePanel.setBorder( BorderFactory.createTitledBorder( "IDE options" ) );

        keepOpenButton = new JRadioButton( "Keep open" );
        idePanel.add( keepOpenButton );
        closeFLAButton = new JRadioButton( "Close FLA" );
        idePanel.add( closeFLAButton );
        closeIDEButton = new JRadioButton( "Close IDE" );
        idePanel.add( closeIDEButton );
        ButtonGroup ideGroup = new ButtonGroup();
        ideGroup.add( keepOpenButton );
        ideGroup.add( closeFLAButton );
        ideGroup.add( closeIDEButton );
        closeFLAButton.setSelected( true );

        controlPanel.add( idePanel );

        JPanel testPanel = new VerticalLayoutPanel();
        testPanel.setBorder( BorderFactory.createTitledBorder( "Testing" ) );

        cleanButton = new JCheckBox( "Clean deploy dir" );
        cleanButton.setSelected( true );
        testPanel.add( cleanButton );

        rebuildHTMLButton = new JCheckBox( "Rebuild HTML" );
        rebuildHTMLButton.setSelected( true );
        testPanel.add( rebuildHTMLButton );

        rebuildSWFButton = new JCheckBox( "Rebuild SWF" );
        rebuildSWFButton.setSelected( true );
        testPanel.add( rebuildSWFButton );

        rebuildJARsButton = new JCheckBox( "Rebuild JARs" );
        rebuildJARsButton.setSelected( false );
        testPanel.add( rebuildJARsButton );

        JButton testButton = new JButton( "Test" );
        testPanel.add( testButton );

        controlPanel.add( testPanel );

        JPanel deployDevPanel = new VerticalLayoutPanel();
        deployDevPanel.setBorder( BorderFactory.createTitledBorder( "Dev Deploy" ) );
        JButton deployDevButton = new JButton( "Deploy Dev" );
        deployDevPanel.add( deployDevButton );

        controlPanel.add( deployDevPanel );

        JPanel deployProdPanel = new VerticalLayoutPanel();
        deployProdPanel.setBorder( BorderFactory.createTitledBorder( "Production Deploy" ) );
        incrementMinor = new JRadioButton( "Increment minor version" );
        incrementMajor = new JRadioButton( "Increment major version " );
        ButtonGroup incrementGroup = new ButtonGroup();
        incrementGroup.add( incrementMinor );
        incrementGroup.add( incrementMajor );
        deployProdPanel.add( incrementMinor );
        deployProdPanel.add( incrementMajor );
        incrementMinor.setSelected( true );
        JButton deployProdButton = new JButton( "Deploy Dev & Prod" );
        deployProdPanel.add( deployProdButton );

        controlPanel.add( deployProdPanel );

        add( controlPanel, BorderLayout.SOUTH );


        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doTest();
            }
        } );

        deployDevButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployDev();
            }
        } );

        deployProdButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployProd();
            }
        } );
    }

    private void doTest() {
        try {
            FlashBuildCommand.closeFLA = !keepOpenButton.isSelected();
            FlashBuildCommand.closeIDE = closeIDEButton.isSelected();
            boolean success = project.testBuild( cleanButton.isSelected(), rebuildHTMLButton.isSelected(), rebuildSWFButton.isSelected(), rebuildJARsButton.isSelected() );
            if ( success ) {
                project.runSim( localeList.getSelectedLocale(), project.getName() );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private void doDeployDev() {
        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

        new BuildScript( trunk, project ).deployDev( buildLocalProperties.getDevAuthenticationInfo(), false );
    }

    private void doDeployProd() {
        boolean confirm = TestGUI.confirmProdDeploy( project, PhetServer.PRODUCTION );

        if ( !confirm ) {
            System.out.println( "Cancelled" );
            return;
        }

        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

        VersionIncrement versionIncrement = null;
        if ( incrementMinor.isSelected() ) {
            versionIncrement = new VersionIncrement.UpdateProdMinor();
        }
        else if ( incrementMajor.isSelected() ) {
            versionIncrement = new VersionIncrement.UpdateProdMajor();
        }

        new BuildScript( trunk, project ).deployProd( buildLocalProperties.getDevAuthenticationInfo(), buildLocalProperties.getProdAuthenticationInfo(), versionIncrement );
    }
}

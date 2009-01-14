
package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.preferences.TrackingDetailsDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.PhetExit;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.HTMLEditorPane;

/**
 * Manages policy related to PhET's software and privacy agreements.
 */
public class SoftwareAgreementManager {

    private static final int SOFTWARE_AGREEMENT_VERSION = 1;
    
    /* not intended for instantiation */
    private SoftwareAgreementManager() {}

    /**
     * Ensures that the user has accepted the agreements that pertain to this software.
     */
    public static void validate( ITrackingInfo trackingInfo ) {
        int acceptedVersion = PhetPreferences.getInstance().getSoftwareAgreementVersion();
        if ( acceptedVersion < SOFTWARE_AGREEMENT_VERSION ) {
            negotiate( trackingInfo );
        }
    }
    
    /*
     * Negotiates the agreement with the user.
     */
    private static void negotiate( ITrackingInfo trackingInfo ) {
        new SoftwareAgreementDialog( trackingInfo ).setVisible( true );
    }
    
    /*
     * Dialog that displays the software agreement and provides options to accept or decline.
     */
    private static class SoftwareAgreementDialog extends JDialog {

        private static final String TITLE = PhetCommonResources.getString( "Common.softwareAgreement.title" );
        private static final String ACCEPT_BUTTON = PhetCommonResources.getString( "Common.softwareAgreement.accept" );
        private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.softwareAgreement.cancel" );
        
        public SoftwareAgreementDialog( ITrackingInfo trackingInfo ) {
            super();
            setTitle( TITLE );
            setModal( true );
            setResizable( false );

            JComponent message = createMessagePanel( trackingInfo );
            JComponent buttonPanel = createButtonPanel();

            JPanel panel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            panel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( message, row++, column );
            layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( buttonPanel, row++, column, GridBagConstraints.HORIZONTAL );

            // close button in window dressing is identical to Cancel button
            addWindowListener( new WindowAdapter() {

                public void windowClosing( WindowEvent e ) {
                    cancel();
                }
            } );

            setContentPane( panel );
            pack();
            SwingUtils.centerWindowOnScreen( this );
        }

        private JComponent createMessagePanel( ITrackingInfo trackingInfo ) {
            JComponent htmlPane = new MessagePane( trackingInfo );
            JPanel panel = new JPanel();
            panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
            panel.add( htmlPane );
            return panel;
        }

        private JComponent createButtonPanel() {

            JButton acceptButton = new JButton( ACCEPT_BUTTON );
            acceptButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    accept();
                }
            } );

            JButton cancelButton = new JButton( CANCEL_BUTTON );
            cancelButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    cancel();
                }
            } );
            
            // layout
            JPanel panel = new JPanel( new FlowLayout() );
            panel.add( acceptButton );
            panel.add( cancelButton );
            return panel;
        }

        /*
         * If the agreement is accepted, write the agreement version number to the preferences file.
         */
        private void accept() {
            PhetPreferences.getInstance().setSoftwareAgreementVersion( SOFTWARE_AGREEMENT_VERSION );
            dispose();
        }

        /*
         * If the agreement is declined, exit, do not allow the software to run.
         */
        private void cancel() {
            PhetExit.exit();
        }
    }
    
    /*
     * This is an HTML editor pane interactive hyperlinks.
     * But instead of opening a web browser, it opens a Swing dialog.
     */
    private static class MessagePane extends HTMLEditorPane {
       
        private static final String MESSAGE_PATTERN = PhetCommonResources.getString( "Common.softwareAgreement.message" );
        private static final String INFORMATION_LINK = "information";
        private static final String AGREEMENTS_LINK = "agreements";
        
        public MessagePane( final ITrackingInfo trackingInfo ) {
            super( "" );
            
            // insert our own hyperlink descriptions into the message
            Object[] args = { INFORMATION_LINK, AGREEMENTS_LINK };
            String htmlFragment = MessageFormat.format( MESSAGE_PATTERN, args );
            System.out.println( "htmlFragment" + htmlFragment );//XXX
            setText( HTMLUtils.createStyledHTMLFromFragment( htmlFragment ) );
            
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        Window owner = SwingUtilities.getWindowAncestor( MessagePane.this );
                        if ( e.getDescription().equals( INFORMATION_LINK ) ) {
                            showTrackingDetails( owner, trackingInfo );
                        }
                        else if ( e.getDescription().equals( AGREEMENTS_LINK ) ) {
                            showAgreements( owner );
                        }
                        else {
                            System.err.println( "SoftwareAgreementManager.MessagePane.hyperlinkUpdate: unsupported hyperlink, description=" + e.getDescription() );
                        }
                    }
                }
            } );
        }
        
        private static void showTrackingDetails( Window owner, ITrackingInfo trackingInfo ) {
            if ( owner instanceof Frame ) {
                new TrackingDetailsDialog( (Frame) owner, trackingInfo ).setVisible( true );
            }
            else if ( owner instanceof Dialog ) {
                new TrackingDetailsDialog( (Dialog) owner, trackingInfo ).setVisible( true );
            }
        }
        
        private static void showAgreements( Window owner ) {
            //TODO: read agreements, display in a dialog with a scrollpane and Close button
            JOptionPane.showMessageDialog( owner, "agreements go here", "Software & Privacy Agreements", JOptionPane.PLAIN_MESSAGE );
        }
    }

    public static void main( String[] args ) {
        PhetApplicationConfig config = new PhetApplicationConfig( args, "balloons" );
        new SoftwareAgreementDialog( config ).setVisible( true );
        System.out.println( "continuing" );
    }
}

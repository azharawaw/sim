/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.CO;
import edu.colorado.phet.greenhouse.model.CO2;
import edu.colorado.phet.greenhouse.model.H2O;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.colorado.phet.greenhouse.model.N2;
import edu.colorado.phet.greenhouse.model.NO2;
import edu.colorado.phet.greenhouse.model.O2;
import edu.colorado.phet.greenhouse.model.O3;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel.PhotonTarget;
import edu.colorado.phet.greenhouse.view.MoleculeNode;

/**
 * Control panel for the Photon Absorption tab of this application.
 *
 * @author John Blanco
 */
public class MoleculesAndLightControlPanel extends ControlPanel {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // Font to use on this panel.
    private static final Font LABEL_FONT = new PhetFont( 14 );

    // Model view transform used for creating images of the various molecules.
    // This is basically a null transform except that it flips the Y axis so
    // that molecules are oriented the same as in the play area.
    private static final ModelViewTransform2D MVT =
            new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point( 0, 0 ), 1, true );

    // Scaling factor for the molecule images, determined empirically.
    private static final double MOLECULE_SCALING_FACTOR = 0.13;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final PhotonAbsorptionModel model;

//    private final RadioButtonWithIconPanel coSelector;
//    private final RadioButtonWithIconPanel n2Selector;
//    private final RadioButtonWithIconPanel o2Selector;
//    private final RadioButtonWithIconPanel co2Selector;
//    private final RadioButtonWithIconPanel h2oSelector;
//    private final RadioButtonWithIconPanel no2Selector;
//    private final RadioButtonWithIconPanel o3Selector;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public MoleculesAndLightControlPanel( PiccoloModule module, final PhotonAbsorptionModel model ) {

        this.model = model;

        // Set the control panel's minimum width.
        int minimumWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        PhetTitledPanel moleculeSelectionPanel = new PhetTitledPanel( GreenhouseResources.getString( "ControlPanel.Molecule" ) );
        moleculeSelectionPanel.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        addControlFullWidth( moleculeSelectionPanel );

        // Add buttons for selecting the molecule.
        /*
        coSelector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.CO" ),
                GreenhouseResources.getString( "ControlPanel.CarbonMonoxide" ),
                createMoleculeImage( new CO(), MOLECULE_SCALING_FACTOR ),
                PhotonTarget.SINGLE_CO_MOLECULE,
                1 );
        coSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( coSelector, constraints );

        n2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.N2" ),
                GreenhouseResources.getString( "ControlPanel.Nitrogen" ),
                createMoleculeImage( new N2() ), PhotonTarget.SINGLE_N2_MOLECULE, MOLECULE_SCALING_FACTOR );
        n2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( n2Selector, constraints );

        o2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.O2" ),
                GreenhouseResources.getString( "ControlPanel.Oxygen" ),
                createMoleculeImage( new O2() ), PhotonTarget.SINGLE_O2_MOLECULE, MOLECULE_SCALING_FACTOR );
        o2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( o2Selector, constraints );

        co2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.CO2" ),
                GreenhouseResources.getString( "ControlPanel.CarbonDioxide" ),
                createMoleculeImage( new CO2() ), PhotonTarget.SINGLE_CO2_MOLECULE, MOLECULE_SCALING_FACTOR );
        co2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( co2Selector, constraints );

        h2oSelector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.H2O" ),
                GreenhouseResources.getString( "ControlPanel.Water" ),
                createMoleculeImage( new H2O() ), PhotonTarget.SINGLE_H2O_MOLECULE, MOLECULE_SCALING_FACTOR );
        h2oSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( h2oSelector, constraints );

        no2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.NO2" ),
                GreenhouseResources.getString( "ControlPanel.NitrogenDioxide" ),
                createMoleculeImage( new NO2() ), PhotonTarget.SINGLE_NO2_MOLECULE, MOLECULE_SCALING_FACTOR );
        no2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( no2Selector, constraints );

        o3Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "ControlPanel.O3" ),
                GreenhouseResources.getString( "ControlPanel.Ozone" ),
                createMoleculeImage( new O3() ), PhotonTarget.SINGLE_O3_MOLECULE, MOLECULE_SCALING_FACTOR );
        o3Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( o3Selector, constraints );
        */

        int interSelectorSpacing = 15;
        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Carbon Monoxide<br>CO</html>",
                createMoleculeImage( new CO(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_CO_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Nitrogen<br>N<sub>2</sub></html>",
                createMoleculeImage( new N2(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_N2_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Oxygen<br>O<sub>2</sub></html>",
                createMoleculeImage( new O2(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_O2_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Carbon Dioxide<br>CO<sub>2</sub></html>",
                createMoleculeImage( new CO2(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_CO2_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Water<br>H<sub>2</sub>O</html>",
                createMoleculeImage( new H2O(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_H2O_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Nitrogen Dioxide<br>NO<sub>2</sub></html>",
                createMoleculeImage( new NO2(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_NO2_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        moleculeSelectionPanel.add( new SelectionPanelWithImage( "<html>Ozone<br>O<sub>3</sub></html>",
                createMoleculeImage( new O3(), MOLECULE_SCALING_FACTOR ), PhotonTarget.SINGLE_O3_MOLECULE ),
                constraints );

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );


        // Put all the buttons in a button group.
        ButtonGroup buttonGroup = new ButtonGroup();
//        buttonGroup.add( coSelector.getButton() );
//        buttonGroup.add( n2Selector.getButton() );
//        buttonGroup.add( o2Selector.getButton() );
//        buttonGroup.add( co2Selector.getButton() );
//        buttonGroup.add( h2oSelector.getButton() );
//        buttonGroup.add( no2Selector.getButton() );
//        buttonGroup.add( o3Selector.getButton() );

        // Add the reset all button.
        addControlFullWidth( createVerticalSpacingPanel( 5 ) );
        addResetAllButton( module );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Creates a selector panel with a radio button and an icon and "attaches"
     * it to the model in the sense that it hooks it up to set the appropriate
     * value when pressed and updates its state when the model sends
     * notifications of changes.  This is a convenience method that exists in
     * order to avoid duplication of code.
     * @param toolTipText TODO
     */
    private RadioButtonWithIconPanel createAndAttachSelectorPanel( String text, String toolTipText,
            BufferedImage image, final PhotonTarget photonTarget, double imageScaleFactor ) {

        // Create the panel.
        final RadioButtonWithIconPanel panel = new RadioButtonWithIconPanel( text, toolTipText, image, imageScaleFactor );

        // Listen to the button so that the specified value can be set in the
        // model when the button is pressed.
        panel.getButton().addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                if ( panel.getButton().isSelected() ) {
                    model.setPhotonTarget( photonTarget );
                }
                }
                } );

        // Listen to the model so that the button state can be updated when
        // the model setting changes.
        model.addListener( new PhotonAbsorptionModel.Adapter() {
            @Override
            public void photonTargetChanged() {
                // The logic in these statements is a little hard to follow,
                // but the basic idea is that if the state of the model
                // doesn't match that of the button, update the button,
                // otherwise leave the button alone.  This prevents a bunch
                // of useless notifications from going to the model.
                if ( ( model.getPhotonTarget() == photonTarget ) != panel.getButton().isSelected() ) {
                    panel.getButton().setSelected( model.getPhotonTarget() == photonTarget );
                }
            }
        } );
        return panel;
    }

    private JPanel createVerticalSpacingPanel( int space ) {
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }

    /**
     * Creates a buffered image of a molecule given an instance of a Molecule
     * object.
     *
     * @param molecule
     * @return
     */
    private BufferedImage createMoleculeImage( Molecule molecule, double scaleFactor ) {
        BufferedImage unscaledMoleculeImage = new MoleculeNode( molecule, MVT ).getImage();
        return BufferedImageUtils.multiScale( unscaledMoleculeImage, scaleFactor );
    }

    /**
     * Class that defines a panel that has a radio button on the left and an
     * image on the right.  This is designed to be fairly general, and
     * therefore potentially reusable in other sims.
     *
     * @author John Blanco
     */
    private class SelectionPanelWithImage extends JPanel {

        private final GridBagConstraints selectorButtonConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        private final GridBagConstraints imageConstraints = new GridBagConstraints( 1, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );

        private final JRadioButton radioButton = new JRadioButton();

        /**
         * Constructor.
         */
        public SelectionPanelWithImage( String moleculeNameAndSymbol, BufferedImage image, final PhotonTarget photonTarget ){
            setLayout( new GridBagLayout() );
            radioButton.setText( moleculeNameAndSymbol );
            radioButton.setFont( new PhetFont(14) );
            add( radioButton, selectorButtonConstraints );
            ImageIcon imageIcon = new ImageIcon( image );
            JLabel icon = new JLabel( imageIcon );
            // Note: Had to put the icon on a panel of its own in order to get
            // it to be all the way to the right.  Not sure why this was
            // necessary, but we're talking about Java layout here, so it's
            // hard to say.
            JPanel iconPanel = new JPanel();
            iconPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
            iconPanel.add( icon );
            add( iconPanel, imageConstraints );
        }

        protected JRadioButton getRadioButton() {
            return radioButton;
        }
    }
}
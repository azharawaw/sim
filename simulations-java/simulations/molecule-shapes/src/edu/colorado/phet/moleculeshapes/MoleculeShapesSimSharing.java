// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class MoleculeShapesSimSharing {

    public static enum UserComponents implements IUserComponent {
        backgroundColor, bond, draggingState, mouseMiddleButton, moleculeShapesTab, realMoleculesTab, moleculeComboBox,
        showLonePairsCheckBox, showAllLonePairsCheckBox, showBondAnglesCheckBox,
        moleculeGeometryCheckBox, electronGeometryCheckBox,
        realViewCheckBox, modelViewCheckBox,

        // these are actually used, just accessed by UserComponents.valueOf()
        CO2, H2O, SO2, XeF2, BF3, ClF3, NH3, CH4, SF4, XeF4, BrF5, PCl5, SF6
    }

    public static enum Actions implements IUserAction {
        created, removed
    }

    public static enum ParamKeys implements IParameterKey {
        bondOrder, color, dragging, dragMode, electronGeometry, moleculeGeometry, removedPair
    }

    public static enum ParamValues {
        black, white
    }

    public static enum ModelObjects implements IModelComponent {
        molecule
    }

    public static enum ModelActions implements IModelAction {
        bondsChanged
    }
}
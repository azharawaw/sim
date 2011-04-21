// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.module.AbstractBuildAMoleculeModule;
import edu.colorado.phet.buildamolecule.module.CollectMultipleModule;
import edu.colorado.phet.buildamolecule.module.LargerMoleculesModule;
import edu.colorado.phet.buildamolecule.module.MakeMoleculeModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/*
TODO:

Send out TODO list

* Move kit label/arrows to right of the kit area, move "reset kit" down
* Just show molecule name above, not the formula (for now)

Arrow and message that "you can put this in the collection box" at the start. Points to correct collection box.
Drop-down menu. Options: "Show in 3D", possibly show chemical formula, possibly option to move to collection box
Close relevant 3d views on: kit switch, break of molecule, or any other way the molecule isn't visible.
Only show 1 3d view of a particular molecule type at a time
Add developer option to allow collection box generation of all molecule types (instead of just a subset)
Talk with translators (Sandor) about translating names. Developer recommendation: translate a subset
Reset kit button enabled whenever something is in the play area
Synchronize colors with Jmol (sulfur and others look different colors in 3d views)
Bugfix: drag 2 atoms to the far lower-left at the start and they are pushed to cover each other (without bonding)
Find what molecules say they are "equivalent" to each other (some isomers pulled from PubChem seem identical to the sim model)
Add in a sound when a collection box is filled
Detect alcohols (or other structures) and modify their molecular formulas accordingly
Reduce delay (Jmol-caused) between clicking on "Show 3D" and it actually appearing
Investigate "molecular workbench", see if there are any other ideas
Possibly attempt to generate less kits? PENDING feedback
(dev) Internationalization
(dev) Switch to more mode-like canvas handling, instead of individual visibility handling

NOTES (questions):

--- feedback? (8): Is the scale of the pseudo-3d molecules in the collection boxes OK? (We can only fit 2 C02s with the current size, but we could increase the
width of the actual collection boxes a bit safely.
    I think it’s important to have the molecules show as large as possible. What do you think about having the molecules be larger in the first tab,
    and in the second tab start out the same size as in the first tab, but as you add molecules they get smaller (to fit)? Would that look weird? I’m
    also ok with having the boxes be different sizes (for example chloromethane box larger than hydrogen box). We should discuss this further at the
    CHEM meeting, I’m not the best at visualizing these types of changes.

    I've added that to the sim for feedback

--- mostly completed (10): For molecular formulas, I was under the impression that the Hill System was the standard, however this gives us "H3N" for "NH3". Is there a
procedure or set of steps that can be used to get an accurate naming convention? (One way that may work would be to break the Hill convention and if
there is no carbon, STILL put the hydrogen first).
    There are a number of categories of naming systems, and unfortunately the molecules included in the sim span two cases, regular covalent compounds
    and organic compounds. For all molecules that are not organic (these contain C and H but do not include HCN) the names follow the rule that the
    symbols are listed in increasing order of electronegativity. Here’s a table of electronegativey ( http://en.wikipedia.org/wiki/Electronegativity
    scroll down to periodic table view). For example, in HF, hydrogen has an electronegativity of 2.2 and fluorine has an electronegativity of 3.98,
    so the order is HF.
    For organic compounds, you start with C, then H, then whatever else...unless it’s an an alcohol in the case of CH3OH (this way of naming indicates
    something about the structure, that you have an OH group hanging off the side). So in short, would be naming rules that work for the molecules
    included in the sim:
        * If don’t include both C and H, name in increasing order of electronegativity
        * If includes C and H name with C first, then H, then whatever else.
    Exceptions:
        * NH3, name like organic where the H is second
        * HCN not considered organic, so name as regular covalent
        * CH3OH, name shows structure, doesn’t follow either naming convention.

(14): What names should we have for "Sphere fill" or "Ball & Stick" in the 3d view?
    Leave as-is

(15): Should molecules (once grabbed) show in front of the buckets? Currently they show behind the front of the bucket, and can cause glitches!
    Leave as-is

(16): Should we change layout of kit "arrows" to far left/right? (where would reset go?) I keep wanting to reach for the "Next kit" on the right side.
    Move kit label/arrows to right, move reset kit down

(17): How to handle 3D? Design won't handle multiple molecules, so will the added "+" to show 3d work, and should we only put it on the 3rd tab? Kind of nice on all tabs.

(18): Potentially reduce bonding threshold distance due to "cheating" annoyance
    Not now

 */

/**
 * The main application for this simulation.
 */
public class BuildAMoleculeApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public BuildAMoleculeApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        Module makeMoleculeModule = new MakeMoleculeModule( parentFrame );
        addModule( makeMoleculeModule );

        Module collectMultipleModule = new CollectMultipleModule( parentFrame );
        addModule( collectMultipleModule );

        Module largerMolecules = new LargerMoleculesModule( parentFrame );
        addModule( largerMolecules );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Create main frame.
        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...
        developerMenu.add( new JMenuItem( "Show Table of Molecules" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new MoleculeTableDialog( getPhetFrame() ).setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Regenerate model" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (AbstractBuildAMoleculeModule) getActiveModule() ).regenerateModelIfPossible();
                }
            } );
        }} );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAMoleculeConstants.PROJECT_NAME, BuildAMoleculeApplication.class );
    }
}

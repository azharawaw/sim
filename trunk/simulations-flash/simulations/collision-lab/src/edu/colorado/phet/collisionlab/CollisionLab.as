//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance

package edu.colorado.phet.collisionlab {

import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.control.Tab;
import edu.colorado.phet.collisionlab.control.TabBar;
import edu.colorado.phet.flashcommon.SimStrings;

import flash.display.*;

public class CollisionLab extends Sprite {  //should the main class extend MovieClip or Sprite?
    private var stageW: Number;
    private var stageH: Number;
    private var introModule: IntroModule = new IntroModule();
    private var advancedModule: AdvancedModule = new AdvancedModule();

    public function CollisionLab() {
        SimStrings.init( loaderInfo );
        //stage width and height hard-coded for now
        this.stageW = 950;//this.stage.stageWidth;
        this.stageH = 700;//this.stage.stageHeight;
        var tabBar: TabBar = new TabBar( CLConstants.BACKGROUND_COLOR, 0xFFFFBB );

        var introHolder: Sprite = new Sprite();
        addChild( introHolder );
        introModule.attach( introHolder );

        var advancedHolder: Sprite = new Sprite();
        addChild( advancedHolder );
        advancedModule.attach( advancedHolder );
        advancedHolder.visible = false;

        var introTab: Tab = new Tab( "Introduction", tabBar );
        tabBar.addTab( introTab );
        var advancedTab: Tab = new Tab( "Advanced", tabBar );
        tabBar.addTab( advancedTab );
        tabBar.selectedTab = introTab;
        tabBar.addListener( function(): void {
            if ( tabBar.selectedTab == introTab ) {
                introHolder.visible = true;
                advancedHolder.visible = false;
            }
            else { // advanced tab
                introHolder.visible = false;
                advancedHolder.visible = true;
            }
        } );
        addChild( tabBar );
    }//end of constructor

}//end of class
}//end of package
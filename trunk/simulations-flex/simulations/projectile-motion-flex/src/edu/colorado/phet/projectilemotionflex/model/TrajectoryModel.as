/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/16/12
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
import edu.colorado.phet.projectilemotionflex.view.MainView;

public class TrajectoryModel {

    public var views_arr: Array;     //views associated with this model
    public var mainView: MainView;
    private var stageH: Number;
    private var stageW: Number;
    private var _xP: Number;    //x- and y_coords of position of projectile in meters
    private var _yP: Number;
    private var _xP0: Number;   //x- and y- coordinates of initial position of projectile
    private var _yP0: Number;
    private var vX: Number;     //x- and y-coords of velocity of projectile
    private var vY: Number;
    private var v: Number;      //speed of projectile
    private var vX0: Number;    //x- and y-coords of initial velocity of projectile
    private var vY0: Number;
    private var _mP: Number;    //mass of projectile in kg
    private var _theta: Number; //initial angle of projectile, in radians, measured CCW from horizontal
    private var _t: Number;     //time in seconds, projectile fired at t = 0
    private var dt: Number;     //time step for trajectory algorithm



    public function TrajectoryModel( mainView: MainView ) {
        this.mainView = mainView;
        this.views_arr = new Array();
        this.stageW = this.mainView.stageW;
        this.stageH = this.mainView.stageH;
        this.initialize();
    }

    private function initialize():void{

    }

    //SETTERS and GETTERS
    public function set xP0( xP0: Number ):void{
        this._xP0 = xP0;
    }

    public function set yP0( yP0: Number ):void{
        this._yP0 = yP0;
    }

    public function get xP(): Number{
        return this._xP;
    }

    public function get yP(): Number{
        return this._yP;
    }

    public function get t(): Number{
        return this._t;
    }
    //end of Setters and Getters

    public function registerView( view: Object ): void {
        this.views_arr.push( view );
    }

    public function unregisterView( view: Object ):void{
        var indexLocation:int = -1;
        indexLocation = this.views_arr.indexOf( view );
        if( indexLocation != -1 ){
            this.views_arr.splice( indexLocation, 1 )
        }
    }

    public function updateViews(): void {
        for(var i:int = 0; i < this.views_arr.length; i++){
            this.views_arr[ i ].update();
        }
    }//end updateView()

}//end class
}//end package

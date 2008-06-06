﻿//this class initialized a movieclip that is attached to instance of the (data)Point Class
	
class PointClipInitializer{
	var myPoint:Point;
	var scale:Number;
	
	function PointClipInitializer(thePoint:Point){
		this.myPoint = thePoint;
		this.scale = this.myPoint.myModel.mainView.scale;
	}//end of constructor
	
	function initializePointClip():Void{
		var currentPoint:Point = this.myPoint;
		var model:Model = currentPoint.myModel;
		var theView = model.mainView;
		var scaleFactor = this.scale;
		var yLimit = 3;
		//var thisView = this;
		var clip:MovieClip = this.myPoint.clip_mc;
		
		clip.dataPoint_mc.onPress = function(){
			clip.startDrag(false, 0, 0, theView.stageW, theView.stageH);
			clip.display_mc._visible = true;
			clip.displayDelY_mc._visible = false;
			clip.onMouseMove = function(){
				var xNow = scaleFactor*(clip._x - Util.ORIGINX);
				var yNow = scaleFactor*(-clip._y + Util.ORIGINY);
				currentPoint.setXY(xNow, yNow);
				clip.display_mc.xDisplay_txt.text = 0.1*Math.round(10*xNow);
				clip.display_mc.yDisplay_txt.text = 0.1*Math.round(10*yNow);
				updateAfterEvent();
			}
		}
		clip.dataPoint_mc.onRelease = function(){
			currentPoint.setXY(scaleFactor*(clip._x - Util.ORIGINX), scaleFactor*(-clip._y + Util.ORIGINY));
			clip.stopDrag();
			clip.display_mc._visible = false;
			clip.onMouseMove = undefined;
			if(clip.dataPoint_mc.hitTest(theView.pointsBucket.hitAreaPoints_mc)){
				model.deletePoint(currentPoint.getPositionInArray());  
			}
		}
		clip.dataPoint_mc.onReleaseOutside = clip.dataPoint_mc.onRelease;
		
		clip.dataPoint_mc.onRollOver = function(){
			clip.display_mc._visible = true;
		}
		clip.dataPoint_mc.onRollOut = function(){
			clip.display_mc._visible = false;
		}
		
		clip.errorBar_mc.top_mc.onPress = function(){
			clip.displayDelY_mc._visible = true;
			this.onMouseMove = function(){
				if(this._y < -yLimit){
					this._y = clip._ymouse;
					clip.errorBar_mc.bottom_mc._y = -this._y;
					//trace("clip.errorBar_mc._height:"+clip.errorBar_mc._height);
					clip.errorBar_mc.middle_mc._height = -2*this._y;
					var delY = -scaleFactor*this._y
					currentPoint.setDeltaY(delY);
					clip.displayDelY_mc.display_txt.text = 0.1*Math.round(10*delY);
					updateAfterEvent();
				}
				//trace("clip._ymouse:"+clip._ymouse);
			}
		}
		clip.errorBar_mc.top_mc.onRelease = function(){
			clip.displayDelY_mc._visible = false;
			if(this._y > -2*yLimit ){
				this._y = -2*yLimit;
				clip.errorBar_mc.bottom_mc._y = -this._y;
				clip.errorBar_mc.middle_mc._height = -2*this._y;
				currentPoint.setDeltaY(-scaleFactor*this._y);
			}
			this.onMouseMove = undefined;
		}
		clip.errorBar_mc.top_mc.onReleaseOutside = clip.errorBar_mc.top_mc.onRelease ;
		
		clip.errorBar_mc.bottom_mc.onPress = function(){
			clip.displayDelY_mc._visible = true;
			this.onMouseMove = function(){
				if(this._y > yLimit){
					this._y = clip._ymouse;
					clip.errorBar_mc.top_mc._y = -this._y;
					//trace("clip.errorBar_mc._height:"+clip.errorBar_mc._height);
					clip.errorBar_mc.middle_mc._height = 2*this._y;
					var delY = scaleFactor*this._y;
					currentPoint.setDeltaY(delY);
					clip.displayDelY_mc.display_txt.text = 0.1*Math.round(10*delY);
					updateAfterEvent();
				}
				//trace("clip._ymouse:"+clip._ymouse);
			}
		}
		clip.errorBar_mc.bottom_mc.onRelease = function(){
			clip.displayDelY_mc._visible = false;
			if(this._y < 2*yLimit ){
				this._y = 2*yLimit;
				clip.errorBar_mc.top_mc._y = -this._y;
				clip.errorBar_mc.middle_mc._height = 2*this._y;
				currentPoint.setDeltaY(scaleFactor*this._y);
			}
			this.onMouseMove = undefined;
		}
		clip.errorBar_mc.bottom_mc.onReleaseOutside = clip.errorBar_mc.bottom_mc.onRelease ;
		
		clip.errorBar_mc.top_mc.onRollOver = function(){
			clip.displayDelY_mc._visible = true;
		}
		clip.errorBar_mc.top_mc.onRollOut = function(){
			clip.displayDelY_mc._visible = false;
		}
		clip.errorBar_mc.bottom_mc.onRollOver = clip.errorBar_mc.top_mc.onRollOver;
		clip.errorBar_mc.bottom_mc.onRollOut = clip.errorBar_mc.top_mc.onRollOut
	}//end of makePointDraggable()
}//end of class
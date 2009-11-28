﻿//The Model for Collision Lab
package{
	import flash.events.*;
	import flash.utils.*;
	import flash.geom.*;
	
	public class Model{
		var nbrBalls:int;  		//current nbr of interacting balls
		var maxNbrBalls:int;	//maximum nbr of interacting balls (5?)
		var ball_arr:Array;		//array of balls
		var initPos:Array;		//array of initial positions of balls
		var initVel:Array;		//array of initial velocities of balls
		var CM:Point;			//center-of-mass of system
		var borderOn:Boolean;	//if true, balls elastically reflect from border 
		var borderWidth:Number;	//length of horizontal border in meters
		var borderHeight:Number;	//length of vertical border in meters
		var e:Number;			//elasticity = 0 to 1: 0 = perfectly inelastic, 1 = perfectly elastic
		var time:Number;		//simulation time in seconds = real time
		var lastTime:Number;	//time of previous step
		var timeStep:Number;	//time step in seconds
		var msTimer:Timer;		//millisecond timer
		var playing:Boolean;	//true if motion is playing, false if paused
		var nbrBallsChanged:Boolean;  //true if number of balls is changed
		var atInitialConfig:Boolean;  //true if t = 0;
		var starting:Boolean;	//true if playing and 1st step not yet taken;
		var reversing:Boolean;	//false if going forward in time, true if going backward
		//var realTimer:Timer;	//real timer, used to maintain time-based updates
		var timeHolder:int;		//scatch for hold results of getTimer
		var timeRate:Number;	//0 to 1: to slow down or speed up action, 1 = realtime, 0 = paused
		var updateRate:int;		//number of time steps between graphics updates
		var frameCount:int;		//when frameCount reaches frameRate, update graphics
		var colliders:Array;	//2D array of ij pairs: value = 1 if pair colliding, 0 if not colliding.
		var view_arr:Array;		//views of this model
		var nbrViews:int;		//number of views
		
		public function Model(){
			this.borderOn = true;
			this.borderWidth = 3.2;
			this.borderHeight = 2;
			this.e = 1;				//set elasticity of collisions, 1 = perfectly elastic
			this.nbrBalls = 3;		//adjustable by user
			this.maxNbrBalls = 5;
			this.CM = new Point();
			this.ball_arr = new Array(this.maxNbrBalls);  //only first nbrBalls elements of array are used
			this.initializeBalls();
			//this.setCenterOfMass();
			this.time = 0;
			this.timeStep = 0.01;
			this.timeRate = 1;
			this.updateRate = 3;
			this.frameCount = 0;
			
			this.msTimer = new Timer(this.timeStep*1000);
			msTimer.addEventListener(TimerEvent.TIMER, stepForward);
			this.stopMotion();
			this.e = 1;				//set elasticity of collisions, 1 = perfectly elastic
			//this.realTimer = new Timer(1000);  //argument 1000 is irrelevant
			this.view_arr = new Array(0);
			this.nbrViews = 0;
			
		}//end of constructor
		
		public function addBall():void{
			//trace("Model.addBall() called");
			if(this.nbrBalls < this.maxNbrBalls){
				this.nbrBalls += 1;
				//trace("Model.nbrBalls: "+this.nbrBalls);
				this.nbrBallsChanged = true;
				this.setCenterOfMass();
				this.updateViews();
				this.nbrBallsChanged = false;
				//this.ball_arr = new Array(nbrBalls);
				//this.initializePositions();
			}
		}//end of addBall()
		
		public function removeBall():void{
			//trace("Model.removeBall()");
			if(this.nbrBalls > 0){
				this.nbrBalls -= 1;
				this.nbrBallsChanged = true;
				this.setCenterOfMass();
				this.updateViews();
				this.nbrBallsChanged = false;
				//this.ball_arr = new Array(nbrBalls);
				//this.initializePositions();
			}
		}
		
		public function setReflectingBorder(tOrF:Boolean):void{
			this.borderOn = tOrF;
		}
		
		public function setMass(ballNbr:int, mass:Number):void{
			//trace("Model.setMass() called. ballNbr is "+ballNbr+"   mass is "+mass);
			this.ball_arr[ballNbr].setMass(mass);
			this.setCenterOfMass();
			this.updateViews();
			//this.updateViews();
		}
		
		//called once, at startup
		public function initializeBalls():void{
			this.atInitialConfig = true;
			this.initPos = new Array(this.maxNbrBalls);
			this.initVel = new Array(this.maxNbrBalls);
			initPos[0] = new TwoVector(0.2,0.2);
			initPos[1] = new TwoVector(0.5,0.5);
			initPos[2] = new TwoVector(1,1);
			initPos[3] = new TwoVector(1.2, 1.2);
			initPos[4] = new TwoVector(1.2, 0.2);
			initVel[0] = new TwoVector(0.7,0.8);
			initVel[1] = new TwoVector(0.12,2);
			initVel[2] = new TwoVector(-0.5,-0.25);
			initVel[3] = new TwoVector(1.1,0.2);
			initVel[4] = new TwoVector(-1.1,0);
			for (var i = 0; i < this.maxNbrBalls; i++){
				//new Ball(mass, position, velocity);
				this.ball_arr[i] = new Ball(1.0, initPos[i].clone(), initVel[i].clone());
			}
			this.nbrBallsChanged = true;
			var maxN:int = this.maxNbrBalls;
			//initialize colliders array
			this.colliders = new Array(maxN);
			//in AS3, duplicate variable definition in same method causes compile error message
			//so do not use var i:int more than one
			for (i = 0; i < maxN; i++){
				this.colliders[i] = new Array(maxN);
			}
			for (i = 0; i < maxN; i++){
				for (var j:int = 0; j < maxN; j++){
					this.colliders[i][j] = 0;  //0 if not colliding
				}
			}
			//No point in updating views, since views not created yet
			this.setCenterOfMass();
		}//end of initializeBalls()
		
		//called whenever reset button pushed by user or when nbrBalls changes
		public function initializePositions():void{
			//trace("Model.initializePositions() called");
			this.atInitialConfig = true;
			for (var i = 0; i < this.nbrBalls; i++){
				//new Ball(mass, position, velocity);
				this.ball_arr[i].position = initPos[i].clone();
				this.ball_arr[i].velocity = initVel[i].clone();
			}
			//trace("myModel.ball_arr[0].position.getX(): "+this.ball_arr[0].position.getX());
			this.time = 0;
			this.setCenterOfMass();
			this.updateViews();
		}//end of initializePositions()
		
		public function startMotion():void{
			msTimer.start();  
			this.atInitialConfig = false;
			this.playing = true;
			this.starting = true;
			this.reversing = false;
		}//startMotion()
		
		public function stopMotion():void{
			msTimer.stop();
			this.playing = false;
			this.starting = false;
		}
		
		public function setTimeRate(rate:Number):void{
			this.timeRate = rate;
			//trace("Model.timeRate: "+timeRate);
		}
		
		public function setElasticity(e:Number):void{
			this.e = e;
			//trace("Model:elasticity: "+this.e);
		}
		
		public function setX(indx:int, xPos:Number):void{
			this.ball_arr[indx].position.setX(xPos);
			if(this.atInitialConfig){
				this.initPos[indx].setX(xPos);
			}
			this.setCenterOfMass();
			this.updateViews();
		}
		
		public function setY(indx:int, yPos:Number):void{
			this.ball_arr[indx].position.setY(yPos);
			if(this.atInitialConfig){
				this.initPos[indx].setY(yPos);
			}
			this.setCenterOfMass();
			this.updateViews();
		}
		
		public function setVX(indx:int, xVel:Number):void{
			this.ball_arr[indx].velocity.setX(xVel);
			if(this.atInitialConfig){
				this.initVel[indx].setX(xVel);
			}
			this.updateViews();
		}
		
		public function setVY(indx:int, yVel:Number):void{
			this.ball_arr[indx].velocity.setY(yVel);
			if(this.atInitialConfig){
				this.initVel[indx].setY(yVel);
			}
			this.updateViews();
		}
		
		public function stepForward(evt:TimerEvent):void{
			//trace("stepForward called.  elasticity is " + this.e);
			//need function without event argument
			this.singleStep();
			
		}//stepForward
		
		public function singleStep():void{
			if(this.atInitialConfig){this.atInitialConfig = false;}
			var dt:Number;
			if(playing && !starting){
				//time-based aminimation
				var realDt = getTimer() - timeHolder;
				dt = realDt/1000;
			}else{
				//frame-based animation
				dt = this.timeStep;
			}
			if(starting){
				this.starting = false;
			}
			//trace("timeRate: "+this.timeRate);
			dt *= this.timeRate;
			if(reversing){
				dt *= -1;
			}
			this.time += dt;
			//trace("dt_after: "+dt);
			for(var i:int = 0; i < this.nbrBalls; i++){
				var x:Number = this.ball_arr[i].position.getX();
				var y:Number = this.ball_arr[i].position.getY();
				var vX:Number = this.ball_arr[i].velocity.getX();
				var vY:Number = this.ball_arr[i].velocity.getY();
				var xLast = x;	//previous value of x before update
				var yLast = y;	//previous value of y before update
				x += vX*dt;
				y += vY*dt;
				this.ball_arr[i].position.setXY(x,y);
				//trace("i: "+i+"  x: "+this.ball_arr[i].position.getX());
				//trace("i: "+i+"  y: "+this.ball_arr[i].position.getY());
				
				//reflect at borders
				var radius:Number = this.ball_arr[i].getRadius();
				
				//if ball beyond reflecting border, then backup to previous position and reflect
				//this guarantees no penetration of border
				if(this.borderOn){
					if((x+radius) > this.borderWidth || (x-radius)< 0){
						this.ball_arr[i].position.setXY(xLast,yLast);
						this.ball_arr[i].velocity.setX(-vX);
					}else if((y+radius) > this.borderHeight || (y-radius)< 0){
						this.ball_arr[i].position.setXY(xLast,yLast);
						this.ball_arr[i].velocity.setY(-vY);
					}
					this.setCenterOfMass();
				}//end if(borderOn)
				
				//following reflection code does not work when going backward in time
				/*
				if((x+radius) > this.borderWidth){
					this.ball_arr[i].velocity.setX(-Math.abs(vX));
				}else if((x-radius) < 0){
					this.ball_arr[i].velocity.setX(Math.abs(vX));
				}
				if((y+radius) > this.borderHeight){
					this.ball_arr[i].velocity.setY(-Math.abs(vY));
				}else if((y-radius) < 0){
					this.ball_arr[i].velocity.setY(Math.abs(vY));
				}
				*/
				
			}//for loop
			this.timeHolder = getTimer();
			
			this.detectCollision();
			this.frameCount += 1;
			if(this.frameCount == this.updateRate){
				//var interval:int = getTimer() - this.timeHolder;
				//trace("getTimer()"+ interval);
				this.frameCount = 0;
				this.updateViews();
				//this.timeHolder = getTimer();
			}
			this.lastTime = this.time;
		}//end of singleStep()
		
		//move forward one Frame = several steps
		public function singleFrame():void{
			for (var i:int = 0; i < this.updateRate; i++){
				this.singleStep();
			}
		}
		
		//move backward in time one frame = several steps
		public function backupOneFrame():void{
			this.reversing = true;
			this.singleFrame();
			this.reversing = false;
		}
		
		public function detectCollision():void{
			//var colliders_arr:Array = new Array(2);
			var N:int = this.nbrBalls;
			for (var i:int = 0; i < N; i++){
				for (var j:int = i+1; j < N; j++){
					var xi:Number = ball_arr[i].position.getX();
					var yi:Number = ball_arr[i].position.getY();
					var xj:Number = ball_arr[j].position.getX();
					var yj:Number = ball_arr[j].position.getY();
					var dist:Number = Math.sqrt((xj-xi)*(xj-xi)+(yj-yi)*(yj-yi));
					var distMin:Number = ball_arr[i].getRadius() + ball_arr[j].getRadius();
					if(dist < distMin){
						//trace("elasticity before collision: "+this.e);
						this.collideBalls(i, j);
						this.colliders[i][j] = 1;  //ball have collided
					}else {
						this.colliders[i][j] = 0;	//balls not touching
					}
					
				}//for(j=..)
			}//for(i=..)
		}//detectCollision
		
		public function collideBalls(i:int, j:int):void{
			if(colliders[i][j] == 0){ //if balls not collided yet
				//trace("collision between i: " + i + " and j: " + j);
				//var totP:TwoVector = this.getTotalMomentum();
				//trace("Before:   pX: " + totP.getX() + "   pY:" + totP.getY());
				var x1:Number = ball_arr[i].position.getX();
				var x2:Number = ball_arr[j].position.getX();
				var y1:Number = ball_arr[i].position.getY();
				var y2:Number = ball_arr[j].position.getY();
				var delX:Number = x2 - x1;
				var delY:Number = y2 - y1;
				var d:Number = Math.sqrt(delX*delX + delY*delY);
				var v1x:Number = ball_arr[i].velocity.getX();
				var v2x:Number = ball_arr[j].velocity.getX();
				var v1y:Number = ball_arr[i].velocity.getY();
				var v2y:Number = ball_arr[j].velocity.getY();
				//normal and tangential components of initial velocities
				var v1n:Number = (1/d)*(v1x*delX + v1y*delY);
				var v2n:Number = (1/d)*(v2x*delX + v2y*delY);
				var v1t:Number = (1/d)*(-v1x*delY + v1y*delX);
				var v2t:Number = (1/d)*(-v2x*delY + v2y*delX);
				var m1:Number = ball_arr[i].getMass();
				var m2:Number = ball_arr[j].getMass();
				//normal components of velocities after collision (P for prime = after)
				//trace("Model.e: "+this.e);
				var v1nP:Number = ((m1 - m2*this.e)*v1n + m2*(1+this.e)*v2n)/(m1 + m2);
				var v2nP:Number = this.e*(v1n - v2n) + v1nP;
				var v1xP = (1/d)*(v1nP*delX - v1t*delY);
				var v1yP = (1/d)*(v1nP*delY + v1t*delX);
				var v2xP = (1/d)*(v2nP*delX - v2t*delY);
				var v2yP = (1/d)*(v2nP*delY + v2t*delX);
				this.ball_arr[i].velocity.setXY(v1xP, v1yP);
				this.ball_arr[j].velocity.setXY(v2xP, v2yP);
				//backup to step just before penentration so balls cannot get stuck
				this.ball_arr[i].backupOneStep();
				this.ball_arr[j].backupOneStep();
				//var v1Mag:Number = Math.sqrt(v1n*v1n + v1t*v1t);
				//var v2Mag:Number = Math.sqrt(v2n*v2n + v2t*v2t);
				//var nHat:TwoVector = new TwoVector()
				
				//var totP:TwoVector = this.getTotalMomentum();
				//trace("After:   pX: " + totP.getX() + "   pY:" + totP.getY());
				//trace("KETot: "+this.getTotalKE());
				//trace("v1: "+v1Mag+"  v1:"+ball_arr[i].velocity.getLength());
				//trace("v2: "+v2Mag+"  v1:"+ball_arr[j].velocity.getLength());
			}
		}//collideBalls
		
		public function getTotalKE():Number{
			var KETot:Number = 0;
			for(var i:Number = 0; i < this.nbrBalls; i++){
				KETot += this.ball_arr[i].getKE();
			}
			return KETot;
		}//getKETotal
		
		public function getTotalMomentum():TwoVector{
			var pX:Number = 0;	//x-component of momentum
			var pY:Number = 0;	//y-component
			for(var i:Number = 0; i < this.nbrBalls; i++){
				pX += this.ball_arr[i].getMomentum().getX();
				pY += this.ball_arr[i].getMomentum().getY();
			}
			var totP:TwoVector = new TwoVector(pX, pY);
			return totP;
		}
		
		public function setCenterOfMass():void{
			var totMass:Number = 0;
			var sumXiMi:Number = 0
			var sumYiMi:Number = 0
			for(var i:int = 0; i < this.nbrBalls; i++){
				var m:Number = this.ball_arr[i].getMass();
				var x:Number = this.ball_arr[i].position.getX();
				var y:Number = this.ball_arr[i].position.getY();
				totMass += m;
				sumXiMi += m*x;
				sumYiMi += m*y;
			}
			this.CM.x = sumXiMi/totMass;
			this.CM.y = sumYiMi/totMass;
		}//end setCenterOfMass();
		
		public function registerView(aView:Object):void{
			this.nbrViews += 1;
			this.view_arr.push(aView);
		}
		
		public function updateViews():void{
			for (var i:int = 0; i < nbrViews; i++){
				this.view_arr[i].update();
			}
		}
		
	}//end of class
}//end of package
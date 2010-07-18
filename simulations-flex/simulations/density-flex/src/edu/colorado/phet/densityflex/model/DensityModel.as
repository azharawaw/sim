package edu.colorado.phet.densityflex.model {
import edu.colorado.phet.densityflex.*;

import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Collision.b2AABB;
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2BodyDef;
import Box2D.Dynamics.b2World;

import edu.colorado.phet.densityflex.model.Block;

import edu.colorado.phet.densityflex.view.DebugText;

import flash.geom.ColorTransform;

public class DensityModel {
    private var densityObjects : Array;

    private var poolWidth : Number = 15;
    private var poolHeight : Number = 7.5;
    private var poolDepth : Number = 5;
    private var waterHeight : Number = 5.5;
    private static var BOUNDS : Number = 50;
    private var volume : Number = poolWidth * poolDepth * waterHeight;

    public static var STEPS_PER_FRAME : Number = 10;

    public static var DT_FRAME : Number = 1 / 30.0;
    public static var DT_STEP : Number = DT_FRAME / STEPS_PER_FRAME;

    public static var DISPLAY_SCALE : Number = 100.0;

    private var world : b2World;

    private var contactHandler : ContactHandler;

    public function DensityModel() {
        densityObjects = new Array();

        initWorld();
        createGround();
    }

    public function initializeTab1SameMass():void {
        densityObjects.push(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), this));
        densityObjects.push(Block.newBlockSizeMass(2, 4.0, -1.5, 0, new ColorTransform(0, 0, 1), this));
        densityObjects.push(Block.newBlockSizeMass(1.5, 4.0, 1.5, 0, new ColorTransform(0, 1, 0), this));
        densityObjects.push(Block.newBlockSizeMass(1, 4.0, 4.5, 0, new ColorTransform(1, 0, 0), this));
        densityObjects.push(new Scale(-9.5, Scale.SCALE_HEIGHT / 2, this));
        densityObjects.push(new Scale(4.5, Scale.SCALE_HEIGHT / 2 - poolHeight, this));
    }

    public function initializeTab1SameVolume():void {
        densityObjects.push(Block.newBlockDensitySize(1.0 / 8.0, 2, -4.5, 0, new ColorTransform(0.5, 0.5, 0), this));
        densityObjects.push(Block.newBlockDensitySize(0.5, 2, -1.5, 0, new ColorTransform(0, 0, 1), this));
        densityObjects.push(Block.newBlockDensitySize(2, 2, 1.5, 0, new ColorTransform(0, 1, 0), this));
        densityObjects.push(Block.newBlockDensitySize(4, 2, 4.5, 0, new ColorTransform(1, 0, 0), this));
        densityObjects.push(new Scale(-9.5, Scale.SCALE_HEIGHT / 2, this));
        densityObjects.push(new Scale(4.5, Scale.SCALE_HEIGHT / 2 - poolHeight, this));
    }

    public function clearDensityObjects() : void {
        for each( var densityObject : DensityObject in densityObjects ) {
            world.DestroyBody(densityObject.getBody());
            densityObject.remove();
        }
        densityObjects = new Array();
    }

    private function createGround():void {
        var groundBodyDef:b2BodyDef = new b2BodyDef();
        groundBodyDef.position.Set(0, 0);

        var groundBody:b2Body = world.CreateBody(groundBodyDef);

        var groundShapeDef:b2PolygonDef = new b2PolygonDef();
        groundShapeDef.SetAsOrientedBox(poolWidth / 2, 50, new b2Vec2(0, -50 - poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2, poolHeight, new b2Vec2(-(poolWidth / 2 + BOUNDS / 2), -poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2, poolHeight, new b2Vec2((poolWidth / 2 + BOUNDS / 2), -poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);
    }

    private function initWorld():void {
        var worldBox : b2AABB = new b2AABB();
        worldBox.lowerBound.Set(-BOUNDS, -BOUNDS);
        worldBox.upperBound.Set(BOUNDS, BOUNDS);
        var gravity : b2Vec2 = new b2Vec2(0, 0);
        var doSleep : Boolean = false;
        world = new b2World(worldBox, gravity, doSleep);

        contactHandler = new ContactHandler();
        world.SetContactListener(contactHandler);
    }

    public function getDensityObjects() : Array {
        return densityObjects;
    }

    public function step() : void {
        DebugText.clear();

        for each( densityObject in densityObjects ) {
            densityObject.resetContacts();
        }

        for ( var i : Number = 0; i < STEPS_PER_FRAME; i++ ) {

            world.Step(DT_STEP, 10);
            var densityObject : DensityObject;
            for each( densityObject in densityObjects ) {
                densityObject.update();
            }
            updateWater();
            var waterY : Number = -poolHeight + waterHeight;
            for each( var cuboid:Cuboid in getCuboids() ) {
                var body : b2Body = cuboid.getBody();

                // gravity?
                body.ApplyForce(new b2Vec2(0, -9.8 * cuboid.getVolume() * cuboid.getDensity()), body.GetPosition());

                if ( waterY < cuboid.getBottomY() ) {
                    continue;
                }
                var submergedVolume : Number;
                if ( waterY > cuboid.getTopY() ) {
                    submergedVolume = cuboid.getVolume();
                }
                else {
                    submergedVolume = (waterY - cuboid.getBottomY() ) * cuboid.getWidth() * cuboid.getDepth();
                }
                // TODO: add in liquid density

                body.ApplyForce(new b2Vec2(0, 9.8 * submergedVolume), body.GetPosition());

                var dragForce:b2Vec2 = body.GetLinearVelocity().Copy();
                dragForce.Multiply(-2 * submergedVolume);
                body.ApplyForce(dragForce, body.GetPosition());
            }
        }
    }

    private function getCuboids():Array {
        var cuboids:Array = new Array();
        for each ( var object:Object in densityObjects ) {
            if (object is Cuboid){
                cuboids.push(object);
            }
        }
        return cuboids;
    }

    public function updateWater() : void {
        var cuboid : Cuboid;
        var sortedHeights : Array = new Array();
        for ( var key : String in densityObjects ) {
            cuboid = densityObjects[key];
            var top : Object = new Object();
            top.y = cuboid.getTopY();
            top.pos = 1;
            top.block = cuboid;
            var bottom : Object = new Object();
            bottom.y = cuboid.getBottomY();
            bottom.pos = 0;
            bottom.block = cuboid;
            sortedHeights.push(top);
            sortedHeights.push(bottom);
        }
        sortedHeights.sortOn(["y"], [Array.NUMERIC]);

        var curHeight : Number = 0;
        var volumeToGo : Number = volume;
        var crossSection : Number = poolWidth * poolDepth;

        for ( var i : String in sortedHeights ) {
            var ob : Object = sortedHeights[i];
            var pos : Number = ob.pos;
            var by : Number = ob.y + poolHeight;
            cuboid = ob.block;
            var idealHeight : Number = volumeToGo / crossSection + curHeight;
            if ( idealHeight < by ) {
                curHeight = idealHeight;
                volumeToGo = 0;
                break;
            }
            var heightGain : Number = by - curHeight;
            volumeToGo -= crossSection * heightGain;
            curHeight = by;
            if ( pos == 0 ) {
                // bottom of block
                crossSection -= cuboid.getWidth() * cuboid.getDepth();
            }
            else {
                // top of block
                crossSection += cuboid.getWidth() * cuboid.getDepth();
            }
        }

        // fill it up the rest of the way
        curHeight += volumeToGo / crossSection;

        waterHeight = curHeight;
    }

    public function getPoolHeight() : Number {
        return poolHeight;
    }

    public function getWaterHeight() : Number {
        return waterHeight;
    }

    public function getPoolWidth() : Number {
        return poolWidth;
    }

    public function getPoolDepth() : Number {
        return poolDepth;
    }

    public function getWorld():b2World {
        return world;
    }
}
}
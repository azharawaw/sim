package edu.colorado.phet.chargesandfields {

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.display.Stage
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;
import flash.text.TextField;
import flash.ui.Keyboard;

public class ChargesAndFieldsDisplay extends Sprite {
    private var myWidth : Number;
    private var myHeight : Number;

    public var model : Model;

    private var background : BackgroundSprite;
    private var mosaic : VoltageMosaic;
    private var charges : Array = new Array();

    public function ChargesAndFieldsDisplay(tempStage : Stage) {
        myWidth = tempStage.stageWidth;
        myHeight = tempStage.stageHeight;

        model = new Model();

        background = new BackgroundSprite(myWidth, myHeight);
        addChild(background);

        mosaic = new VoltageMosaic(model, myWidth, myHeight);
        addChild(mosaic);

        for(var i : uint = 0; i < 100; i++) {
            var charge : Charge;
            if(i % 2 == 0) {
                charge = new MinusCharge(mosaic);
            } else {
                charge = new PlusCharge(mosaic);
            }
            addChild(charge);
            charges.push(charge);
            model.addCharge(charge);
            charge.setDisplayPosition(Math.random() * myWidth, Math.random() * myHeight);
        }

        mosaic.draw();

        var txt1 : TextField = new TextField();
        txt1.text = "foobar";
        txt1.alpha = 50;
        addChild(txt1);

        var txt2 : TextField = new TextField();
        txt2.text = "foobar";
        txt2.alpha = 100;
        txt2.x = 100;
        addChild(txt2);
    }

    public function onResize(evt : Event) : void {
        myWidth = this.stage.stageWidth;
        myHeight = this.stage.stageHeight;
        background.changeSize(myWidth, myHeight);
        mosaic.changeSize(myWidth, myHeight);
    }
}
}

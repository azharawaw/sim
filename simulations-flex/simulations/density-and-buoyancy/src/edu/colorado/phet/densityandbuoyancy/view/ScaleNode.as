package edu.colorado.phet.densityandbuoyancy.view {
import away3d.materials.*;

import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Scale;

import flash.display.Bitmap;
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFormat;

import mx.core.BitmapAsset;

public class ScaleNode extends CuboidNode implements Pickable {

    private var frontSprite:Sprite;
    private var _scale:Scale;
    private var textureHolder:Sprite; // holds wood or etc. texture in frontSprite so it stays under the text
    private var textField:TextField;

    private var base:PickableCube;
    private var top:PickableCube;
    private var stand:PickableCube;

    private static var WALL_RES:Number = 100;
    private const textureBitmap:Bitmap = getCustomBitmap();

    public function ScaleNode(scale:Scale, view:AbstractDensityModule):void {
        super(scale, view);
        this._scale = scale;

        trace("scale addNodes()");

        var totalWidth:Number = getCuboid().getWidth() * DensityModel.DISPLAY_SCALE;
        var totalHeight:Number = getCuboid().getHeight() * DensityModel.DISPLAY_SCALE;
        var totalDepth:Number = getCuboid().getDepth() * DensityModel.DISPLAY_SCALE;

        base = new PickableCube(this);
        base.width = totalWidth;
        base.height = totalHeight / 2;
        base.depth = totalDepth;
        base.segmentsH = 2;
        base.segmentsW = 2;
        base.y = -totalHeight / 4;
        addChild(base);

        top = new PickableCube(this);
        top.width = totalWidth;
        top.height = totalHeight / 8;
        top.depth = totalDepth;
        top.segmentsH = 2;
        top.segmentsW = 2;
        top.y = 7 * totalHeight / 16;
        addChild(top);

        stand = new PickableCube(this);
        stand.width = totalWidth / 5;
        stand.height = totalHeight - base.height - top.height;
        stand.depth = totalDepth / 5;
        stand.y = base.y + base.height / 2 + stand.height / 2;
        addChild(stand);


        frontSprite = new Sprite();
        textureHolder = new Sprite();
        textureHolder.addChild(textureBitmap);

        frontSprite.graphics.beginFill(0xFFFFFF);
        frontSprite.graphics.drawRect(0, 0, getFrontWidth(), getFrontHeight());
        frontSprite.graphics.endFill();

        textField = new TextField();

        frontSprite.addChild(textureHolder);
        frontSprite.addChild(textField);
        updateText();

        var frontMaterial:MovieMaterial = new MovieMaterial(frontSprite);
        frontMaterial.smooth = true; //makes the font smooth instead of jagged, see http://www.mail-archive.com/away3d-dev@googlegroups.com/msg06699.html
        var brightMaterial:ColorMaterial = new ColorMaterial(0xFFFFFF);
        var sideMaterial:ColorMaterial = new ColorMaterial(0xCCCCCC);
        var hidMaterial:ColorMaterial = new ColorMaterial(0x999999);

        frontMaterial.smooth = true;

        base.cubeMaterials.left = base.cubeMaterials.right = base.cubeMaterials.top = base.cubeMaterials.bottom = base.cubeMaterials.front = sideMaterial;
        base.cubeMaterials.top = hidMaterial;

        base.cubeMaterials.back = frontMaterial;

        top.material = sideMaterial;
        top.cubeMaterials.back = brightMaterial;

        stand.material = sideMaterial;
    }

    [Embed(source="../../../../../../data/density-and-buoyancy/images/scale-front.jpg")]
    private static var wallClass:Class;

    private static function getCustomBitmap():Bitmap {
        return new Bitmap((new wallClass() as BitmapAsset).bitmapData);
    }

    public function updateText():void {
        textField.text = String(_scale.getScaleReadout());
        textField.width = textureBitmap.bitmapData.width;
        textField.height = textureBitmap.bitmapData.height;
        var format:TextFormat = new TextFormat();
        //        format.size = int(45 * ((Scale.SCALE_WIDTH * 200 / 3) / base.width));
        format.size = 52;
        format.bold = true;
        format.font = "Arial";
        textField.multiline = true;
        textField.setTextFormat(format);
    }

    override public function updateGeometry():void {
        super.updateGeometry();
        updateText();
    }

    override public function remove():void {
        view.removeObject(this);
    }

    private function getFrontWidth():Number {
        return WALL_RES * _scale.getWidth();
    }

    private function getFrontHeight():Number {
        return WALL_RES * _scale.getHeight() / 2;
    }
}
}
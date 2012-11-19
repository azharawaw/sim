// Copyright 2002-2012, University of Colorado

/**
 * Stage, sets up the scenegraph.
 * Uses composition of Easel.Stage, inheritance proved to be problematic.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/Property',
          'view/BarMagnetDisplay',
          'view/CompassDisplay',
          'view/FieldDisplay',
          'view/FieldMeterDisplay'
        ],
        function ( Easel, Property, BarMagnetDisplay, CompassDisplay, FieldDisplay, FieldMeterDisplay ) {

    function FaradayStage( canvas, model ) {

        // properties that are specific to the view (have no model counterpart.)
        this.magnetTransparent = new Property( false );
        this.fieldVisible = new Property( true );
        this.compassVisible = new Property( true );
        this.fieldMeterVisible = new Property( true );

        // stage
        this.stage = new Easel.Stage( canvas );
        this.stage.enableMouseOver();

        // black background
        var background = new Easel.Shape();
        background.graphics.beginFill( 'black' );
        background.graphics.rect( 0, 0, canvas.width, canvas.height );

        // field
        this.field = new FieldDisplay( model.field, model.mvt );
        this.field.x = 50;
        this.field.y = 50;
        this.field.visible = this.fieldVisible.get();

        // bar magnet
        this.barMagnet = new BarMagnetDisplay( model.barMagnet, model.mvt );

        // compass
        this.compass = new CompassDisplay( model.compass, model.mvt );
        this.compass.visible = this.compassVisible.get();

        // field meter
        this.meter = new FieldMeterDisplay( model.fieldMeter, model.mvt );
        this.meter.visible = this.fieldMeterVisible.get();

        // rendering order
        this.stage.addChild( background );
        this.stage.addChild( this.field );
        this.stage.addChild( this.barMagnet );
        this.stage.addChild( this.compass );
        this.stage.addChild( this.meter );
    }

    FaradayStage.prototype.reset = function() {
       this.magnetTransparent.reset();
       this.fieldVisible.reset();
       this.compassVisible.reset();
       this.fieldMeterVisible.reset();
    };

    return FaradayStage;
} );

// Copyright 2002-2012, University of Colorado

/**
 * Stage, sets up the scenegraph.
 * Uses composition of Easel.Stage, inheritance proved to be problematic.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/Dimension2D',
            'common/Property',
            'view/BarMagnetDisplay',
            'view/CompassDisplay',
            'view/FieldInsideDisplay',
            'view/FieldMeterDisplay',
            'view/FieldOutsideDisplay'
        ],
        function ( Easel, Dimension2D, Property, BarMagnetDisplay, CompassDisplay, FieldInsideDisplay, FieldMeterDisplay, FieldOutsideDisplay ) {

            function FaradayStage( canvas, model ) {

                // stage
                this.stage = new Easel.Stage( canvas );
                this.stage.enableMouseOver();

                // black background
                var background = new Easel.Shape();
                background.graphics.beginFill( 'black' );
                background.graphics.rect( 0, 0, canvas.width, canvas.height );

                // needle size, used for field inside and outside the magnet
                var NEEDLE_SIZE = new Dimension2D( 25, 7 );

                // field outside the magnet
                var field = new FieldOutsideDisplay( model.field, model.barMagnet, model.mvt, new Dimension2D( canvas.width, canvas.height ), NEEDLE_SIZE );

                // bar magnet
                var barMagnet = new BarMagnetDisplay( model.barMagnet, model.mvt );

                // field inside magnet
                this.magnetTransparent = new Property( false );
                var fieldInside = new FieldInsideDisplay( model.barMagnet, model.mvt, NEEDLE_SIZE );
                fieldInside.visible = this.magnetTransparent.get();
                var that = this;
                this.magnetTransparent.addObserver( function () {
                    fieldInside.visible = that.magnetTransparent.get();
                } );

                // compass
                var compass = new CompassDisplay( model.compass, model.mvt, NEEDLE_SIZE );

                // field meter
                var meter = new FieldMeterDisplay( model.fieldMeter, model.mvt );

                // rendering order
                this.stage.addChild( background );
                this.stage.addChild( field );
                this.stage.addChild( barMagnet );
                this.stage.addChild( fieldInside );
                this.stage.addChild( compass );
                this.stage.addChild( meter );
            }

            // Resets all view-specific properties
            FaradayStage.prototype.reset = function () {
                this.magnetTransparent.reset();
            };

            return FaradayStage;
        } );
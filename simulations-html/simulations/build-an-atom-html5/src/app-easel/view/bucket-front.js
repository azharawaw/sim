// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel'
        ], function ( _, Easel ) {

    /**
     * Sets the stroke style for the current subpath. Like all drawing methods, this can be chained, so you can define the stroke style and color in a single line of code like so:
     * myGraphics.setStrokeStyle(8,"round").beginStroke("#F00");
     * @param centerX Center of the bucket front.
     * @param topY Top of the bucket front.
     * @param width Width of the bucket front.
     * @param height Height of the bucket front.
     * @param labelText Text shown on front of bucket.
     * @return {BucketFront} The representation of the front of the bucket.
     **/
    var BucketFront = function ( centerX, topY, width, labelText ) {
        Easel.Container.prototype.initialize.call( this );
        this.initialize( centerX, topY, width, labelText );
    };

    var p = BucketFront.prototype;

    _.extend( p, Easel.Container.prototype );

    p.initialize = function ( centerX, topY, width, labelText ) {
        var height = width * 0.4; // Determined empirically for best look.
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "gray" ).setStrokeStyle( 2 );
        shape.graphics.moveTo( 0, 0 ).lineTo( width * 0.2, height ).lineTo( width * 0.8, height ).lineTo(width, 0 ).closePath();
        shape.graphics.endStroke().endFill();
        this.addChild( shape );
        var label = new Easel.Text( labelText, "bold 24px Helvetica", "white" );
        label.textBaseline = "middle";
        label.x = width / 2 - label.getMeasuredWidth() / 2;
        label.y = height / 2;
        this.addChild( label );

        // Set the position.
        this.x = centerX - width / 2;
        this.y = topY;
    };

    return BucketFront;
} );

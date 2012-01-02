// Copyright 2002-2011, University of Colorado
var canvas;
var context;

var rootNode = null;
var globals = {};

function loadImage( string ) {
    var image = new Image();
    image.src = string;
    return image;
}

//Uses width and height for bounds checking
function rectangularNode( width, height ) {
    var that = {x:0, y:0, selected:false};
    that.width = width;
    that.height = height;

    that.onTouchStart = function ( point ) {
        that.selected = point.x >= that.x && point.x <= that.x + that.width && point.y >= that.y && point.y <= that.y + that.height;
        that.initTouchPoint = point;
        that.objectTouchPoint = {x:that.x, y:that.y};

        //flag for consumed
        return that.selected;
    }
    that.onTouchEnd = function ( point ) {
        that.selected = false;
    }
    that.onTouchMove = function ( point ) {
        if ( that.selected ) {
            that.x = point.x + that.objectTouchPoint.x - that.initTouchPoint.x;
            that.y = point.y + that.objectTouchPoint.y - that.initTouchPoint.y;
        }
    }
    return that;
}

function fillRectNode( width, height, style ) {
    var that = rectangularNode( width, height );
    that.draw = function ( context ) {
        context.save();
        context.globalCompositeOperation = "source-over";
        context.fillStyle = style;
        context.fillRect( that.x, that.y, width, height );
        context.restore();
    };
    return that;
}

function textNode( string ) {

    //Context must be initialized for us to determine the width, so only create labels during or after init
    context.fillStyle = '#00f';
    context.font = '30px sans-serif';
    var width = context.measureText( string ).width;

    var that = rectangularNode( width, 30 );
    that.draw = function ( context ) {
        context.textBaseline = 'top';
        context.fillStyle = '#00f';
        context.font = '30px sans-serif';
        context.fillText( string, that.x, that.y );
    };
    return that;
}

function imageNode( string, x, y ) {
    var image = loadImage( string );

    var that = rectangularNode( image.width, image.height );
    that.x = x;
    that.y = y;
    that.image = image;
    that.image.onload = function () {
        that.width = that.image.width;
        that.height = that.image.height;
        draw();
    };
    that.draw = function ( context ) {
        context.drawImage( image, that.x, that.y );
        if ( that.selected ) {
            context.fillStyle = '#00f';
            context.font = '30px sans-serif';
            context.textBaseline = 'top';
            context.fillText( 'dragging', that.x, that.y );
        }
    };
    return that;
}

function vbox( args ) {
    var that = compositeNode( args.children );
    that.x = args.x;
    that.y = args.y;

    function vertical( components, spacing ) {
        components[0].x = 0;
        components[0].y = 0;
        for ( var i = 1; i < components.length; i++ ) {
            var prev = components[i - 1];
            var c = components[i];
            c.y = prev.y + prev.height + spacing;
            c.x = prev.x;
        }
    }

    vertical( args.children, 10 );

    return that;
}

function compositeNode( children ) {
    var that = {
        x:0,
        y:0
    };

    that.onTouchEnd = function ( point ) {
        var relativePoint = {x:point.x - that.x, y:point.y - that.y};
        for ( var i = 0; i < children.length; i++ ) {
            children[i].onTouchEnd( relativePoint );
        }
    };
    that.onTouchMove = function ( point ) {
        var relativePoint = {x:point.x - that.x, y:point.y - that.y};
        for ( var i = 0; i < children.length; i++ ) {
            children[i].onTouchMove( relativePoint );
        }
    };

    //Define these methods outside of initial that declaration so we can refer to that. (Maybe a better way to do that)
    that.onTouchStart = function ( point ) {

        var relativePoint = {x:point.x - that.x, y:point.y - that.y};
        //Reverse order so things in front will consume the event
        for ( var i = children.length - 1; i >= 0; i-- ) {
            var consumed = children[i].onTouchStart( relativePoint );
            if ( consumed ) {

                var child = children[i];

                //Move to front (i.e. end of list) by default
                children.splice( i, 1 );
                children.push( child );

                break;
            }
        }
    }
    that.draw = function ( context ) {
        context.save();
        context.translate( that.x, that.y );
        for ( var i = 0; i < children.length; i++ ) {
            children[i].draw( context );
        }
        context.restore();
    };
    return that;
}

// Hook up the initialization function.
$( document ).ready( function () {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );

// Initialize the canvas, context,
function init() {

    // Initialize references to the HTML5 canvas and its context.
    canvas = $( '#canvas' )[0];
    if ( canvas.getContext ) {
        context = canvas.getContext( '2d' );
    }

    // Set up event handlers.
    // TODO: Work with JO to "jquery-ize".
    document.onmousedown = onDocumentMouseDown;
    document.onmouseup = onDocumentMouseUp;
    document.onmousemove = onDocumentMouseMove;

    document.addEventListener( 'touchstart', onDocumentTouchStart, false );
    document.addEventListener( 'touchmove', onDocumentTouchMove, false );
    document.addEventListener( 'touchend', onDocumentTouchEnd, false );

    // Disable elastic scrolling.  This is specific to iOS.
    document.addEventListener(
            'touchmove',
            function ( e ) {
                e.preventDefault();
            },
            false
    );

    globals.springs = new Array();
    globals.springs.push( spring( "1", 200 ) );
    globals.springs.push( spring( "2", 300 ) );
    globals.springs.push( spring( "3", 400 ) );

//    var rootComponents = new Array(
//            new ImageSprite( "resources/red-mass.png", 114, 496 ),
//            new ImageSprite( "resources/green-mass.png", 210, 577 ),
//            new ImageSprite( "resources/gold-mass.png", 276, 541 ),
//            new ImageSprite( "resources/gram-50.png", 577, 590 ),
//            new ImageSprite( "resources/gram-100.png", 392, 562 ),
//            new ImageSprite( "resources/gram-250.png", 465, 513 ),
//            new ImageSprite( "resources/ruler.png", 12, 51 ),
//            new Node( { components:new Array( new Label( "Friction" ), new SliderKnob( 0, 0 ) ), x:700, y:80, layout:vertical} ),
//            new Node( { components:new Array( new Label( "Spring 3 Smoothness" ), new SliderKnob( 0, 0 ) ), x:700, y:150, layout:vertical} ),
//            new Node( { components:new Array( new CheckBox(), new Label( "Stopwatch" ) ), x:700, y:300, layout:horizontal} ),
//            new Node( { components:new Array( new CheckBox(), new Label( "Sound" ) ), x:700, y:350, layout:horizontal} )
//    );
//
//    //Add to the nodes for rendering
//    for ( var i = 0; i < globals.springs.length; i++ ) {
//        rootComponents.push( globals.springs[i] );
//    }
//    globals.rootNode = new Node( { components:rootComponents, x:0, y:0, layout:absolute } );

    //Init label components after canvas non-null
//    nodes.push( new BoxNode( { components:new Array( new Label( "Friction" ), new Label( "other label" ) ), x:700, y:200, layout:horizontal} ) );


    //Function that returns a function for rending from an image
//    function drawImageFromString( string ) {
//        var image = loadImage( string );
//        return function drawImage( context ) {
//            context.drawImage( image, 0, 0 );
//        };
//    }

    var rootNodeComponents = new Array(
            imageNode( "resources/red-mass.png", 114, 496 ),
            imageNode( "resources/green-mass.png", 210, 577 ),
            imageNode( "resources/gold-mass.png", 276, 541 ),
            imageNode( "resources/gram-50.png", 577, 590 ),
            imageNode( "resources/gram-100.png", 392, 562 ),
            imageNode( "resources/gram-250.png", 465, 513 ),
            imageNode( "resources/ruler.png", 12, 51 ),
            fillRectNode( 200, 200, "rgb(10, 30, 200)" ),
            textNode( "hello" ),
            vbox( {children:new Array( textNode( "label" ), textNode( "bottom" ) ), x:200, y:200} ),
            vbox( {children:new Array( checkbox( 0, 0 ), checkbox( 0, 0 ), checkbox( 0, 0 ) ), x:600, y:0} ) );

    //Add to the nodes for rendering
    for ( var i = 0; i < globals.springs.length; i++ ) {
        rootNodeComponents.push( globals.springs[i] );
    }

    rootNode = compositeNode( rootNodeComponents );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();

    // Start the game loop
    animate();
}

// Handler for window resize events.
function resizer() {
    console.log( "resize received" );
    canvas.width = $( window ).width();
    canvas.height = $( window ).height();
    draw();
}

function clearBackground() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}
// Main drawing function.
function draw() {
    clearBackground();
    if ( rootNode != null ) {
        rootNode.draw( context );
    }
}

function onDocumentMouseDown( event ) {
    onTouchStart( {x:event.clientX, y:event.clientY} );
}

function onDocumentMouseUp( event ) {
    onTouchEnd( {x:event.clientX, y:event.clientY} );
}

function onDocumentMouseMove( event ) {
    onDrag( {x:event.clientX, y:event.clientY} );
}

function onDocumentTouchStart( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onTouchStart( {x:event.touches[0].pageX, y:event.touches[0].pageY} );
    }
}

function onDocumentTouchMove( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onDrag( {x:event.touches[0].pageX, y:event.touches[0].pageY} );
    }
}

function onDocumentTouchEnd( event ) {
    onTouchEnd( {x:event.clientX, y:event.clientY} );
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    rootNode.onTouchStart( location );
}

function onDrag( location ) {
    rootNode.onTouchMove( location );
    draw();
}

function onTouchEnd( point ) {
    rootNode.onTouchEnd( point );
    draw();
}

var count = 0;
function animate() {

    //http://animaljoy.com/?p=254
    // insert your code to update your animation here

    for ( i = 0; i < globals.springs.length; i++ ) {
        globals.springs[i].attachmentPoint.y = 300 + 100 * Math.sin( 6 * count / 100.0 );
    }
    count++;
    requestAnimationFrame( animate );
    draw();
}

/**
 * Provides requestAnimationFrame in a cross browser way.
 * https://gist.github.com/838785
 * @author paulirish / http://paulirish.com/
 */

if ( !window.requestAnimationFrame ) {
    window.requestAnimationFrame = ( function () {
        return window.webkitRequestAnimationFrame ||
               window.mozRequestAnimationFrame ||
               window.oRequestAnimationFrame ||
               window.msRequestAnimationFrame ||
               function ( /* function FrameRequestCallback */ callback, /* DOMElement Element */ element ) {
                   window.setTimeout( callback, 1000 / 60 );
               };
    } )();
}

function spring( name, x ) {
    var that = rectangularNode( 0, 0 );
    that.name = name;
    that.anchor = new Point2D( x, 50 );
    that.attachmentPoint = new Point2D( x, 250 );
    that.draw = function ( context ) {
        context.beginPath();
        context.fillStyle = '#00f';
        context.strokeStyle = '#f00';
        context.lineWidth = 4;
        context.beginPath();
        context.moveTo( this.anchor.x, this.anchor.y );
        const numZigs = 10;
        const zigHeight = -(this.attachmentPoint.y - this.anchor.y) / numZigs / 2;
        //    javascript: console.log( "zig Height = " + zigHeight );

        var pt = new Point2D( this.anchor.x, this.anchor.y );
        for ( var i = 0; i < numZigs; i++ ) {
            var pt2 = new Point2D( pt.x + 10, pt.y - zigHeight );
            var pt3 = new Point2D( pt2.x - 10, pt2.y - zigHeight );
            context.lineTo( pt2.x, pt2.y );
            context.lineTo( pt3.x, pt3.y );
            pt = pt3;
        }
        context.stroke();
        context.closePath();

        const textHeight = 32;
        context.font = textHeight + "px sans-serif";
        const defaultTextAlign = context.textAlign;
        context.textAlign = "center";
        context.fillText( this.name, this.anchor.x, this.anchor.y - 32, 1000 );
        context.textAlign = defaultTextAlign;
    }
    return that;
}

function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
}

Point2D.prototype.toString = function () {
    return this.x + ", " + this.y;
}

Point2D.prototype.setComponents = function ( x, y ) {
    this.x = x;
    this.y = y;
}

Point2D.prototype.minus = function ( pt ) {
    return new Point2D( this.x - pt.x, this.y - pt.y );
}

Point2D.prototype.minus = function ( pt ) {
    return new Point2D( this.x + pt.x, this.y + pt.y );
}

Point2D.prototype.set = function ( point2D ) {
    this.setComponents( point2D.x, point2D.y );
}

function checkbox( x, y ) {
    var that = rectangularNode( 30, 30 );
    var that = rectangularNode( 30, 30 );
    that.x = x;
    that.y = y;
    that.checkboxSelected = true;
    that.draw = function ( context ) {
        context.fillStyle = '#fff';
        context.strokeStyle = '#88e';
        context.lineWidth = 2;
        roundRect( context, this.x, this.y, this.width, this.height, 7, true, true )

        context.strokeStyle = '#000';
        context.lineWidth = 4;
        if ( this.checkboxSelected ) {
            context.beginPath();
            context.beginPath();
            context.strokeStyle = '#222';
            context.moveTo( this.x + 5, this.y + 5 );
            context.lineTo( this.x + this.width - 5, this.y + this.width - 5 );
            context.moveTo( this.x + this.width - 5, this.y + 5 );
            context.lineTo( this.x + 5, this.y + this.height - 5 );
            context.stroke();
            context.closePath();
        }
    }

    that.onTouchStart = function ( point ) {

        //todo factor out
        var contains = point.x >= that.x && point.x <= that.x + that.width && point.y >= that.y && point.y <= that.y + that.height;
        if ( contains ) {
            this.checkboxSelected = !this.checkboxSelected;
            draw();
        }
    }
    return that;
}

/**
 * Draws a rounded rectangle using the current state of the canvas.
 * If you omit the last three params, it will draw a rectangle
 * outline with a 5 pixel border radius
 * @param {CanvasRenderingContext2D} ctx
 * @param {Number} x The top left x coordinate
 * @param {Number} y The top left y coordinate
 * @param {Number} width The width of the rectangle
 * @param {Number} height The height of the rectangle
 * @param {Number} radius The corner radius. Defaults to 5;
 * @param {Boolean} fill Whether to fill the rectangle. Defaults to false.
 * @param {Boolean} stroke Whether to stroke the rectangle. Defaults to true.
 *
 * @author http://js-bits.blogspot.com/2010/07/canvas-rounded-corner-rectangles.html
 */
function roundRect( ctx, x, y, width, height, radius, fill, stroke ) {
    if ( typeof stroke == "undefined" ) {
        stroke = true;
    }
    if ( typeof radius === "undefined" ) {
        radius = 5;
    }
    ctx.beginPath();
    ctx.moveTo( x + radius, y );
    ctx.lineTo( x + width - radius, y );
    ctx.quadraticCurveTo( x + width, y, x + width, y + radius );
    ctx.lineTo( x + width, y + height - radius );
    ctx.quadraticCurveTo( x + width, y + height, x + width - radius, y + height );
    ctx.lineTo( x + radius, y + height );
    ctx.quadraticCurveTo( x, y + height, x, y + height - radius );
    ctx.lineTo( x, y + radius );
    ctx.quadraticCurveTo( x, y, x + radius, y );
    ctx.closePath();
    if ( stroke ) {
        ctx.stroke();
    }
    if ( fill ) {
        ctx.fill();
    }
}
// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [
             'easel',
             'common/Logger',
             'common/ModelViewTransform2D',
             'model/FaradayModel',
             'view/ControlPanel',
             'view/FaradayStage'
         ],
         function ( Easel, Logger, ModelViewTransform2D, FaradayModel, ControlPanel, FaradayStage ) {

             Logger.enabled = true;

             // Canvas --------------------------------------------------------------------

             var canvas = document.getElementById( 'faraday-canvas' );

             // Get rid of canvas text cursor by disabling text selection.
             // See http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
             canvas.onselectstart = function () {
                 return false;
             }; // IE
             canvas.onmousedown = function () {
                 return false;
             }; // Mozilla

             // MVC --------------------------------------------------------------------

             var model = new FaradayModel( canvas.width, canvas.height );
             var stage = new FaradayStage( canvas, model );
             ControlPanel.connect( model, stage );

             // Animation loop ----------------------------------------------------------

             Easel.Ticker.addListener( stage );
             Easel.Ticker.setFPS( 60 );
             Easel.Touch.enable( stage, false, false );
         } );

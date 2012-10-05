(function () {

    /**
     * This function will be called to let you define new scenes that will be
     * shown after the splash screen.
     * @param director
     */
    function createScenes( director ) {
        var scene = director.createScene();

        for ( var i = 0; i < 30; i++ ) {
            var w = 30 + (100 * Math.random()) >> 0;
            var r = (255 * Math.random()) >> 0;
            var g = (255 * Math.random()) >> 0;
            var b = (255 * Math.random()) >> 0;
            scene.addChild(
                    new CAAT.Actor().
                            setBounds(
                            (director.width * Math.random()) >> 0,
                            (director.height * Math.random()) >> 0,
                            w,
                            w ).
                            setFillStyle( 'rgb(' + r + ',' + g + ',' + b + ')' )
            );
            scene.addChild(
                    new CAAT.Actor().
                            setBackgroundImage( director.getImage( 'shaker' ), true )
            );
        }
    }

    /**
     * Startup it all up when the document is ready.
     * Change for your favorite frameworks initialization code.
     */
    window.addEventListener(
            'load',
            function () {
                CAAT.modules.initialization.init(
                        /* canvas will be 800x600 pixels */
                        window.innerWidth, window.innerHeight,

                        /* and will be added to the end of document. set an id of a canvas or div element */
                        undefined,

                        /*
                         load these images and set them up for non splash scenes.
                         image elements must be of the form:
                         {id:'<unique string id>',    url:'<url to image>'}

                         No images can be set too.
                         */
                        [
                            {id:'shaker', url:'resources/shaker.png'}
                        ],

                        /*
                         onEndSplash callback function.
                         Create your scenes on this method.
                         */
                        createScenes

                );
            },
            false );
})();
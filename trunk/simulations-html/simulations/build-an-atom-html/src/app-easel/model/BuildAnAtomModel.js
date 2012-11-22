// Copyright 2002-2012, University of Colorado
define( [
            'model/atom',
            'model/Bucket',
            'model/particle'
        ], function ( Atom, Bucket, Particle ) {

    function BuildAnAtomModel() {

        this.atom = new Atom();

        this.buckets = {
            protonBucket:new Bucket( 105, 600, 150, "Protons" ),
            neutronBucket:new Bucket( 305, 600, 150, "Neutrons" ),
            electronBucket:new Bucket( 505, 600, 150, "Electrons" )
        };

        this.particles = [ ];

        this.initializeParticles();
    }

    BuildAnAtomModel.prototype.initializeParticles = function () {
        this.particles.push( new Particle(  this.buckets.protonBucket.x, this.buckets.protonBucket.y, "red", 15, "proton" ));
    };

    return BuildAnAtomModel;
} );
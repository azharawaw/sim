// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'common/Utils',
            'model/atom',
            'model/Bucket',
            'model/particle'
        ], function ( _, Utils, Atom, Bucket, Particle ) {

    var NUCLEON_DIAMETER = 15; // In pixels.
    var NUM_PROTONS = 5;
    var PROTON_COLOR = "red";
    var NUM_NEUTRONS = 5;
    var NEUTRON_COLOR = "gray";
    var PARTICLE_CAPTURE_RADIUS = 100;

    /**
     * Constructor for main model object.
     *
     * @constructor
     */
    function BuildAnAtomModel() {

        this.atom = new Atom( 0, 0 );

        this.buckets = {
            protonBucket:new Bucket( -200, 300, 150, "Protons" ),
            neutronBucket:new Bucket( 0, 300, 150, "Neutrons" ),
            electronBucket:new Bucket( 200, 300, 150, "Electrons" )
        };

        this.nucleons = [];
        var self = this;

        // Add the protons.
        _.times( NUM_PROTONS, function () {
            var proton = new Particle( self.buckets.protonBucket.x, self.buckets.protonBucket.y, PROTON_COLOR, NUCLEON_DIAMETER, "proton" );
            self.nucleons.push( proton );
            self.buckets.protonBucket.addParticle( proton );
            proton.events.on( 'userReleased', function () {
                if ( Utils.distanceBetweenPoints( self.atom.xPos, self.atom.yPos, proton.x, proton.y ) < PARTICLE_CAPTURE_RADIUS ) {
                    self.atom.addParticle( proton );
                }
                else {
                    self.buckets.protonBucket.addParticle( proton );
                }
            } );
        } );

        // Add the neutron.
        _.times( NUM_NEUTRONS, function () {
            var neutron = new Particle( self.buckets.neutronBucket.x, self.buckets.protonBucket.y, NEUTRON_COLOR, NUCLEON_DIAMETER, "neutron" );
            self.nucleons.push( neutron );
            self.buckets.neutronBucket.addParticle( neutron );
            neutron.events.on( 'userReleased', function () {
                if ( Utils.distanceBetweenPoints( self.atom.xPos, self.atom.yPos, neutron.x, neutron.y ) < PARTICLE_CAPTURE_RADIUS ) {
                    self.atom.addParticle( neutron );
                }
                else {
                    self.buckets.neutronBucket.addParticle( neutron );
                }
            } );
        } );
    }

    return BuildAnAtomModel;
} );

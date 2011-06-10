// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Model for a water molecule, including mappings between box2d physics engine and SI coordinates
 *
 * @author Sam Reid
 */
public class WaterMolecule implements Removable, Particle {
    public final double oxygenRadius = 1E-10;
    public final double hydrogenRadius = 0.5E-10;

    public final Body body;
    private final ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    private final ArrayList<Atom> atoms = new ArrayList<Atom>();

    public WaterMolecule( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener ) {
        //Find the box2d coordinates
        final Point2D box2DLocation = transform.modelToView( x, y );
        Point2D box2DVelocity = transform.modelToView( vx, vy );

        //First create the body def at the right location
        BodyDef bodyDef = new BodyDef() {{
            position = new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() );
            angle = (float) theta;
        }};

        //Now create the Box2D body for physics updates
        body = world.createBody( bodyDef );

        atoms.add( new Atom( x, y, transform, oxygenRadius, body, 0, 0, -2, true ) );
        atoms.add( new Atom( x + 0.5E-10, y + 0.5E-10, transform, hydrogenRadius, body, 0.5E-10, 0.5E-10, 1, false ) );
        atoms.add( new Atom( x - 1E-10, y + 1E-10, transform, hydrogenRadius, body, -0.5E-10, 0.5E-10, 1, false ) );

        //Construct other hydrogen
        body.setMassFromShapes();

        //Set the velocity
        body.setLinearVelocity( new Vec2( (float) box2DVelocity.getX(), (float) box2DVelocity.getY() ) );

        addUpdateListener.apply( new VoidFunction0() {
            public void apply() {
                final ImmutableVector2D origin = new ImmutableVector2D( transform.viewToModel( body.getPosition().x, body.getPosition().y ) );
                for ( Atom atom : atoms ) {
                    atom.updatePosition( body.getAngle(), origin );
                }
            }
        } );
    }

    //Add a listener that will be notified when this water leaves the model
    public void addRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.add( removalListener );
    }

    //Notify listeners that this water molecule has left the model
    public void notifyRemoved() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
    }

    public Particle getOxygenParticle() {
        return atoms.get( 0 ).particle;
    }

    public Particle getH1Particle() {
        return atoms.get( 1 ).particle;
    }

    public Particle getH2Particle() {
        return atoms.get( 2 ).particle;
    }

    public Vec2 getBox2DPosition() {
        return getOxygenParticle().getBox2DPosition();
    }

    public double getCharge() {
        return 0;
    }

    public ImmutableVector2D getModelPosition() {
        return atoms.get( 0 ).particle.getModelPosition();
    }

    public void setModelPosition( ImmutableVector2D immutableVector2D ) {
        getOxygenParticle().setModelPosition( immutableVector2D );
    }

    public ImmutableVector2D getOxygenPosition() {
        return atoms.get( 0 ).position.get();
    }

    public ImmutableVector2D getHydrogen1Position() {
        return atoms.get( 1 ).position.get();
    }

    public ImmutableVector2D getHydrogen2Position() {
        return atoms.get( 2 ).position.get();
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

// displays an atom in the 3d view
public class AtomNode extends Geometry {
    public final Property<ImmutableVector3D> position;

    public AtomNode( final Property<ImmutableVector3D> position, final ColorRGBA color, AssetManager assetManager ) {
        super( "Atom", new Sphere( 32, 32, 2f ) {{
            setTextureMode( Sphere.TextureMode.Projected ); // better quality on spheres
            TangentBinormalGenerator.generate( this );           // for lighting effect
        }} );
        this.position = position;

        setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );

            setColor( "Diffuse", color );
            setFloat( "Shininess", 1f ); // [0,128]
        }} );

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                setLocalTranslation( (float) position.get().getX(), (float) position.get().getY(), (float) position.get().getZ() );
            }
        } );

        //rotate( 1.6f, 0, 0 );
    }
}

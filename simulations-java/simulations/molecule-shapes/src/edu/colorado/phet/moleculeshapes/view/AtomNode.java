// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.model.ElectronPair;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

public class AtomNode extends Geometry {
    public final ElectronPair pair;

    public AtomNode( final ElectronPair pair, AssetManager assetManager ) {
        super( "Atom", new Sphere( 32, 32, 2f ) {{
            setTextureMode( Sphere.TextureMode.Projected ); // better quality on spheres
            TangentBinormalGenerator.generate( this );           // for lighting effect
        }} );
        this.pair = pair;

        setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );

            setColor( "Diffuse", pair.getColor() );
            setFloat( "Shininess", 1f ); // [0,128]
        }} );

        // update based on electron pair position
        pair.position.addObserver( new SimpleObserver() {
            public void update() {
                setLocalTranslation( (float) pair.position.get().getX(), (float) pair.position.get().getY(), (float) pair.position.get().getZ() );
            }
        } );

        //rotate( 1.6f, 0, 0 );
    }
}

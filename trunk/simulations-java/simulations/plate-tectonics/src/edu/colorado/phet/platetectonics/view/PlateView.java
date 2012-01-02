// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.Grid3D;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 */
public class PlateView extends GLNode {

    public PlateView( final PlateModel model, final PlateTectonicsTab module, final Grid3D grid ) {
        // add all of the terrain
        for ( Terrain terrain : model.getTerrains() ) {
            addChild( new TerrainNode( terrain, model, module ) ); // TODO: strip out model reference? a lot of unneeded stuff

            // only include water nodes if it wants water
            if ( terrain.hasWater() ) {
                addChild( new WaterNode( terrain, model, module ) );
            }
        }

        // add all of the regions
        for ( Region region : model.getRegions() ) {
            addChild( new RegionNode( region, model, module ) );
        }

        // handle regions when they are added
        model.regionAdded.addListener( new VoidFunction1<Region>() {
            @Override public void apply( Region region ) {
                addChild( new RegionNode( region, model, module ) );
            }
        } );

        // TODO: handle region removals
    }

    @Override protected void renderChildren( GLOptions options ) {
        // render children with a normal pass
        super.renderChildren( options );

        // then render them with the transparency pass
        GLOptions transparencyOptions = options.getCopy();
        transparencyOptions.renderPass = RenderPass.TRANSPARENCY;

        for ( GLNode child : getChildren() ) {
            child.render( transparencyOptions );
        }
    }
}

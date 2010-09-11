package edu.colorado.phet.densityandbuoyancy.view.modes {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;

public class SameDensityMode extends Mode {

    public function SameDensityMode( module:AbstractDensityModule ) {
        super( module );
    }

    override public function init():void {
        super.init();
        const model:DensityModel = module.model;
        var density:Number = 800; //Showing the blocks as partially floating allows easier visualization of densities
        var block1:Block = Block.newBlockDensityMass( density, 3, 0, 0, DensityConstants.YELLOW, model, Material.CUSTOM );
        block1.setPosition( -DensityConstants.POOL_WIDTH_X / 2, block1.getHeight() / 2 );
        model.addDensityObject( block1 );

        var block2:Block = Block.newBlockDensityMass( density, 2, 0, 0, DensityConstants.BLUE, model, Material.CUSTOM );
        block2.setPosition( -DensityConstants.POOL_WIDTH_X / 2 - block1.getWidth(), block2.getHeight() / 2 );
        model.addDensityObject( block2 );

        var block3:Block = Block.newBlockDensityMass( density, 1, 0, 0, DensityConstants.GREEN, model, Material.CUSTOM );
        block3.setPosition( DensityConstants.POOL_WIDTH_X / 2, block3.getHeight() / 2 );
        model.addDensityObject( block3 );

        var block4:Block = Block.newBlockDensityMass( density, 0.5, 0, 0, DensityConstants.RED, model, Material.CUSTOM );
        block4.setPosition( DensityConstants.POOL_WIDTH_X / 2 + block3.getWidth(), block4.getHeight() / 2 );
        model.addDensityObject( block4 );
    }
}
}
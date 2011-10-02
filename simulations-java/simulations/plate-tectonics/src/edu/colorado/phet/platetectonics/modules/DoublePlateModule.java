// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.test.AnimatedPlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;

import com.jme3.renderer.Camera;

public class DoublePlateModule extends PlateTectonicsModule {

    private PlateModel model;
    private PlateView plateView;

    public DoublePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.DOUBLE_PLATE__TITLE );
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
        plateView.updateView();
//        terrainNode.rotate( tpf, 0, 0 );
    }

    @Override public void initialize() {
        super.initialize();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D( new Bounds3D(
                -100000,
                -100000,
                -50000,
                200000,
                200000,
                50000 ), 512, 512, 32 );

        // create the model and terrain
        model = new AnimatedPlateModel();
        plateView = new PlateView( model, this, grid );
        mainView.getScene().attachChild( plateView );
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }
}

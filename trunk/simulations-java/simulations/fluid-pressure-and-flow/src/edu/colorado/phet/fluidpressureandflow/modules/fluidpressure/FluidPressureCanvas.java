package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.view.*;

/**
 * @author Sam Reid
 */
public class FluidPressureCanvas extends FluidPressureAndFlowCanvas {
    private FluidPressureModule module;

    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 2.2;
    private static final double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( module, new ModelViewTransform2D( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true ) );
        this.module = module;

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
        addChild( new PhetPPath( transform.createTransformedShape( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.lightGray ) );//so earth doesn't bleed through transparent pool
        addChild( new PoolHeightReadoutNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().getDistanceUnitProperty() ) );
        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) );
        }
        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( module.getFluidPressureAndFlowModel().getPool().getMinX(), module.getFluidPressureAndFlowModel().getPool().getMinY() );
        addChild( new MeterStick( transform, module.getMeterStickVisibleProperty(), rulerModelOrigin ) );
        addChild( new EnglishRuler( transform, module.getYardStickVisibleProperty(), rulerModelOrigin ) );

        final PoolNode poolNode = new PoolNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().getLiquidDensityProperty() );

        addChild( poolNode );

        // Control Panel
        final ControlPanel controlPanelNode = new ControlPanel( new FluidPressureControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetButton( module ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
        }} );
        addControls( new Point2D.Double( poolNode.getFullBounds().getMinX(), poolNode.getFullBounds().getMaxY() ) );
    }

}

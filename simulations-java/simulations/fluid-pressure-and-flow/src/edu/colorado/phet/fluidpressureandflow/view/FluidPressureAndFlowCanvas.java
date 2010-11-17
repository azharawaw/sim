package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.ButtonExpander;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas<T extends FluidPressureAndFlowModel> extends PhetPCanvas {
    protected final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;
    private final FluidPressureAndFlowModule<T> module;

    public FluidPressureAndFlowCanvas( final FluidPressureAndFlowModule module, ModelViewTransform2D transform ) {
        this.module = module;
        this.transform = transform;
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );
    }

    protected static void makeTransparent( JComponent component ) {
        if ( !( component instanceof JTextComponent ) ) {
            component.setBackground( new Color( 0, 0, 0, 0 ) );
            component.setOpaque( false );
        }
        for ( Component component1 : component.getComponents() ) {
            if ( component1 instanceof JComponent ) {
                makeTransparent( (JComponent) component1 );
            }
        }
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    public static class ControlPanel extends PNode {
        public ControlPanel( JComponent controlPanel ) {
            final PSwing pswing = new PSwing( controlPanel );
            addChild( pswing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, pswing.getFullBounds().getWidth(), pswing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.darkGray ) );
        }
    }

    public void addControls( final Point2D layoutPoint ) {
        final SliderControl fluidDensityControl = new SliderControl( "Fluid Density", "kg/m^3", FluidPressureAndFlowModel.GASOLINE_DENSITY, FluidPressureAndFlowModel.HONEY_DENSITY, module.getFluidPressureAndFlowModel().getLiquidDensityProperty(), new HashMap<Double, TickLabel>() {{
            put( FluidPressureAndFlowModel.GASOLINE_DENSITY, new TickLabel( "gasoline" ) );
            put( FluidPressureAndFlowModel.WATER_DENSITY, new TickLabel( "water" ) );
            put( FluidPressureAndFlowModel.HONEY_DENSITY, new TickLabel( "honey" ) );
        }} ) {{
            setOffset( layoutPoint.getX() - getFullBounds().getWidth() - 2, layoutPoint.getY() - getFullBounds().getHeight() );
            module.getFluidDensityControlVisible().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.getFluidDensityControlVisible().getValue() );
                }
            } );
        }};

        final SliderControl gravityControl = new SliderControl( "Gravity", "m/s^2", FluidPressureAndFlowModel.MOON_GRAVITY, FluidPressureAndFlowModel.JUPITER_GRAVITY, module.getFluidPressureAndFlowModel().getGravityProperty(), new HashMap<Double, TickLabel>() {{
            put( FluidPressureAndFlowModel.EARTH_GRAVITY, new TickLabel( "Earth" ) );
            put( FluidPressureAndFlowModel.MOON_GRAVITY, new TickLabel( "Moon" ) );
            put( FluidPressureAndFlowModel.JUPITER_GRAVITY, new TickLabel( "Jupiter" ) );
        }} ) {{
            module.getGravityControlVisible().addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( module.getGravityControlVisible().getValue() );
                }
            } );
        }};


        addChild( fluidDensityControl );

        final ButtonExpander fluidDensityExpander = new ButtonExpander( "Fluid Density >", "Fluid Density <", module.getFluidDensityControlVisible() ) {{
            setOffset( fluidDensityControl.getFullBounds().getX(), fluidDensityControl.getFullBounds().getY() - getFullBounds().getHeight() );
        }};
        addChild( fluidDensityExpander );
        gravityControl.setOffset( fluidDensityExpander.getFullBounds().getMinX(), fluidDensityExpander.getFullBounds().getY() - gravityControl.getFullBounds().getHeight() - 20 );

        final ButtonExpander gravityExpander = new ButtonExpander( "Gravity >", "Gravity <", module.getGravityControlVisible() ) {{
            setOffset( gravityControl.getFullBounds().getX(), gravityControl.getFullBounds().getY() - getFullBounds().getHeight() );
        }};
//        addChild( gravityExpander );
//        addChild( gravityControl );
    }
}

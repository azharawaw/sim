// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.intro.*;
import edu.colorado.phet.bendinglight.view.BendingLightWavelengthControl;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Or;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MoreToolsCanvas extends IntroCanvas<MoreToolsModel> {
    public MoreToolsCanvas( MoreToolsModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, resetAll, new Function3<IntroModel, Double, Double, PNode>() {
            public PNode apply( IntroModel introModel, final Double x, final Double y ) {
                return new BendingLightWavelengthControl( introModel.wavelengthProperty, introModel.getLaser().color ) {{
                    setOffset( x, y );
                }};
            }
        }, 0, new Or( new ValueEquals<LaserView>( model.laserView, LaserView.WAVE ), model.waveSensor.visible ) );
    }

    @Override protected PNode[] getMoreTools( ResetModel resetModel ) {

        final VelocitySensorNode velocitySensorNode = new VelocitySensorNode( transform, new VelocitySensor() );
        final Property<Boolean> showVelocitySensor = new Property<Boolean>( false );
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                showVelocitySensor.reset();
            }
        } );
        Function1<Rectangle2D.Double, Boolean> container = new Function1<Rectangle2D.Double, Boolean>() {
            public Boolean apply( Rectangle2D.Double bounds ) {
                return getToolboxNode().getGlobalFullBounds().contains( new Point2D.Double( bounds.getCenterX(), bounds.getCenterY() ) );
            }
        };
        final MoreToolsModel m = (MoreToolsModel) model;//todo: use generics to remove this cast
        final Tool velocityTool = new Tool( velocitySensorNode.toImage( ToolboxNode.ICON_WIDTH, (int) ( velocitySensorNode.getFullBounds().getHeight() / velocitySensorNode.getFullBounds().getWidth() * ToolboxNode.ICON_WIDTH ), new Color( 0, 0, 0, 0 ) ),
                                            showVelocitySensor,
                                            transform, container, this, new Tool.NodeFactory() {
                    public DraggableNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D modelPt ) {
                        m.velocitySensor.position.setValue( new ImmutableVector2D( modelPt ) );
                        return new VelocitySensorNode( transform, m.velocitySensor ) {{
                            showTool.addObserver( new VoidFunction1<Boolean>() {
                                public void apply( Boolean visible ) {
                                    setVisible( visible );
                                }
                            } );
                        }};
                    }
                }, resetModel );


        final Function1.Constant<ImmutableVector2D, Option<Double>> value = new Function1.Constant<ImmutableVector2D, Option<Double>>( new Option.None<Double>() );
        final WaveSensorNode waveSensorNode = new WaveSensorNode( transform, new WaveSensor( new ConstantDtClock(), value, value ) );
        final WaveSensor waveSensor = m.waveSensor;
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                waveSensor.visible.reset();
            }
        } );
        final Tool waveTool = new Tool( waveSensorNode.toImage( ToolboxNode.ICON_WIDTH, (int) ( waveSensorNode.getFullBounds().getHeight() / waveSensorNode.getFullBounds().getWidth() * ToolboxNode.ICON_WIDTH ), new Color( 0, 0, 0, 0 ) ),
                                        waveSensor.visible,
                                        transform, container, this, new Tool.NodeFactory() {
                    public DraggableNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D model ) {
                        waveSensor.translateToHotSpot( model );
                        return new WaveSensorNode( transform, waveSensor ) {{
                            showTool.addObserver( new SimpleObserver() {
                                public void update() {
                                    setVisible( showTool.getValue() );
                                }
                            } );
                        }};
                    }
                }, resetModel );

        return new PNode[] { velocityTool, waveTool };
    }
}

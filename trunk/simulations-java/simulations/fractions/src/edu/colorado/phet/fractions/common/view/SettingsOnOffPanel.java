// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import fj.F;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.fractionmatcher.view.PaddedIcon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractions.common.view.SettingsOnOffPanel.Element.nodes;

/**
 * Shows nodes in a grid.
 *
 * @author Sam Reid
 */
public class SettingsOnOffPanel extends PNode {
    public static final @Data class Element {
        public final PNode off;
        public final PNode on;
        public final BooleanProperty onProperty;
        public static final F<Element, List<PNode>> nodes = new F<Element, List<PNode>>() {
            @Override public List<PNode> f( final Element element ) {
                return List.list( element.on, element.off );
            }
        };
    }

    public SettingsOnOffPanel( final List<Element> icons ) {
        final List<PNode> all = icons.bind( nodes );
        List<Element> padded = icons.map( new F<Element, Element>() {
            @Override public Element f( final Element element ) {
                return new Element( new PaddedIcon( PaddedIcon.getMaxSize( all ), element.off ),
                                    new PaddedIcon( PaddedIcon.getMaxSize( all ), element.on ), element.onProperty );
            }
        } );
        VBox box = new VBox( 4 );
        for ( final P2<Element, Integer> e : padded.zipIndex() ) {
            final Element element = e._1();
            final PBasicInputEventHandler toggle = new PBasicInputEventHandler() {
                @Override public void mouseReleased( final PInputEvent event ) {
                    element.onProperty.toggle();
                }
            };
            element.on.addInputEventListener( toggle );
            element.on.addInputEventListener( new CursorHandler() );
            element.off.addInputEventListener( toggle );
            element.off.addInputEventListener( new CursorHandler() );
            PNode offButton = new PSwing( new PropertyRadioButton<Boolean>( null, "off", element.onProperty, false ) {{
                setFont( new PhetFont( 20, true ) );
                setOpaque( false );
            }} );
            PNode onButton = new PSwing( new PropertyRadioButton<Boolean>( null, "on", element.onProperty, true ) {{
                setFont( new PhetFont( 20, true ) );
                setOpaque( false );
            }} );
            box.addChild( new HBox( 4, new PNode() {{
                addChild( element.on );
                addChild( element.off );
                element.onProperty.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( final Boolean on ) {
                        if ( on ) {
                            element.on.animateToTransparency( 1, 300 );
                            element.off.animateToTransparency( 0, 300 );
                        }
                        else {
                            element.on.animateToTransparency( 0, 300 );
                            element.off.animateToTransparency( 1, 300 );
                        }
                    }
                } );
            }}, offButton, onButton ) );
            element.on.setTransparency( element.onProperty.get() ? 1 : 0 );
            element.off.setTransparency( element.onProperty.get() ? 0 : 1 );

            if ( e._2() < padded.length() - 1 ) {
                box.addChild( new PhetPPath( new Line2D.Double( 0, 0, box.getFullWidth(), 0 ) ) );
            }
        }
        addChild( new ZeroOffsetNode( box ) );
    }
}
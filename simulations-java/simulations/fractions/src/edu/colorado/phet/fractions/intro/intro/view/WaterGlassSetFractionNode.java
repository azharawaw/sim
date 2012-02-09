// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;

/**
 * Shows a fraction as a set of glasses of water.
 *
 * @author Sam Reid
 */
public class WaterGlassSetFractionNode extends VisibilityNode {

    public WaterGlassSetFractionNode( final Property<ContainerState> containerState, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        new RichSimpleObserver() {
            public void update() {

                //6 fit on the screen
                int distanceBetweenPies = 10;
                double spaceForPies = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetweenPies * 5;
                final double DIAMETER = spaceForPies / 6;

                removeAllChildren();
                SpacedHBox box = new SpacedHBox( DIAMETER + distanceBetweenPies );

                for ( int i = 0; i < containerState.get().numContainers; i++ ) {
                    final int container = i;
                    box.addChild( new WaterGlassNode( containerState.get().getContainer( i ).numFilledCells, containerState.get().getContainer( i ).numCells, new VoidFunction0() {
                        public void apply() {
                            CellPointer cp = new CellPointer( container, containerState.get().getContainer( container ).getLowestEmptyCell() );
                            containerState.set( containerState.get().toggle( cp ) );
                        }
                    }, new VoidFunction0() {
                        public void apply() {
                            CellPointer cp = new CellPointer( container, containerState.get().getContainer( container ).getHighestFullCell() );
                            containerState.set( containerState.get().toggle( cp ) );
                        }
                    }
                    ) );
                }

                addChild( box );
            }
        }.observe( containerState );
    }

    @Override public CellPointer getClosestOpenCell( Shape globalShape, Point2D center2D ) {
        return null;
    }
}
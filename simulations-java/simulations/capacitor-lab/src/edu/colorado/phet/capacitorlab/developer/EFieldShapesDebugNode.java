// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.developer;

import java.awt.*;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of E-Field.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 * This is a developer feature, not intended to be visible to the user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldShapesDebugNode extends PComposite {

    private static final Stroke STROKE = new BasicStroke( 2f );
    private static final Color STROKE_COLOR = CLPaints.EFIELD_DEBUG_SHAPES;

    public EFieldShapesDebugNode( final ICircuit circuit ) {

        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );

        // capacitor
        for ( Capacitor capacitor : circuit.getCapacitors() ) {

            final CapacitorShapeFactory shapeFactory = capacitor.getShapeFactory();

            final PPath dielectricBetweenPlatesNode = new PhetPPath( shapeFactory.createDielectricBetweenPlatesShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( dielectricBetweenPlatesNode );

            final PPath airBetweenPlatesNode = new PhetPPath( shapeFactory.createAirBetweenPlatesShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( airBetweenPlatesNode );

            //REVIEW: consider using RichSimpleObserver in cases like this
            // set shapes to match model
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    dielectricBetweenPlatesNode.setPathTo( shapeFactory.createDielectricBetweenPlatesShapeOccluded() );
                    airBetweenPlatesNode.setPathTo( shapeFactory.createAirBetweenPlatesShapeOccluded() );
                }
            };
            capacitor.addPlateSizeObserver( o );
            capacitor.addPlateSeparationObserver( o );
            capacitor.addDielectricOffsetObserver( o );
        }
    }
}

// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.Cursor;

import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {

    private final static float TRANSPARENCY = 0.75f;

    public static enum DielectricChargeView { NONE, TOTAL, EXCESS };

    private final Capacitor capacitor;

    private final Property<DielectricChargeView> dielectricChargeViewProperty;

    public DielectricNode( final Capacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange, DielectricChargeView dielectricChargeView,
            double maxExcessDielectricPlateCharge, double maxDielectricEField ) {
        super( mvt, capacitor.getDielectricMaterial().getColor(), capacitor.getDielectricSize() );

        this.capacitor = capacitor;

        // dielectric is directly draggable
        addInputEventListener( new CursorHandler( Cursor.E_RESIZE_CURSOR ) );
        addInputEventListener( new DielectricOffsetDragHandler( this, capacitor, mvt, valueRange ) );

        final DielectricTotalChargeNode totalChargeNode = new DielectricTotalChargeNode( capacitor, mvt, maxDielectricEField );
        addChild( totalChargeNode );

        final DielectricExcessChargeNode excessChargeNode = new DielectricExcessChargeNode( capacitor, mvt, maxExcessDielectricPlateCharge );
        addChild( excessChargeNode );

        dielectricChargeViewProperty = new Property<DielectricNode.DielectricChargeView>( dielectricChargeView );
        dielectricChargeViewProperty.addObserver( new SimpleObserver() {
            public void update() {
                totalChargeNode.setVisible( getDielectricChargeView() == DielectricChargeView.TOTAL );
                excessChargeNode.setVisible( getDielectricChargeView() == DielectricChargeView.EXCESS );
            }
        });

        // change color when dielectric material changes
        capacitor.addDielectricMaterialObserver( new SimpleObserver() {
            public void update() {
                setColor( DielectricNode.this.capacitor.getDielectricMaterial().getColor() );
            }
        });
    }

    public void reset() {
        dielectricChargeViewProperty.reset();
    }

    public void addDielectricChargeViewObserver( SimpleObserver o ) {
        dielectricChargeViewProperty.addObserver( o );
    }

    public Property<DielectricChargeView> getDielectricChargeViewProperty() {
        return dielectricChargeViewProperty;
    }

    public void setDielectricChargeView( DielectricChargeView dielectricChargeView ) {
        dielectricChargeViewProperty.setValue( dielectricChargeView );
    }

    public DielectricChargeView getDielectricChargeView() {
        return dielectricChargeViewProperty.getValue();
    }

    /**
     * Controls the opacity of the dielectric.
     * This is needed because the dielectric must be transparent to see E-field.
     * @param opaque
     */
    public void setOpaque( boolean opaque ) {
        float transparency = ( opaque ) ? 1f : TRANSPARENCY;
        /*
         * Some dielectric materials are naturally transparent.
         * Modify dielectric transparency only if it's not already transparent.
         */
        if ( capacitor.getDielectricMaterial().isOpaque() ) {
            setTransparency( transparency );
        }
    }
}

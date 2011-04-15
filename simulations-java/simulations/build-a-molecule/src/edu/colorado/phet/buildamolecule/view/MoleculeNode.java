package edu.colorado.phet.buildamolecule.view;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays the molecule name and 'X' to break apart the molecule
 */
public class MoleculeNode extends PNode {
    private Kit kit;
    private MoleculeStructure moleculeStructure;
    private ModelViewTransform mvt;

    private Map<AtomModel, SimpleObserver> observerMap = new HashMap<AtomModel, SimpleObserver>();

    private static final double PADDING_BETWEEN_NODE_AND_ATOM = 10;
    private HTMLNode commonNameLabel = null;
    private PNode closeNode;

    public MoleculeNode( final Kit kit, final MoleculeStructure moleculeStructure, ModelViewTransform mvt ) {
        this.kit = kit;
        this.moleculeStructure = moleculeStructure;
        this.mvt = mvt;

        if ( moleculeStructure.getAtoms().size() < 2 ) {
            // we don't need anything at all if it is not a "molecule"
            return;
        }

        CompleteMolecule completeMolecule = CompleteMolecule.findMatchingCompleteMolecule( moleculeStructure );

        if ( completeMolecule != null ) {
            commonNameLabel = new HTMLNode( completeMolecule.getCommonName() ) {{
                setFont( new PhetFont( 12 ) );
            }};
            addChild( commonNameLabel );
        }

        closeNode = new PNode() {{
            addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    kit.breakMolecule( moleculeStructure );
                }
            } );
        }};
        if ( commonNameLabel != null ) {
            closeNode.setOffset( commonNameLabel.getFullBounds().getWidth() + 10, 0 );
        }
        addChild( closeNode );

        for ( Atom atom : moleculeStructure.getAtoms() ) {
            final AtomModel atomModel = kit.getAtomModel( atom );
            atomModel.addPositionListener( new SimpleObserver() {
                {
                    observerMap.put( atomModel, this );
                }

                public void update() {
                    updatePosition();
                }
            } );
        }

        updatePosition(); // sanity check. should update (unfortunately) a number of times above
    }

    public void destruct() {
        for ( AtomModel atomModel : observerMap.keySet() ) {
            atomModel.removePositionListener( observerMap.get( atomModel ) );
        }
    }

    public void updatePosition() {
        PBounds modelPositionBounds = kit.getMoleculePositionBounds( moleculeStructure );
        Rectangle2D moleculeViewBounds = mvt.modelToView( modelPositionBounds ).getBounds2D();


        setOffset( moleculeViewBounds.getCenterX() - getFullBounds().getWidth() / 2, // horizontally center
                   moleculeViewBounds.getY() - getFullBounds().getHeight() - PADDING_BETWEEN_NODE_AND_ATOM ); // offset from top of molecule
    }
}

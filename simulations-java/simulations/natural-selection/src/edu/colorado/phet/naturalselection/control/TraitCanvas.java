package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class TraitCanvas extends PhetPCanvas {

    private PNode rootNode;

    public static Dimension canvasSize = new Dimension( 530, 300 );
    private BigVanillaBunny bunny;
    private TraitControlNode earsTraitNode;
    public TraitControlNode tailTraitNode;
    private TraitControlNode eyesTraitNode;
    public TraitControlNode teethTraitNode;
    public TraitControlNode colorTraitNode;

    public TraitCanvas() {
        super( canvasSize );

        rootNode = new PNode();
        addWorldChild( rootNode );

        bunny = new BigVanillaBunny();
        bunny.translate( 200, 150 );
        rootNode.addChild( bunny );

        PText traitsText = new PText( "Traits" );
        traitsText.setFont( new PhetFont( 16, true ) );
        traitsText.translate( 200 + ( 86 - traitsText.getWidth() ) / 2, 260 );
        rootNode.addChild( traitsText );

        earsTraitNode = new EarsTraitNode();
        earsTraitNode.translate( 30, 60 );
        drawConnectingLine( earsTraitNode );
        rootNode.addChild( earsTraitNode );

        tailTraitNode = new TailTraitNode();
        tailTraitNode.translate( 10, 210 );
        drawConnectingLine( tailTraitNode );
        rootNode.addChild( tailTraitNode );

        eyesTraitNode = new EyesTraitNode();
        eyesTraitNode.translate( 215, 40 );
        drawConnectingLine( eyesTraitNode );
        rootNode.addChild( eyesTraitNode );

        teethTraitNode = new TeethTraitNode();
        teethTraitNode.translate( 375, 85 );
        drawConnectingLine( teethTraitNode );
        rootNode.addChild( teethTraitNode );

        colorTraitNode = new ColorTraitNode();
        colorTraitNode.translate( 330, 210 );
        drawConnectingLine( colorTraitNode );
        rootNode.addChild( colorTraitNode );

        setPreferredSize( canvasSize );

        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void reset() {
        earsTraitNode.reset();
        tailTraitNode.reset();
        eyesTraitNode.reset();
        teethTraitNode.reset();
        colorTraitNode.reset();
    }

    private void drawConnectingLine( TraitControlNode traitNode ) {
        PPath node = new PPath();

        node.setStroke( new BasicStroke( 1f ) );
        node.setStrokePaint( Color.BLACK );

        Point2D bunnySpot = traitNode.getBunnyLocation( bunny );
        Point2D nodeCenter = traitNode.getCenter();

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( bunnySpot.getX(), bunnySpot.getY() );
        path.lineTo( nodeCenter.getX(), nodeCenter.getY() );
        node.setPathTo( path.getGeneralPath() );

        rootNode.addChild( node );
    }

    public void updateLayout() {

    }
}

package edu.colorado.phet.functions.buildafunction2;

import java.awt.Color;

/**
 * @author Sam Reid
 */
public class BuildAFunctionPrototype2Canvas extends AbstractFunctionsCanvas {

    public static final Color BACKGROUND_COLOR = new Color( 236, 251, 251 );

    public BuildAFunctionPrototype2Canvas() {

        //Set a really light blue because there is a lot of white everywhere
        setBackground( BACKGROUND_COLOR );

        addChild( new UnaryNumberFunctionNode( "*2" ) );
        addChild( new UnaryNumberFunctionNode( "+1" ) );
        addChild( new UnaryNumberFunctionNode( "-1" ) );
        addChild( new UnaryNumberFunctionNode( "^2" ) );

        addChild( new BinaryNumberFunctionNode( "+" ) );
        addChild( new BinaryNumberFunctionNode( "-" ) );
        addChild( new CopyNumberFunctionNode( "copy" ) );

        addChild( new NumberValueNode( 3 ) );
    }
}
/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.cck3.common.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.cck3.common.phetgraphics.MultiLineTextGraphic;
import edu.colorado.phet.cck3.common.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.help.HelpItem;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 12:04:51 AM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PositionedHelpItem extends CompositePhetGraphic {
    private Target target;
    private MultiLineTextGraphic textGraphic;
    private PhetShapeGraphic arrowGraphic;
    private Arrow arrow;
    private Color arrowColor = Color.blue;
    private boolean noTarget = false;

    public PositionedHelpItem( String text, Target target, Font font, Component component ) {
        super( component );
        this.target = target;
        String[] lines = HelpItem.tokenizeString( text );
        Point location = target.getTextLocation();
        if( location == null ) {
            location = new Point();
        }
        int x = location.x;
        int y = location.y;
        textGraphic = new MultiLineTextGraphic( component, lines, font, x, y, Color.blue, 1, 1, Color.yellow );
        target.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
        arrowGraphic = new PhetShapeGraphic( component, new Area(), arrowColor );
        addGraphic( arrowGraphic );
        addGraphic( textGraphic );
    }

    public void changed() {
        Point location = target.getTextLocation();
        if( location == null ) {
            noTarget = true;
        }
        else {
            noTarget = false;
        }
//        System.out.println( "PHI.location = " + location );
        if( location != null ) {
            int x = location.x;
            int y = location.y;
            textGraphic.setPosition( x, y );
            arrow = target.getArrow( textGraphic );
            arrowGraphic.setShape( arrow.getShape() );
        }
        if( isVisible() && !noTarget ) {
            super.setBoundsDirty();
            super.repaint();
        }

    }

    public Rectangle getTextBounds() {
        return textGraphic.getBounds();
    }

    public abstract static class Target extends SimpleObservable {
        public abstract Point getTextLocation();

        public abstract Arrow getArrow( MultiLineTextGraphic textGraphic );
    }
}

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.ProvisionalBond;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.*;

/**
 * ProvisionalBondGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondGraphic extends PNode implements SimpleObserver {
    private Stroke stroke = new BasicStroke( 3 );
//    private Stroke stroke = new BasicStroke( 3,
//                                             BasicStroke.CAP_ROUND,
//                                             BasicStroke.JOIN_BEVEL,
//                                             0,
//                                             new float[]{9},
//                                             0 );
    private Line2D line;
    private ProvisionalBond bond;

    public ProvisionalBondGraphic( ProvisionalBond bond ) {
        this.bond = bond;
        line = new Line2D.Double();
        PPath linePath = new PPath( line, stroke );
        linePath.setStrokePaint( Color.red );
        addChild( linePath );
        update();
    }

    public void update() {
        Point2D p1 = bond.getMolecules()[0].getPosition();
        Point2D p2 = bond.getMolecules()[1].getPosition();
        line.setLine( p1, p2 );
    }
}

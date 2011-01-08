// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a node that represents a periodic table of the elements.
 * It is not interactive by default, but provides overrides that can be used
 * to add interactivity.
 *
 * This makes some assumptions about which portions of the table to display,
 * and may not work for all situations.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class PeriodicTableNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static int CELL_DIMENSION = 20;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    public Color backgroundColor = null;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     * @param backgroundColor
     */
    public PeriodicTableNode( final IDynamicAtom atom, Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
        //See http://www.ptable.com/
        final PNode table = new PNode();
        for ( int i = 1; i <= 56; i++ ) {
            addElement( atom, table, i );
        }
        // Add in a single entry to represent the lanthanide series.
        addElement( atom, table, 57 );
        for ( int i = 71; i <= 88; i++ ) {
            addElement( atom, table, i );
        }
        // Add in a single entry to represent the actinide series.
        addElement( atom, table, 89 );
        for ( int i = 103; i <= 112; i++ ) {
            addElement( atom, table, i );
        }

        addChild( table );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void addElement( final IDynamicAtom atom, final PNode table, int atomicNumber ) {
        ElementCell elementCell = new ElementCell( atom, atomicNumber, backgroundColor );
        final Point gridPoint = getGridPoint( atomicNumber );
        double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
        double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
        elementCell.setOffset( x, y );
        table.addChild( elementCell );
        elementCellCreated( elementCell );
    }

    /**
     * Listener callback, override when needing notification of the creation
     * of element cells.  This is useful when creating an interactive chart,
     * since it is a good opportunity to hook up event listeners to the cell.
     *
     * @param elementCell
     */
    protected void elementCellCreated( ElementCell elementCell ) {
    }

    /**
     * Reports (row,column) on the grid, with a 1-index
     *
     * @param i
     * @return
     */
    private Point getGridPoint( int i ) {
        //http://www.ptable.com/ was useful here
        if ( i == 1 ) {
            return new Point( 1, 1 );
        }
        if ( i == 2 ) {
            return new Point( 1, 18 );
        }
        else if ( i == 3 ) {
            return new Point( 2, 1 );
        }
        else if ( i == 4 ) {
            return new Point( 2, 2 );
        }
        else if ( i >= 5 && i <= 10 ) {
            return new Point( 2, i + 8 );
        }
        else if ( i == 11 ) {
            return new Point( 3, 1 );
        }
        else if ( i == 12 ) {
            return new Point( 3, 2 );
        }
        else if ( i >= 13 && i <= 18 ) {
            return new Point( 3, i );
        }
        else if ( i >= 19 && i <= 36 ) {
            return new Point( 4, i - 18 );
        }
        else if ( i >= 37 && i <= 54 ) {
            return new Point( 5, i - 36 );
        }
        else if ( i >= 19 && i <= 36 ) {
            return new Point( 4, i - 36 );
        }
        else if ( i == 55 ) {
            return new Point( 6, 1 );
        }
        else if ( i == 56 ) {
            return new Point( 6, 2 );
        }
        else if ( i >= 57 && i <= 71 ) {
            return new Point( 6, 3 );
        }
        else if ( i >= 72 && i <= 86 ) {
            return new Point( 6, i - 68 );
        }
        else if ( i == 87 ) {
            return new Point( 7, 1 );
        }
        else if ( i == 88 ) {
            return new Point( 7, 2 );
        }
        else if ( i >= 89 && i <= 103 ) {
            return new Point( 7, 3 );
        }
        else if ( i >= 104 && i <= 118 ) {
            return new Point( 7, i - 100 );
        }
        return new Point( 1, 1 );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public class ElementCell extends PNode {
        private final int atomicNumber;
        private final PText text;
        private final PhetPPath box;
        private boolean disabledLooking = false;

        public ElementCell( final IDynamicAtom atom, final int atomicNumber, final Color backgroundColor ) {
            this.atomicNumber = atomicNumber;
            box = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ),
                    backgroundColor, new BasicStroke( 1 ), Color.black );
            addChild( box );

            String abbreviation = AtomIdentifier.getSymbol( atomicNumber );
            text = new PText( abbreviation );
            text.setOffset( box.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2,
                    box.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
            addChild( text );
            atom.addObserver( new SimpleObserver() {
                public void update() {
                    boolean match = atom.getNumProtons() == atomicNumber;
                    text.setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
                    if ( match ) {
                        box.setStroke( new BasicStroke( 2 ) );
                        box.setStrokePaint( Color.RED );
                        box.setPaint( Color.white );
                        ElementCell.this.moveToFront();
                    }
                    else {
                        if ( !disabledLooking ){
                            box.setStroke( new BasicStroke( 1 ) );
                            box.setStrokePaint( Color.BLACK );
                            box.setPaint( backgroundColor );
                        }
                        else{
                            text.setTextPaint( Color.LIGHT_GRAY );
                            box.setStrokePaint( Color.LIGHT_GRAY );
                        }
                    }
                }
            } );
        }

        public int getAtomicNumber() {
            return atomicNumber;
        }

        // FIXME: This is prototype.  Should come up with a better way to make the cells look disabled once we have
        // figured out how they should look.
        public void setDisabledLooking( boolean disabledLooking ){
            this.disabledLooking = disabledLooking;
        }
    }
}

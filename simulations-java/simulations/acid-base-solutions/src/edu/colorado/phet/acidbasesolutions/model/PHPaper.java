/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for pH paper.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaper extends SolutionRepresentation {
    
    private static final Color PAPER_COLOR = Color.WHITE;
    
    private final PDimension size;
    private final Beaker beaker;

    public PHPaper( AqueousSolution solution, Point2D location, boolean visible, PDimension size, Beaker beaker ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
        this.beaker = beaker;
    }
    
    public PDimension getSizeReference() {
        return size;
    }
    
    public double getWidth() {
        return size.getWidth();
    }
    
    public double getHeight() {
        return size.getHeight();
    }
    
    @Override
    public void setLocation( double x, double y ) {
        super.setLocation( constrainX( x ), constrainY( y ) );
    }
    
    /*
     * Constrains an x coordinate to be between the walls of the beaker.
     */
    private double constrainX( double requestedX ) {
        double min = beaker.getLocationReference().getX() - ( beaker.getWidth() / 2 ) + ( this.getWidth() / 2 );
        double max = beaker.getLocationReference().getX() + ( beaker.getWidth() / 2 ) - ( this.getWidth() / 2 );
        double x = requestedX;
        if ( x < min ) {
            x = min;
        }
        else if ( x > max ) {
            x = max;
        }
        return x;
    }
    
    /*
     * Constraints a y coordinate to be in or slightly above the solution.
     */
    private double constrainY( double requestedY ) {
        double min = beaker.getLocationReference().getY() - beaker.getHeight() - this.getHeight() - 20;
        double max = beaker.getLocationReference().getY() - this.getHeight() - 20;
        double y = requestedY;
        if ( y < min ) {
            y = min;
        }
        else if ( y > max ) {
            y = max;
        }
        return y;
    }
    
    /**
     * Gets the original color of the paper, before it's dipped in solution.
     * @return
     */
    public Color getColor() {
        return PAPER_COLOR;
    }
    
    /**
     * Gets the color of the paper when it's dipped in solution.
     * @return
     */
    public Color getDippedColor() {
        return createColor( getSolution().getPH() );
    }
    
    private Color createColor( double pH ) {
        return Color.RED; //TODO compute color based on pH, ROYGBIV color scheme
    }

}

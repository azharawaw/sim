// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Point2D;

/**
 * A horizontal surface on which objects within the model can be placed or
 * dropped.  It is basically a horizontal line, but it also includes an image
 * and additional information about how to represent it in the view.
 *
 * @author John Blanco
 */
public class Shelf {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Position of the left edge of the shelf in model coordinates.
    private final Point2D position;

    // Width of the shelf in model units (meters).
    private final double width;

    // Name of the image file that should be used to represent the shelf.
    private final String imageFileName;

    // In the view, the shelf is portrayed as having a 3D appearance.  These
    // variables are used to define how this is done.  Note that these must be
    // coordinated with the image of the shelf that is used.
    private final double thickness; // In meters.
    private final double foreshortenedHeight; // In meters.
    private final double perspectiveAngle; // In radians.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param position
     * @param width
     * @param imageFileName
     * @param thickness
     * @param foreshortenedHeight
     * @param perspectiveAngle
     */
    public Shelf( Point2D position, double width, String imageFileName, double thickness, double foreshortenedHeight, double perspectiveAngle ) {
        this.position = position;
        this.width = width;
        this.imageFileName = imageFileName;
        this.thickness = thickness;
        this.foreshortenedHeight = foreshortenedHeight;
        this.perspectiveAngle = perspectiveAngle;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public Point2D getPosition() {
        return position;
    }

    public double getWidth() {
        return width;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public double getThickness() {
        return thickness;
    }

    public double getForeshortenedHeight() {
        return foreshortenedHeight;
    }

    public double getPerspectiveAngle() {
        return perspectiveAngle;
    }
}

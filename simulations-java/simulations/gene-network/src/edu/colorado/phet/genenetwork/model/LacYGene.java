/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class LacYGene extends SimpleModelElement {

	private static final double WIDTH = 20;  // Nanometers. 
	private static final double HEIGHT = 4;  // Nanometers.
	
	public LacYGene(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 1, 1),
				new Point2D.Double(), new Color(138, 198, 118));
	}
	
    public LacYGene(IObtainGeneModelElements model, double x, double y) {
        this(model, new Point2D.Double(x,y));
    }

	public LacYGene(IObtainGeneModelElements model){
		this(model, new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_Y_GENE;
	}
	
	@Override
	public String getLabel() {
		// TODO: i18n
		return "LacY Gene";
	}
}

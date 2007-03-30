package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 1:38:23 PM
 */
public class SkaterCharacter {
    private String imageURL;
    private String name;
    private double mass;

    public SkaterCharacter( String imageURL, String name, double mass ) {
        this.imageURL = imageURL;
        this.name = name;
        this.mass = mass;
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    public String getImageURL() {
        return imageURL;
    }

    public BufferedImage getImage() {
        try {
            return ImageLoader.loadBufferedImage( getImageURL() );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}

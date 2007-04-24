
package edu.colorado.phet.cck.model.components;

import edu.colorado.phet.cck.model.*;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.BulbComponentGraphic;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 *
 */
public class Bulb extends CircuitComponent {
    private Filament filament;
    private SimpleObserver so;
    private double width;
    private boolean isSchematic = false;
    private boolean connectAtLeft = true;

    private static double filamentHeightScale = 1.0;

    public Bulb( Point2D start, AbstractVector2D dir,
                 double distBetweenJunctions,
                 double width, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, distBetweenJunctions, height );
        this.width = width;
        setKirkhoffEnabled( false );
        init( kl );
        setKirkhoffEnabled( true );
    }

    public Bulb( CircuitChangeListener kl, Junction startJunction, Junction endJunction, double width, double length, double height, boolean schematic ) {
        super( kl, startJunction, endJunction, length, height );
        this.width = width;
        super.setHeight( height );
        init( kl );
        setSchematic( schematic, null );
    }

    public static void setHeightScale( double heightScale ) {
        filamentHeightScale = heightScale;
    }

    private void init( CircuitChangeListener kl ) {
        double height = super.getHeight();
        filament = new Filament( kl, getStartJunction(), getEndJunction(), 3, height * filamentHeightScale, width * .8, height * .061 );
        so = new SimpleObserver() {
            public void update() {
                filament.recompute();
            }
        };
        addObserver( so );
        getStartJunction().addObserver( so );
        getEndJunction().addObserver( so );
        setResistance( 10 );
    }

    public double getWidth() {
        return width;
    }

    public void setStartJunction( Junction newJunction ) {
        super.setStartJunction( newJunction );
        filament.setStartJunction( newJunction );
        newJunction.addObserver( so );
    }

    public void setEndJunction( Junction newJunction ) {
        super.setEndJunction( newJunction );
        filament.setEndJunction( newJunction );
        newJunction.addObserver( so );
    }

    public Filament getFilament() {
        return filament;
    }

    public Point2D getPosition( double x ) {
        if( isSchematic ) {
            return super.getPosition( x );
        }
        if( containsScalarLocation( x ) ) {
            return filament.getPosition( x );
        }
        else {
            if( Double.isNaN( getLength() ) ) {
                throw new RuntimeException( "Length was NaN." );
            }
            //this occurs when dragging the bulb after splitting.  maybe splitting needs to relayout.
            Toolkit.getDefaultToolkit().beep();
            throw new RuntimeException( "position not within bulb: x=" + x + ", length=" + getLength() );
        }
    }

    public double getLength() {
        if( isSchematic ) {
            return getStartJunction().getPosition().distance( getEndJunction().getPosition() );
        }
        return filament.getLength();
    }

    public double getComponentLength() {
        if( isSchematic ) {
            return getLength();
        }
        else {
            return super.getLength();
        }
    }

    public void setSchematic( boolean schematic, Circuit circuit ) {
        if( this.isSchematic == schematic ) {
            return;
        }
        this.isSchematic = schematic;
        //move junctions, if necessary.
//        Vector2D delta = null;
        if( schematic ) {
            expandToSchematic( this, circuit );
        }
        else {
            collaspeToLifelike( this, circuit );
        }
    }

    private Vector2D collaspeToLifelike( Bulb bulb, Circuit circuit ) {
        double distBetweenJ = CCKModel.BULB_DIMENSION.getDistBetweenJunctions();
        AbstractVector2D vector = bulb.getDirectionVector().getInstanceOfMagnitude( distBetweenJ );
        Point2D dst = vector.getDestination( bulb.getStartJunction().getPosition() );
        Vector2D delta = new Vector2D.Double( bulb.getEndJunction().getPosition(), dst );
        if( circuit != null ) {
            Branch[] sc = circuit.getStrongConnections( bulb, bulb.getEndJunction() );
            BranchSet bs = new BranchSet( circuit, sc );
            bs.addJunction( bulb.getEndJunction() );
            bs.translate( delta );
        }
        else {
            bulb.getEndJunction().setPosition( dst.getX(), dst.getY() );
        }
        return delta;
    }

    private static void expandToSchematic( Bulb bulb, Circuit circuit ) {
        Vector2D vec = new Vector2D.Double( bulb.getStartJunction().getPosition(), bulb.getEndJunction().getPosition() );
        Point2D dst = vec.getInstanceOfMagnitude( CCKModel.SCH_BULB_DIST ).getDestination( bulb.getStartJunction().getPosition() );
        Vector2D delta = new Vector2D.Double( bulb.getEndJunction().getPosition(), dst );
        if( circuit != null ) {
            Branch[] sc = circuit.getStrongConnections( bulb, bulb.getEndJunction() );
            BranchSet bs = new BranchSet( circuit, sc );
            bs.addJunction( bulb.getEndJunction() );
            bs.translate( delta );
        }
        else {
            bulb.getEndJunction().setPosition( dst.getX(), dst.getY() );
        }
    }

    public boolean isSchematic() {
        return isSchematic;
    }

    public boolean isConnectAtLeft() {
        return connectAtLeft;
    }

    public void setConnectAtLeftXML( boolean connectAtRight ) {
        this.connectAtLeft = connectAtRight;
        filament.setConnectAtRight( connectAtRight );
    }

    public void flip( Circuit circuit ) {
        connectAtLeft = !connectAtLeft;
        filament.setConnectAtRight( connectAtLeft );
        double sign = -1;
        if( connectAtLeft ) {
            sign = 1;
        }
        double tilt = BulbComponentGraphic.determineTilt();
        AbstractVector2D vector = getDirectionVector();
        vector = vector.getRotatedInstance( tilt * 2 * sign );
        Point2D target = vector.getDestination( getStartJunction().getPosition() );
        Vector2D delta = new Vector2D.Double( getEndJunction().getPosition(), target );

        if( circuit != null ) {
            Branch[] sc = circuit.getStrongConnections( this, getEndJunction() );
            BranchSet bs = new BranchSet( circuit, sc );
            bs.addJunction( getEndJunction() );
            bs.translate( delta );
        }

    }

    public double getIntensity() {
        double power = Math.abs( getCurrent() * getVoltageDrop() );
        double maxPower = 60;
        if( power > maxPower ) {
            power = maxPower;
        }
        return Math.pow( power / maxPower, 0.354 );
    }

}

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Body {
    private final Property<ImmutableVector2D> positionProperty;
    private final Property<ImmutableVector2D> velocityProperty;
    private final Property<ImmutableVector2D> accelerationProperty;
    private final Property<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;

    public Body( String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight ) {
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        positionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        velocityProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( vx, vy ) );
        accelerationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        massProperty = new Property<Double>( mass );
        diameterProperty = new Property<Double>( diameter );
    }

    public Color getColor() {
        return color;
    }

    public Color getHighlight() {
        return highlight;
    }

    public Property<ImmutableVector2D> getPositionProperty() {
        return positionProperty;
    }

    public ImmutableVector2D getPosition() {
        return positionProperty.getValue();
    }

    public Property<Double> getDiameterProperty() {
        return diameterProperty;
    }

    public double getDiameter() {
        return diameterProperty.getValue();
    }

    public void translate( Point2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    public void translate( double dx, double dy ) {
        positionProperty.setValue( new ImmutableVector2D( getX() + dx, getY() + dy ) );
    }

    private double getY() {
        return positionProperty.getValue().getY();
    }

    private double getX() {
        return positionProperty.getValue().getX();
    }

    public String getName() {
        return name;
    }

    public void setDiameter( double value ) {
        diameterProperty.setValue( value );
    }

    public VelocityVerlet.BodyState toBodyState() {
        return new VelocityVerlet.BodyState( getPosition(), getVelocity(), getAcceleration(), getMass() );
    }

    private double getMass() {
        return massProperty.getValue();
    }

    private ImmutableVector2D getAcceleration() {
        return accelerationProperty.getValue();
    }

    private ImmutableVector2D getVelocity() {
        return velocityProperty.getValue();
    }

    public void setBodyState( VelocityVerlet.BodyState bodyState ) {
        positionProperty.setValue( bodyState.position );
        velocityProperty.setValue( bodyState.velocity );
        accelerationProperty.setValue( bodyState.acceleration );
    }
}

package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern;

/**
 * Target the user tries to create when creating numbers to match a given picture.
 *
 * @author Sam Reid
 */
public @Data class NumberTarget {
    public final Fraction fraction;
    public final Color color;
    public final List<FilledPattern> filledPattern;

    //Convenience for single pattern
    public static NumberTarget target( int numerator, int denominator, Color color, F<Fraction, FilledPattern> pattern ) {
        return new NumberTarget( new Fraction( numerator, denominator ), color, BuildAFractionModel.composite( pattern ).f( new Fraction( numerator, denominator ) ) );
    }

    public static NumberTarget target( Fraction fraction, Color color, F<Fraction, FilledPattern> pattern ) {
        return target( fraction.numerator, fraction.denominator, color, pattern );
    }
}
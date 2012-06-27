package edu.colorado.phet.fractionmatcher.model;

import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.intro.view.FractionNumberNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionmatcher.model.FillType.MIXED;
import static edu.colorado.phet.fractionmatcher.model.FillType.SEQUENTIAL;
import static edu.colorado.phet.fractionmatcher.model.ShapeType.*;
import static edu.colorado.phet.fractionmatcher.view.FilledPattern.randomFill;
import static edu.colorado.phet.fractionmatcher.view.FilledPattern.sequentialFill;
import static edu.colorado.phet.fractions.util.FJUtils.shuffle;
import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.fraction;
import static fj.data.List.*;

/**
 * @author Sam Reid
 */
class Levels {

    /**
     * Level 1
     * No mixed numbers
     * Only “exact” matches will be present.  So for instance if there is a 3/6  and a pie with 6 divisions and 3 shaded slices, there will not be a ½  present .  In other words, the numerical representation on this level will exactly match the virtual manipulative.
     * Only numbers/representations  ≦ 1 possible on this level
     * “Easy” shapes on this level (not some of the more abstract representations)
     */
    private static final List<Fraction> LEVEL_1_FRACTIONS = list( fraction( 1, 3 ), fraction( 2, 3 ),
                                                                  fraction( 1, 4 ), fraction( 3, 4 ), fraction( 1, 2 ),
                                                                  fraction( 1, 1 ) );

    /**
     * Level 2
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    private static final List<Fraction> LEVEL_2_FRACTIONS = list( fraction( 1, 2 ),
                                                                  fraction( 2, 4 ), fraction( 3, 4 ),
                                                                  fraction( 1, 3 ), fraction( 2, 3 ),
                                                                  fraction( 3, 6 ), fraction( 2, 6 ) );

    /**
     * Level 3:
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    private static final List<Fraction> LEVEL_3_FRACTIONS = list( fraction( 3, 2 ), fraction( 4, 2 ),
                                                                  fraction( 4, 3 ), fraction( 6, 3 ),
                                                                  fraction( 4, 5 ),
                                                                  fraction( 7, 4 ), fraction( 5, 4 ), fraction( 6, 4 ),
                                                                  fraction( 5, 6 ), fraction( 4, 6 ), fraction( 3, 6 ), fraction( 2, 6 ), fraction( 7, 6 ),
                                                                  fraction( 3, 8 ), fraction( 4, 8 ), fraction( 5, 8 ), fraction( 6, 8 ), fraction( 7, 8 ) );

    /**
     * Level 4:
     * All representations possible as well as complicated mixed/improper numbers
     */
    private static final List<Fraction> LEVEL_4_FRACTIONS = list( fraction( 13, 7 ), fraction( 13, 7 ),
                                                                  fraction( 14, 8 ),
                                                                  fraction( 9, 5 ),
                                                                  fraction( 6, 3 ),
                                                                  fraction( 9, 8 ),
                                                                  fraction( 8, 9 ), fraction( 6, 9 ), fraction( 4, 9 ), fraction( 3, 9 ), fraction( 2, 9 ),
                                                                  fraction( 9, 7 ) );

    /**
     * Level 5:
     * All representations possible as well as complicated mixed/improper numbers.  Same fractions as level 4 but different representations.
     */
    private static final List<Fraction> LEVEL_5_FRACTIONS = LEVEL_4_FRACTIONS;

    /**
     * Level 6:
     * All representations possible as well as complicated mixed/improper numbers
     */
    private static final List<Fraction> LEVEL_6_FRACTIONS = list( fraction( 9, 5 ), fraction( 8, 5 ), fraction( 7, 5 ), fraction( 6, 5 ),
                                                                  fraction( 7, 6 ), fraction( 8, 6 ), fraction( 9, 6 ),
                                                                  fraction( 9, 7 ), fraction( 10, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 14, 8 ),
                                                                  fraction( 4, 9 ), fraction( 6, 9 ), fraction( 8, 9 ), fraction( 10, 9 ), fraction( 11, 9 ) );

    /**
     * Level 7:
     * All representations possible as well as complicated mixed/improper numbers
     */
    private static final List<Fraction> LEVEL_7_FRACTIONS = list( fraction( 3, 2 ),
                                                                  fraction( 4, 3 ), fraction( 5, 3 ),
                                                                  fraction( 5, 4 ), fraction( 7, 4 ),
                                                                  fraction( 6, 5 ), fraction( 7, 5 ), fraction( 8, 5 ), fraction( 9, 5 ),
                                                                  fraction( 7, 6 ), fraction( 11, 6 ) );

    private static final List<Fraction> LEVEL_8_FRACTIONS = list( fraction( 8, 7 ), fraction( 9, 7 ), fraction( 10, 7 ), fraction( 11, 7 ), fraction( 12, 7 ), fraction( 13, 7 ),
                                                                  fraction( 9, 8 ), fraction( 10, 8 ), fraction( 11, 8 ), fraction( 12, 8 ), fraction( 13, 8 ), fraction( 14, 8 ),
                                                                  fraction( 15, 8 ),
                                                                  fraction( 10, 9 ), fraction( 11, 9 ), fraction( 12, 9 ), fraction( 13, 9 ), fraction( 14, 9 ), fraction( 15, 9 ),
                                                                  fraction( 16, 9 ), fraction( 17, 9 ) );

    private final Random RANDOM = new Random();

    //Primary method for this class, creates a set of movable fractions for a level.
    public List<MovableFraction> createLevel( final int level, final List<Cell> cells ) {
        final List<Fraction> fractionList = shuffle( level == 1 ? LEVEL_1_FRACTIONS :
                                                     level == 2 ? LEVEL_2_FRACTIONS :
                                                     level == 3 ? LEVEL_3_FRACTIONS :
                                                     level == 4 ? LEVEL_4_FRACTIONS :
                                                     level == 5 ? LEVEL_5_FRACTIONS :
                                                     level == 6 ? LEVEL_6_FRACTIONS :
                                                     level == 7 ? LEVEL_7_FRACTIONS :
                                                     level == 8 ? LEVEL_8_FRACTIONS :
                                                     List.<Fraction>nil() );

        final List<Integer> numericScaleFactors = level < 5 ? single( 1 ) :
                                                  level == 5 ? list( 1, 2, 3 ) :
                                                  level == 6 ? list( 1, 4, 5 ) :
                                                  level == 7 ? list( 1, 6, 7 ) :
                                                  level == 8 ? list( 1, 2, 3, 4, 5, 6, 7, 8, 9 ) :
                                                  List.<Integer>nil();
        final List<Fraction> selectedFractions = fractionList.take( 6 );
        ArrayList<Cell> remainingCells = new ArrayList<Cell>( shuffle( cells ).toCollection() );

        final List<ShapeType> easy = list( PIES, HORIZONTAL_BARS, VERTICAL_BARS );
        final List<ShapeType> medium = list( PLUSSES, GRID, PYRAMID, POLYGON, TETRIS, FLOWER, LETTER_L_SHAPES, INTERLEAVED_L_SHAPES, RING_OF_HEXAGONS, NINJA_STAR ).append( easy );

        final List<GraphicalRepresentation> r =
                level == 1 ? generateAll( easy, list( SEQUENTIAL ) ) :
                level == 2 ||
                level == 3 ? generateAll( medium, list( SEQUENTIAL ) ) :
                level == 4 ? generateAll( medium, list( SEQUENTIAL ) ) :
                level == 5 ? generateAll( medium, list( SEQUENTIAL, MIXED ) ) :
                level == 6 ? generateAll( medium, list( MIXED, FillType.RANDOM ) ) :
                level == 7 ? generateAll( medium, list( MIXED, FillType.RANDOM ) ) :
                level == 8 ? generateAll( medium, list( MIXED, FillType.RANDOM ) ) :
                null;
        if ( r == null ) { throw new RuntimeException( "No representations found for level: " + level ); }
        ArrayList<GraphicalRepresentation> representations = new ArrayList<GraphicalRepresentation>( r.toCollection() );

        //for each cell, create a MovableFraction
        ArrayList<MovableFraction> result = new ArrayList<MovableFraction>();
        for ( int i = 0; i < selectedFractions.length(); i++ ) {

            //choose one of the fractions at random, but don't repeat it.
            final Fraction fraction = selectedFractions.index( i % 6 );

            ArrayList<GraphicalRepresentation> allowedRepresentations = filter( representations, fraction );
            Collections.shuffle( allowedRepresentations );

            PNode node;
            GraphicalRepresentation representation = null;

            //Flag for the primary representation.  Ensure a minimum of 3 numeric representations per stage
            boolean numeric = i < 3 || RANDOM.nextBoolean();
            if ( numeric ) {
                int scaleFactor = numericScaleFactors.index( RANDOM.nextInt( numericScaleFactors.length() ) );
                final double fractionSizeScale = 0.3;

                //AP: Usually mixed numbers are written with the "1" nearly as tall as the entire fraction
                final double mixedNumberWholeScale = 2.4;
                if ( level == 7 && fraction.toDouble() > 1 && RANDOM.nextBoolean() ) {
                    //Try to use a mixed number representation
                    node = new HBox( 0, new FractionNumberNode( new Property<Integer>( 1 ) ) {{setScale( fractionSizeScale * mixedNumberWholeScale );}},
                                     new FractionNode( new Fraction( fraction.numerator - fraction.denominator, fraction.denominator ), fractionSizeScale ) );
                }
                else if ( level == 8 && fraction.toDouble() > 1 && RANDOM.nextBoolean() ) {
                    //Try to use a mixed number representation
                    node = new HBox( 0, new FractionNumberNode( new Property<Integer>( 1 ) ) {{setScale( fractionSizeScale * mixedNumberWholeScale );}},
                                     new FractionNode( new Fraction( ( fraction.numerator - fraction.denominator ) * scaleFactor, fraction.denominator * scaleFactor ), fractionSizeScale ) );
                }
                else {
                    node = new FractionNode( new Fraction( fraction.numerator * scaleFactor, fraction.denominator * scaleFactor ), fractionSizeScale );
                }
            }
            //ensure a minimum of 3 shape representations per stage
            else {
                if ( allowedRepresentations.size() == 0 ) {
                    allowedRepresentations = filter( new ArrayList<GraphicalRepresentation>( r.toCollection() ), fraction );
                }
                representation = allowedRepresentations.get( 0 );
                node = createGraphic( fraction, representation );
                representations.remove( representation );
                allowedRepresentations.remove( representation );
            }

            {
                Cell cell = remainingCells.remove( 0 );
                final int id = MovableFraction.nextID();
                final String representationString = numeric ? "numeric" : representation.toString();
                result.add( new MovableFraction( id, cell.getPosition(), fraction.numerator, fraction.denominator,
                                                 false, cell, 1.0, new RichPNode( node ), Motions.WAIT, false, new IUserComponent() {
                    @Override public String toString() {
                        return "fraction.id=" + id + ".value=" + fraction.numerator + "/" + fraction.denominator + ".representation=" + representationString;
                    }
                },
                                                 numeric ? Color.black : representation.color,
                                                 representationString ) );
            }

            //If fraction is numeric, partner must not also be numeric.
            {
                if ( allowedRepresentations.size() == 0 ) {
                    allowedRepresentations = filter( new ArrayList<GraphicalRepresentation>( r.toCollection() ), fraction );
                }
                final GraphicalRepresentation alternateRepresentation = allowedRepresentations.get( 0 );
                node = createGraphic( fraction, alternateRepresentation );
                representations.remove( alternateRepresentation );

                Cell cell = remainingCells.remove( 0 );

                final int id = MovableFraction.nextID();
                result.add( new MovableFraction( id, cell.getPosition(), fraction.numerator, fraction.denominator,
                                                 false, cell, 1.0, new RichPNode( node ), Motions.WAIT, false, new IUserComponent() {
                    @Override public String toString() {
                        return "fraction.id=" + id + ".value=" + fraction.numerator + "/" + fraction.denominator + ".representation=" + alternateRepresentation.toString();
                    }
                },
                                                 alternateRepresentation.color,
                                                 alternateRepresentation.toString() ) );
            }
        }
        return iterableList( result );
    }

    //Choose only the representations that match a fraction.
    private ArrayList<GraphicalRepresentation> filter( final ArrayList<GraphicalRepresentation> representations, final Fraction fraction ) {
        ArrayList<GraphicalRepresentation> filtered = new ArrayList<GraphicalRepresentation>();
        for ( GraphicalRepresentation representation : representations ) {
            if ( matches( representation.shapeType, fraction ) ) { filtered.add( representation ); }
        }
        return filtered;
    }

    //See if the specified shape can render the given fraction.
    private boolean matches( final ShapeType s, final Fraction fraction ) {
        final int d = fraction.denominator;
        return s == PIES ? true :
               s == HORIZONTAL_BARS ? true :
               s == VERTICAL_BARS ? true :
               s == GRID ? ( d == 4 || d == 9 ) :
               s == FLOWER ? d == 6 :
               s == PYRAMID ? ( d == 1 || d == 4 || d == 9 ) :
               s == PLUSSES ? d == 6 :
               s == POLYGON ? d >= 3 :
               s == TETRIS ? d == 4 :
               s == LETTER_L_SHAPES ? d % 2 == 0 :
               s == INTERLEAVED_L_SHAPES ? ( d == 2 || d == 4 ) :
               s == RING_OF_HEXAGONS ? d == 7 :
               s == NINJA_STAR ? d == 8 :
               false;
    }

    //Generate all possible combinations of color, shape, fill.
    private List<GraphicalRepresentation> generateAll( final List<ShapeType> shapes, final List<FillType> fills ) {
        ArrayList<GraphicalRepresentation> all = new ArrayList<GraphicalRepresentation>();
        for ( ShapeType shape : shapes ) {
            for ( Color color : list( LIGHT_BLUE, LIGHT_GREEN, LIGHT_RED ) ) {
                for ( FillType fill : fills ) {
                    all.add( new GraphicalRepresentation( shape, color, fill ) );
                }
            }
        }
        return iterableList( all );
    }

    //Create the graphic for one fraction.
    private PNode createGraphic( Fraction f, final GraphicalRepresentation r ) {
        if ( f.numerator <= f.denominator ) {
            final PatternNode single = createSingle( f, r.shapeType, r.fillType == FillType.RANDOM, r.color );
            HBox box = new HBox( single );
            scaleBoxNode( box, 80 );
            return box;
        }
        else {
            int numShapes = (int) Math.ceil( f.toDouble() );
            if ( numShapes >= 3 ) {
                throw new RuntimeException( "3+ not handled yet" );
            }

            if ( r.fillType == SEQUENTIAL || r.fillType == MIXED ) {
                PatternNode first = createSingle( new Fraction( f.denominator, f.denominator ), r.shapeType, false, r.color );
                PatternNode second = createSingle( new Fraction( f.numerator - f.denominator, f.denominator ), r.shapeType, r.fillType == MIXED, r.color );

                return addToBox( first, second );
            }
            else {
                ContainerSet containerSet = new ContainerSet( f.denominator, list( new Container( f.denominator, List.<Integer>nil() ),
                                                                                   new Container( f.denominator, List.<Integer>nil() ) ) );
                for ( int i = 0; i < f.numerator; i++ ) {
                    containerSet = containerSet.toggle( containerSet.getRandomEmptyCell( RANDOM ) );
                }
                final int numInFirst = containerSet.containers.index( 0 ).getFilledCells().length();
                Fraction firstFraction = new Fraction( numInFirst, f.denominator );
                final int numInSecond = containerSet.containers.index( 1 ).getFilledCells().length();
                Fraction secondFraction = new Fraction( numInSecond, f.denominator );

                double sum = firstFraction.toDouble() + secondFraction.toDouble();
                double difference = sum - f.toDouble();
                if ( difference > 1E-6 ) {
                    System.out.println( "fraction = " + f + ", numInFirst = " + numInFirst + ", numInSecond = " + numInSecond + ", difference = " + difference );
                    throw new RuntimeException( "values didn't add up" );
                }
                PatternNode first = createSingle( firstFraction, r.shapeType, true, r.color );
                PatternNode second = createSingle( secondFraction, r.shapeType, true, r.color );

                return addToBox( first, second );
            }

        }
    }

    //Combine two patterns horizontally
    private PNode addToBox( final PatternNode first, final PatternNode second ) {

        //AP: If I resample at the highest levels (definitely 7 and 8) a few double shapes appear that have a very small amount of negative space between them.  We should probably widen the negative space or scale those shapes slightly differently.
        //SR: Therefore spacing should be dependent on width
        double width = first.getFullBounds().getWidth();
        LinearFunction function = new LinearFunction( 80, 160, 10, 20 );

        final HBox box = new HBox( function.evaluate( width ), first, second );
        scaleBoxNode( box, 110.0 );
        return box;
    }

    //Scale the node so it will be a good fit for the starting cells and score cells and still have the right stroke.
    private void scaleBoxNode( final PNode box, double newWidth ) {
        double size = box.getFullBounds().getWidth();
        final double scale = newWidth / size;

        double size2 = box.getFullBounds().getHeight();
        final double scale2 = newWidth / size2;

        box.scale( Math.min( scale, scale2 ) );

        if ( scale < 1 ) {
            //if the objects got scaled down, then scale up the strokes so they will look like they have the same width
            for ( Object child : box.getChildrenReference() ) {
                if ( child instanceof PatternNode ) {
                    PatternNode patternNode = (PatternNode) child;
                    patternNode.scaleStrokes( 1.0 / scale );
                }
            }
        }
    }

    //Create the node for a single (<=1) fraction.
    private PatternNode createSingle( final Fraction fraction, ShapeType s, boolean random, Color color ) {
        final boolean ok = fraction.numerator <= fraction.denominator && fraction.numerator >= 0 && fraction.denominator > 0;
        if ( !ok ) {
            throw new RuntimeException( "Failed assertion, fraction = " + fraction );
        }
        final int d = fraction.denominator;
        final Pattern container = s == PIES ? Pattern.pie( d ) :
                                  s == VERTICAL_BARS ? Pattern.verticalBars( d ) :
                                  s == HORIZONTAL_BARS ? Pattern.horizontalBars( d ) :
                                  s == FLOWER && d == 6 ? Pattern.sixFlower() :
                                  s == TETRIS && d == 4 ? Pattern.tetrisPiece( 50 ) :
                                  s == PLUSSES && d == 6 ? Pattern.plusSigns( d ) :
                                  s == POLYGON ? Pattern.polygon( 80, d ) :
                                  s == PYRAMID && d == 1 ? Pattern.pyramidSingle() :
                                  s == PYRAMID && d == 4 ? Pattern.pyramidFour() :
                                  s == PYRAMID && d == 9 ? Pattern.pyramidNine() :
                                  s == GRID && d == 4 ? Pattern.grid( 2 ) :
                                  s == GRID && d == 9 ? Pattern.grid( 3 ) :
                                  s == LETTER_L_SHAPES && d % 2 == 0 ? Pattern.letterLShapedDiagonal( 14, d / 2 ) :
                                  s == INTERLEAVED_L_SHAPES && d == 2 ? Pattern.interleavedLShape( 80, 1, 1 ) :
                                  s == INTERLEAVED_L_SHAPES && d == 4 ? Pattern.interleavedLShape( 80, 2, 1 ) :
                                  s == RING_OF_HEXAGONS && d == 7 ? Pattern.ringOfHexagons() :
                                  s == NINJA_STAR && d == 8 ? Pattern.ninjaStar() :
                                  null;
        if ( container == null ) {
            throw new RuntimeException( "Null pattern for rep = " + s + ", f = " + fraction );
        }
        return new PatternNode( random ? randomFill( container, fraction.numerator, 123 ) :
                                sequentialFill( container, fraction.numerator ), color );
    }
}
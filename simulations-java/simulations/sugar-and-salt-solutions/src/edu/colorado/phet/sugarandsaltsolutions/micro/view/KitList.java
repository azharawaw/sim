// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose.Glucose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;

/**
 * List of the kits the user can choose from, for showing the appropriate bar charts + controls
 *
 * @author Sam Reid
 */
public class KitList {
    private final ArrayList<MicroKit> kits = new ArrayList<MicroKit>();

    public KitList( final MicroModel model, final ModelViewTransform transform ) {

        //Functions to create each button.  This code should be read with code folding on and a large right margin
        Function0<RemoveSoluteButtonNode> createSodiumChlorideButton = new Function0<RemoveSoluteButtonNode>() {
            public RemoveSoluteButtonNode apply() {
                return new RemoveSoluteButtonNode( "Remove Sodium Chloride", model.isAnySaltToRemove(), new VoidFunction0() {
                    public void apply() {
                        model.removeAllSodiumChloride();
                    }
                } );
            }
        };

        Function0<RemoveSoluteButtonNode> createSucroseButton = new Function0<RemoveSoluteButtonNode>() {
            public RemoveSoluteButtonNode apply() {
                return new RemoveSoluteButtonNode( "Remove Sucrose", model.isAnySugarToRemove(), new VoidFunction0() {
                    public void apply() {
                        model.removeAllGlucose();
                    }
                } );
            }
        };

        Function0<RemoveSoluteButtonNode> createGlucoseButton = new Function0<RemoveSoluteButtonNode>() {
            public RemoveSoluteButtonNode apply() {
                return new RemoveSoluteButtonNode( "Remove Glucose", new Property<Boolean>( true ), new VoidFunction0() {
                    public void apply() {
                        model.removeAllGlucose();
                    }
                } );
            }
        };

        Function0<RemoveSoluteButtonNode> createSodiumNitrateButton = new Function0<RemoveSoluteButtonNode>() {
            public RemoveSoluteButtonNode apply() {
                return new RemoveSoluteButtonNode( "Remove Sodium Nitrate", model.nitrateConcentration.greaterThan( 0.0 ), new VoidFunction0() {
                    public void apply() {
                        model.removeAllSodiumNitrate();
                    }
                } );
            }
        };

        Function0<RemoveSoluteButtonNode> createCalciumChlorideButton = new Function0<RemoveSoluteButtonNode>() {
            public RemoveSoluteButtonNode apply() {
                return new RemoveSoluteButtonNode( "Remove Calcium Chloride", model.calciumConcentration.greaterThan( 0.0 ), new VoidFunction0() {
                    public void apply() {
                        model.removeAllCalciumChloride();
                    }
                } );
            }
        };

        //Create icons to be shown beneath each bar.  Functions are used to create new icons for each kit since giving the same PNode multiple parents caused layout problems
        Function0<Option<PNode>> sodiumIcon = new Function0<Option<PNode>>() {
            public Option<PNode> apply() {
                return new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) );
            }
        };
        Function0<Option<PNode>> chlorideIcon = new Function0<Option<PNode>>() {
            public Option<PNode> apply() {
                return new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) );
            }
        };
        Function0<Option<PNode>> sucroseIcon = new Function0<Option<PNode>>() {
            public Option<PNode> apply() {
                return new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Sucrose(), model.showChargeColor ) );
            }
        };
        Function0<Option<PNode>> glucoseIcon = new Function0<Option<PNode>>() {
            public Option<PNode> apply() {
                return new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Glucose(), model.showChargeColor ) );
            }
        };
        Function0<Option<PNode>> calciumIcon = new Function0<Option<PNode>>() {
            public Option<PNode> apply() {
                return new Some<PNode>( new SphericalParticleNode( transform, new Calcium(), model.showChargeColor ) );
            }
        };
        Function0<Option<PNode>> nitrateIcon = new Function0<Option<PNode>>() {
            public Option<PNode> apply() {
                return new Some<PNode>( new CompositeParticleNode<Particle>( transform, new Nitrate( 0, ImmutableVector2D.ZERO ), model.showChargeColor ) );
            }
        };

        //This is the logic for which components are present within each kit.  If kits change, this will need to be updated
        //Put the positive ions to the left of the negative ions
        kits.add( new MicroKit( new RemoveSoluteButtonNode[] { createSodiumChlorideButton.apply(), createSucroseButton.apply() },
                                new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, sodiumIcon ),
                                new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, chlorideIcon ),
                                new BarItem( model.sucroseConcentration, model.sucroseColor, SUCROSE, sucroseIcon ) ) );

        kits.add( new MicroKit( new RemoveSoluteButtonNode[] { createSodiumChlorideButton.apply(), createCalciumChlorideButton.apply() },
                                new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, sodiumIcon ),
                                new BarItem( model.calciumConcentration, model.calciumColor, CALCIUM, calciumIcon ),
                                new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, chlorideIcon ) ) );

        kits.add( new MicroKit( new RemoveSoluteButtonNode[] { createSodiumChlorideButton.apply(), createSodiumNitrateButton.apply() },
                                new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, sodiumIcon ),
                                new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, chlorideIcon ),
                                new BarItem( model.nitrateConcentration, model.nitrateColor, NITRATE, nitrateIcon ) ) );

        kits.add( new MicroKit( new RemoveSoluteButtonNode[] { createSucroseButton.apply(), createGlucoseButton.apply() },
                                new BarItem( model.sucroseConcentration, model.sucroseColor, SUCROSE, sucroseIcon ),
                                new BarItem( model.glucoseConcentration, model.glucoseColor, GLUCOSE, glucoseIcon ) ) );
    }

    public MicroKit getKit( int kit ) {
        return kits.get( kit );
    }
}

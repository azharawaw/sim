// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

/**
 * Primary model for the manual gene expression tab.
 * <p/>
 * Dimensions in this model (and all sub-elements of the model) are in nano-
 * meters, i.e. 10E-9 meters.
 * <p/>
 * The point (0,0) in model space is at the leftmost edge of the DNA strand, and
 * at the vertical center of the strand.
 *
 * @author John Blanco
 */
public class ManualGeneExpressionModel implements Resettable {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    private final DnaMolecule dnaStrand = new DnaMolecule();

    // The gene that the user is focusing on, other gene activity is
    // suspended.  Start with the 0th gene in the DNA (leftmost).
    public final Property<Gene> activeGene = new Property<Gene>( dnaStrand.getGenes().get( 0 ) );

    // Properties that keep track of whether the first or last gene is
    // currently active, which means that the user is viewing it.
    public final ObservableProperty<Boolean> isFirstGeneActive = activeGene.valueEquals( dnaStrand.getGenes().get( 0 ) );
    public final ObservableProperty<Boolean> isLastGeneActive = activeGene.valueEquals( dnaStrand.getLastGene() );

    // List of RNA polymerase in the model.
    public final ObservableList<MobileBiomolecule> mobileBiomoleculeList = new ObservableList<MobileBiomolecule>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public ManualGeneExpressionModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    public DnaMolecule getDnaMolecule() {
        return dnaStrand;
    }

    public void previousGene() {
        switchToGeneRelative( -1 );
    }

    public void nextGene() {
        switchToGeneRelative( +1 );
    }

    private void switchToGeneRelative( int i ) {
        final ArrayList<Gene> genes = dnaStrand.getGenes();
        int index = clamp( 0, genes.indexOf( activeGene.get() ) + i, genes.size() - 1 );
        activeGene.set( genes.get( index ) );
    }

    private void activateGene( int i ) {
        final ArrayList<Gene> genes = dnaStrand.getGenes();
        activeGene.set( dnaStrand.getGenes().get( i ) );
    }

    public List<MobileBiomolecule> getMobileBiomoleculeList() {
        return mobileBiomoleculeList;
    }

    public void addMobileBiomolecule( final MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.add( mobileBiomolecule );
        mobileBiomolecule.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( userControlled ) {
                    dnaStrand.activateHints( mobileBiomolecule );
                }
                else {
                    dnaStrand.deactivateAllHints();
                }
            }
        } );
    }

    public void removeMobileBiomolecule( MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.remove( mobileBiomolecule );
    }

    public void reset() {
        for ( MobileBiomolecule mobileBiomolecule : new ArrayList<MobileBiomolecule>( mobileBiomoleculeList ) ) {
            removeMobileBiomolecule( mobileBiomolecule );
        }
        activateGene( 0 );
    }

    private void stepInTime( double dt ) {
        for ( MobileBiomolecule mobileBiomolecule : mobileBiomoleculeList ) {
            mobileBiomolecule.stepInTime( dt );
            dnaStrand.proposeAttachmentSitesTo( mobileBiomolecule );
        }
    }
}

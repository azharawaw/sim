/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class SchrodingerEnergyDiagram extends AbstractEnergyDiagram implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Margins inside the drawing area
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 10;
    
    private static final double LINE_LINE_SPACING = 10;
    private static final double LINE_LABEL_SPACING = 10;
    
    private static final double L_LABEL_VALUE_SPACING = 2;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SchrodingerModel _atom;
    private EnergySquiggle _squiggle;
    private int _nPrevious, _lPrevious;
    private double _lLabelWidth, _lLabelHeight;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param canvas
     */
    public SchrodingerEnergyDiagram( PSwingCanvas canvas ) {
        super( SchrodingerModel.getNumberOfStates(), canvas );
        
        assert( SchrodingerModel.getGroundState() == 1 ); // n=1 must be ground state
        assert( SchrodingerModel.getNumberOfStates() == 6 ); // 6 states
        
        Rectangle2D drawingArea = getDrawingArea();
        
        PText lLabelNode = new PText( "l=" );
        lLabelNode.setFont( LABEL_FONT );
        lLabelNode.setTextPaint( LABEL_COLOR );
        lLabelNode.setOffset( drawingArea.getX() + X_MARGIN, drawingArea.getY() + Y_MARGIN );
        getStateLayer().addChild( lLabelNode );
        _lLabelWidth = lLabelNode.getWidth();
        _lLabelHeight = lLabelNode.getHeight();

        PNode lNode = createLValuesNode();
        lNode.setOffset( drawingArea.getX() + X_MARGIN + _lLabelWidth + L_LABEL_VALUE_SPACING, drawingArea.getY() + Y_MARGIN );
        getStateLayer().addChild( lNode );
        
        for ( int n = 1; n <= SchrodingerModel.getNumberOfStates(); n++ ) { 
            PNode levelNode = createNNode( n );
            double x = getXOffset( 0 );
            double y = getYOffset( n );
            levelNode.setOffset( x, y );
            getStateLayer().addChild( levelNode );
        }
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the atom associated with the diagram.
     * Initializes the electron's position, based on it's current state.
     * 
     * @param atom
     */
    public void setAtom( SchrodingerModel atom ) {
        
        if ( _atom != null ) {
            // remove association with existing atom
            _atom.deleteObserver( this );
            _atom = null;
        }
        
        // Electron is invisible if there is no associated atom
        ElectronNode electronNode = getElectronNode();
        electronNode.setVisible( atom != null );
        
        if ( atom != null ) {
            
            // observe the atom
            _atom = atom;
            _atom.addObserver( this );

            // Set electron's initial position
            updateElectronPosition();
            
            // Remember electron's initial state
            _nPrevious = atom.getElectronState();
            _lPrevious = atom.getSecondaryElectronState();
        }
    }
    
    /**
     * Removes the association between this diagram and any atom.
     */
    public void clearAtom() {
        setAtom( null );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                updateElectronPosition();
                updateSquiggle();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Updates the squiggle to show a state change.
     */
    private void updateSquiggle() {

        // remove any existing squiggle
        if ( _squiggle != null ) {
            getSquiggleLayer().removeChild( _squiggle );
            _squiggle = null;
        }

        // create the new squiggle for photon absorption/emission
        final int n = _atom.getElectronState();
        if ( n != _nPrevious ) {
            double wavelength = 0;
            if ( n > _nPrevious ) {
                // a photon has been absorbed
                wavelength = BohrModel.getWavelengthAbsorbed( _nPrevious, n );
            }
            else {
                // a photon has been emitted
                wavelength = BohrModel.getWavelengthAbsorbed( n, _nPrevious );
            }
            final int l = _atom.getSecondaryElectronState();
            double x1 = getXOffset( _lPrevious ) + ( LINE_LENGTH / 2 );
            double y1 = getYOffset( _nPrevious );
            double x2 = getXOffset( l ) + ( LINE_LENGTH / 2 );
            double y2 = getYOffset( n );
            _squiggle = new EnergySquiggle( x1, y1, x2, y2, wavelength, SQUIGGLE_STROKE );
            getSquiggleLayer().addChild( _squiggle );
            
            // Remember electron's state
            _nPrevious = n;
            _lPrevious = l;
        }
    }
    
    /*
     * Updates the position of the electron in the diagram
     * to match the electron's (n,l) state.
     */
    private void updateElectronPosition() {
        ElectronNode electronNode = getElectronNode();
        final int n = _atom.getElectronState();
        final int l = _atom.getSecondaryElectronState();
        final double x = getXOffset( l ) + ( LINE_LENGTH / 2 );
        final double y = getYOffset( n ) - ( electronNode.getFullBounds().getHeight() / 2 );
        electronNode.setOffset( x, y );
    }
    
    /*
     * Gets the x-offset that corresponds to a specified state.
     * Horizontal position is based on the electron's secondary state (l).
     * This is used for positioning both the state lines and the electron.
     * 
     * @param state
     * @return double
     */
    protected double getXOffset( int l ) {
        return getDrawingArea().getX() + X_MARGIN + _lLabelWidth + L_LABEL_VALUE_SPACING + ( l * LINE_LENGTH ) + ( l * LINE_LINE_SPACING );
    }
    
    /*
     * Gets the y-offset that corresponds to a specific state.
     * Vertical position is based on the electron's primary state (n).
     * This is used for positioning both the state lines and the electron.
     * 
     * @param state
     * @return double
     */
    protected double getYOffset( int n ) {
        final double minE = getEnergy( 1 );
        final double maxE = getEnergy( BohrModel.getNumberOfStates() );
        final double rangeE = maxE - minE;
        Rectangle2D drawingArea = getDrawingArea();
        final double electronHeight = getElectronNode().getFullBounds().getHeight();
        final double height = drawingArea.getHeight() - ( 2 * Y_MARGIN ) - _lLabelHeight - electronHeight;
        double y = drawingArea.getY() + Y_MARGIN + _lLabelHeight + electronHeight + ( height * ( maxE - getEnergy( n ) ) / rangeE );
        return y;
    }
    
    /*
     * Creates a node that displays the various value of the secondary state (l).
     * The values are spaced out horizontally so that they are centered above
     * the lines that represent states.
     * 
     * @param state
     * @return PNode
     */
    private static PNode createLValuesNode() {
        
        final int numberOfStates = SchrodingerModel.getNumberOfStates();
        
        PComposite parentNode = new PComposite();
        
        for ( int l = 0; l < numberOfStates; l++ ) {
            PText valueNode = new PText( String.valueOf( l ) );
            valueNode.setFont( LABEL_FONT );
            valueNode.setTextPaint( LABEL_COLOR );
            valueNode.setOffset( ( l * LINE_LENGTH ) + ( l * LINE_LINE_SPACING ) + ( LINE_LENGTH / 2 ) - ( valueNode.getWidth() / 2 ), 0 );
            parentNode.addChild( valueNode );
        }
        
        return parentNode;
    }
    
    /*
     * Creates a node that represents the possible electron states for 
     * some value of the electron's primary state (n).  State n has n 
     * possible secondary states.  Each of these possible states is 
     * represented as a horizontal line, and the lines are arranged 
     * horizontally.
     * 
     * @param state
     * @return PNode
     */
    private static PNode createNNode( int state ) {
        
        final int numberOfStates = SchrodingerModel.getNumberOfStates();

        PComposite linesParentNode = new PComposite();
        for ( int i = 0; i < state; i++ ) {
            PNode lineNode = createStateLineNode();
            lineNode.setOffset( i * ( LINE_LENGTH + LINE_LINE_SPACING ), 0 );
            linesParentNode.addChild( lineNode );
        }

        PNode labelNode = createStateLabelNode( state );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( linesParentNode );
        parentNode.addChild( labelNode );
        
        linesParentNode.setOffset( 0, 0 );
        double x = ( numberOfStates * LINE_LENGTH ) + ( ( numberOfStates - 1 ) * LINE_LINE_SPACING ) + LINE_LABEL_SPACING;
        double y = -( ( linesParentNode.getHeight() / 2 ) + ( labelNode.getHeight() / 2 ) );
        if ( state == 6 ) {
            // HACK requested by Sam McKagan: for n=6, move label up a bit to prevent overlap with n=5
            labelNode.setOffset( x, y - 3.5 );
        }
        else {
            labelNode.setOffset( x, y );
        }
        
        return parentNode;
    }
}

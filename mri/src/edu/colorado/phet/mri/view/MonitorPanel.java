/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.model.PhotonSource;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MonitorPanel
 * <p/>
 * Displays icons representing the number of hydrogen nuclei at each of two energy levels
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MonitorPanel extends PhetPCanvas {

    private BufferedImage spinUpImage, spinDownImage;
    private double imageWidth;
    private static double BASE_SCALE = 0.5;
    private double scale = 0.4;
    private double BASE_SQUIGGLE_LENGTH_CALIBRATION_FACTOR = 1.21E8 * 2.8; // for scale = 0;5
    private double SQUIGGLE_LENGTH_CALIBRATION_FACTOR = 1.21E8 * 2.45; // for scale = 0.4
//    private double SQUIGGLE_LENGTH_CALIBRATION_FACTOR = 1.21E8 * 2.6; // for scale = 0.4

    private ArrayList spinUpReps = new ArrayList();
    private ArrayList spinDownReps = new ArrayList();
    private EnergyLevel lowerLine, upperLine;
    private double fieldStrength;
    private EnergySquiggle energySquiggle;
    // Reserved horizontal space for the squiggle
    private double energySquiggleReserveX = 15;
    // Reserved horizontal space for the energy axis
    private double energyAxisReserveX = 30;
    // fraction of panel height that is usable for representing energy levels
    private double heightFractionUsed = 0.85;
    // Flag that tells if the energy of the radiowave source is considered close enough to the
    // energy difference between the spin levels to be considered a match. This keeps the squiggles from
    // flashing once it's matched, then moved an amount that is still within the mathing tolerancd
    private boolean matched = false;
    // There are several possibilities for how dipoles are represented. This specifies the representation du jour
    private DipoleRepresentationPolicy dipoleRepresentationPolicy = MriConfig.InitialConditions.MONITOR_PANEL_REP_POLICY_DIPOLE;
    private EnergySquiggleUpdater energySquiggleUpdater;

    /**
     * Constructor
     *
     * @param model
     */
    public MonitorPanel( final MriModel model ) {

        setBackground( SampleChamberGraphic.BACKGROUND );

        makeImages( scale, model.getSampleMaterial() );

        // Add graphics for the energy levels
        lowerLine = new EnergyLevel( 200, spinDownReps, model, spinDownImage );
        addWorldChild( lowerLine );
        lowerLine.setOffset( 0, 100 );

        upperLine = new EnergyLevel( 200, spinUpReps, model, spinUpImage );
        addWorldChild( upperLine );
        upperLine.setOffset( 0, 60 );

        // Add a squiggle to show the energy of the radio waves
        energySquiggle = new EnergySquiggle( EnergySquiggle.VERTICAL );
        addWorldChild( energySquiggle );
        energySquiggle.setOffset( energyAxisReserveX, 0 );
        energySquiggleUpdater = new EnergySquiggleUpdater( energySquiggle,
                                                           model );

        // Add a legend
        EnergyAxis energyAxis = new EnergyAxis();
        energyAxis.setOffset( 5, 50 );
        addWorldChild( energyAxis );

        // Add elements to the model that will get notified when things we are monitoring change
        model.addListener( new ModelChangeListener( model ) );
        model.addModelElement( new DipoleRepUpdater( model ) );
        model.getLowerMagnet().addChangeListener( new EnergyLevelSeparationUpdater( model ) );
        model.getRadiowaveSource().addChangeListener( new SquiggleUpdater( model ) );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updatePanel( model );
            }
        } );
        updatePanel( model );
    }

    private void updatePanel( MriModel model ) {
        setLinePositions( model );
        energySquiggleUpdater.updateSquiggle( energySquiggle.getOffset().getX(),
                                              lowerLine.getOffset().getY(),
                                              SQUIGGLE_LENGTH_CALIBRATION_FACTOR );
    }

    public void setRepresentationPolicy( DipoleRepresentationPolicy dipoleRepresentationPolicy ) {
        this.dipoleRepresentationPolicy = dipoleRepresentationPolicy;
    }


    /**
     * Establish the center point of the panel, and position the energy levels
     * symetrically above and below it
     */
    private void setLinePositions( MriModel model ) {
        double imageReserveSpace = spinDownImage.getHeight() * 1 / 2;
        double maxOffset = getHeight() / 2 * heightFractionUsed - imageReserveSpace;
        double fractionMaxField = Math.min( fieldStrength / MriConfig.MAX_FADING_COIL_FIELD, 1 );
        double sampleMaterialRatio = model.getSampleMaterial().getMu() / SampleMaterial.HYDROGEN.getMu();

        double offsetY = maxOffset * fractionMaxField * sampleMaterialRatio;
        double centerY = getHeight() / 2 + imageReserveSpace;
        lowerLine.setPositionY( centerY + offsetY );
        upperLine.setPositionY( centerY - offsetY );
    }

    /**
     * Sets the length, wavelength and location of the squiggle, and flashes it if  necessary
     *
     * @param model
     */
    private void adjustSquiggle( MriModel model ) {
        energySquiggleUpdater.updateSquiggle( energySquiggle.getOffset().getX(),
                                              lowerLine.getOffset().getY(),
                                              SQUIGGLE_LENGTH_CALIBRATION_FACTOR );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class EnergyAxis extends PNode {
        public EnergyAxis() {
            PText axisLabel = new PText( "Energy" );
            axisLabel.rotate( -Math.PI / 2 );
            addChild( axisLabel );
            Arrow arrow = new Arrow( new Point2D.Double( 10, 40 ),
                                     new Point2D.Double( 10, 0 ),
                                     10, 8, 3,
                                     1, false );
            PPath arrowGraphic = new PPath( arrow.getShape() );
            arrowGraphic.setPaint( new Color( 80, 80, 80 ) );
            addChild( arrowGraphic );
            arrowGraphic.setOffset( 10, -40 );
        }
    }

    /**
     * A graphic with a line and an icon for each dipole in that energy level. It actually has an icon for all
     * the dipoles, but only displays the one that are currently in this energy level.
     */
    private class EnergyLevel extends PNode {
        private PPath lineNode;
        private List nucleiReps;
        private BufferedImage dipoleRepImage;
        private Line2D line;

        public EnergyLevel( double width, List nucleiReps, MriModel model, BufferedImage dipoleRepImage ) {
            this.nucleiReps = nucleiReps;
            this.dipoleRepImage = dipoleRepImage;

            line = new Line2D.Double( 0, 0, width, 0 );
            lineNode = new PPath( line );
            lineNode.setStroke( new BasicStroke( 3 ) );
            addChild( lineNode );

            MonitorPanel.this.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    setLinelength( e.getComponent().getWidth() );
                }
            } );

            // Add reps for all the dipoles that we have right now
            for( int i = 0; i < model.getDipoles().size(); i++ ) {
                addDipoleRep();
            }

            // Add a listener that will add and remove dipole reps as they go in and out of the model
            model.addListener( new MriModel.ChangeAdapter() {
                public void modelElementAdded( ModelElement modelElement ) {
                    if( modelElement instanceof Dipole ) {
                        addDipoleRep();
                    }
                }
            } );
        }

        public void setDipoleRepImage( BufferedImage dipoleRepImage ) {
            this.dipoleRepImage = dipoleRepImage;
            Iterator nucleiRepsIt = nucleiReps.iterator();
            while( nucleiRepsIt.hasNext() ) {
                PImage pImage = (PImage)nucleiRepsIt.next();
                pImage.setImage( dipoleRepImage );
            }
        }

        private void setLinelength( double lineLength ) {
            line.setLine( line.getX1(), line.getY1(), line.getX1() + lineLength, line.getY2() );
            lineNode.setPathTo( line );
        }

        private void addDipoleRep() {
            PNode dipoleGraphic = new PImage( dipoleRepImage );
            nucleiReps.add( dipoleGraphic );
            dipoleGraphic.setOffset( ( nucleiReps.size() - 1 ) * imageWidth + energySquiggleReserveX + energyAxisReserveX,
                                     -dipoleGraphic.getHeight() );
            addChild( dipoleGraphic );

            // Set the size of the panel
            MonitorPanel.this.setPreferredSize( new Dimension( (int)( dipoleRepImage.getWidth() * ( nucleiReps.size() )
                                                                      + energySquiggleReserveX
                                                                      + energyAxisReserveX ),
                                                               getPreferredSize().height ) );
        }

        public void setPositionY( double y ) {
            setOffset( 0, y );
        }
    }

    /**
     * Tracks the number of dipoles with spin up and spin down
     */
    private class DipoleRepUpdater implements ModelElement {
        private MriModel model;

        DipoleRepUpdater( MriModel model ) {
            this.model = model;
        }

        public void stepInTime( double dt ) {
            List dipoles = model.getDipoles();
            dipoleRepresentationPolicy.representSpins( dipoles, spinUpReps, spinDownReps );
        }
    }

    //----------------------------------------------------------------
    // Representation policy
    //----------------------------------------------------------------
    public interface DipoleRepresentationPolicy {
        void representSpins( List dipoles, List spinUpReps, List spinDownReps );
    }

    public static class DiscretePolicy implements DipoleRepresentationPolicy {
        public void representSpins( List dipoles, List spinUpReps, List spinDownReps ) {
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                boolean isUp = dipole.getSpin() == Spin.UP;
                boolean isDown = !isUp;
                ( (PNode)spinUpReps.get( i ) ).setVisible( isUp );
                ( (PNode)spinDownReps.get( i ) ).setVisible( isDown );
            }
        }
    }

    public static class TransparencyPolicy implements DipoleRepresentationPolicy {
        public void representSpins( List dipoles, List spinUpReps, List spinDownReps ) {
            int numUp = 0;
            int numDown = 0;
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                if( dipole.getSpin() == Spin.DOWN ) {
                    numUp++;
                }
                else if( dipole.getSpin() == Spin.UP ) {
                    numDown++;
                }
            }

            float upTransparency = ( (float)numUp ) / dipoles.size();
            for( int j = 0; j < spinUpReps.size(); j++ ) {
                ( (PNode)spinUpReps.get( j ) ).setVisible( true );
                ( (PNode)spinUpReps.get( j ) ).setTransparency( upTransparency );
            }
            float downTransparency = ( (float)numDown ) / dipoles.size();
            for( int j = 0; j < spinDownReps.size(); j++ ) {
                ( (PNode)spinDownReps.get( j ) ).setVisible( true );
                ( (PNode)spinDownReps.get( j ) ).setTransparency( downTransparency );
            }
        }
    }

    private class EnergyLevelSeparationUpdater implements Electromagnet.ChangeListener {
        private MriModel model;

        public EnergyLevelSeparationUpdater( MriModel model ) {
            this.model = model;
        }

        public void stateChanged( Electromagnet.ChangeEvent event ) {
            fieldStrength = model.getTotalFieldStrengthAt( new Point2D.Double( MriConfig.SAMPLE_CHAMBER_WIDTH / 2,
                                                                               MriConfig.SAMPLE_CHAMBER_HEIGHT / 2 ) );
            updatePanel( model );
        }
    }

    //----------------------------------------------------------------
    // ModelChangeListener
    //----------------------------------------------------------------
    private class ModelChangeListener extends MriModel.ChangeAdapter {
        private MriModel model;

        public ModelChangeListener( MriModel model ) {
            this.model = model;
        }

        public void sampleMaterialChanged( SampleMaterial sampleMaterial ) {
            updatePanel( model );
            makeImages( scale, sampleMaterial );

            // Add graphics for the energy levels
            lowerLine.setDipoleRepImage( spinDownImage );
            upperLine.setDipoleRepImage( spinDownImage );
        }
    }

    private void makeImages( double scale, SampleMaterial sampleMaterial ) {
        spinDownImage = DipoleGraphic.getDipoleImage( sampleMaterial );
        spinUpImage = DipoleGraphic.getDipoleImage( sampleMaterial );
        spinDownImage = BufferedImageUtils.getRotatedImage( spinDownImage, Math.PI / 2 );
        spinUpImage = BufferedImageUtils.getRotatedImage( spinUpImage, -Math.PI / 2 );

        AffineTransform scaleTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        spinDownImage = scaleOp.filter( spinDownImage, null );
        spinUpImage = scaleOp.filter( spinUpImage, null );

        imageWidth = Math.max( spinDownImage.getWidth(), spinUpImage.getWidth() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of RadiowaveSource.ChangeListener
    //--------------------------------------------------------------------------------------------------
    private class SquiggleUpdater implements RadiowaveSource.ChangeListener {
        private MriModel model;

        public SquiggleUpdater( MriModel model ) {
            this.model = model;
        }

        public void rateChangeOccurred( PhotonSource.ChangeEvent event ) {
            adjustSquiggle( model );
        }

        public void wavelengthChanged( PhotonSource.ChangeEvent event ) {
            adjustSquiggle( model );
        }
    }
}
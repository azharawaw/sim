package edu.colorado.phet.acidbasesolutions.module.solutions;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.control.EquationScalingControl;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.AbstractEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.AcidEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.BaseEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.WaterEquilibriumExpressionNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class SolutionsEquilibriumExpressionsNode extends PhetPNode {
    
    private static final double X_MARGIN = 20;
    private static final double Y_SPACING = 50;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private final EquationScalingControl scalingControl;
    private final PSwing scalingControlWrapper;
    private AbstractEquilibriumExpressionNode soluteNode;
    private final AbstractEquilibriumExpressionNode waterNode;
    
    public SolutionsEquilibriumExpressionsNode( final AqueousSolution solution ) {
        super();

        this.solution = solution;
        this.solutionListener = new SolutionListener() {

            public void soluteChanged() {
                handleSoluteChanged();
            }

            public void concentrationChanged() {
                handleConcentrationOrStrengthChanged();
            }

            public void strengthChanged() {
                handleConcentrationOrStrengthChanged();
            }
        };
        this.solution.addSolutionListener( solutionListener );
        
        // scaling control
        scalingControl = new EquationScalingControl();
        scalingControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setScalingEnabled( scalingControl.isScalingEnabled() );
            }
        });
        scalingControlWrapper = new PSwing( scalingControl );
        addChild( scalingControlWrapper );
        
        // solute equilibrium expression, will be set based on solution
        soluteNode = null;
        
        // water equilibrium expression
        waterNode = new WaterEquilibriumExpressionNode( solution );
        addChild( waterNode );
        
        handleSoluteChanged();
    }

    private void handleSoluteChanged() {

        // remove any existing solute equation
        if ( soluteNode != null ) {
            removeChild( soluteNode );
            soluteNode = null;
        }

        // create the proper type of solute equations 
        if ( solution.isAcidic() ) {
            soluteNode = new AcidEquilibriumExpressionNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteNode = new BaseEquilibriumExpressionNode( solution );
        }

        // add the new solute equations
        if ( soluteNode != null ) {
            addChild( soluteNode );
            soluteNode.setScalingEnabled( waterNode.isScalingEnabled() );
        }
        
        // update the water equation
        waterNode.update();
        
        updateLayout();
    }

    private void handleConcentrationOrStrengthChanged() {
        if ( soluteNode != null ) {
            soluteNode.update();
        }
        waterNode.update();
    }

    public void setScalingEnabled( boolean enabled ) {
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( enabled );
        }
        waterNode.setScalingEnabled( enabled );
    }

    private void updateLayout() {

        // do layout with scaling off
        final boolean scalingWasEnabled = waterNode.isScalingEnabled();
        setScalingEnabled( false );

        scalingControlWrapper.setOffset( 0, Y_SPACING );

        double xOffset = scalingControlWrapper.getXOffset();
        double yOffset = scalingControlWrapper.getFullBoundsReference().getMaxY();

        // solute reaction equation
        if ( soluteNode != null ) {
            xOffset = xOffset - PNodeUtils.getOriginXOffset( soluteNode ) + X_MARGIN;
            yOffset = yOffset - PNodeUtils.getOriginYOffset( soluteNode ) + Y_SPACING;
            soluteNode.setOffset( xOffset, yOffset );
            xOffset = 0;
            yOffset = soluteNode.getFullBoundsReference().getMaxY();
        }

        // water reaction equation
        xOffset = xOffset - PNodeUtils.getOriginXOffset( waterNode ) + X_MARGIN;
        yOffset = yOffset - PNodeUtils.getOriginYOffset( waterNode ) + Y_SPACING;
        waterNode.setOffset( xOffset, yOffset );
        yOffset = waterNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( waterNode ) + Y_SPACING;

        // restore scaling
        setScalingEnabled( scalingWasEnabled );
    }
}

package edu.colorado.phet.ec3.test.phys1d;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Mar 2, 2007
 * Time: 1:58:37 PM
 * Copyright (c) Mar 2, 2007 by Sam Reid
 */
public class SplineLayer extends PNode {
    private ParticleStage particleStage;
    private boolean showTopOffsetSpline = false;
    private double offsetDistance = CubicSpline2DNode.HUMAN_CENTER_OF_MASS_HEIGHT;
    private boolean showBottomOffsetSpline=false;

    public SplineLayer( ParticleStage particleStage ) {
        this.particleStage = particleStage;
        particleStage.addListener( new ParticleStage.Listener() {
            public void splineAdded() {
                update();
            }

            public void splineRemoved() {
                update();
            }

        } );

        update();
    }

    private void update() {
        while( particleStage.numCubicSpline2Ds() < getChildrenCount() ) {
            removeChild( getChildrenCount() - 1 );
        }
        while( particleStage.numCubicSpline2Ds() > getChildrenCount() ) {
            addChild( new CubicSpline2DNode( (CubicSpline2D)particleStage.getCubicSpline2D( particleStage.numCubicSpline2Ds() - 1 ) ) );//todo other spline types
        }
        for( int i = 0; i < getChildrenCount(); i++ ) {
            CubicSpline2DNode node = (CubicSpline2DNode)getChild( i );
            node.setCubicSpline2D( (CubicSpline2D)particleStage.getCubicSpline2D( i ) );//todo: other spline types
        }
        for( int i = 0; i < getChildrenCount(); i++ ) {
            CubicSpline2DNode node = (CubicSpline2DNode)getChild( i );
            node.setShowTopOffsetSpline( showTopOffsetSpline );
            node.setShowBottomOffsetSpline(showBottomOffsetSpline);
        }
        for( int i = 0; i < getChildrenCount(); i++ ) {
            CubicSpline2DNode node = (CubicSpline2DNode)getChild( i );
            node.setOffsetSplineDistance( offsetDistance );
        }
    }

    public void setNormalsVisible( boolean selected ) {
        for( int i = 0; i < getChildrenCount(); i++ ) {
            CubicSpline2DNode node = (CubicSpline2DNode)getChild( i );
            node.setNormalsVisible( selected );
        }
    }

    public boolean isNormalsVisible() {
        if( getChildrenCount() == 0 ) {
            return false;
        }
        if( ( (CubicSpline2DNode)getChild( 0 ) ).isNormalsVisible() ) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isCurvatureVisible() {
        if( getChildrenCount() == 0 ) {
            return false;
        }
        if( ( (CubicSpline2DNode)getChild( 0 ) ).isCurvatureVisible() ) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setCurvatureVisible( boolean selected ) {
        for( int i = 0; i < getChildrenCount(); i++ ) {
            CubicSpline2DNode node = (CubicSpline2DNode)getChild( i );
            node.setCurvatureVisible( selected );
        }
    }

    public boolean isShowTopOffsetSpline() {
        return showTopOffsetSpline;
    }

    public void setShowTopOffsetSpline( boolean showTopOffsetSpline ) {
        this.showTopOffsetSpline = showTopOffsetSpline;
        update();
    }

    public double getOffsetDistance() {
        return offsetDistance;
    }

    public void setOffsetDistance( double value ) {
        this.offsetDistance = value;
        update();
    }

    public boolean isShowBottomOffsetSpline() {
        return showBottomOffsetSpline;
    }

    public void setShowBottomOffsetSpline( boolean selected ) {
        this.showBottomOffsetSpline=selected;
        update();
    }
}

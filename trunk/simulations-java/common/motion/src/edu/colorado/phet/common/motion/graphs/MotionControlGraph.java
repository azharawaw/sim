package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.ArrayList;

/**
 * This subclass of ControlGraph is automatically connected to the supplied MotionModel
 * for update/notification messaging.
 *
 * @author Sam Reid
 */
public class MotionControlGraph extends ControlGraph {
    private ArrayList listeners = new ArrayList();
    private MotionModel motionModel;
    private JFreeChartCursorNode jFreeChartCursorNode;

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel,
                               boolean editable, TimeSeriesModel timeSeriesModel,IUpdateStrategy iPositionDriven ) {
        this( pSwingCanvas, simulationVariable, observableTimeSeries, label, title, min, max, color, thumb, motionModel, editable, timeSeriesModel, null,iPositionDriven );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel,
                               boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy,IUpdateStrategy iPositionDriven ) {
        this( pSwingCanvas, simulationVariable, observableTimeSeries, label, title, min, max, color, thumb, motionModel, editable, timeSeriesModel, updateStrategy, 1000,iPositionDriven );
    }

    public MotionControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, String label, String title,
                               double min, double max, Color color, PNode thumb, final MotionModel motionModel,
                               boolean editable, final TimeSeriesModel timeSeriesModel, final UpdateStrategy updateStrategy, int maxDomainValue, final IUpdateStrategy iPositionDriven ) {
        super( pSwingCanvas, simulationVariable, observableTimeSeries, label, title, min, max, color, thumb, timeSeriesModel, maxDomainValue );
        this.motionModel = motionModel;
        addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                notifyZoomChanged();
            }

            public void zoomedIn() {
                notifyZoomChanged();
            }
        } );
        setEditable( editable );

        jFreeChartCursorNode = new JFreeChartCursorNode( getJFreeChartNode() );
        addChild( jFreeChartCursorNode );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                jFreeChartCursorNode.setTime( timeSeriesModel.getTime() );
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                timeSeriesModel.setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                updateCursorVisible();
            }

            public void pauseChanged() {
                updateCursorLocation();
                updateCursorVisible();
            }
        } );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                motionModel.getTimeSeriesModel().setPlaybackMode();
                motionModel.getTimeSeriesModel().setPlaybackTime( jFreeChartCursorNode.getTime() );
                System.out.println( "playback time=" + jFreeChartCursorNode.getTime() );
            }
        } );
        motionModel.getTimeSeriesModel().addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                jFreeChartCursorNode.setMaxDragTime( motionModel.getTimeSeriesModel().getRecordTime() );
//                System.out.println( "max record time=" + motionModel.getTimeSeriesModel().getRecordTime() );
            }

            public void dataSeriesCleared() {
                clear();
            }
        } );
        updateCursorVisible();

        if( updateStrategy != null ) {
            addListener( new Adapter() {
                public void controlFocusGrabbed() {
                    iPositionDriven.setUpdateStrategy( updateStrategy );
                }
            } );
        }
    }

    private void updateCursorLocation() {
        jFreeChartCursorNode.setTime( motionModel.getTimeSeriesModel().getTime() );
    }

    private void updateCursorVisible() {
        jFreeChartCursorNode.setVisible( motionModel.getTimeSeriesModel().isPlaybackMode() || motionModel.getTimeSeriesModel().isPaused() );
    }

    public boolean hasListener( Listener listener ) {
        return listeners.contains( listener );
    }

    public static interface Listener {
        void horizontalZoomChanged( MotionControlGraph source );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyZoomChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).horizontalZoomChanged( this );
        }
    }

}

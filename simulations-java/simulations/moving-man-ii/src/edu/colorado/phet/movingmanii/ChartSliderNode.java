package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This component contains both a MovingManChart (supplied by the caller) and a vertical Slider node,
 * which spans the data range of the JFreeChart.
 * This component can be used to display and control an xy dataset.
 *
 * @author Sam Reid
 */
public class ChartSliderNode extends PNode {
    private PhetPPath trackPPath;
    private PNode sliderThumb;
    private Color highlightColor;
    private double value = 0.0;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private MovingManChart movingManChart;
    private boolean selected = false;//highlight

    public ChartSliderNode(MovingManChart movingManChart, final PNode sliderThumb, Color highlightColor) {
        this.sliderThumb = sliderThumb;
        this.highlightColor = highlightColor;
        this.sliderThumb.addInputEventListener(new CursorHandler());
        trackPPath = new PhetPPath(new BasicStroke(1), Color.black);
        addChild(trackPPath);
        addChild(sliderThumb);
        sliderThumb.addInputEventListener(new PBasicInputEventHandler() {
            Point2D initDragPoint = null;
            double origY;

            public void mousePressed(PInputEvent event) {
                initDragPoint = event.getPositionRelativeTo(sliderThumb.getParent());
                if (value < getMinRangeValue()) {
                    origY = getMinRangeValue();
                } else if (value > getMaxRangeValue()) {
                    origY = getMaxRangeValue();
                } else {
                    origY = value;
                }
                notifySliderThumbGrabbed();
            }

            public void mouseReleased(PInputEvent event) {
                initDragPoint = null;
            }

            public void mouseDragged(PInputEvent event) {
                if (initDragPoint == null) {
                    mousePressed(event);
                }
                double yCurrent = event.getPositionRelativeTo(sliderThumb.getParent()).getY();
                double nodeDY = yCurrent - initDragPoint.getY();
                double plotDY = ChartSliderNode.this.movingManChart.viewToModelDY(nodeDY);
//                Point2D plot1 = nodeToPlot(new Point2D.Double(0, 0));
//                Point2D plot2 = nodeToPlot(new Point2D.Double(0, nodeDY));
//                double plotDY = plot2.getY() - plot1.getY();
                double value = clamp(origY + plotDY);
//                setValue(value);
                notifySliderDragged(value);
            }
        });

        this.movingManChart = movingManChart;
        movingManChart.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateLayout();
            }
        });
//        MovingManChart.getChart().addChangeListener( new ChartChangeListener() {
//            public void chartChanged( ChartChangeEvent chartChangeEvent ) {
//                updateLayout();
//            }
//        } );
        //todo: catch layout changes

        updateLayout();
        updateTrackPPath();
    }

    private void notifySliderDragged(double value) {
        for (Listener listener : listeners) {
            listener.sliderDragged(value);
        }
    }

    private void notifySliderThumbGrabbed() {
        for (Listener listener : listeners) {
            listener.sliderThumbGrabbed();
        }
    }

    protected double getMaxRangeValue() {
        return movingManChart.getMaxRangeValue();
    }

    protected double getMinRangeValue() {
        return movingManChart.getMinRangeValue();
    }

    protected Rectangle2D getDataArea() {
        return new Rectangle2D.Double(0, 0, movingManChart.dataAreaWidth, movingManChart.dataAreaHeight);
    }

    protected Point2D plotToNode(Point2D.Double aDouble) {
        return movingManChart.modelToView(new TimeData(aDouble.getY(), aDouble.getX()));
    }

    private double clamp(double v) {
        if (v > getMaxRangeValue()) {
            v = getMaxRangeValue();
        }
        if (v < getMinRangeValue()) {
            v = getMinRangeValue();
        }
        return v;
    }

    /**
     * Sets the value of the slider for this chart.
     *
     * @param value the value to set for this controller.
     */
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            updateThumbLocation();
            notifyValueChanged();
        }
    }

    /**
     * Gets the value of the control slider.
     *
     * @return the value of the control slider.
     */
    public double getValue() {
        return value;
    }

    protected void updateLayout() {
        Rectangle2D dataArea = getDataArea();
        trackPPath.setPathTo(new Rectangle2D.Double(0, dataArea.getY(), 5, dataArea.getHeight()));
        updateThumbLocation();
    }

    private void updateThumbLocation() {
        Point2D nodeLocation = plotToNode(new Point2D.Double(0, clamp(value)));
        sliderThumb.setOffset(trackPPath.getFullBounds().getCenterX() - sliderThumb.getFullBounds().getWidth() / 2.0,
                nodeLocation.getY() - sliderThumb.getFullBounds().getHeight() / 2.0);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateTrackPPath();
    }

    private void updateTrackPPath() {
        trackPPath.setStroke(new BasicStroke(selected ? 2 : 1));
        trackPPath.setStrokePaint(selected ? highlightColor : Color.black);
    }

    public Rectangle2D.Double getTrackFullBounds() {
        return trackPPath.getFullBounds();
    }

    public Rectangle2D.Double getThumbFullBounds() {
        return sliderThumb.getFullBounds();
    }

    /**
     * Clients can listen for value change events, whether generated by the slider or another call.
     */
    public static interface Listener {
        void valueChanged();

        void sliderThumbGrabbed();

        void sliderDragged(double value);
    }

    public static class Adapter implements Listener {

        public void valueChanged() {
        }

        public void sliderThumbGrabbed() {
        }

        public void sliderDragged(double value) {
        }
    }

    /**
     * Adds a listener for value change events.
     *
     * @param listener the value change listener.
     */
    public void addListener(ChartSliderNode.Listener listener) {
        listeners.add(listener);
    }

    private void notifyValueChanged() {
        for (Listener listener : listeners) {
            listener.valueChanged();
        }
    }
}
package edu.colorado.phet.recordandplayback.gui;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.recordandplayback.model.RecordModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Hashtable;

public class PlaybackSpeedSlider<T> extends PNode {
    private RecordModel<T> model;

    public PlaybackSpeedSlider(final RecordModel<T> model) {
        this.model = model;
        addInputEventListener(new CursorHandler());

        final JSlider slider = new JSlider();
        slider.setBackground(new Color(0, 0, 0, 0));
        final Function.LinearFunction transform = new Function.LinearFunction(slider.getMinimum(), slider.getMaximum(), 0.25, 2.0);

        Hashtable<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();

        dict.put(slider.getMinimum(), new JLabel(PhetCommonResources.getString("Common.time.slow")));
        dict.put(slider.getMaximum(), new JLabel(PhetCommonResources.getString("Common.time.fast")));

        slider.setLabelTable(dict);
        slider.setPaintLabels(true);
        PSwing playbackSpeedSlider = new PSwing(slider);
        addChild(playbackSpeedSlider);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                model.setPlayback(transform.evaluate(slider.getValue()));
            }
        });

        model.addObserver(new SimpleObserver() {
            public void update() {
                updatePlaybackSliderVisible();
            }
        });
    }

    private void updatePlaybackSliderVisible() {
        setVisible(model.isPlayback());
    }
}

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.ModuleApplication;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:07:30 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class SoundModule extends WaveInterferenceModule {
    private SoundSimulationPanel soundSimulationPanel;
    private WaveInterferenceModel waveInterferenceModel;
    private SoundControlPanel waterControlPanel;

    public SoundModule() {
        super( "Sound" );
        waveInterferenceModel = new WaveInterferenceModel();
        soundSimulationPanel = new SoundSimulationPanel( this );
        waterControlPanel = new SoundControlPanel( this );

        addModelElement( waveInterferenceModel );
        addModelElement( soundSimulationPanel );

        setSimulationPanel( soundSimulationPanel );
        setControlPanel( waterControlPanel );
    }

    public WaveModel getWaveModel() {
        return waveInterferenceModel.getWaveModel();
    }

    public SlitPotential getSlitPotential() {
        return waveInterferenceModel.getSlitPotential();
    }

    public Oscillator getSecondaryOscillator() {
        return waveInterferenceModel.getSecondaryOscillator();
    }

    public Oscillator getPrimaryOscillator() {
        return waveInterferenceModel.getPrimaryOscillator();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return soundSimulationPanel.getIntensityReaderSet();
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return soundSimulationPanel.getMeasurementToolSet();
    }

    public FaucetGraphic getPrimaryFaucetGraphic() {
        return soundSimulationPanel.getPrimaryFaucetGraphic();
    }

    public SoundSimulationPanel getSoundSimulationPanel() {
        return soundSimulationPanel;
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return soundSimulationPanel.getRotationWaveGraphic();
    }

    public MultiDrip getMultiDrip() {
        return soundSimulationPanel.getMultiDrip();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return soundSimulationPanel.getLatticeScreenCoordinates();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new SoundModule() );
    }
}

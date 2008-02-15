/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * GlaciersStrings is a collection of localized strings used by this simulation.
 * All strings are loaded statically so that we can easily test for missing strings on start up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersStrings {

    /* not intended for instantiation */
    private GlaciersStrings() {}
    
    //----------------------------------------------------------------------------
    // Simulation-specific strings
    //----------------------------------------------------------------------------

    public static final String BUTTON_STEADY_STATE = GlaciersResources.getString( "button.steadyState" );
    
    public static final String CHECK_BOX_EQUILIBRIUM_LINE = GlaciersResources.getString( "checkBox.equilibriumLine" );
    public static final String CHECK_BOX_ICE_FLOW = GlaciersResources.getString( "checkBox.iceFlow" );
    public static final String CHECK_BOX_SNOWFALL = GlaciersResources.getString( "checkBox.snowfall" );
    public static final String CHECK_BOX_COORDINATES = GlaciersResources.getString( "checkBox.coordinates" );
    public static final String CHECK_BOX_AGE_OF_ICE = GlaciersResources.getString( "checkBox.ageOfIce" );
    
    public static final String HELP_EQUILIBRIUM_BUTTON = GlaciersResources.getString( "help.equilibriumButton" );
    
    public static final String LABEL_ACCUMULATION = GlaciersResources.getString( "label.accumulation" );
    public static final String LABEL_ABLATION = GlaciersResources.getString( "label.ablation" );
    public static final String LABEL_GLACIAL_BUDGET = GlaciersResources.getString( "label.glacialBudget" );
    public static final String LABEL_EQUILIBRIUM_LINE_ALTITUDE = GlaciersResources.getString( "label.equilibriumLineAltitude" );
    public static final String LABEL_MAXIMUM_SNOWFALL = GlaciersResources.getString( "label.maximumSnowfall" );
    
    public static final String MENU_FILE_LOAD = GlaciersResources.getString( "menu.file.load" );
    public static final char MENU_FILE_LOAD_MNEMONIC = GlaciersResources.getChar( "menu.file.load.mnemonic", 'L' );
    public static final String MENU_FILE_SAVE = GlaciersResources.getString( "menu.file.save" );
    public static final char MENU_FILE_SAVE_MNEMONIC = GlaciersResources.getChar( "menu.file.save.mnemonic", 'S' );
    public static final String MENU_OPTIONS = GlaciersResources.getString( "menu.options" );
    public static final char MENU_OPTIONS_MNEMONIC = GlaciersResources.getChar( "menu.options.mnemonic", 'O' );
    
    public static final String MESSAGE_NOT_A_CONFIG_FILE = GlaciersResources.getString( "message.notAConfigFile" );
    
    public static final String RADIO_BUTTON_SNOWFALL_AND_TEMPERATURE = GlaciersResources.getString( "radioButton.snowfallAndTemperature" );
    public static final String RADIO_BUTTON_CLIMATE_PRESETS = GlaciersResources.getString( "radioButton.climatePresets" );
    public static final String RADIO_BUTTON_MASS_BALANCE = GlaciersResources.getString( "radioButton.massBalance" );
    public static final String RADIO_BUTTON_NO_GRAPH = GlaciersResources.getString( "radioButton.noGraph" );
    public static final String RADIO_BUTTON_GLACIER_LENGTH_VERSUS_TIME = GlaciersResources.getString( "radioButton.glacierLengthVersusTime" );
    public static final String RADIO_BUTTON_EQUILIBRIUM_LINE_VERSUS_TIME = GlaciersResources.getString( "radioButton.equilibriumLineAltitudeVersusTime" );
    public static final String RADIO_BUTTON_ACCUMULATION_VERSUS_ALTITUDE = GlaciersResources.getString( "radioButton.accumulationVersusAltitude" );
    public static final String RADIO_BUTTON_ABLATION_VERSUS_ALTITUDE = GlaciersResources.getString( "radioButton.ablationVersusAltitude" );
    public static final String RADIO_BUTTON_MASS_BALANCE_VERSUS_ALTITUDE = GlaciersResources.getString( "radioButton.massBalanceVersusAltitude" );
    public static final String RADIO_BUTTON_TEMPERATURE_VERSUS_ALTITUDE = GlaciersResources.getString( "radioButton.temperatureVersusAltitude" );
    public static final String RADIO_BUTTON_VALLEY_FLOOR_VERSUS_ALTITUDE = GlaciersResources.getString( "radioButton.valleyFloorVersusAltitude" );
    
    public static final String SLIDER_CLOCK_SLOW = GlaciersResources.getString( "slider.clock.slow" );
    public static final String SLIDER_CLOCK_FAST = GlaciersResources.getString( "slider.clock.fast" );
    public static final String SLIDER_SNOWFALL = GlaciersResources.getString( "slider.snowfall" );
    public static final String SLIDER_SNOWFALL_REFERENCE_ELEVATION = GlaciersResources.getString( "slider.snowfallReferenceElevation" );
    public static final String SLIDER_TEMPERATURE = GlaciersResources.getString( "slider.temperature" );
    
    public static final String TOOLBOX_THERMOMETER = GlaciersResources.getString( "toolbox.thermometer" );
    public static final String TOOLBOX_GLACIAL_BUDGET_METER = GlaciersResources.getString( "toolbox.glacialBudgetMeter" );
    public static final String TOOLBOX_TRACER_FLAG = GlaciersResources.getString( "toolbox.tracerFlag" );
    public static final String TOOLBOX_ICE_THICKNESS_TOOL = GlaciersResources.getString( "toolbox.iceThicknessTool" );
    public static final String TOOLBOX_BOREHOLD_DRILL = GlaciersResources.getString( "toolbox.boreholeDrill" );
    public static final String TOOLBOX_GPS_RECEIVER = GlaciersResources.getString( "toolbox.gpsReceiver" );
    public static final String TOOLBOX_TRASH_CAN = GlaciersResources.getString( "toolbox.trashCan" );
    
    public static final String TITLE_ADVANCED = GlaciersResources.getString( "title.advanced" );
    public static final String TITLE_BASIC = GlaciersResources.getString( "title.basic" );
    public static final String TITLE_CLIMATE_CONTROLS = GlaciersResources.getString( "title.climateControls" );
    public static final String TITLE_ERROR = GlaciersResources.getString( "title.error" );
    public static final String TITLE_GRAPHS = GlaciersResources.getString( "title.graphs" );
    public static final String TITLE_TOOLBOX = GlaciersResources.getString( "title.toolbox" );
    public static final String TITLE_VIEW = GlaciersResources.getString( "title.view" );
    
    public static final String UNITS_ABLATION = GlaciersResources.getString( "units.ablation" );
    public static final String UNITS_ACCUMULATION = GlaciersResources.getString( "units.accumulation" );
    public static final String UNITS_DISTANCE = GlaciersResources.getString( "units.distance" );
    public static final String UNITS_ELEVATION = GlaciersResources.getString( "units.elevation" );
    public static final String UNITS_EQUILIBRIUM_LINE_ALTITUDE = GlaciersResources.getString( "units.equilibriumLineAltitude" );
    public static final String UNITS_GLACIAL_BUDGET = GlaciersResources.getString( "units.glacialBudget" );
    public static final String UNITS_ICE_THICKNESS = GlaciersResources.getString( "units.iceThickness" );
    public static final String UNITS_TEMPERATURE = GlaciersResources.getString( "units.temperature" );
    public static final String UNITS_TIME = GlaciersResources.getString( "units.time" );
    
    //----------------------------------------------------------------------------
    // Common library strings
    //----------------------------------------------------------------------------

    public static final String BUTTON_RESET_ALL = GlaciersResources.getCommonString( "ControlPanel.button.resetAll" );
    public static final String BUTTON_SHOW_HELP = PhetCommonResources.getInstance().getLocalizedString( "Common.HelpPanel.ShowHelp" );
    public static final String BUTTON_HIDE_HELP = PhetCommonResources.getInstance().getLocalizedString( "Common.HelpPanel.HideHelp" );
}

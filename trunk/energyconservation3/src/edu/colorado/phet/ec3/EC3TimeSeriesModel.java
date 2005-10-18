/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.timeseries.ObjectTimeSeries;
import edu.colorado.phet.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 6:08:19 PM
 * Copyright (c) Oct 9, 2005 by Sam Reid
 */

public class EC3TimeSeriesModel extends TimeSeriesModel {
    private EC3Module module;
    private ObjectTimeSeries series = new ObjectTimeSeries();

    public EC3TimeSeriesModel( EC3Module module ) {
        super( Double.POSITIVE_INFINITY );
        this.module = module;
    }

    protected boolean confirmReset() {
        return true;
    }

    public void updateModel( ClockTickEvent clockEvent ) {
        module.stepModel( clockEvent.getDt() / 10.0 );
        Object state = module.getModelState();
        series.addPoint( state, getRecordTime() );
    }
}

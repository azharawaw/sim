package edu.colorado.phet.wickettest;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.tomcattest.WebSimulation;
import edu.colorado.phet.tomcattest.util.SqlUtils;
import edu.colorado.phet.wickettest.util.StaticImage;

public class HelloWorld extends WebPage {
    public HelloWorld( PageParameters parameters ) {
        ServletContext context = ( (WebApplication) getApplication() ).getServletContext();

        Locale myLocale = LocaleUtils.stringToLocale( parameters.getString( "localeString" ) );

        List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( context, null, null, myLocale );
        WebSimulation.orderSimulations( simulations, myLocale );

        List<SimulationModel> models = new LinkedList<SimulationModel>();

        for ( WebSimulation simulation : simulations ) {
            models.add( new SimulationModel( simulation ) );
        }

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );
                item.add( new Label( "title", simulation.getTitle() ) );
                //item.add( new StaticImage( "screenshot", new Model( simulation.getImageUrl() ) ) );
                item.add( new StaticImage( "thumbnail", new Model( simulation.getThumbnailUrl() ) ) );
            }
        };
        add( simulationList );

    }
}
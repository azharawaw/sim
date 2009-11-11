package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutLegendPanel extends PhetPanel {
    public AboutLegendPanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "about.legend";
    }

    public static String getUrl() {
        return "about/legend";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context ) {
                if ( DistributionHandler.redirectPageClassToProduction( context.getCycle(), AboutLegendPanel.class ) ) {
                    return "http://phet.colorado.edu/about/legend.php";
                }
                else {
                    return super.getRawUrl( context );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
package edu.colorado.phet.website.content;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TeacherIdeasPanel extends PhetPanel {
    public TeacherIdeasPanel( String id, PageContext context ) {
        super( id, context );

//        add( SimulationDisplay.createLink( "online-link", context ) );
//        add( FullInstallPanel.getLinker().getLink( "install-link", context, getPhetCycle() ) );
//        add( OneAtATimePanel.getLinker().getLink( "offline-link", context, getPhetCycle() ) );
//
//        add( new LocalizedText( "get-phet-install-header", "get-phet.install.header" ) );
//        add( new LocalizedText( "get-phet-offline-header", "get-phet.offline.header" ) );
//
//        add( new LocalizedText( "get-phet-install-howToGet", "get-phet.install.howToGet", new Object[]{
//                FullInstallPanel.getLinker().getHref( context, getPhetCycle() )
//        } ) );
//
//        add( new LocalizedText( "get-phet-offline-howToGet", "get-phet.offline.howToGet", new Object[]{
//                OneAtATimePanel.getLinker().getHref( context, getPhetCycle() )
//        } ) );

    }

    public static String getKey() {
        return "teacherIdeas";
    }

    public static String getUrl() {
        return "contributions";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
package edu.colorado.phet.website.content;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.ContributionBrowsePanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionBrowsePage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( ContributionBrowsePage.class.getName() );

    public ContributionBrowsePage( PageParameters parameters ) {
        super( parameters );

//        if ( category == null && parameters.containsKey( "categories" ) ) {
//            // didn't find the category
//            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
//        }

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.browse" ) );

        // TODO: localize
        addTitle( "Browse PhET contributions" );

        // TODO: for now, only showing all contributions
        final List<Contribution> contributions = new LinkedList<Contribution>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select c from Contribution as c" ).list();
                for ( Object o : list ) {
                    contributions.add( (Contribution) o );
                }
                return true;
            }
        } );

        add( new ContributionBrowsePanel( "contribution-browse-panel", getPageContext(), contributions ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^contributions/browse$", ContributionBrowsePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "contributions/browse";
            }
        };
    }

}
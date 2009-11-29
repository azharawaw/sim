package edu.colorado.phet.website;

import java.util.*;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.admin.AdminMainPage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.content.*;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.templates.StaticPage;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.translation.TranslationUrlStrategy;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.PhetUrlStrategy;

public class PhetWicketApplication extends WebApplication {

    private PhetUrlMapper mapper;
    private NavMenu menu;

    private List<Translation> translations = new LinkedList<Translation>();

    public Class getHomePage() {
        return IndexPage.class;
    }

    @Override
    protected void init() {
        super.init();

        getApplicationSettings().setPageExpiredErrorPage( ErrorPage.class );
        getApplicationSettings().setAccessDeniedPage( ErrorPage.class );
        getApplicationSettings().setInternalErrorPage( ErrorPage.class );

        // add static pages, that are accessed through reflection. this is used so that separate page AND panel classes
        // are not needed for each visual page.
        // NOTE: do this before adding StaticPage into the mapper
        StaticPage.addPanel( TroubleshootingMainPanel.class );
        StaticPage.addPanel( AboutMainPanel.class );
        StaticPage.addPanel( WorkshopsPanel.class );
        StaticPage.addPanel( ContributePanel.class );
        StaticPage.addPanel( RunOurSimulationsPanel.class );
        StaticPage.addPanel( FullInstallPanel.class );
        StaticPage.addPanel( OneAtATimePanel.class );
        StaticPage.addPanel( ResearchPanel.class );
        StaticPage.addPanel( TroubleshootingJavaPanel.class );
        StaticPage.addPanel( TroubleshootingFlashPanel.class );
        StaticPage.addPanel( TroubleshootingJavascriptPanel.class );
        StaticPage.addPanel( AboutSourceCodePanel.class );
        StaticPage.addPanel( AboutLegendPanel.class );
        StaticPage.addPanel( AboutContactPanel.class );
        StaticPage.addPanel( AboutWhoWeArePanel.class );
        StaticPage.addPanel( AboutLicensingPanel.class );
        StaticPage.addPanel( AboutSponsorsPanel.class );
        StaticPage.addPanel( TranslatedSimsPanel.class );

        // create a url mapper, and add the page classes to it
        mapper = new PhetUrlMapper();
        SimulationDisplay.addToMapper( mapper );
        SimulationPage.addToMapper( mapper );
        StaticPage.addToMapper( mapper );
        IndexPage.addToMapper( mapper );
        SimsByKeywordPage.addToMapper( mapper );

        // set up the custom localizer
        getResourceSettings().setLocalizer( new PhetLocalizer() );

        // set up the locales that will be accessible
        initializeTranslations();
        mount( new PhetUrlStrategy( "en", mapper ) );
        for ( Translation translation : translations ) {
            mount( new PhetUrlStrategy( LocaleUtils.localeToString( translation.getLocale() ), mapper ) );
        }
        mount( new TranslationUrlStrategy( "translation", mapper ) );

        mountBookmarkablePage( "admin", AdminMainPage.class );

        // this will remove the default string resource loader. essentially this new one has better locale-handling,
        // so that if a string is not found for a more specific locale (es_MX), it would try "es", then the default
        // properties file
        // NOTE: This may break in Wicket 1.4
        getResourceSettings().addStringResourceLoader( new ClassStringResourceLoader( PhetWicketApplication.class ) );

        // initialize the navigation menu
        menu = new NavMenu();

        // get rid of wicket:id's and other related tags in the produced HTML.
        getMarkupSettings().setStripWicketTags( true );

        mount( new HybridUrlCodingStrategy( "/error", ErrorPage.class ) );
        mount( new HybridUrlCodingStrategy( "/error/404", NotFoundPage.class ) );
        mount( new HybridUrlCodingStrategy( "/activities", BlankPage.class ) );

        String wicketMode = getServletContext().getInitParameter( "configuration" );
        System.out.println( "Running as: " + wicketMode );

        System.out.println( "Detected phet-document-root: " + getServletContext().getInitParameter( "phet-document-root" ) );

    }

    private PhetLocales supportedLocales = null;

    public PhetLocales getSupportedLocales() {
        if ( supportedLocales == null ) {
            supportedLocales = PhetLocales.getInstance();
        }
        return supportedLocales;
    }

    private void initializeTranslations() {
        org.hibernate.Session session = HibernateUtils.getInstance().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            translations = HibernateUtils.getVisibleTranslations( session );

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "WARNING: exception:\n" + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }
        session.close();

        sortTranslations();
    }

    private void sortTranslations() {
        Collections.sort( translations, new Comparator<Translation>() {
            public int compare( Translation a, Translation b ) {
                return a.getLocale().getDisplayName().compareTo( b.getLocale().getDisplayName() );
            }
        } );
    }

    public List<String> getTranslationLocaleStrings() {
        // TODO: maybe cache this list if it's called a lot?
        List<String> ret = new LinkedList<String>();
        for ( Translation translation : translations ) {
            ret.add( LocaleUtils.localeToString( translation.getLocale() ) );
        }
        return ret;
    }

    public NavMenu getMenu() {
        return menu;
    }

    @Override
    public Session newSession( Request request, Response response ) {
        return new PhetSession( request );
    }

    @Override
    public RequestCycle newRequestCycle( Request request, Response response ) {
        return new PhetRequestCycle( this, (WebRequest) request, response );
    }

    private static Locale defaultLocale = LocaleUtils.stringToLocale( "en" );

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void addTranslation( Translation translation ) {
        String localeString = LocaleUtils.localeToString( translation.getLocale() );
        System.out.println( "Adding translation for " + localeString );
        getResourceSettings().getLocalizer().clearCache();
        translations.add( translation );
        mount( new PhetUrlStrategy( localeString, mapper ) );
        sortTranslations();
    }

    public void removeTranslation( Translation translation ) {
        String localeString = LocaleUtils.localeToString( translation.getLocale() );
        System.out.println( "Removing translation for " + localeString );
        getResourceSettings().getLocalizer().clearCache();
        int oldNumTranslations = translations.size();
        for ( Translation tr : translations ) {
            if ( tr.getId() == translation.getId() ) {
                translations.remove( tr );
                break;
            }
        }
        if ( translations.size() != oldNumTranslations - 1 ) {
            throw new RuntimeException( "Did not correctly remove old translation" );
        }
        unmount( localeString );
        sortTranslations();
    }
}

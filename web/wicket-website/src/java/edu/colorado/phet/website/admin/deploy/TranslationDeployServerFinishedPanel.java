package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class TranslationDeployServerFinishedPanel extends PhetPanel {

    private Component publisher;

    public TranslationDeployServerFinishedPanel( String id, PageContext context, String finalText, File translationDir ) {
        super( id, context );

        add( new RawLabel( "text", finalText ) );

        List<File> fileList = Arrays.asList( translationDir.listFiles() );

        Collections.sort( fileList, new Comparator<File>() {
            public int compare( File a, File b ) {
                return a.getName().compareToIgnoreCase( b.getName() );
            }
        } );

        add( new ListView( "file", fileList ) {
            @Override
            protected void populateItem( ListItem item ) {
                try {
                    File file = (File) item.getModelObject();
                    String path = file.getCanonicalPath().substring( file.getCanonicalPath().indexOf( "/staging/translations" ) );

                    RawLink link = new RawLink( "link", path );
                    item.add( link );

                    link.add( new Label( "name", file.getName() ) );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );

        AjaxLink publishLink = new AjaxLink( "publish-link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                target.addComponent( this );
                setVisible( false );

                Label newPublisher = new Label( "publisher", "BOO!!!" );
                newPublisher.setOutputMarkupId( true );
                newPublisher.setMarkupId( "deploy-publisher" );
                publisher.replaceWith( newPublisher );
                target.addComponent( newPublisher );
            }
        };
        publishLink.setOutputMarkupId( true );
        add( publishLink );

        publisher = new Label( "publisher", "" );
        publisher.setOutputMarkupId( true );
        publisher.setMarkupId( "deploy-publisher" );
        add( publisher );
    }
}
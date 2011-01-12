// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

/**
 * @author Sam Reid
 */
public class Server {
    private Object dataSample;

    public static void main( String[] args ) {
        new Server().start();
    }

    private void start() {
        remote().start( Config.serverIP, Config.SERVER_PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof TeacherDataRequest ) {
                            getContext().replySafe( dataSample );
                        }
                        else {
                            dataSample = o;
                        }
                    }
                };
            }
        } ) );
    }
}

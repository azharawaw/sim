/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/persistence/StateDescriptor.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util.persistence;

/**
 * StateDescriptor
 * <p/>
 * Interface for an object that defines all the persistent information about a Persistent object. Each
 * concrete implementer of type Persistent should have an associated concrete implementer of StateDescriptor.
 * <p/>
 * Concrete subclasses must be Java Beans conformant. They must have a no-arguments constructor, and
 * public getters and setters for the information they hold.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public interface StateDescriptor {

    /**
     * Sets the state of a PersistentObject based on the information in the StateDescriptor
     * <p/>
     *
     * @param persistentObject
     */
    void setState( Persistent persistentObject );
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simeventprocessor.scala.{studySessionsNov2011, phet}
import java.io.File
import edu.colorado.phet.common.phetcommon.util.FileUtils

/**
 * Copy log files to separate directories for processing.
 * @author Sam Reid
 */
object CopyLogFiles extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

  for ( session <- studySessionsNov2011.all ) {

    val sessionDir = new File("C:\\Users\\Sam\\Desktop\\session-dirs\\" + session.study + "_" + studySessionsNov2011.all.indexOf(session))
    sessionDir.mkdirs()

    val logs = all.filter(session)
    logs.foreach(log => FileUtils.copyToDir(log.file, sessionDir))
  }
}
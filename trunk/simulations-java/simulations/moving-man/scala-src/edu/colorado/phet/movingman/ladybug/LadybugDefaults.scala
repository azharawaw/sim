/*
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Jan 19, 2009
 * Time: 11:20:01 AM
 */
package edu.colorado.phet.movingman.ladybug

import model.Vector2D

object LadybugDefaults {
  var remoteIsIndicator = true
  var vaSticky = true
  var timelineLengthSeconds: Double = 15
  var pauseAtEndOfPlayback = true
  var recordAtEndOfPlayback = false
  val defaultLocation = new Vector2D(5, 1)
}
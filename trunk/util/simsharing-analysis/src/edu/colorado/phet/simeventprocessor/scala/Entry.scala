// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.util.StringTokenizer
import edu.colorado.phet.common.phetcommon.simsharing.Parameter
import collection.mutable.HashMap
import java.lang.RuntimeException

case class Entry(time: Long, //Time since sim started in millisec
                 actor: String,
                 event: String,
                 parameters: Map[String, String]) {

  def matches(actor: String, event: String, params: Map[String, String]): Boolean = {
    for ( key <- params.keys ) {
      if ( !hasParameter(key, params(key)) ) {
        return false
      }
    }
    this.actor == actor && this.event == event
  }

  def matches(_actor: String, params: Map[String, String]): Boolean = {
    for ( param <- params ) {
      if ( !hasParameter(param._1, param._2) ) {
        return false
      }
    }
    actor == _actor
  }

  def apply(key: String): Option[String] = {
    if ( parameters.contains(key) ) {
      Some(parameters(key))
    }
    else {
      None
    }
  }

  def brief = actor + " " + event + ( if ( ( actor == "button node" ) ) {": " + apply("actionCommand")}
  else {""} )

  def hasParameter(key: String, value: String): Boolean = {
    for ( parameter <- parameters ) {
      if ( ( parameter._1 == key ) && ( parameter._2 == value ) ) {
        return true
      }
    }
    false
  }
}

object Entry {
  def parse(line: String): Entry = {
    val tokenizer = new StringTokenizer(line, "\t")
    val time = java.lang.Long.parseLong(tokenizer.nextToken)
    val obj = tokenizer.nextToken
    val event = tokenizer.nextToken
    val remainderOfLineBuf = new StringBuffer
    while ( tokenizer.hasMoreTokens ) {
      remainderOfLineBuf.append(tokenizer.nextToken)
      remainderOfLineBuf.append(Parameter.DELIMITER)
    }
    val remainderOfLine = remainderOfLineBuf.toString.trim
    val parameters = Parameter.parseParameters(remainderOfLine)
    val map = new HashMap[String, String]()
    for ( p <- parameters ) {
      if ( map.contains(p.name) ) {
        throw new RuntimeException("Duplicate string key for " + p.name)
      }
      map.put(p.name, p.value)
    }

    //make map immutable
    new Entry(time, obj, event, map.toMap)
  }
}
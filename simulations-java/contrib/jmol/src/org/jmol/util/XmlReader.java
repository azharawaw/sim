/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */

package org.jmol.util;

import java.io.BufferedReader;

import javax.vecmath.Point3f;

public class XmlReader {
  
  // a relatively simple XML reader requiring "nice" line and tag format.
  

  BufferedReader br;
  String line;

  public String getLine() {
    return line;
  }

  public XmlReader(BufferedReader br) {
    this.br = br;
  }

  public String toTag(String name) throws Exception {
    skipTo("<" + name);
    if (line == null)
      return "";
    int i = line.indexOf("<" + name) + name.length() + 1;
    if (i == line.length())
      return line;
    if (line.charAt(i) == ' ' || line.charAt(i) == '>')
      return line;
    line = null;
    return toTag(name);
  }
  
  public void skipTag(String name) throws Exception {
    skipTo("</" + name + ">");
  }

  /**
   * 
   * @param name
   * @param data
   * @param withTag
   * @param allowSelfCloseOption TODO
   * @return trimmed contents or tag + contents, never closing tag
   * @throws Exception
   */
  public String getXmlData(String name, String data, boolean withTag, boolean allowSelfCloseOption)
      throws Exception {
    // crude
    String closer = "</" + name + ">";
    String tag = "<" + name;
    if (data == null) {
      StringBuffer sb = new StringBuffer();
      try {
        if (line == null)
          line = br.readLine();
        while (line.indexOf(tag) < 0) {
          line = br.readLine();
        }
      } catch (Exception e) {
        return null;
      }
      sb.append(line);
      boolean selfClosed = false;
      int pt = line.indexOf("/>");
      int pt1 = line.indexOf(">");
      if (pt1 < 0 || pt == pt1 - 1)
        selfClosed = allowSelfCloseOption;
      while (line.indexOf(closer) < 0 && (!selfClosed  || line.indexOf("/>") < 0))
        sb.append(line = br.readLine());
      data = sb.toString();
    }
    return extractTag(data, tag, closer, withTag);
  }

  private static String extractTag(String data, String tag, String closer, boolean withTag) {
    int pt1 = data.indexOf(tag);
    if (pt1 < 0)
      return "";
    int pt2 = data.indexOf(closer, pt1);
    if (pt2 < 0) {
      pt2 = data.indexOf("/>", pt1);
      closer = "/>";
    }
    if (pt2 < 0)
      return "";
    if (withTag) {
      pt2 += closer.length();
      return data.substring(pt1, pt2);
    }
    boolean quoted = false;
    for (; pt1 < pt2; pt1++) {
      char ch;
      if ((ch = data.charAt(pt1)) == '"')
        quoted = !quoted;
      else if (quoted && ch == '\\')
        pt1++;
      else if (!quoted && (ch == '>' || ch == '/'))
        break;
    }
    if (pt1 >= pt2)
      return "";
    while (Character.isWhitespace(data.charAt(++pt1))) {
    }
    return XmlUtil.unwrapCdata(data.substring(pt1, pt2));
  }

  public static String getXmlAttrib(String data, String what) {
    // TODO
    // presumes what = "xxxx"
    // no check for spurious "what"; skips check for "=" entirely
    // uses Jmol's decoding, not standard XML decoding (of &xxx;)
    int[] nexta = new int[1];
    int pt = setNext(data, what, nexta, 1);
    if (pt < 2 || (pt = setNext(data, "\"", nexta, 0)) < 2)
      return "";
    int pt1 = setNext(data, "\"", nexta, -1);
    return (pt1 <= 0 ? "" : data.substring(pt, pt1));
  }

  public Point3f getXmlPoint(String data, String key) {
    String spt = getXmlAttrib(data, key).replace('(', '{').replace(')', '}');
    Object value = Escape.unescapePoint(spt);
    if (value instanceof Point3f)
      return (Point3f) value;
    return new Point3f();
  }

  /**
   * shift pointer to a new tag or field contents
   * 
   * @param data
   *          string of data
   * @param what
   *          tag or field name
   * @param next
   *          current pointer into data
   * @param offset
   *          offset past end of "what" for pointer
   * @return pointer to data
   */
  private static int setNext(String data, String what, int[] next, int offset) {
    int ipt = next[0];
    if (ipt < 0 || (ipt = data.indexOf(what, next[0])) < 0)
      return -1;
    return next[0] = ipt + what.length() + offset;
  }

  private void skipTo(String key) throws Exception {
    if (line == null)
      line = br.readLine();
    while (line != null && line.indexOf(key) < 0)
      line = br.readLine();
  }

  public boolean isNext(String name) throws Exception {
    if (line == null || line.indexOf("</") >= 0 && line.indexOf("</") == line.indexOf("<"))
      line = br.readLine();
    return (line.indexOf("<" + name) >= 0);
  }

}

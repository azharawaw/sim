/*
* The Physics Education Technology (PhET) project provides
* a suite of interactive educational simulations.
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.TestDynamicJFreeChartNodeTree;

/**
 * Simple demonstration for usage of JFreeChartCursorNode
 * @author Sam Reid
 */
public class TestJFreeChartCursorNode extends TestDynamicJFreeChartNodeTree {

    public TestJFreeChartCursorNode() {
        JFreeChartCursorNode jFreeChartCursorNode = new JFreeChartCursorNode( getDynamicJFreeChartNode() );
        getPhetPCanvas().addScreenChild( jFreeChartCursorNode );
    }

    public static void main( String[] args ) {
        new TestJFreeChartCursorNode().start();
    }

}

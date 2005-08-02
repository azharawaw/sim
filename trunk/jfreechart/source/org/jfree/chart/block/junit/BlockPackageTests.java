/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * BlockPackageTests.java
 * ----------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 22-Oct-2004 : Version 1 (DG);
 * 18-Jan-2005 : Added main() method (DG);
 * 04-Feb-2005 : Added new tests (DG);
 *
 */

package org.jfree.chart.block.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the org.jfree.chart.block package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 */
public class BlockPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return The test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.block");
        suite.addTestSuite(BlockBorderTests.class);
        suite.addTestSuite(BlockContainerTests.class);
        suite.addTestSuite(BorderArrangementTests.class);
        suite.addTestSuite(ColumnArrangementTests.class);
        suite.addTestSuite(EmptyBlockTests.class);
        suite.addTestSuite(FlowArrangementTests.class);
        suite.addTestSuite(GridArrangementTests.class);
        suite.addTestSuite(RectangleConstraintTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public BlockPackageTests(String name) {
        super(name);
    }
    
    /**
     * Runs the test suite using JUnit's text-based runner.
     * 
     * @param args  ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}


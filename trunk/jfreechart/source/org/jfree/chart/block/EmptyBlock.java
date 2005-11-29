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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------
 * EmptyBlock.java
 * ---------------
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
 * 04-Feb-2005 : Now cloneable and serializable (DG);
 * 20-Apr-2005 : Added new draw() method (DG);
 * 
 */

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.util.PublicCloneable;

/**
 * An empty block with a fixed size.
 */
public class EmptyBlock extends AbstractBlock 
                        implements Block, Cloneable, PublicCloneable,
                                   Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = -4083197869412648579L;
    
    /**
     * Creates a new block with the specified width and height.
     * 
     * @param width  the width.
     * @param height  the height.
     */
    public EmptyBlock(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Draws the block.  Since the block is empty, this method does nothing.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        // do nothing, we're empty
    }
    
    /**
     * Draws the block within the specified area.  Since the block is empty, 
     * this method does nothing.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored (<code>null</code> permitted).
     * 
     * @return Always <code>null</code>.
     */
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        return null;
    }

    /**
     * Returns a clone of the block.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();   
    }

}

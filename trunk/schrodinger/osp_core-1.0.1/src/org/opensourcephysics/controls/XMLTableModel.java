/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.opensourcephysics.tools.*;

/**
 * A table model for an XMLTable.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class XMLTableModel extends AbstractTableModel {
  // instance fields
  XMLControl control;
  boolean editable = true;
  Collection uneditablePropNames = new HashSet();

  /**
   * Constructor.
   *
   * @param control an xml control
   */
  public XMLTableModel(XMLControl control) {
    this.control = control;
  }

  /**
   * Gets the number of columns.
   *
   * @return the column count
   */
  public int getColumnCount() {
    return 2;
  }

  /**
   * Gets the name of the specified column.
   *
   * @param column the column index
   * @return the column name
   */
  public String getColumnName(int column) {
    return column==0 ? "Name" : "Value";
  }

  /**
   * Gets the number of rows.
   *
   * @return the row count
   */
  public int getRowCount() {
    return control.getPropertyContent().size();
  }

  /**
   * Gets the value at the given cell.
   * Column 0 = property name
   * Column 1 = property content (String for int, double, boolean, string types,
   * XMLControl for object type, XMLProperty for array, collection types)
   *
   * @param row the row index
   * @param column the column index
   * @return the value
   */
  public Object getValueAt(int row, int column) {
    try {
      XMLProperty val = (XMLProperty) control.getPropertyContent().get(row);
      return column==0 ? val.getPropertyName() : val.getPropertyContent().get(0);
    } catch(Exception ex) {
      return null;
    }
  }

  /**
   * Determines whether the given cell is editable.
   *
   * @param row the row index
   * @param col the column index
   * @return true if editable
   */
  public boolean isCellEditable(int row, int col) {
    // not editable if column 0
    if(col==0) {
      return false;
    }
    // not editable if property name is in uneditable collection
    String propName = (String) getValueAt(row, 0);
    if(uneditablePropNames.contains(propName)) {
      return false;
    }
    // not editable if value is uninspectable array or collection
    Object value = getValueAt(row, col);
    if(value instanceof XMLControl) {
      return true;
    } else if(value instanceof XMLProperty) {
      XMLProperty prop = (XMLProperty) value;
      XMLProperty parent = prop.getParentProperty();
      if(parent.getPropertyType().equals("array")) {
        return ArrayInspector.canInspect(parent);
      }
      return false;
    }
    // otherwise editable
    return true;
  }

  /**
   * Sets the value at the given cell. This method only sets values for
   * int, double, boolean and string types.
   *
   * @param value the value
   * @param row the row index
   * @param col the column index
   */
  public void setValueAt(Object value, int row, int col) {
    if(value==null) {
      return;
    }
    if(value instanceof String) {
      String s = (String) value;
      // determine class type at row and column
      XMLProperty prop = (XMLProperty) control.getPropertyContent().get(row);
      String type = prop.getPropertyType();
      if(type.equals("string")) {
        control.setValue(prop.getPropertyName(), s);
      } else if(type.equals("int")) {
        try {
          int i = Integer.parseInt(s);
          control.setValue(prop.getPropertyName(), i);
        } catch(NumberFormatException ex) {}
      } else if(type.equals("double")) {
        try {
          double x = Double.parseDouble(s);
          control.setValue(prop.getPropertyName(), x);
        } catch(NumberFormatException ex) {}
      } else if(type.equals("boolean")) {
        boolean bool = s.toLowerCase().startsWith("t");
        control.setValue(prop.getPropertyName(), bool);
      }
      fireTableCellUpdated(row, col);
    }
  }
}

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */

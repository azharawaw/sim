/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display.dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import org.opensourcephysics.display.DrawingPanel;

public class DrawingPanelInspector extends JDialog {
  static DrawingPanelInspector inspector;
  DrawingPanel drawingPanel;
  DecimalFormat format = new DecimalFormat("0.00000E00");
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel scalePanel = new JPanel();
  JPanel contentPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JTextPane contentTextPane = new JTextPane();
  JTextField ymaxField = new JTextField();
  JPanel yminmaxpanel = new JPanel();
  JTextField yminField = new JTextField();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JPanel jPanel3 = new JPanel();
  JCheckBox zoomenableBox = new JCheckBox();
  JCheckBox autoscaleyBox = new JCheckBox();
  JCheckBox autoscalexBox = new JCheckBox();
  JTextField xmaxField = new JTextField();
  JTextField xminField = new JTextField();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JPanel xminmaxpanel = new JPanel();
  JPanel jPanel1 = new JPanel();
  JButton applyButton = new JButton();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();
  JPanel jPanel4 = new JPanel();
  JButton measureButton = new JButton();
  JButton snapButton = new JButton();
  BoxLayout scaleLayout = new javax.swing.BoxLayout(scalePanel, javax.swing.BoxLayout.Y_AXIS);

  public DrawingPanelInspector(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    } catch(Exception ex) {
      ex.printStackTrace();
    }
    setVisible(true);
  }

  public DrawingPanelInspector(DrawingPanel dp) {
    this(null, "Drawing Panel Inspector", false);
    drawingPanel = dp;
    getValues();
    getContent();
  }

  public DrawingPanelInspector() {
    this(null, "", false);
  }

  void getValues() {
    xminField.setText(""+format.format((float) drawingPanel.getXMin()));
    xmaxField.setText(""+format.format((float) drawingPanel.getXMax()));
    yminField.setText(""+format.format((float) drawingPanel.getYMin()));
    ymaxField.setText(""+format.format((float) drawingPanel.getYMax()));
    zoomenableBox.setSelected(drawingPanel.isZoom());
    autoscalexBox.setSelected(drawingPanel.isAutoscaleX());
    autoscaleyBox.setSelected(drawingPanel.isAutoscaleY());
  }

  void getContent() {
    Iterator it = drawingPanel.getDrawables().iterator();
    StringBuffer buffer = new StringBuffer();
    while(it.hasNext()) {
      Object obj = it.next();
      buffer.append(obj.toString());
      buffer.append('\n');
    }
    contentTextPane.setText(buffer.toString());
  }

  void applyValues() {
    double newXMin = drawingPanel.getXMin();
    try {
      newXMin = Double.parseDouble(xminField.getText());
    } catch(Exception ex) {
      ;
    } // keep the current value if there is an exception
    double newXMax = drawingPanel.getXMax();
    try {
      newXMax = Double.parseDouble(xmaxField.getText());
    } catch(Exception ex) {
      ;
    } // keep the current value if there is an exception
    double newYMin = drawingPanel.getYMin();
    try {
      newYMin = Double.parseDouble(yminField.getText());
    } catch(Exception ex) {
      ;
    } // keep the current value if there is an exception
    double newYMax = drawingPanel.getYMax();
    try {
      newYMax = Double.parseDouble(ymaxField.getText());
    } catch(Exception ex) {
      ;
    } // keep the current value if there is an exception
    drawingPanel.setAutoscaleX(autoscalexBox.isSelected());
    drawingPanel.setAutoscaleY(autoscaleyBox.isSelected());
    drawingPanel.setZoom(zoomenableBox.isSelected());
    // setting min/max also sets autoscale to false
    if(!drawingPanel.isAutoscaleX()&&!drawingPanel.isAutoscaleY()) {
      drawingPanel.setPreferredMinMax(newXMin, newXMax, newYMin, newYMax);
    } else if(!drawingPanel.isAutoscaleX()) {
      drawingPanel.setPreferredMinMaxX(newXMin, newXMax);
    } else if(!drawingPanel.isAutoscaleY()) {
      drawingPanel.setPreferredMinMaxY(newYMin, newYMax);
    }
    drawingPanel.scale();
    getValues();
    drawingPanel.repaint();
  }

  void applyButton_actionPerformed(ActionEvent e) {
    applyValues();
  }

  void measureButton_actionPerformed(ActionEvent e) {
    drawingPanel.measure();
    getValues();
    drawingPanel.repaint();
  }

  void snapButton_actionPerformed(ActionEvent e) {
    drawingPanel.snapshot();
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  void okButton_actionPerformed(ActionEvent e) {
    applyValues();
    setVisible(false);
  }

  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    scalePanel.setToolTipText("Set the drawing panel scale.");
    scalePanel.setLayout(scaleLayout);
    contentPanel.setLayout(borderLayout3);
    contentTextPane.setText("jTextPane1");
    ymaxField.setPreferredSize(new Dimension(100, 21));
    ymaxField.setText("0");
    yminField.setPreferredSize(new Dimension(100, 21));
    yminField.setText("0");
    jLabel4.setText("  ymax =");
    jLabel3.setText("ymin =");
    zoomenableBox.setText("enable zoom");
    autoscaleyBox.setText("autoscale y");
    autoscalexBox.setText("autoscale x");
    xmaxField.setText("0");
    xmaxField.setPreferredSize(new Dimension(100, 21));
    xminField.setText("0");
    xminField.setPreferredSize(new Dimension(100, 21));
    jLabel5.setText("  xmax =");
    jLabel6.setText("xmin =");
    xminmaxpanel.setToolTipText("Set the drawing panel scale.");
    applyButton.setText("Apply");
    applyButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    measureButton.setFont(new java.awt.Font("Dialog", 0, 10));
    measureButton.setText("Measure");
    measureButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        measureButton_actionPerformed(e);
      }
    });
    snapButton.setFont(new java.awt.Font("Dialog", 0, 10));
    snapButton.setText("Snapshot");
    snapButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        snapButton_actionPerformed(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(jTabbedPane1, BorderLayout.CENTER);
    jPanel3.add(zoomenableBox, null);
    jPanel3.add(autoscalexBox, null);
    jPanel3.add(autoscaleyBox, null);
    xminmaxpanel.add(jLabel6, null);
    xminmaxpanel.add(xminField, null);
    xminmaxpanel.add(jLabel5, null);
    xminmaxpanel.add(xmaxField, null);
    scalePanel.add(xminmaxpanel, null);
    scalePanel.add(yminmaxpanel, null);
    yminmaxpanel.add(jLabel3, null);
    yminmaxpanel.add(yminField, null);
    yminmaxpanel.add(jLabel4, null);
    yminmaxpanel.add(ymaxField, null);
    scalePanel.add(jPanel4, null);
    jPanel4.add(measureButton, null);
    jPanel4.add(snapButton, null);
    scalePanel.add(jPanel3, null);
    jTabbedPane1.add(scalePanel, "scale");
    jTabbedPane1.add(contentPanel, "content");
    contentPanel.add(contentTextPane, BorderLayout.CENTER);
    scalePanel.add(jPanel1, null);
    jPanel1.add(okButton, null);
    jPanel1.add(cancelButton, null);
    jPanel1.add(applyButton, null);
  }

  public static DrawingPanelInspector getInspector(DrawingPanel dp) {
    if(DrawingPanelInspector.inspector==null) {
      inspector = new DrawingPanelInspector(dp);
    } else {
      DrawingPanelInspector.inspector.drawingPanel = dp;
      DrawingPanelInspector.inspector.getValues();
      DrawingPanelInspector.inspector.getContent();
      DrawingPanelInspector.inspector.setVisible(true);
    }
    return inspector;
  }

  public static void hideInspector() {
    if(DrawingPanelInspector.inspector!=null) {
      inspector.setVisible(false);
    }
  }

  public static void updateValues(DrawingPanel dp) {
    if(DrawingPanelInspector.inspector==null||DrawingPanelInspector.inspector.drawingPanel!=dp||!DrawingPanelInspector.inspector.isShowing()) {
      return;
    }
    DrawingPanelInspector.inspector.drawingPanel.scale();
    DrawingPanelInspector.inspector.getValues();
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

/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.logging.*;
import org.opensourcephysics.display.OSPFrame;

/**
 * This is a viewable file-based message log for a java package.
 * It displays log records in a text pane and saves them in a temp file.
 *
 * @author Douglas Brown
 * @author Wolfgang Christian
 * @version 1.0
 */
public class OSPLog extends JFrame {

  /**
   * A logger that can be used by any OSP program to log and show messages.
   */
  private static OSPLog OSPLOG;
  private static JFileChooser chooser;
  protected static Style black, red, blue, green, magenta, gray;
  protected static final Color DARK_GREEN = new Color(0, 128, 0), DARK_BLUE = new Color(0, 0, 128), DARK_RED = new Color(128, 0, 0);
  public static final Level[] levels = new Level[] {
    Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO, ConsoleLevel.ERR_CONSOLE, ConsoleLevel.OUT_CONSOLE, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.ALL
  };

  // instance fields
  private Logger logger;
  private Handler fileHandler;
  private Handler logHandler;
  private JTextPane textPane;
  private String logFileName;
  private String tempFileName;
  private JPanel logPanel;
  private JPopupMenu popup;
  private ButtonGroup popupGroup, menubarGroup;
  private String pkgName;
  private String bundleName;
  private JMenuItem logToFileItem;
  private boolean hasPermission = true;
  private static LogRecord[] messageStorage = new LogRecord[128];
  private static int messageIndex = 0;
  static String eol = "\n";
  static String tmpdir = ".";

  static {
    try { // system properties may not be readable in some environments
      eol = System.getProperty("line.separator", eol);
      tmpdir = System.getProperty("java.io.tmpdir", tmpdir);
    } catch(SecurityException ex) {}
  }

  /**
   * Gets the OSPLog that can be shared by multiple OSP packages.
   *
   * @return the shared OSPLog
   */
  public static OSPLog getOSPLog() {
    if(OSPLOG==null) {
      OSPLOG = new OSPLog("org.opensourcephysics", null);
      if(!OSPFrame.appletMode) { // cannot redirect applet streams
        try {
          System.setOut(new PrintStream(new LoggerOutputStream(ConsoleLevel.OUT_CONSOLE, System.out)));
          System.setErr(new PrintStream(new LoggerOutputStream(ConsoleLevel.ERR_CONSOLE, System.err)));
        } catch(SecurityException ex) {}
      }
    }
    return OSPLOG;
  }

  /**
   * Determines if the shared log is visible.
   *
   * @return true if visible
   */
  public static boolean isLogVisible() {
    if(OSPFrame.appletMode&&MessageFrame.APPLET_MESSAGEFRAME!=null) {
      return MessageFrame.APPLET_MESSAGEFRAME.isVisible();
    } else if(OSPLOG!=null) {
      return OSPLOG.isVisible();
    }
    return false;
  }

  public void setVisible(boolean b) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.showLog();
    } else {
      super.setVisible(b);
    }
  }

  public boolean isVisible(boolean b) {
    if(OSPFrame.appletMode) {
      return org.opensourcephysics.controls.MessageFrame.isLogVisible();
    } else {
      return super.isVisible();
    }
  }

  /**
   * Shows the log.
   */
  public static void showLog() {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.showLog();
    } else {
      getOSPLog().setVisible(true);
      Logger logger = OSPLOG.getLogger();
      for(int i = 0, n = messageStorage.length;i<n;i++) {
        LogRecord record = messageStorage[(i+messageIndex)%n];
        if(record!=null) {
          logger.log(record);
        }
      }
      messageIndex = 0;
    }
  }

  /**
   * Sets the logger level.
   *
   * @param level the Level
   */
  public static void setLevel(Level level) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.setLevel(level);
    } else {
      getOSPLog().getLogger().setLevel(level);
      // refresh the level menus
      for(int i = 0;i<2;i++) {
        Enumeration e = getOSPLog().menubarGroup.getElements();
        if(i==1) {
          e = getOSPLog().popupGroup.getElements();
        }
        while(e.hasMoreElements()) {
          JMenuItem item = (JMenuItem) e.nextElement();
          if(getOSPLog().getLogger().getLevel().toString().equals(item.getActionCommand())) {
            item.setSelected(true);
            break;
          }
        }
      }
    }
  }

  /**
   * Returns the Level with the specified name, or null if none.
   *
   * @param level the Level
   */
  public static Level parseLevel(String level) {
    for(int i = 0;i<levels.length;i++) {
      if(levels[i].getName().equals(level)) {
        return levels[i];
      }
    }
    return null;
  }

  /**
   * Logs a severe error message.
   *
   * @param msg the message
   */
  public static void severe(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.severe(msg);
    } else {
      log(Level.SEVERE, msg);
    }
  }

  /**
   * Logs a warning message.
   *
   * @param msg the message
   */
  public static void warning(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.warning(msg);
    } else {
      log(Level.WARNING, msg);
    }
  }

  /**
   * Logs an information message.
   *
   * @param msg the message
   */
  public static void info(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.info(msg);
    } else {
      log(Level.INFO, msg);
    }
  }

  /**
   * Logs a configuration message.
   *
   * @param msg the message
   */
  public static void config(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.config(msg);
    } else {
      log(Level.CONFIG, msg);
    }
  }

  /**
   * Logs a fine debugging message.
   *
   * @param msg the message
   */
  public static void fine(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.fine(msg);
    } else {
      log(Level.FINE, msg);
    }
  }

  /**
   * Logs a finer debugging message.
   *
   * @param msg the message
   */
  public static void finer(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.finer(msg);
    } else {
      log(Level.FINER, msg);
    }
  }

  /**
   * Logs a finest debugging message.
   *
   * @param msg the message
   */
  public static void finest(String msg) {
    if(OSPFrame.appletMode) {
      org.opensourcephysics.controls.MessageFrame.finest(msg);
    } else {
      log(Level.FINEST, msg);
    }
  }

  /**
   * Constructs an OSPLog for a specified package.
   *
   * @param pkg the package
   */
  public OSPLog(Package pkg) {
    this(pkg.getName(), null);
  }

  /**
   * Constructs an OSPLog for a specified package and resource bundle.
   *
   * @param pkg the package
   * @param resourceBundleName the name of the resource bundle
   */
  public OSPLog(Package pkg, String resourceBundleName) {
    this(pkg.getName(), resourceBundleName);
  }

  /**
   * Constructs an OSPLog for a specified class.
   *
   * @param type the class
   */
  public OSPLog(Class type) {
    this(type, null);
  }

  /**
   * Constructs an OSPLog for a specified class and resource bundle.
   *
   * @param type the class
   * @param resourceBundleName the name of the resource bundle
   */
  public OSPLog(Class type, String resourceBundleName) {
    this(type.getPackage().getName(), resourceBundleName);
  }

  /**
   * Gets the log panel so it can be displayed in a dialog or other container.
   *
   * @return a JPanel containing the log
   */
  public JPanel getLogPanel() {
    return logPanel;
  }

  /**
   * Clears the log.
   */
  public void clear() {
    textPane.setText(null);
  }

  /**
   * Saves the log to a text file specified by name.
   *
   * @param fileName the file name
   * @return the name of the file
   */
  public String saveLog(String fileName) {
    if(fileName==null||fileName.trim().equals("")) {
      return saveLogAs();
    }
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
      out.write(textPane.getText());
      out.flush();
      out.close();
      return fileName;
    } catch(IOException ex) {
      return null;
    }
  }

  /**
   * Saves a log to a text file selected with a chooser.
   *
   * @return the name of the file
   */
  public String saveLogAs() {
    int result = getChooser().showSaveDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = getChooser().getSelectedFile();
      // check to see if file already exists
      if(file.exists()) {
        int selected = JOptionPane.showConfirmDialog(this, "Replace existing "+file.getName()+"?", "Replace File", JOptionPane.YES_NO_CANCEL_OPTION);
        if(selected!=JOptionPane.YES_OPTION) {
          return null;
        }
      }
      String fileName = XML.getRelativePath(file.getAbsolutePath());
      return saveLog(fileName);
    }
    return null;
  }

  /**
   * Saves the xml-formatted log records to a file specified by name.
   *
   * @param fileName the file name
   * @return the name of the file
   */
  public String saveXML(String fileName) {
    if(OSPFrame.appletMode) {
      logger.log(Level.FINE, "Cannot save XML file in applet mode");
      return null; // cannot log to file in applet mode
    }
    if(fileName==null||fileName.trim().equals("")) {
      return saveXMLAs();
    }
    // open temp file and get xml string
    String xml = read(tempFileName);
    // add closing tag to xml
    Handler fileHandler = getFileHandler();
    String tail = fileHandler.getFormatter().getTail(fileHandler);
    xml = xml+tail;
    // write the xml
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
      out.write(xml);
      out.flush();
      out.close();
      return fileName;
    } catch(IOException ex) {
      return null;
    }
  }

  /**
   * Saves the xml-formatted log records to a file selected with a chooser.
   *
   * @return the name of the file
   */
  public String saveXMLAs() {
    int result = getChooser().showSaveDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = getChooser().getSelectedFile();
      // check to see if file already exists
      if(file.exists()) {
        int selected = JOptionPane.showConfirmDialog(this, "Replace existing "+file.getName()+"?", "Replace File", JOptionPane.YES_NO_CANCEL_OPTION);
        if(selected!=JOptionPane.YES_OPTION) {
          return null;
        }
      }
      logFileName = XML.getRelativePath(file.getAbsolutePath());
      return saveXML(logFileName);
    }
    return null;
  }

  /**
   * Opens a text file selected with a chooser and writes the contents to the log.
   *
   * @return the name of the file
   */
  public String open() {
    int result = getChooser().showOpenDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = getChooser().getSelectedFile();
      String fileName = XML.getRelativePath(file.getAbsolutePath());
      return open(fileName);
    }
    return null;
  }

  /**
   * Opens a text file specified by name and writes the contents to the log.
   *
   * @param fileName the file name
   * @return the file name
   */
  public String open(String fileName) {
    textPane.setText(read(fileName));
    return fileName;
  }

  /**
   * Gets the logger.
   *
   * @return the logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Enables logging to a file.
   *
   * @param enable true to log to a file
   */
  public void setLogToFile(boolean enable) {
    if(OSPFrame.appletMode) {
      logger.log(Level.FINE, "Cannot log to file in applet mode");
      return; // cannot log to file in applet mode
    }
    if(enable) {
      logToFileItem.setSelected(true);
      logger.addHandler(getFileHandler());
    } else {
      logToFileItem.setSelected(false);
      logger.removeHandler(fileHandler);
    }
  }

  public static void main(String[] args) {
    JMenuBar menubar = getOSPLog().getJMenuBar();
    JMenu menu = new JMenu("Test");
    menubar.add(menu);
    String[] levels = new String[] {
      "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"
    };
    for(int i = 0;i<levels.length;i++) {
      JMenuItem item = new JMenuItem(levels[i]);
      menu.add(item, 0);
      item.setActionCommand(levels[i]);
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          OSPLOG.getLogger().log(Level.parse(e.getActionCommand()), "Testing "+e.getActionCommand());
        }
      });
    }
    menu = new JMenu("Console");
    menubar.add(menu);
    JMenuItem item = new JMenuItem("Console Out");
    menu.add(item, 0);
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Out console message.");
      }
    });
    item = new JMenuItem("Console Err");
    menu.add(item, 0);
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.err.println("Error console message.");
      }
    });
    item = new JMenuItem("Exception");
    menu.add(item, 0);
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double[] x = null;
        x[0] = 1;
      }
    });
    OSPLOG.setVisible(true);
    OSPLOG.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  /**
   * Creates the GUI.
   */
  protected void createGUI() {
    // create the panel, text pane and scroller
    logPanel = new JPanel(new BorderLayout());
    logPanel.setPreferredSize(new Dimension(480, 240));
    setContentPane(logPanel);
    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setAutoscrolls(true);
    JScrollPane textScroller = new JScrollPane(textPane);
    textScroller.setWheelScrollingEnabled(true);
    logPanel.add(textScroller, BorderLayout.CENTER);
    // create the colored styles
    black = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    red = textPane.addStyle("red", black);
    StyleConstants.setForeground(red, DARK_RED);
    blue = textPane.addStyle("blue", black);
    StyleConstants.setForeground(blue, DARK_BLUE);
    green = textPane.addStyle("green", black);
    StyleConstants.setForeground(green, DARK_GREEN);
    magenta = textPane.addStyle("magenta", black);
    StyleConstants.setForeground(magenta, Color.MAGENTA);
    gray = textPane.addStyle("gray", black);
    StyleConstants.setForeground(gray, Color.GRAY);
    // create the logger
    createLogger();
    // create the menus
    createMenus();
    pack();
    textPane.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()||e.getButton()==MouseEvent.BUTTON3||(e.isControlDown()&&System.getProperty("os.name").indexOf("Mac")>-1)) {
          // show popup menu
          if(popup!=null) {
            popup.show(textPane, e.getX(), e.getY()+8);
          }
        }
      }
    });
  }

  /**
   * Creates and initializes the logger.
   *
   * @return the logger
   */
  protected Logger createLogger() {
    // get the package logger and reference the ResourceBundle it will use
    if(bundleName!=null) {
      try {
        logger = Logger.getLogger(pkgName, bundleName);
      } catch(Exception ex) {
        logger = Logger.getLogger(pkgName);
      }
    } else {
      logger = Logger.getLogger(pkgName);
    }
    try {
      logger.setLevel(ConsoleLevel.OUT_CONSOLE);
      // add a log handler for this log
      logHandler = new OSPLogHandler(textPane);
      logHandler.setFormatter(new ConsoleFormatter());
      logHandler.setLevel(Level.ALL);
      // ignore parent handlers (specifically root console handler)
      OSPFrame.class.getClass(); // force the static methods to execute
      logger.setUseParentHandlers(false);
      logger.addHandler(logHandler);
    } catch(SecurityException ex) {
      hasPermission = false;
    }
    return logger;
  }

  /**
   * Gets the file handler using lazy instantiation.
   *
   * @return the Handler
   */
  protected synchronized Handler getFileHandler() {
    if(fileHandler!=null) {
      return fileHandler;
    }
    try {
      // add a file handler with file name equal to short package name
      int i = pkgName.lastIndexOf(".");
      if(i>-1) {
        pkgName = pkgName.substring(i+1);
      }
      tempFileName = tmpdir+pkgName+".log";
      fileHandler = new FileHandler(tempFileName);
      fileHandler.setFormatter(new XMLFormatter());
      fileHandler.setLevel(Level.ALL);
      logger.addHandler(fileHandler);
      logger.log(Level.FINE, "Logging to file enabled. File = "+tempFileName);
    } catch(IOException ex) {}
    catch(SecurityException ex) {}
    return fileHandler;
  }

  /**
   * Creates the popup menu.
   */
  protected void createMenus() {
    if(!hasPermission) {
      return;
    }
    popup = new JPopupMenu();
    JMenu menu = new JMenu("Level");
    popup.add(menu);
    popupGroup = new ButtonGroup();
    for(int i = 0;i<levels.length;i++) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(levels[i].getName());
      menu.add(item, 0);
      popupGroup.add(item);
      if(logger.getLevel().toString().equals(levels[i])) {
        item.setSelected(true);
      }
      item.setActionCommand(levels[i].getName());
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logger.setLevel(ConsoleLevel.parse(e.getActionCommand()));
          Enumeration e2 = menubarGroup.getElements();
          while(e2.hasMoreElements()) {
            JMenuItem item = (JMenuItem) e2.nextElement();
            if(logger.getLevel().toString().equals(item.getActionCommand())) {
              item.setSelected(true);
              break;
            }
          }
        }
      });
    }
    popup.addSeparator();
    Action openAction = new AbstractAction("Open...") {
      public void actionPerformed(ActionEvent e) {
        open();
      }
    };
    openAction.setEnabled(!OSPFrame.appletMode);
    popup.add(openAction);
    Action saveAsAction = new AbstractAction("Save As...") {
      public void actionPerformed(ActionEvent e) {
        saveLogAs();
      }
    };
    saveAsAction.setEnabled(!OSPFrame.appletMode);
    popup.add(saveAsAction);
    popup.addSeparator();
    Action clearAction = new AbstractAction("Clear") {
      public void actionPerformed(ActionEvent e) {
        clear();
      }
    };
    popup.add(clearAction);
    // create menubar
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    menu = new JMenu("File");
    menubar.add(menu);
    menu.add(openAction);
    menu.add(saveAsAction);
    menu = new JMenu("Edit");
    menubar.add(menu);
    menu.add(clearAction);
    menu = new JMenu("Level");
    menubar.add(menu);
    menubarGroup = new ButtonGroup();
    for(int i = 0;i<levels.length;i++) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(levels[i].getName());
      menu.add(item, 0);
      menubarGroup.add(item);
      if(logger.getLevel().toString().equals(levels[i])) {
        item.setSelected(true);
      }
      item.setActionCommand(levels[i].getName());
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logger.setLevel(ConsoleLevel.parse(e.getActionCommand()));
          Enumeration e2 = popupGroup.getElements();
          while(e2.hasMoreElements()) {
            JMenuItem item = (JMenuItem) e2.nextElement();
            if(logger.getLevel().toString().equals(item.getActionCommand())) {
              item.setSelected(true);
              break;
            }
          }
        }
      });
    }
    JMenu prefMenu = new JMenu("Options");
    menubar.add(prefMenu);
    logToFileItem = new JCheckBoxMenuItem("Log to file.");
    logToFileItem.setSelected(false);
    logToFileItem.setEnabled(!OSPFrame.appletMode);
    logToFileItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
        setLogToFile(item.isSelected());
      }
    });
    prefMenu.add(logToFileItem);
  }

  /**
   * Gets a file chooser.
   *
   * @return the chooser
   */
  protected static JFileChooser getChooser() {
    if(chooser==null) {
      chooser = new JFileChooser(new File(OSPFrame.chooserDir));
    }
    return chooser;
  }

  /**
   * Reads a file.
   *
   * @param fileName the name of the file
   * @return the file contents as a String
   */
  protected String read(String fileName) {
    File file = new File(fileName);
    StringBuffer buffer = null;
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      buffer = new StringBuffer();
      String line = in.readLine();
      while(line!=null) {
        buffer.append(line+XML.NEW_LINE);
        line = in.readLine();
      }
      in.close();
    } catch(IOException ex) {
      logger.warning(ex.toString());
    }
    return buffer.toString();
  }

  private OSPLog(String name, String resourceBundleName) {
    super("Message Log");
    this.setName("LogTool"); // identify this as a tool
    bundleName = resourceBundleName;
    pkgName = name;
    ConsoleLevel.class.getName(); // force ConsoleLevel to load static constants
    createGUI();
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
  }

  private static void log(Level level, String msg) {
    LogRecord record = new LogRecord(level, msg);
    // get the stack trace
    StackTraceElement stack[] = (new Throwable()).getStackTrace();
    // find the first method not in class OSPLog
    for(int i = 0;i<stack.length;i++) {
      StackTraceElement el = stack[i];
      String className = el.getClassName();
      if(!className.equals("org.opensourcephysics.controls.OSPLog")) {
        // set the source class and method
        record.setSourceClassName(className);
        record.setSourceMethodName(el.getMethodName());
        break;
      }
    }
    if(OSPLOG!=null) {
      OSPLOG.getLogger().log(record);
    } else {
      messageStorage[messageIndex] = record;
      messageIndex++;
      messageIndex = messageIndex%messageStorage.length;
    }
  }
}

/**
 * A class that formats a record as if this were the console.
 */
class ConsoleFormatter extends SimpleFormatter {

  /**
   * Formats the record as if this were the console.
   *
   * @param record LogRecord
   * @return String
   */
  public String format(LogRecord record) {
    String message = formatMessage(record);
    if(record.getLevel().intValue()==ConsoleLevel.OUT_CONSOLE.intValue()||record.getLevel().intValue()==ConsoleLevel.ERR_CONSOLE.intValue()) {
      StringBuffer sb = new StringBuffer();
      if(message.charAt(0)=='\t') {
        message = message.replaceFirst("\t", "    ");
      } else {
        sb.append("CONSOLE: ");
      }
      sb.append(message);
      sb.append(OSPLog.eol);
      // new line after message
      if(record.getThrown()!=null) {
        try {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          record.getThrown().printStackTrace(pw);
          pw.close();
          sb.append(sw.toString());
        } catch(Exception ex) {}
      }
      return sb.toString();
    }
    return super.format(record);
  }
}

/**
 * A class that writes an output stream to the logger.
 */
class LoggerOutputStream extends OutputStream {
  StringBuffer buffer = new StringBuffer();
  OutputStream oldStream;
  ConsoleLevel level;

  LoggerOutputStream(ConsoleLevel level, OutputStream oldStream) {
    this.level = level;
    this.oldStream = oldStream;
  }

  public void write(int c) throws IOException {
    oldStream.write(c);
    if(c=='\n') {
      LogRecord record = new LogRecord(level, buffer.toString());
      OSPLog.getOSPLog().getLogger().log(record);
      buffer = new StringBuffer();
    } else {
      buffer.append((char) c);
    }
  }
}

/**
 * A handler class for a text log.
 */
class OSPLogHandler extends Handler {
  JTextPane log;

  public OSPLogHandler(JTextPane textPane) {
    this.log = textPane;
  }

  public void publish(LogRecord record) {
    if(!isLoggable(record)) {
      return;
    }
    String msg = getFormatter().format(record);
    Style style = OSPLog.green; // default style
    int val = record.getLevel().intValue();
    if(val==ConsoleLevel.ERR_CONSOLE.intValue()) {
      style = OSPLog.magenta;
    } else if(val==ConsoleLevel.OUT_CONSOLE.intValue()) {
      style = OSPLog.gray;
    } else if(val>=Level.WARNING.intValue()) {
      style = OSPLog.red;
    } else if(val>=Level.INFO.intValue()) {
      style = OSPLog.black;
    } else if(val>=Level.CONFIG.intValue()) {
      style = OSPLog.green;
    } else if(val>=Level.FINEST.intValue()) {
      style = OSPLog.blue;
    }
    try {
      Document doc = log.getDocument();
      doc.insertString(doc.getLength(), msg+'\n', style);
      // scroll to display new message
      Rectangle rect = log.getBounds();
      rect.y = rect.height;
      log.scrollRectToVisible(rect);
    } catch(BadLocationException ex) {
      System.err.println(ex);
    }
  }

  public void flush() {}

  public void close() {}
}

/**
 * A formatter class that formats a log record as an osp xml string
 */
class OSPLogFormatter extends java.util.logging.Formatter {
  XMLControl control = new XMLControlElement();

  /**
   * Format the given log record and return the formatted string.
   *
   * @param record the log record to be formatted
   * @return the formatted log record
   */
  public String format(LogRecord record) {
    control.saveObject(record);
    return control.toXML();
  }
}

/**
 * A class to save and load LogRecord data in an XMLControl.
 * Note: this is in a very primitive state for testing only
 */
class OSPLogRecordLoader extends XMLLoader {
  public void saveObject(XMLControl control, Object obj) {
    LogRecord record = (LogRecord) obj;
    control.setValue("message", record.getMessage());
    control.setValue("level", record.getLevel().toString());
  }

  public Object createObject(XMLControl control) {
    String message = control.getString("message");
    String level = control.getString("level");
    return new LogRecord(ConsoleLevel.parse(level), message);
  }

  public Object loadObject(XMLControl control, Object obj) {
    return obj;
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

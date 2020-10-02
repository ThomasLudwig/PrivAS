package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Part of a JOptionPane used to establish a Connection to a RPP Server
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-04
 * 
 * Javadoc Complete on 2019-08-09
 */
public class ConnectionPane extends JPanel {

  /**
   * To select the address of the RPP
   */
  private final JTextField addressJTF;
  /**
   * To select the port number of the RPP
   */
  private final JTextField portJTF;

  /**
   * Empty constructor
   */
  public ConnectionPane() {
    super();
    this.addressJTF = new JTextField(Parameters.RPP_DEFAULT_ADDRESS);
    this.portJTF = new JTextField("" + Parameters.RPP_DEFAULT_PORT);
    this.init();
  }

  /**
   * Sets the layout
   */
  private void init() {
    this.add(new JLabel(GUI.CP_LABEL_ADDRESS));
    this.add(addressJTF);
    this.add(Box.createHorizontalStrut(GUI.HSP_MEDIUM));
    this.add(new JLabel(GUI.CP_LABEL_PORT));
    this.add(portJTF);
  }

  /**
   * Gets the selected address of the RPP
   * @return 
   */
  public String getAddress() {
    return this.addressJTF.getText();
  }

  /**
   * Gets the selected port number of the RPP
   * @return 
   */
  public int getPort() {
    try {
      return new Integer(this.portJTF.getText());
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

  /**
   * Show inside a JOptionPane
   * @return 
   */
  public int display() {
    return JOptionPane.showConfirmDialog(null, this, GUI.CP_TITLE, JOptionPane.OK_CANCEL_OPTION);
  }
}

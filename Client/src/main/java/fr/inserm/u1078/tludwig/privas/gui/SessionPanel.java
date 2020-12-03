package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.listener.SessionListener;
import fr.inserm.u1078.tludwig.privas.instances.ClientSession;
import fr.inserm.u1078.tludwig.privas.instances.RPPStatus;
import fr.inserm.u1078.tludwig.privas.instances.RPPStatus.State;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Main Panel of the Client GUI Window. Displays all the informations relative to a Session
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-11
 */
public class SessionPanel extends JPanel implements SessionListener {
  //DONE  add to Panel and to Session - Dataset Name
  //DONE  display identity of ThirdParty Server

  /**
   * the ClientWindow in which this Panel will be displayed
   */
  private final ClientWindow clientWindow;

  /**
   * Shows the ID of the Session
   */
  private final JTextField sessionID;
  /**
   * Shows the Hash Salt share by the Client and the RPP
   */
  private final JTextField hashKey;
  /**
   * Shows the Client's Public RSA Key
   */
  private final JTextArea publicKey;
  /**
   * Shows the Client's Private RSA Key
   */
  private final JTextArea privateKey;
  /**
   * Shows the AES Key share by the Client and the Third Party Server
   */
  private final JTextField aes;
  /**
   * Shows the selected RPP Dataset
   */
  private final JTextField dataset;
  /**
   * Shows the Maximum Allele Frequency allowed in variant selected
   */
  private final JTextField maf;
  /**
   * Shows the Maximum Allele Frequency allowed in variant selected
   */
  private final JTextField mafNFE;
  /**
   * Shows the Least Severe vep Consequence allowed in variant selected
   */
  private final JTextField csq;
  /**
   * Is variant selection limited to SNVs ?
   */
  private final JCheckBox limitToSNVs;  
  /**
   * Shows the RPP address
   */
  private final JTextField rpp;
  /**
   * Shows the name of the Third Party Server
   */
  private final JTextField tpsName;
  /**
   * Shows the Third Party Server's Public RSA Key
   */
  private final JTextArea thirdParty;
  /**
   * Shows the selected algorithm and its trailing parameters
   */
  private final JTextField algorithm;
  /**
   * Shows the Status of this Session on the RPP Server
   */
  private final JTextField rppStatus;
  /**
   * Shows the path to the Genotype File used by the Client (and its number of variants)
   */
  private final JTextField genotypeFile;

  /**
   * Shows the information relative to the RPP
   */
  private final JPanel rppPanel;
  /**
   * Shows the State of the RPP (Not chosen, not connected, connected)
   */
  private final JPanel rppLed;

  /**
   * Size of the RPP state "LED"
   */
  private static final int SIZE = 16;

  /**
   * LED used when no RPP is chosen
   */
  private static final JLabel RPP_OFF = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(GUI.IMAGE_PATH_LED_OFF)).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
  /**
   * LED used when connected to the RPP
   */
  private static final JLabel RPP_OK = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(GUI.IMAGE_PATH_LED_OK)).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
  /**
   * LED used when connection to the RPP failed
   */
  private static final JLabel RPP_KO = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(GUI.IMAGE_PATH_LED_KO)).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));

  /**
   * Constructor
   * @param session the Session to Monitor/Update
   * @param clientWindow the ClientWindow in which this Panel will be displayed
   */
  public SessionPanel(ClientSession session, ClientWindow clientWindow) {
    super();
    final int nbrow = 3;
    final int nbcol = 98;
    
    this.clientWindow = clientWindow;
    this.sessionID = new JTextField();
    this.hashKey = new JTextField();
    this.publicKey = new JTextArea(nbrow, nbcol);
    this.privateKey = new JTextArea(nbrow, nbcol);
    this.aes = new JTextField();
    this.dataset = new JTextField();
    this.maf = new JTextField();
    this.mafNFE = new JTextField();
    this.csq = new JTextField();
    this.limitToSNVs = new JCheckBox("", true);
    this.rpp = new JTextField();
    this.tpsName = new JTextField();
    this.thirdParty = new JTextArea(nbrow, nbcol);
    this.algorithm = new JTextField();
    this.rppStatus = new JTextField();
    this.genotypeFile = new JTextField();

    this.rppLed = new JPanel();
    this.rppPanel = new JPanel();

    init(session);
  }

  /**
   * Initializes the Panel (Layout, SessionListener)
   * @param session 
   */
  private void init(ClientSession session) {
    this.idUpdated(session.getId());
    this.hashUpdated(session.getHash());
    this.clientPublicKeyUpdated(session.getClientPublicRSAAsString());
    this.clientPrivateKeyUpdated(session.getClientPrivateRSAAsString());
    this.aesKeyUpdated(session.getAesKey());
    this.selectedDatasetUpdated(session.getSelectedDataset());
    this.maxMAFUpdated(session.getMaxMAF());
    this.leastSeverConsequenceUpdated(session.getLeastSevereConsequence());
    this.rppAddressUpdated(session.getRPP(), session.getPort());
    this.thirdPartyPublicNameUpdated(session.getThirdPartyName());
    this.thirdPartyPublicKeyUpdated(session.getThirdPartyPublicKeyAsString());
    this.algorithmUpdated(session.getAlgorithm());
    this.rppStatusUpdated(session.getLastRPPStatus());
    this.clientGenotypeFilenameUpdated(session.getClientGenotypeFilename(), session.getClientGenotypeFileSize());

    this.limitToSNVs.setEnabled(false);    
    
    this.sessionID.setEditable(false);
    this.hashKey.setEditable(false);
    this.publicKey.setEditable(false);
    this.privateKey.setEditable(false);
    this.aes.setEditable(false);
    this.dataset.setEditable(false);
    this.maf.setEditable(false);
    this.mafNFE.setEditable(false);
    this.csq.setEditable(false);
    this.rpp.setEditable(false);
    this.tpsName.setEditable(false);
    this.thirdParty.setEditable(false);
    this.algorithm.setEditable(false);
    this.rppStatus.setEditable(false);
    this.genotypeFile.setEditable(false);

    this.publicKey.setLineWrap(true);
    this.privateKey.setLineWrap(true);
    this.thirdParty.setLineWrap(true);

    setupGUI();
    session.addSessionListener(this);
  }
  
  private void setupGUI(){
    JScrollPane publicKeySP = new JScrollPane();
    JScrollPane privateKeySP = new JScrollPane();
    JScrollPane thirdPartySP = new JScrollPane();
    genotypeFile.setBackground(LookAndFeel.TEXT_BG_COLOR); //TODO don't understand with it isn't already done be L&F
    publicKey.setBackground(LookAndFeel.TEXT_BG_COLOR); //TODO don't understand with it isn't already done be L&F
    publicKeySP.setViewportView(publicKey);
    privateKey.setBackground(LookAndFeel.TEXT_BG_COLOR);
    privateKeySP.setViewportView(privateKey);
    thirdParty.setBackground(LookAndFeel.TEXT_BG_COLOR);
    thirdPartySP.setViewportView(thirdParty);
    rppStatus.setBackground(LookAndFeel.TEXT_BG_COLOR);

    rppPanel.setLayout(new BoxLayout(rppPanel, BoxLayout.LINE_AXIS));
    rppPanel.add(rppLed);
    rppPanel.add(rpp);

    JPanel main = new JPanel();
    main.setLayout(new GridBagLayout());
    addElement(main, rppPanel, GUI.SP_LABEL_RPP, GUI.SP_TOOLTIP_RPP, 1, LEFT);
    addElement(main, tpsName, GUI.SP_LABEL_THIRD_PARTY_NAME, GUI.SP_TOOLTIP_THIRD_PARTY_NAME, 1, RIGHT);
    addElement(main, dataset, GUI.SP_LABEL_DATASET, GUI.SP_TOOLTIP_DATASET, 2, LEFT);
    addElement(main, maf, GUI.SP_LABEL_MAF, GUI.SP_TOOLTIP_MAF, 3, LEFT);
    addElement(main, mafNFE, GUI.SP_LABEL_MAF_NFE, GUI.SP_TOOLTIP_MAF, 3, RIGHT);
    addElement(main, sessionID, GUI.SP_LABEL_ID, GUI.SP_TOOLTIP_ID, 4, LEFT);
    addElement(main, csq, GUI.SP_LABEL_CSQ, GUI.SP_TOOLTIP_CSQ, 4, RIGHT);
    addElement(main, aes, GUI.SP_LABEL_AES, GUI.SP_TOOLTIP_AES, 5, LEFT);
    addElement(main, limitToSNVs, GUI.SP_LABEL_LIMIT_SNV, GUI.SP_TOOLTIP_LIMIT_SNV, 5, RIGHT);
    
        
    addElement(main, algorithm, GUI.SP_LABEL_ALGORITHM, GUI.SP_TOOLTIP_ALGORITHM, 6, ALL);
    addElement(main, genotypeFile, GUI.SP_LABEL_GENOTYPE, GUI.SP_TOOLTIP_GENOTYPE, 7, ALL);
    addElement(main, hashKey, GUI.SP_LABEL_HASH, GUI.SP_TOOLTIP_HASH, 8, ALL);    
    addElement(main, publicKeySP, GUI.SP_LABEL_PUBLIC, GUI.SP_TOOLTIP_PUBLIC, 9, ALL);
    addElement(main, privateKeySP, GUI.SP_LABEL_PRIVATE, GUI.SP_TOOLTIP_PRIVATE, 10, ALL);
    addElement(main, thirdPartySP, GUI.SP_LABEL_THIRD_PARTY_KEY, GUI.SP_TOOLTIP_THIRD_PARTY_KEY, 11, ALL);
    addElement(main, rppStatus, GUI.SP_LABEL_STATUS, GUI.SP_TOOLTIP_STATUS, 12, ALL);

    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    this.add(main);    
  }
  
  public static final int LEFT = 1;
  public static final int RIGHT = 2;
  public static final int ALL = 3;
  private static final int LEFT_MARGIN = 10;
  private static final int MIDDLE_MARGIN = 10;
  private static final int RIGHT_MARGIN = 5;  
  private static final int TOP_MARGIN = 2;
  private static final int BOTTOM_MARGIN = 2;
  
  private void addElement(JPanel dest, JComponent comp, String label, String tooltip, int row, int pos){
    GridBagConstraints c = new GridBagConstraints();    
    c.fill = GridBagConstraints.BOTH;
    c.gridheight = 1;
    
    //Top-Left pad
    c.gridy = row*3;
    c.gridx = pos == RIGHT ? 5 : 0;    
    c.gridwidth = 1;
    dest.add(Box.createRigidArea(new Dimension(LEFT_MARGIN, TOP_MARGIN)), c);
    
    //Label
    c.gridx++;
    c.gridy++;
    JLabel jlab = new JLabel(label);
    setToolTipRecursively(jlab, tooltip);
    dest.add(jlab, c);
    
    //Middle pad
    c.gridx++;
    dest.add(Box.createRigidArea(new Dimension(MIDDLE_MARGIN, 1)), c);
    
    //Component
    c.gridx++;
    c.gridwidth = pos == ALL ? 6 : 1;
    setToolTipRecursively(comp, tooltip);
    dest.add(comp, c);
    
    //Bottom-Right pad    
    c.gridx = pos == LEFT ? 4 : 9;
    c.gridy++;
    c.gridheight = 1;
    c.gridwidth = 1;
    dest.add(Box.createRigidArea(new Dimension(RIGHT_MARGIN, BOTTOM_MARGIN)), c);
  }

  /**
   * Sets a ToolTipText to a component and all its chidren components
   * @param c - the component to affect
   * @param text the Tool Tip Text
   */
  private static void setToolTipRecursively(JComponent c, String text) {
    c.setToolTipText("<html>" + text + "</html>");

    for (Component cc : c.getComponents())
      if (cc instanceof JComponent)
        setToolTipRecursively((JComponent) cc, text);
  }

  /**
   * <ol>
   * <li>Adds a Component to a JPanel
   * <li>Surrend the component with a TitledBorder
   * <li>Affect the Component with a Tool Tip Text
   * </ol>
   * @param panel the parent JPanel
   * @param comp the component to add
   * @param label the title of the component
   * @param tooltip  the Tool Tip Text
   */
  private static void addBordered(JPanel panel, JComponent comp, String label, String tooltip) {
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(comp, BorderLayout.CENTER);
    p.setBorder(BorderFactory.createTitledBorder(label));
    setToolTipRecursively(p, tooltip);
    panel.add(p);
  }

  /**
   * Sets a text to a JTextField. If the Text is empty, the background is "grayed out"
   * @param jtf the JTextField
   * @param text the Text
   */
  private void setText(JTextField jtf, String text) {
    jtf.setText(text);
  }

  /**
   * Sets a double value to a JTextField. If the value is -1 (unexpected), the background is "grayed out"
   * @param jtf the JTextField
   * @param d the double value
   */
  private void setText(JTextField jtf, double d) {
    if (d == -1) {
      jtf.setText("");
    } else {
      jtf.setText("" + d);
    }
  }

  /**
   * Sets a text to a JTextArea. If the Text is empty, the background is "grayed out"
   * @param jta the JTextArea
   * @param text the Text
   */
  private void setText(JTextArea jta, String text) {
    jta.setText(text);
  }

  @Override
  public void idUpdated(String id) {
    this.setText(this.sessionID, id);
    this.clientWindow.monitorRPP();
  }

  @Override
  public void hashUpdated(String hash) {
    this.setText(this.hashKey, hash);
  }

  @Override
  public void maxMAFUpdated(double maf) {
    this.setText(this.maf, maf);
  }
  
  @Override
  public void maxMAFNFEUpdated(double mafNFE) {
    this.setText(this.mafNFE, mafNFE);
  }
  
  @Override
  public void limitToSNVsUpdated(boolean limitToSNVs) {
    this.limitToSNVs.setSelected(limitToSNVs);
  }

  @Override
  public void leastSeverConsequenceUpdated(String csq) {
    this.setText(this.csq, csq);
  }

  @Override
  public void aesKeyUpdated(String aes) {
    this.setText(this.aes, aes);
  }

  @Override
  public void clientGenotypeFilenameUpdated(String genotypeFilename, int lines) {
    this.setText(this.genotypeFile, genotypeFilename);
    if (genotypeFilename != null && genotypeFilename.length() > 0)
      this.setText(this.genotypeFile, genotypeFilename + " (" + lines + ")");
  }

  @Override
  public void clientPublicKeyUpdated(String publicKey) {
    this.setText(this.publicKey, publicKey);
  }

  @Override
  public void clientPrivateKeyUpdated(String privateKey) {
    this.setText(this.privateKey, privateKey);
  }

  @Override
  public void rppStatusUpdated(String status) {    
    
    String msg;
    String[] st = status.split("\t");
    //Status = [DATE] STATE [details]
       
    String date = null;
    String stat;
    String extra = null;
    if(st.length == 1)
      stat = st[0];
    else if(st.length > 2){
      date = st[0];
      stat = st[1];
      extra = st[2];
    } else {
      if(st[0].charAt(0)== '['){
        date = st[0];
        stat = st[1];
      } else {
        stat = st[0];
        extra = st[1];  
      }
    }
    
    
    RPPStatus.State state = State.UNKNOWN;
    try {
      state = State.valueOf(stat);
    } catch (Exception e) {
      //Ignore
    }
    msg = MSG.STATUS(state);
    if(extra != null)
      msg += " "+extra;
    if(date != null)
      msg = date + " " + msg;

    msg = msg.replace("<RET>", "\n");

    switch(state){
      case RPP_EMPTY_DATA:
      case ERROR:
      case TPS_ERROR:
        this.clientWindow.alertError("RPP reported an error", msg);
        break;
      case TPS_DONE:
        this.clientWindow.alertInfo("Message from RPP", msg);
        this.clientWindow.getResults();
        break;
      default :
        //ignore
    }

    this.setText(this.rppStatus, msg);
    try {
      this.clientWindow.updateMenuItems(state);
    } catch (Exception e) {
      //Ignore
    }
  }

  @Override
  public void thirdPartyPublicKeyUpdated(String thirdPartyKey) {
    this.setText(this.thirdParty, thirdPartyKey);
  }

  @Override
  public void sessionReady() {
    this.clientWindow.sendData();
  }

  @Override
  public void rppAddressUpdated(String address, int portNumber) {
    if (portNumber == -1 || address == null || address.length() < 1) {
      this.setText(this.rpp, null);
      setRPPLedOFF();
    } else {
      if (this.clientWindow.doConnect())
        setRPPLedOK();
      else
        setRPPLedKO();
      this.setText(this.rpp, address + ":" + portNumber);
    }
    this.clientWindow.monitorRPP();
  }
  
  public void setRPPLedOK(){
    this.rppLed.removeAll();
    this.rppLed.add(RPP_OK);
    this.rppLed.revalidate();
    this.revalidate();
    this.clientWindow.revalidate();
    this.clientWindow.repaint();
  }
  
  public void setRPPLedKO(){
    this.rppLed.removeAll();
    this.rppLed.add(RPP_KO);
    this.rppLed.revalidate();
    this.revalidate();
    this.clientWindow.revalidate();
    this.clientWindow.repaint();
  }
  
  public void setRPPLedOFF(){
    this.rppLed.removeAll();
    this.rppLed.add(RPP_OFF);
    this.rppLed.revalidate();
    this.revalidate();
    this.clientWindow.revalidate();
    this.clientWindow.repaint();
  }

  @Override
  public void algorithmUpdated(String algorithm) {
    this.setText(this.algorithm, algorithm);
  }

  @Override
  public void selectedDatasetUpdated(String dataset) {
    this.setText(this.dataset, dataset);
  }

  @Override
  public void availableDatasetsUpdated(String datasets) {
    //Nothing - SessionPanel will not display available datasets
  }

  @Override
  public void thirdPartyPublicNameUpdated(String name) {
    this.setText(this.tpsName, name);
  }
}

package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.listener.SessionListener;
import fr.inserm.u1078.tludwig.privas.instances.ClientSession;
import fr.inserm.u1078.tludwig.privas.instances.RPPStatus;
import fr.inserm.u1078.tludwig.privas.instances.RPPStatus.State;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
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
 * Main Panel of the Client GUI Window. Displays all the information relative to a Session
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
   * Shows the selected GnomAD Version on RPP
   */
  private final JTextField gnomADVersion;
  /**
   * Shows the selected GnomAD subpopulation
   */
  private final JTextField selectSubpop;
  /**
   * Shows the Maximum Allele Frequency allowed in variant selected
   */
  private final JTextField maf;
  /**
   * Shows the Maximum Allele Frequency allowed in variant selected
   */
  private final JTextField mafSubpop;
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
   * Shows the path to the binary GnomAD file
   */
  private final JTextField gnomADFilename;
  /**
   * Shows the path to the bed file of well covered positions
   */
  private final JTextField bedFilename;
  /**
   * Shows the path to the Client's Excluded Variants File
   */
  private final JTextField excludedVariantsFilename;
  /**
   * Shows the path to the File containing the Quality Control Parameters
   */
  private final JTextField qcParamFilename;
  /**
   * Shows the information relative to the RPP
   */
  private final JPanel rppPanel;
  /**
   * Shows the State of the RPP (Not chosen, not connected, connected)
   */
  private final JPanel rppLed;

  /**
   * Size of the RPP state LED
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
    final int nbRow = 3;
    final int nbCol = 98;
    
    this.clientWindow = clientWindow;
    this.sessionID = new JTextField(20);
    this.hashKey = new JTextField(20);
    this.publicKey = new JTextArea(nbRow, nbCol);
    this.privateKey = new JTextArea(nbRow, nbCol);
    this.aes = new JTextField(20);
    this.dataset = new JTextField(30);
    this.gnomADVersion = new JTextField(20);
    this.selectSubpop = new JTextField(5);
    this.maf = new JTextField(10);
    this.mafSubpop = new JTextField(10);
    this.csq = new JTextField(30);
    this.limitToSNVs = new JCheckBox("", true);
    this.rpp = new JTextField(30);
    this.tpsName = new JTextField(30);
    this.thirdParty = new JTextArea(nbRow, nbCol);
    this.algorithm = new JTextField(50);
    this.rppStatus = new JTextField();
    this.genotypeFile = new JTextField(40);
    this.gnomADFilename = new JTextField(40);
    this.bedFilename = new JTextField(40);
    this.excludedVariantsFilename = new JTextField(40);
    this.qcParamFilename = new JTextField(40);
    this.rppLed = new JPanel();
    this.rppPanel = new JPanel();

    init(session);
  }

  /**
   * Initializes the Panel (Layout, SessionListener)
   * @param session the ClientSession
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
    this.gnomADVersion.setEditable(false);
    this.selectSubpop.setEditable(false);
    this.maf.setEditable(false);
    this.mafSubpop.setEditable(false);
    this.csq.setEditable(false);
    this.rpp.setEditable(false);
    this.tpsName.setEditable(false);
    this.thirdParty.setEditable(false);
    this.algorithm.setEditable(false);
    this.rppStatus.setEditable(false);
    this.genotypeFile.setEditable(false);
    this.gnomADFilename.setEditable(false);
    this.bedFilename.setEditable(false);
    this.excludedVariantsFilename.setEditable(false);
    this.qcParamFilename.setEditable(false);

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
    gnomADFilename.setBackground(LookAndFeel.TEXT_BG_COLOR); //don't understand why it isn't already done by L&F
    bedFilename.setBackground(LookAndFeel.TEXT_BG_COLOR); //don't understand why it isn't already done by L&F
    excludedVariantsFilename.setBackground(LookAndFeel.TEXT_BG_COLOR);//don't understand why it isn't already done by L&F
    qcParamFilename.setBackground(LookAndFeel.TEXT_BG_COLOR);//don't understand why it isn't already done by L&F
    genotypeFile.setBackground(LookAndFeel.TEXT_BG_COLOR); //don't understand why it isn't already done by L&F
    publicKey.setBackground(LookAndFeel.TEXT_BG_COLOR); //don't understand why it isn't already done by L&F
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
    int row = 1;
    addElement(main, rppPanel, GUI.SP_LABEL_RPP, GUI.SP_TOOLTIP_RPP, row, HPosition.LEFT);
    addElement(main, tpsName, GUI.SP_LABEL_THIRD_PARTY_NAME, GUI.SP_TOOLTIP_THIRD_PARTY_NAME, row++, HPosition.BIG_RIGHT);
    addElement(main, dataset, GUI.SP_LABEL_DATASET, GUI.SP_TOOLTIP_DATASET, row, HPosition.BIG_LEFT);
    addElement(main, gnomADVersion, GUI.SP_LABEL_GNOMAD_VERSION, GUI.SP_TOOLTIP_GNOMAD_VERSION, row++, HPosition.RIGHT);
    addElement(main, maf, GUI.SP_LABEL_MAF, GUI.SP_TOOLTIP_MAF, row, HPosition.LEFT);
    addElement(main, selectSubpop, GUI.SP_LABEL_SUBPOP, GUI.SP_TOOLTIP_SUBPOP, row, HPosition.MIDDLE);
    addElement(main, mafSubpop, GUI.SP_LABEL_MAF_SUBPOP, GUI.SP_TOOLTIP_MAF_SUBPOP, row++, HPosition.RIGHT);
    addElement(main, sessionID, GUI.SP_LABEL_ID, GUI.SP_TOOLTIP_ID, row, HPosition.LEFT);
    addElement(main, csq, GUI.SP_LABEL_CSQ, GUI.SP_TOOLTIP_CSQ, row, HPosition.MIDDLE);
    addElement(main, limitToSNVs, GUI.SP_LABEL_LIMIT_SNV, GUI.SP_TOOLTIP_LIMIT_SNV, row++, HPosition.RIGHT);

    addElement(main, aes, GUI.SP_LABEL_AES, GUI.SP_TOOLTIP_AES, row, HPosition.LEFT);
    addElement(main, algorithm, GUI.SP_LABEL_ALGORITHM, GUI.SP_TOOLTIP_ALGORITHM, row++, HPosition.BIG_RIGHT);

    addElement(main, genotypeFile, GUI.SP_LABEL_GENOTYPE, GUI.SP_TOOLTIP_GENOTYPE, row++, HPosition.ALL);
    addElement(main, gnomADFilename, GUI.SP_LABEL_GNOMAD_FILENAME, GUI.SP_TOOLTIP_GNOMAD_FILENAME, row++, HPosition.ALL);
    addElement(main, bedFilename, GUI.SP_LABEL_BED_FILENAME, GUI.SP_TOOLTIP_BED_FILENAME, row++, HPosition.ALL);
    addElement(main, qcParamFilename, GUI.SP_LABEL_QC_PARAM_FILENAME, GUI.SP_TOOLTIP_QC_PARAM_FILENAME, row++, HPosition.ALL);
    addElement(main, excludedVariantsFilename, GUI.SP_LABEL_EXCLUDED_VARIANTS_FILENAME, GUI.SP_TOOLTIP_EXCLUDED_VARIANTS_FILENAME, row++, HPosition.ALL);
    addElement(main, hashKey, GUI.SP_LABEL_HASH, GUI.SP_TOOLTIP_HASH, row++, HPosition.ALL);
    addElement(main, publicKeySP, GUI.SP_LABEL_PUBLIC, GUI.SP_TOOLTIP_PUBLIC, row++, HPosition.ALL);
    addElement(main, privateKeySP, GUI.SP_LABEL_PRIVATE, GUI.SP_TOOLTIP_PRIVATE, row++, HPosition.ALL);
    addElement(main, thirdPartySP, GUI.SP_LABEL_THIRD_PARTY_KEY, GUI.SP_TOOLTIP_THIRD_PARTY_KEY, row++, HPosition.ALL);
    addElement(main, rppStatus, GUI.SP_LABEL_STATUS, GUI.SP_TOOLTIP_STATUS, row++, HPosition.ALL);

    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    this.add(main);    
  }

  private enum HPosition {LEFT, BIG_LEFT, MIDDLE, BIG_RIGHT, RIGHT, ALL}

  private static final int LEFT_MARGIN = 10;
  private static final int MIDDLE_MARGIN = 10;
  private static final int RIGHT_MARGIN = 5;  
  private static final int TOP_MARGIN = 2;
  private static final int BOTTOM_MARGIN = 2;
  
  private void addElement(JPanel dest, JComponent comp, String label, String tooltip, int row, HPosition pos){
    GridBagConstraints c = new GridBagConstraints();    
    c.fill = GridBagConstraints.BOTH;
    c.gridheight = 1;
    
    //Top-Left pad
    c.gridy = row*3;

    switch(pos){
      case LEFT:
      case BIG_LEFT:
      case ALL :
        c.gridx = 0;
        break;
      case MIDDLE:
      case BIG_RIGHT:
        c.gridx = 5;
        break;
      case RIGHT:
        c.gridx = 10;
        break;
    }

    c.gridwidth = 1;
    dest.add(Box.createRigidArea(new Dimension(LEFT_MARGIN, TOP_MARGIN)), c);
    
    //Label
    c.gridx++;
    c.gridy++;
    JLabel jLabel = new JLabel(label);
    setToolTipRecursively(jLabel, tooltip);
    dest.add(jLabel, c);
    
    //Middle pad
    c.gridx++;
    dest.add(Box.createRigidArea(new Dimension(MIDDLE_MARGIN, 1)), c);
    
    //Component
    c.gridx++;

    switch(pos){
      case LEFT:
      case MIDDLE:
      case RIGHT:
        c.gridwidth = 1;
        break;
      case BIG_LEFT:
      case BIG_RIGHT:
        c.gridwidth = 6;
        break;
      case ALL:
        c.gridwidth = 11;
        break;
    }

    setToolTipRecursively(comp, tooltip);
    dest.add(comp, c);
    
    //Bottom-Right pad
    switch(pos){
      case LEFT:
        c.gridx = 4;
        break;
      case BIG_LEFT:
      case MIDDLE:
        c.gridx = 9;
        break;
      case RIGHT:
      case BIG_RIGHT:
      case ALL:
        c.gridx = 14;
        break;
    }
    c.gridy++;
    c.gridheight = 1;
    c.gridwidth = 1;
    dest.add(Box.createRigidArea(new Dimension(RIGHT_MARGIN, BOTTOM_MARGIN)), c);
  }

  /**
   * Sets a ToolTipText to a component and all its children components
   * @param c - the component to affect
   * @param text the Tool Tip Text
   */
  private static void setToolTipRecursively(JComponent c, String text) {
    c.setToolTipText(Constants.HTML(text));

    for (Component cc : c.getComponents())
      if (cc instanceof JComponent)
        setToolTipRecursively((JComponent) cc, text);
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
  public void maxMAFSubpopUpdated(double mafSubpop) {
    this.setText(this.mafSubpop, mafSubpop);
  }

  @Override
  public void selectedGnomADSubpopulationUpdated(String selectedGnomADSubpopulation) { this.setText(this.selectSubpop, selectedGnomADSubpopulation); }

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

    msg = msg.replace(Constants.RET, "\n");

    switch(state){
      case RPP_EMPTY_DATA:
      case ERROR:
      case TPS_ERROR:
        this.clientWindow.showAndLogError(msg, GUI.SP_TIT_RPP_ERROR);
        break;
      case TPS_DONE:
        this.clientWindow.showMessage(msg, GUI.SP_TIT_RPP_MESSAGE);
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
        if (this.clientWindow.doConnect()) {
          setRPPLedOK();
          this.clientWindow.monitorRPP();
        }
        else {
          setRPPLedKO();
          clientWindow.showAndLogError(MSG.CL_KO_CONNECT, MSG.CL_KO_CONNECT);
        }
      this.setText(this.rpp, address + ":" + portNumber);
    }
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
  public void availableGnomADVersionsUpdated(String gnomADVersions) {
    //Nothing - SessionPanel will not display available GnomAD Versions
  }

  @Override
  public void excludedVariantsFilenameUpdated(String excludedVariantsFilename) {this.setText(this.excludedVariantsFilename, excludedVariantsFilename);  }

  @Override
  public void qcParamFilenameUpdated(String qcParamFilename) {this.setText(this.qcParamFilename, qcParamFilename);}

  @Override
  public void selectedGnomADVersionUpdated(String gnomADVersion) { this.setText(this.gnomADVersion, gnomADVersion);  }

  @Override
  public void thirdPartyPublicNameUpdated(String name) {
    this.setText(this.tpsName, name);
  }

  @Override
  public void bedFilenameUpdated(String bedFilename) { this.setText(this.bedFilename, bedFilename); }

  @Override
  public void selectedGnomADFilenameUpdated(String gnomADFilename) { this.setText(this.gnomADFilename, gnomADFilename); }
}

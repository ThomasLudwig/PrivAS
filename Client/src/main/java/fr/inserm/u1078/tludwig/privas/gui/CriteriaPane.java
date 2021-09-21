package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.gui.helper.FileCheckerTextField;
import fr.inserm.u1078.tludwig.privas.gui.helper.FrequencyCheckerTextField;
import fr.inserm.u1078.tludwig.privas.gui.helper.PositiveLongCheckTextField;
import fr.inserm.u1078.tludwig.privas.utils.FileUtils;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

/**
 * Part of a JOptionPane used to set the criteria before ask the RPP to create a new Session
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-05
 * 
 * Javadoc Complete on 2019-08-09
 */
public class CriteriaPane extends JPanel {

  /**
   * Used in estimating the duration of the Association Test
   */
  private static final double RATE = 1000000000;//FIXME completely obsolete

  /**
   * The ClientWindow Calling this Pane
   */
  private final ClientWindow clientWindow;

  //filters
  /**
   * To select the RPP Dataset
   */
  private final JComboBox<String> datasetJCB;
  /**
   * To select the GnomAD version on the RPP
   */
  private final JComboBox<String> gnomadVersionJCB;
  /**
   * To set the Maximum Allele Frequency allowed in variant selection
   */
  private final JTextField mafTF;
  /**
   * To choose the GnomAD subpopulation
   */
  private final JComboBox<String> subpopJCB;
  /**
   * To set the Maximum Allele Frequency allowed in the subpopulation in variant selection
   */
  private final JTextField mafSubpopTF;
  /**
   * To set the Least Severe vep Consequence allowed in variant selection
   */
  private final JComboBox<String> csqJCB;
  
  /**
   * To set is variant selection is limited to SNVs
   */
  private final JCheckBox limitToSNVsCB;
  /**
   * To set the Bed File of well covered positions
   */
  private final JTextField bedFileTF;
    /**
   * To set the list of excluded variants (manually)
   */
  private final JTextField excludedVariantsTF;

  /**
   * To set the QC parameter file
   */
  private final JTextField qcParametersTF;

  //WSS parameters
  /**
   * Do we run the WSS algorithm ?
   */
  private final JRadioButton doWSS;
  /**
   * To choose the maximum number of permutations in the test
   */
  private final JTextField permutationTF;
  
  /**
   * To choose the maximum frequency of alleles over pooled data
   */
  private final JTextField frqThresholdTF;
  /**
   * The most precise p-value with this number of permutations
   */
  private final JLabel pvalueLabel;
  /**
   * Estimation of the computation duration with this number of permutations
   */
  private final JLabel durationLabel;

  /**
   * Defaults number of variants in the client's Genotype File (so as not the divide by 0 on creation)
   */
  private double nbVariants = 65000;
  /**
   * The current algorithm selected and its trailing parameters
   */
  private String algorithm;

  /**
   * Constructor 
   * @param clientWindow the ClientWindow associated to (calling) this Pane
   */
  public CriteriaPane(ClientWindow clientWindow) {
    super();
    this.clientWindow = clientWindow;
    this.datasetJCB = new JComboBox<>();
    this.gnomadVersionJCB = new JComboBox<>();
    this.mafTF = new FrequencyCheckerTextField("" + Parameters.CRIT_DEFAULT_MAF);
    this.subpopJCB = new JComboBox<>(Constants.GNOMAD_SUBPOPS);
    this.mafSubpopTF = new FrequencyCheckerTextField("" + Parameters.CRIT_DEFAULT_MAF);
    this.csqJCB = new JComboBox<>(Constants.VEP_CONSEQUENCES);
    this.limitToSNVsCB = new JCheckBox("", true);
    this.bedFileTF = new FileCheckerTextField(30);
    this.excludedVariantsTF = new FileCheckerTextField(30);
    this.qcParametersTF = new FileCheckerTextField(30);

    this.doWSS = new JRadioButton(GUI.CRIT_RADIO_WSS);
    this.permutationTF = new PositiveLongCheckTextField("" + Parameters.CRIT_DEFAULT_WSS_PERM, 10){
      @Override
      public void whenOK(){ permutationUpdated();}
      @Override
      public void whenKO(){ permutationUpdateFailed();}
    };
    this.frqThresholdTF = new FrequencyCheckerTextField("" + Parameters.CRIT_DEFAULT_WSS_FRQ, 4){
      @Override
      public void whenOK(){ updateAlgorithm(); }};

    this.pvalueLabel = new JLabel(GUI.CRIT_LABEL_MIN_PVALUE);
    this.durationLabel = new JLabel(GUI.CRIT_LABEL_DURATION);

    this.init();
  }

  /**
   * Initializes this Pane
   */
  private void init() {
    this.csqJCB.setSelectedIndex(Parameters.CRIT_DEFAULT_CSQ - 1);

    JButton bedFileButton = new JButton(GUI.CHOOSE);
    JButton excludedVariantsButton = new JButton(GUI.CHOOSE);
    JButton qcParametersButton = new JButton(GUI.CHOOSE);

    bedFileButton.addActionListener(e -> chooseBedFile());
    excludedVariantsButton.addActionListener(e -> chooseExcludedVariants());
    qcParametersButton.addActionListener(e -> chooseQCParameters());

    JPanel selectionPanel = new JPanel();
    selectionPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.BOTH;

    int row = 0;
    addComp(selectionPanel, c, GUI.CRIT_LABEL_DATASET, datasetJCB, null, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_AVAILABLE_GNOMAD_VERSION, gnomadVersionJCB, null, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_CSQ, csqJCB, null, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_MAF, mafTF, null, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_SUBPOP, subpopJCB, null, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_MAF_SUBPOP, mafSubpopTF, null, row++);
    addComp(selectionPanel, c, GUI.SP_LABEL_LIMIT_SNV, limitToSNVsCB, null, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_BEDFILE, bedFileTF, bedFileButton, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_QC_PARAMETERS, qcParametersTF, qcParametersButton, row++);
    addComp(selectionPanel, c, GUI.CRIT_LABEL_EXCLUDED_VARIANTS, excludedVariantsTF, excludedVariantsButton, row++);

    JPanel wssS1Panel = new JPanel();
    wssS1Panel.add(doWSS);
    wssS1Panel.add(Box.createHorizontalStrut(GUI.HSP_MEDIUM));
    wssS1Panel.add(new JLabel(GUI.CRIT_LABEL_PERM));
    wssS1Panel.add(permutationTF);
    wssS1Panel.add(Box.createHorizontalStrut(GUI.HSP_MEDIUM));
    wssS1Panel.add(new JLabel(GUI.CRIT_LABEL_FRQ));
    wssS1Panel.add(frqThresholdTF);
    JPanel wssS2Panel = new JPanel();
    wssS2Panel.add(pvalueLabel);
    wssS2Panel.add(durationLabel);
    durationLabel.setToolTipText(GUI.CRIT_TOOLTIP_DISCLAIMER);

    JPanel wssPanel = new JPanel();
    wssPanel.setLayout(new BoxLayout(wssPanel, BoxLayout.PAGE_AXIS));
    wssPanel.add(wssS1Panel);
    wssPanel.add(wssS2Panel);

    selectionPanel.setBorder(BorderFactory.createTitledBorder(GUI.CRIT_TITLE_SELECT));
    wssPanel.setBorder(BorderFactory.createTitledBorder(GUI.CRIT_TITLE_WSS));

    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    this.add(selectionPanel);
    this.add(wssPanel);

    this.doWSS.setSelected(true);
    permutationTF.setText("" + Parameters.CRIT_DEFAULT_WSS_PERM);
    frqThresholdTF.setText("" + Parameters.CRIT_DEFAULT_WSS_FRQ);
  }

  private void permutationUpdateFailed() {
    pvalueLabel.setText(GUI.CRIT_LABEL_MIN_PVALUE + ": " + Constants.DETAILS_UNKNOWN);
    durationLabel.setText(GUI.CRIT_LABEL_DURATION + ": " + Constants.DETAILS_UNKNOWN);
  }

  private void permutationUpdated() {
    try {
      long permutation = new Long(permutationTF.getText());
      double minPvalue = 1d / (permutation);
      pvalueLabel.setText(GUI.CRIT_LABEL_MIN_PVALUE + ": " + minPvalue);
      double dur = (permutation * nbVariants) / RATE;
      long sec = (long) dur;
      double ms = dur - sec;
      if (ms < 0.001)
        ms = 0;
      String msString = "" + ms;
      int dot = msString.indexOf('.');
      msString = msString.substring(dot + 1);
      if (msString.length() > 3)
        msString = msString.substring(0, 3);

      long min = sec / 60;
      sec = sec % 60;
      String out = sec + "." + msString + "s";
      if (min > 0) {
        long hour = min / 60;
        min = min % 60;
        out = min + "m" + out;
        if (hour > 0) {
          long days = hour / 24;
          hour = hour % 24;
          out = hour + "h" + out;
          if (days > 0)
            out = days + " days " + out;
        }
      }
      durationLabel.setText(GUI.CRIT_LABEL_DURATION + ": " + out);
      updateAlgorithm();

    } catch (NumberFormatException e) {
      permutationUpdateFailed();
    }
  }

  private void updateAlgorithm() {
    if (doWSS.isSelected())
      algorithm = Constants.ALGO_WSS + ":" + permutationTF.getText() + ":" + frqThresholdTF.getText();
  }
  
  private void addComp(JPanel panel, GridBagConstraints c, String label, Component comp, JButton button, int row){
    Dimension dim = new Dimension(15,4);
    c.gridx = 0;
    c.gridy = 2*row;
    panel.add(Box.createRigidArea(dim), c);
    c.gridy++;
    c.gridx++;    
    panel.add(new JLabel(label), c);
    c.gridx++;
    panel.add(Box.createRigidArea(dim), c);
    c.gridx++;
    panel.add(comp, c);
    c.gridx++;
    panel.add(Box.createRigidArea(dim), c);
    if(button != null){
      c.gridx++;
      panel.add(button, c);
      c.gridx++;
      panel.add(Box.createRigidArea(dim), c);
    }
  }

  public void chooseBedFile(){
    FileExtensionChooser dial = this.clientWindow.getBedFileDialog();
    if (dial.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      this.setBedFileName(dial.getSelectedFile().getAbsolutePath());
  }

  public void chooseQCParameters(){
    FileExtensionChooser dial = this.clientWindow.getQCParametersDialog();
    if (dial.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      this.setQCParam(dial.getSelectedFile().getAbsolutePath());
  }

  public void chooseExcludedVariants(){
    FileExtensionChooser dial = this.clientWindow.getExclusionDialog();
    if (dial.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      setExcludedVariantsFilename(dial.getSelectedFile().getAbsolutePath());
  }

  public String getBedFileName(){
    return this.bedFileTF.getText();
  }
  
  public String getExcludedVariantsFilename(){
    return this.excludedVariantsTF.getText();
  }

  public String getQCParamFilename(){
    return this.qcParametersTF.getText();
  }

  /**
   * Sets the total number of variants in the Client Genotype File
   * @param nbVariants the total number of variants in the Client Genotype File
   */
  public void setNbVariants(double nbVariants) {
    this.nbVariants = nbVariants;
  }

  /**
   * Gets the selected algorithm and its trailing parameters
   * @return the selected algorithm and its trailing parameters
   */
  public String getAlgorithm() {
    return algorithm;
  }

  /**
   * Gets the Maximum Allele Frequency used during the variant selection
   * @return the Maximum Allele Frequency used during the variant selection
   */
  public double getMAF() {
    try {
      return new Double(this.mafTF.getText());
    } catch (NumberFormatException nfe) {
      return 1;
    }
  }
  
  /**
   * Gets the Maximum Allele Frequency used during the variant selection
   * @return the Maximum Allele Frequency used during the variant selection
   */
  public double getMAFSubpop() {
    try {
      return new Double(this.mafSubpopTF.getText());
    } catch (NumberFormatException nfe) {
      return 1;
    }
  }

  public String getSubpop() {
    return (String)subpopJCB.getSelectedItem();
  }

  /**
   * Gets the name of the selected RPP Dataset
   * @return the name of the selected RPP Dataset
   */
  public String getDataset() {
    return (String) this.datasetJCB.getSelectedItem();
  }

  public String getGnomADVersion() {
    return (String) this.gnomadVersionJCB.getSelectedItem();
  }

  /**
   * Before showing this Pane, the Available RPP datasets are refreshed from the Client's list
   * @param datasets the new datasets
   */
  private void updateDatasets(String datasets) {
    this.datasetJCB.removeAllItems();
    for (String ds : datasets.split(","))
      this.datasetJCB.addItem(ds);
  }

  /**
   * Before showing this Pane, the Available RPP GnomAD versions are refreshed from the Client's list
   * @param versions the GnomAD Versions
   */
  private void updateGnomadVersions(String versions) {
    this.gnomadVersionJCB.removeAllItems();
    for(String version : versions.split(","))
      this.gnomadVersionJCB.addItem(version);
  }

  /**
   * Gets the selected Least Severe vep Consequence
   * @return the selected Least Severe vep Consequence
   */
  public String getCsq() {
    return (String) this.csqJCB.getSelectedItem();
  }
  
  /**
   * Is variant selection limited to SNVs ?
   * @return true if selection is limited to SNVs
   */
  public boolean getLimitToSNVs() {
    return this.limitToSNVsCB.isSelected();    
  }

  /**
   * Show inside a JOptionPane
   * @return the return of JOptionPane.showConfirmDialog()
   */
  public int display() {
    this.permutationTF.setText(this.permutationTF.getText());
    this.frqThresholdTF.setText(this.frqThresholdTF.getText());
    this.updateDatasets(this.clientWindow.getClient().getSession().getAvailableDatasets());
    this.updateGnomadVersions(this.clientWindow.getClient().getSession().getAvailableGnomADVersions());
    return JOptionPane.showConfirmDialog(null, this, GUI.CRIT_TITLE, JOptionPane.OK_CANCEL_OPTION);
  }

  public void setBedFileName(String filename) {
    this.bedFileTF.setText(filename);
  }

  public void setQCParam(String filename) {
    this.qcParametersTF.setText(filename);
  }

  public void setExcludedVariantsFilename(String filename) {
    this.excludedVariantsTF.setText(filename);
  }

  public int checkQC(){
    return this.checkFile(this.qcParametersTF.getText());
  }

  public int checkExcludedVariants(){
    return this.checkFile(this.excludedVariantsTF.getText());
  }

  public int checkBed(){
    return this.checkFile(this.bedFileTF.getText());
  }

  public static final int CHECK_OK = 0;
  public static final int CHECK_EMPTY = 1;
  public static final int CHECK_NOT_FOUND = 2;

  private int checkFile(String filename){
    if(filename == null)
      return CHECK_EMPTY;
    if(filename.isEmpty())
      return CHECK_EMPTY;
    return FileUtils.exists(filename) ? CHECK_OK : CHECK_NOT_FOUND;
  }
}

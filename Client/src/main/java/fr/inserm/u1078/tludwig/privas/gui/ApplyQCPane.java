package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.gui.helper.FileCheckerTextField;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Pane to Apply QC on a VCF File
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-12
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class ApplyQCPane extends JPanel {
  private final ClientWindow clientWindow;
  private final FileExtensionChooser fcVCF;
  private final FileExtensionChooser fcQCParam;
  private final FileExtensionChooser fcGnomAD;

  private final JTextField inputVCFTF;
  private final JTextField qcParamTF;
  private final JTextField gnomADTF;

  private GridBagConstraints c;

  public ApplyQCPane(ClientWindow clientWindow, FileExtensionChooser fcVCF, FileExtensionChooser fcQCParam, FileExtensionChooser fcGnomAD){
    this.clientWindow = clientWindow;
    this.fcVCF = fcVCF;
    this.fcQCParam = fcQCParam;
    this.fcGnomAD = fcGnomAD;

    this.inputVCFTF = new FileCheckerTextField(30);
    this.qcParamTF = new FileCheckerTextField(30);
    this.gnomADTF = new FileCheckerTextField(30);

    this.init();
  }

  private void init(){
    this.inputVCFTF.setText("");
    this.qcParamTF.setText("");
    this.gnomADTF.setText("");
    JButton inputVCFButton = new JButton(GUI.CHOOSE);
    JButton qcParamButton = new JButton(GUI.CHOOSE);
    JButton qcParamCreateButton = new JButton(GUI.CREATE);
    JButton gnomADButton = new JButton(GUI.CHOOSE);

    inputVCFButton.addActionListener(e -> chooseInputVCF());
    qcParamButton.addActionListener(e -> chooseQCParam());
    qcParamCreateButton.addActionListener(e -> createQCParam());
    gnomADButton.addActionListener(e -> chooseGnomADFile());

    this.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.BOTH;

    int row = 0;
    addComp(new JComponent[]{new JLabel(GUI.APQC_LABEL_INPUT_VCF), this.inputVCFTF, inputVCFButton}, row++);
    addComp(new JComponent[]{new JLabel(GUI.APQC_LABEL_QC_PARAM), this.qcParamTF, qcParamButton, qcParamCreateButton}, row++);
    if(this.fcGnomAD != null)
      addComp(new JComponent[]{new JLabel(GUI.APQC_LABEL_GNOMAD), this.gnomADTF, gnomADButton}, row++);
  }

  private void addComp(JComponent[] comps, int row){
    Dimension dim = new Dimension(15,4);
    c.gridx = 0;
    c.gridy = 2*row;
    this.add(Box.createRigidArea(dim), c);
    c.gridy++;

    for(JComponent comp : comps){
      c.gridx++;
      this.add(comp, c);
      c.gridx++;
      this.add(Box.createRigidArea(dim), c);
    }
  }

  private void createQCParam() {
    QCParamEditorPane editorPane = new QCParamEditorPane(fcQCParam);
    if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, editorPane, GUI.QC_EDITOR_TITLE, JOptionPane.OK_CANCEL_OPTION))
      return;
    try {
      this.qcParamTF.setText(editorPane.save());
    } catch(IOException e){
      clientWindow.showAndLogError(MSG.cat(GUI.APQC_MSG_QC_SAVE_FAILED, e.getMessage()), GUI.APQC_TIT_QC_SAVE_FAILED, e);
    }
  }

  private void chooseQCParam() {
    if (this.fcQCParam.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      this.qcParamTF.setText(this.fcQCParam.getSelectedFile().getAbsolutePath());
  }

  private void chooseInputVCF() {
    if (this.fcVCF.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      this.inputVCFTF.setText(this.fcVCF.getSelectedFile().getAbsolutePath());
  }

  private void chooseGnomADFile() {
    if (fcGnomAD != null && this.fcGnomAD.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      this.gnomADTF.setText(this.fcGnomAD.getSelectedFile().getAbsolutePath());
  }

  public String getInputVCFFilename(){
    return this.inputVCFTF.getText();
  }

  public String getQCParamFilename(){
    return this.qcParamTF.getText();
  }

  public String getGnomADFilename() {
    return this.gnomADTF.getText();
  }
}

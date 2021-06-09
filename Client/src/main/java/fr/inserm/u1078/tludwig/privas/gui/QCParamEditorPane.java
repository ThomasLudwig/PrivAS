package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * QC Parameters Editor
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-12
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class QCParamEditorPane extends JPanel {

  private final QCParam qcParam;

  private final JTextField minQD;
  private final JTextField maxABHetDev;
  private final JTextField minInbreeding;
  private final JTextField minMQRanksum;
  private final JTextField indelMaxFS;
  private final JTextField snpMaxFS;
  private final JTextField indelMaxSOR;
  private final JTextField snpMaxSOR;
  private final JTextField indelMinMQ;
  private final JTextField snpMinMQ;
  private final JTextField indelMinRPRS;
  private final JTextField snpMinRPRS;
  private final JTextField minGQ;
  private final JTextField minDP;
  private final JTextField maxDP;
  private final JTextField minCallrate;
  //private final JTextField minHQRatio;
  //private final JTextField minAltHQ;
  private final JTextField minFisherCallrate;

  private final FileExtensionChooser fcQCParam;
  //private final JTextField filenameTF;
  private GridBagConstraints c;
  private int row;

  public QCParamEditorPane(FileExtensionChooser fcQCParam){
    this.fcQCParam = fcQCParam;
    this.qcParam = new QCParam();
    minQD = new JTextField(QCParam.asString(qcParam.getMinQD()), 8);
    maxABHetDev = new JTextField(QCParam.asString(qcParam.getMaxABHetDev()),8);
    minInbreeding = new JTextField(QCParam.asString(qcParam.getMinInbreeding()), 8);
    minMQRanksum = new JTextField(QCParam.asString(qcParam.getMinMQRanksum()), 8);
    indelMaxFS = new JTextField(QCParam.asString(qcParam.getIndelMaxFS()), 8);
    snpMaxFS = new JTextField(QCParam.asString(qcParam.getSnpMaxFS()), 8);
    indelMaxSOR = new JTextField(QCParam.asString(qcParam.getIndelMaxSOR()), 8);
    snpMaxSOR = new JTextField(QCParam.asString(qcParam.getSnpMaxSOR()), 8);
    indelMinMQ = new JTextField(QCParam.asString(qcParam.getIndelMinMQ()), 8);
    snpMinMQ = new JTextField(QCParam.asString(qcParam.getSnpMinMQ()), 8);
    indelMinRPRS = new JTextField(QCParam.asString(qcParam.getIndelMinRPRS()), 8);
    snpMinRPRS = new JTextField(QCParam.asString(qcParam.getSnpMinRPRS()), 8);
    minGQ = new JTextField(QCParam.asString(qcParam.getMinGQ()), 8);
    minDP = new JTextField(QCParam.asString(qcParam.getMinDP()), 8);
    maxDP = new JTextField(QCParam.asString(qcParam.getMaxDP()), 8);
    minCallrate = new JTextField(QCParam.asString(qcParam.getMinCallrate()), 8);
    //minHQRatio = new JTextField(QCParam.asString(qcParam.getMinHQRatio()), 8);
    //minAltHQ = new JTextField(QCParam.asString(qcParam.getMinAltHQ()), 8);
    minFisherCallrate = new JTextField(QCParam.asString(qcParam.getMinFisherCallrate()), 8);

    //filenameTF = new JTextField(30);

    init();
  }

  private void init(){
    //filenameTF.setEnabled(false);
    //JButton filenameButton = new JButton(GUI.CHOOSE);
    //filenameButton.addActionListener(e -> chooseQCParam());

    this.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.BOTH;

    row = 0;
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_QD), minQD});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MAX_ABHET_DEV), maxABHetDev});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_INBREEDING), minInbreeding});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_MQRANKSUM), minMQRanksum});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MAX_FS_INDEL), indelMaxFS});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MAX_FS_SNP), snpMaxFS});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MAX_SOR_INDEL), indelMaxSOR});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MAX_SOR_SNP), snpMaxSOR});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_MQ_INDEL), indelMinMQ});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_MQ_SNP), snpMinMQ});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_RPRS_INDEL), indelMinRPRS});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_RPRS_SNP), snpMinRPRS});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_GQ), minGQ});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_DP), minDP});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MAX_DP), maxDP});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_CALLRATE), minCallrate});
    //addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_HQ_RATIO), minHQRatio});
    //addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_ALT_HQ), minAltHQ});
    addComp(new JComponent[]{new JLabel(QCParam.LABEL_MIN_FISHER_CALLRATE ), minFisherCallrate});
    //addComp(new JComponent[]{new JLabel("Save As"), filenameTF, filenameButton});
  }

  private void addComp(JComponent[] comps){
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
    row++;
  }

  public String save() throws IOException {
    qcParam.unsafeSetValue(QCParam.KEY_MIN_GQ, minQD.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MAX_ABHET_DEV, maxABHetDev.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_INBREEDING, minInbreeding.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_MQRANKSUM, minMQRanksum.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MAX_FS_INDEL, indelMaxFS.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MAX_FS_SNP, snpMaxFS.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MAX_SOR_INDEL, indelMaxSOR.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MAX_SOR_SNP, snpMaxSOR.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_MQ_INDEL, indelMinMQ.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_MQ_SNP, snpMinMQ.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_RPRS_INDEL, indelMinRPRS.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_RPRS_SNP, snpMinRPRS.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_GQ, minGQ.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_DP, minDP.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MAX_DP, maxDP.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_CALLRATE, minCallrate.getText());
    //qcParam.unsafeSetValue(QCParam.KEY_MIN_HQ_RATIO, minHQRatio.getText());
    //qcParam.unsafeSetValue(QCParam.KEY_MIN_ALT_HQ, minAltHQ.getText());
    qcParam.unsafeSetValue(QCParam.KEY_MIN_FISHER_CALLRATE , minFisherCallrate.getText());

    String defaultName = "QC"+qcParam.hashCode()+".param";
    fcQCParam.setSelectedFile(new File(defaultName));
    if (this.fcQCParam.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      //this.filenameTF.setText(this.fcQCParam.getSelectedFile().getAbsolutePath());
      String filename = fcQCParam.getSelectedFile().getAbsolutePath();
      qcParam.save(filename);

      return filename;
    }
    return null;
  }

  /*public String getFilename() {
    return filenameTF.getText();
  }*/
}

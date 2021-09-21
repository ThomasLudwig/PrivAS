package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.gui.results.ResultsPane;
import fr.inserm.u1078.tludwig.privas.instances.*;
import fr.inserm.u1078.tludwig.privas.gui.results.ResultsPane.ParsingException;
import fr.inserm.u1078.tludwig.privas.utils.FileUtils;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCException;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import static fr.inserm.u1078.tludwig.privas.gui.CriteriaPane.CHECK_NOT_FOUND;
import static fr.inserm.u1078.tludwig.privas.gui.CriteriaPane.CHECK_OK;

/**
 * The Main Window of the GUI (Client Application)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-04
 *
 * Javadoc Complete on 2019-08-09
 */
public class ClientWindow extends JFrame {
  private final JMenuItem applyQCtoVCFAndAnnotate;
  private final JMenuItem justApplyQCtoVCF;
  private final JMenuItem loadQCedVCFAndAnnotate;
  private final JMenuItem loadQCedAnnotatedGenotypes;
  private final JMenuItem loadSession;
  private final JMenuItem saveSession;
  private final JMenuItem loadResults;
  private final JMenuItem quit;
  private final JMenuItem connect;
  private final JMenuItem startSession;
  private final JMenuItem showTPSLog;
  private final JMenuItem getResults;

  private final LoggingPanel loggingPanel;
  private final ConnectionPane connectionPane;
  private final CriteriaPane criteriaPane;
  private final ResultsPane resultsPane;
  private final TPSLogWindow tpsLogWindow;
  private final FileExtensionChooser fcVCF;
  private final FileExtensionChooser fcGenotypes;
  private final FileExtensionChooser fcSession;
  private final FileExtensionChooser fcResults;
  private final FileExtensionChooser fcBedFile;
  private final FileExtensionChooser fcQCParam;
  private final FileExtensionChooser fcGnomAD;
  private final FileExtensionChooser fcExclusion;
  private final SessionPanel sessionPanel;
  private final Client client;

  /**
   * Constructor
   *
   * @param startDir the default directory to open when loading/saving files
   * @param client   the client
   */
  public ClientWindow(String startDir, Client client) {
    super();
    this.client = client;
    this.sessionPanel = new SessionPanel(this.client.getSession(), this);
    this.loggingPanel = new LoggingPanel();
    this.client.addLogListener(loggingPanel);
    this.applyQCtoVCFAndAnnotate = new JMenuItem(GUI.CW_MI_APPLY_QC_VCF_ANNOTATE);
    this.justApplyQCtoVCF = new JMenuItem(GUI.CW_MI_APPLY_QC_VCF_ONLY);
    this.loadQCedVCFAndAnnotate = new JMenuItem(GUI.CW_MI_LOAD_QCED_VCF_ANNOTATE);
    this.loadQCedAnnotatedGenotypes = new JMenuItem(GUI.CW_MI_LOAD_GENO);
    this.loadSession = new JMenuItem(GUI.CW_MI_LOAD_SSS);
    this.saveSession = new JMenuItem(GUI.CW_MI_SAVE_SSS);
    this.loadResults = new JMenuItem(GUI.CW_MI_LOAD_RES);
    this.quit = new JMenuItem(GUI.CW_MI_QUIT);
    this.connect = new JMenuItem(GUI.CW_MI_CONNECT);
    this.startSession = new JMenuItem(GUI.CW_MI_NEW_SSS);
    this.showTPSLog = new JMenuItem(GUI.CW_MI_SHOW_TPS);
    this.getResults = new JMenuItem(GUI.CW_MI_GET_RESULTS);
    this.connectionPane = new ConnectionPane();
    this.criteriaPane = new CriteriaPane(this);
    this.resultsPane = new ResultsPane(this);
    this.tpsLogWindow = new TPSLogWindow();
    this.fcVCF = new FileExtensionChooser(FileFormat.FILE_VCF_EXTENSION, true, startDir);
    this.fcGenotypes = new FileExtensionChooser(FileFormat.FILE_GENO_EXTENSION, true, startDir);
    this.fcSession = new FileExtensionChooser(FileFormat.FILE_SESSION_EXTENSION, false, startDir);
    this.fcResults = new FileExtensionChooser(FileFormat.FILE_RESULTS_EXTENSION, false, startDir);
    this.fcBedFile = new FileExtensionChooser(FileFormat.FILE_BED_EXTENSION, true, startDir);
    this.fcQCParam = new FileExtensionChooser(FileFormat.FILE_QC_PARAM_EXTENSION, true, startDir);
    this.fcGnomAD = new FileExtensionChooser(FileFormat.FILE_GNOMAD_EXTENSION, false , startDir);
    this.fcExclusion = new FileExtensionChooser(FileFormat.FILE_EXCLUSION_EXTENSION, true, startDir);
    this.init();
    this.pack();
  }

  /**
   * Initializes everything
   */
  private void init() {
    this.sessionPanel.rppStatusUpdated("" + RPPStatus.State.NO_SESSION);
    this.setTitle(MSG.getTitle());
    final JPanel main = new JPanel();
    final JMenuBar bar = new JMenuBar();
    final JMenu file = new JMenu(GUI.CW_MN_FILE);
    final JMenu rpp = new JMenu(GUI.CW_MN_SERVER);
    file.add(applyQCtoVCFAndAnnotate);
    file.add(justApplyQCtoVCF);
    file.add(this.loadQCedVCFAndAnnotate);
    file.add(this.loadQCedAnnotatedGenotypes);
    file.addSeparator();
    file.add(this.loadSession);
    file.add(this.saveSession);
    file.addSeparator();
    file.add(this.loadResults);
    file.add(this.quit);
    bar.add(file);
    rpp.add(this.connect);
    rpp.add(this.startSession);
    rpp.add(this.showTPSLog);
    rpp.add(this.getResults);
    bar.add(rpp);

    this.applyQCtoVCFAndAnnotate.addActionListener(e -> applyQCtoVCF(true));
    this.justApplyQCtoVCF.addActionListener(e -> applyQCtoVCF(false));
    this.loadQCedVCFAndAnnotate.addActionListener(e -> loadQCedVCFAndAnnotate());
    this.loadQCedAnnotatedGenotypes.addActionListener(e -> loadQCedAnnotatedGenotypes());
    this.loadSession.addActionListener(e -> loadSession());
    this.saveSession.addActionListener(e -> saveSession());
    this.loadResults.addActionListener(e -> loadResults());
    this.quit.addActionListener(e -> quit());
    this.connect.addActionListener(e -> connect());
    this.startSession.addActionListener(e -> askSession());
    this.showTPSLog.addActionListener(e -> showTPSLog());
    this.getResults.addActionListener(e -> getResults());

    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(bar, BorderLayout.NORTH);
    this.getContentPane().add(main, BorderLayout.CENTER);
    this.getContentPane().add(this.loggingPanel, BorderLayout.SOUTH);

    main.setLayout(new BorderLayout());

    //this.main.add(logo, BorderLayout.CENTER);
    main.add(this.sessionPanel, BorderLayout.CENTER);

    try {
      this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(GUI.IMAGE_PATH_LOGO)));
    } catch (Exception e) {
      //Nothing
    }

    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quit();
        setVisible(true);
      }
    });

    this.pack();
    this.setVisible(true);
  }

  /**
   * OptionPane Popup to Connect to a RPP
   */
  private void connect() {
    int result = this.connectionPane.display();
    if (result == JOptionPane.OK_OPTION) {
      this.client.setRPP(this.connectionPane.getAddress(), this.connectionPane.getPort());
      if (this.client.isConnected())
        showMessage(GUI.CW_DG_OK_MSG_CONNECT(this.connectionPane.getAddress(), this.connectionPane.getPort()), GUI.CW_DG_OK_CONNECT);
      else
        showAndLogError(GUI.CW_DG_KO_MSG_CONNECT(this.connectionPane.getAddress(), this.connectionPane.getPort()), GUI.CW_DG_KO_CONNECT);
    }
  }
  
  public void reconnect(boolean success){
    if(success)
      this.sessionPanel.setRPPLedOK();
    else
      this.sessionPanel.setRPPLedKO();
  }

  /**
   * The actual connection trigger (through the Client instance)
   *
   * @return true is connection is successful
   */
  public boolean doConnect() {
    this.client.communicationGetRPPConfiguration();
    return this.client.isConnected();
  }

  /**
   * Opens a CriteriaPane in order to Start a new Session (@see fr.inserm.u1078.tludwig.privas.gui.CriteriaPane)
   */
  private void askSession() {
    if (this.client.getGenotypeFilename() == null) {
      showAndLogError(GUI.CW_DG_MSG_NO_GENO, GUI.CW_DG_NO_GENO);
      return;
    }

    while (!this.client.isConnected()) {
      int doConnect = JOptionPane.showConfirmDialog(this, GUI.CW_DG_MSG_NOT_CONNECTED, GUI.CW_DG_NOT_CONNECTED, JOptionPane.YES_NO_OPTION);
      if (doConnect == JOptionPane.OK_OPTION)
        this.connect();
      else
        return;
    }

    boolean run = true;
    while (run && (JOptionPane.OK_OPTION == this.criteriaPane.display())) {
      int checkBed = this.criteriaPane.checkBed();
      int checkQC = this.criteriaPane.checkQC();
      int checkExcl = this.criteriaPane.checkExcludedVariants();

      if(checkBed == CHECK_OK && checkQC == CHECK_OK && checkExcl == CHECK_OK) {
        this.waitForSession();
        run = false;
      }
      else {
        if(checkQC != CHECK_OK){
          String message = GUI.CRIT_MSG_EMPTY_QC;
          if(checkQC == CHECK_NOT_FOUND){
            message = GUI.CRIT_MSG_NOT_FOUND_QC(this.criteriaPane.getQCParamFilename());
          }
          showAndLogError(message, GUI.CRIT_TIT_EMPTY_QC);
        } else if(checkExcl != CHECK_OK) {
          String message = GUI.CRIT_MSG_EMPTY_EXCL;
          if(checkExcl == CHECK_NOT_FOUND){
            message = GUI.CRIT_MSG_NOT_FOUND_EXCL(this.criteriaPane.getExcludedVariantsFilename());
          }
          showAndLogError(message, GUI.CRIT_TIT_EMPTY_EXCL);
        } else {
          String message = GUI.CRIT_MSG_EMPTY_BED;
          if(checkBed == CHECK_NOT_FOUND){
            message = GUI.CRIT_MSG_NOT_FOUND_BED(this.criteriaPane.getBedFileName());
          }
          if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, message, GUI.CRIT_TIT_EMPTY_BED, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)){
            this.waitForSession();
            run = false;
          }
        }
      }
    }
  }

  ProgressDialog newSessionPD;

  public void waitForSession() {
    newSessionPD = new ProgressDialog(this, GUI.CW_CREATING_SESSION_TITLE, GUI.CW_CREATING_SESSION_NORTH, GUI.CW_CREATING_SESSION_SOUTH);
    newSessionPD.setWorker(new SwingWorker<Object, Object>() {
      @Override
      protected Object doInBackground() {
      try {
        client.setAlgorithm(criteriaPane.getAlgorithm());
        client.communicationAskSession(criteriaPane.getDataset(), criteriaPane.getGnomADVersion(), criteriaPane.getMAF(), criteriaPane.getSubpop(), criteriaPane.getMAFSubpop(), criteriaPane.getCsq(), criteriaPane.getLimitToSNVs(), criteriaPane.getBedFileName(), criteriaPane.getExcludedVariantsFilename(), criteriaPane.getQCParamFilename());
        newSessionPD.done();
        setTitle(GUI.CW_TITLE(client.getSessionId()));
        showMessage(MSG.cat(GUI.CW_DG_NEW_SESSION, client.getSessionId()), GUI.CW_DG_OK_SESSION);
        client.communicationStartSession(client.getSessionId());
      } catch (MessageException e) {
        newSessionPD.done();
        showAndLogError(client.getLastError(), GUI.CW_DG_KO_SESSION, e);
      }
      return null;
      }
    });
  }

  /**
   * Opens a Dialog to Apply QC and Annotate with GnomAD if needed
   */
  private void applyQCtoVCF(boolean annotate) {
    //Get Filenames
    FileExtensionChooser fc = annotate ? fcGnomAD : null;
    ApplyQCPane applyQCParam = new ApplyQCPane(this, fcVCF, fcQCParam, fc);
    int choice = JOptionPane.showConfirmDialog(this, applyQCParam, GUI.APQC_TITLE, JOptionPane.OK_CANCEL_OPTION);
    if(choice != JOptionPane.OK_OPTION)
      return;
    //ApplyQC
    String inputVCF = applyQCParam.getInputVCFFilename();
    String qcParamFilename = applyQCParam.getQCParamFilename();
    if(inputVCF == null || inputVCF.isEmpty()){
      showAndLogError(GUI.APQC_MSG_VCF_NULL, GUI.APQC_TIT_VCF_NULL);
      return;
    }

    if(!FileUtils.exists(inputVCF)){
      showAndLogError(GUI.APQC_MSG_VCF_MISSING(inputVCF), GUI.APQC_TIT_VCF_MISSING);
      return;
    }

    if(qcParamFilename == null || qcParamFilename.isEmpty()){
      showAndLogError(GUI.APQC_MSG_QC_NULL, GUI.APQC_TIT_QC_NULL);
      return;
    }

    if(!FileUtils.exists(qcParamFilename)){
      showAndLogError(GUI.APQC_MSG_QC_MISSING(qcParamFilename), GUI.APQC_TIT_QC_MISSING);
      return;
    }

    QCParam qcParam;
    try {
      qcParam = new QCParam(qcParamFilename);
    } catch(IOException | QCException e) {
      //TODO Fail POPUP / LOG
      return;
    }

    final ProgressDialog pd = new ProgressDialog(this, GUI.CW_APPLYING_QC_TITLE, GUI.CW_APPLYING_QC_NORTH, GUI.CW_APPLYING_QC_SOUTH);
    pd.setWorker(new SwingWorker<Object, Object>() {
      @Override
      protected Object doInBackground() {
        //dlg.setVisible(true);
        boolean success = client.applyQC(inputVCF, qcParamFilename);
        if (success)
          showMessage(GUI.CW_DG_MSG_OK_QC, GUI.CW_DG_OK_QC);
        else
          showAndLogError(MSG.cat(GUI.CW_DG_MSG_KO_QC, client.getLastError()), GUI.CW_DG_KO_QC);
        pd.done();
        return null;
      }

      @Override
      protected void done() {
        //Extract as genotype File
        if(annotate) {
          String gnomADFile = applyQCParam.getGnomADFilename();
          if(gnomADFile == null || gnomADFile.isEmpty()){
            showAndLogError(GUI.APQC_MSG_GNOMAD_NULL, GUI.APQC_TIT_GNOMAD_NULL);
            return;
          }
          if(!FileUtils.exists(gnomADFile)){
            showAndLogError(GUI.APQC_MSG_GNOMAD_MISSING(gnomADFile), GUI.APQC_TIT_GNOMAD_MISSING);
            return;
          }
          loadQCedVCFAndAnnotate(FileUtils.addQCPrefixToVCFFilename(inputVCF, qcParam), gnomADFile);
        }
      }
    });
  }

  /**
   * load a VCF File   *
   */
  private void loadQCedVCFAndAnnotate(String vcfFilename, String gnomADFilename){
    /*if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, GUI.CW_DG_MSG_CONVERT(vcfFilename), GUI.CW_DG_CONVERT, JOptionPane.ERROR_MESSAGE))
      return;
    */

    final ProgressDialog pd = new ProgressDialog(this, GUI.CW_CONVERTING_TITLE, GUI.CW_CONVERTING_NORTH, GUI.CW_CONVERTING_SOUTH);
    pd.setWorker(new SwingWorker<Object, Object>() {
      @Override
      protected Object doInBackground() {
        //dlg.setVisible(true);
        String genotypeFilename = client.convert(vcfFilename, gnomADFilename);
        pd.done();
        if (genotypeFilename != null) {
          setQCCriteria(new File(genotypeFilename));
          showMessage(GUI.CW_DG_MSG_OK_CONVERT, GUI.CW_DG_OK_CONVERT);
        } else
          showAndLogError(MSG.cat(GUI.CW_DG_MSG_KO_CONVERT, client.getLastError()), GUI.CW_DG_KO_CONVERT);
        return null;
      }
      });
  }

  /**
   * Opens a JFileChooser to load a VCF File
   */
  private void loadQCedVCFAndAnnotate() {
    //TODO nice panel here
    if (JFileChooser.APPROVE_OPTION != this.fcVCF.showOpenDialog(this))
      return;
    if (JFileChooser.APPROVE_OPTION != this.fcGnomAD.showOpenDialog(this))
      return;
    loadQCedVCFAndAnnotate(this.fcVCF.getSelectedFile().getAbsolutePath(), this.fcGnomAD.getSelectedFile().getAbsolutePath());
  }

  /**
   * Opens a JFileChooser to load a Genotype File
   */
  private void loadQCedAnnotatedGenotypes() {
    int returnVal = this.fcGenotypes.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File genotype = this.fcGenotypes.getSelectedFile();

      final ProgressDialog pd = new ProgressDialog(this, GUI.CW_LOADING_TITLE, GUI.CW_LOADING_NORTH, GUI.CW_LOADING_SOUTH);
      pd.setWorker(new SwingWorker<Object, Object>() {
        @Override
        protected Object doInBackground() throws Exception {
          client.setGenotypeFilename(genotype.getAbsolutePath());
          setQCCriteria(genotype);
          Thread.sleep(Parameters.LOAD_GENOTYPE_DELAY);
          pd.done();
          criteriaPane.setNbVariants(client.getGenotypeFileSize());
          return null;
        }
      });
    }
  }

  private void setQCCriteria(File genotypeFile){
    //client.logDebug("Trying to set "+genotypeFile);
    if(FileUtils.isQCedFile(genotypeFile)) {
      String filename = genotypeFile.getAbsolutePath();
      criteriaPane.setBedFileName("");
      String excluded = FileUtils.excludedVariantFromGnomADGenotypeFile(filename);
      //client.logDebug("Inferring excluded ["+excluded+"] from ["+filename+"]");
      criteriaPane.setExcludedVariantsFilename(excluded);
      String directory = FileUtils.getDirectory(genotypeFile);
      String paramFile = FileUtils.getBasenameIgnoreExt(FileUtils.getQCParamFromGenotypeFile(filename));
      File genoDirParamFile = new File(directory + File.separator + paramFile);
      if(genoDirParamFile.exists() && !genoDirParamFile.isDirectory())
        criteriaPane.setQCParam(genoDirParamFile.getAbsolutePath());
      else {
        File defaultDirParamFile = new File(MSG.GUI_DEFAULT_DIRECTORY + File.separator + paramFile);
        if(defaultDirParamFile.exists() && !defaultDirParamFile.isDirectory())
          criteriaPane.setQCParam(defaultDirParamFile.getAbsolutePath());
      }
    } else {
      client.logWarning(MSG.cat(MSG.CL_MISSING_QC_TAG, genotypeFile.getAbsolutePath()));
    }
  }

  /**
   * Opens a JFileChooser to load a Session File
   */
  private void loadSession() {
    int returnVal = this.fcSession.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File sessionFile = this.fcSession.getSelectedFile();
      this.client.loadSession(sessionFile.getAbsolutePath());
    }
    this.pack();
  }

  /**
   * Opens a JFileChooser to save the Session to a File
   */
  private void saveSession() {
    String last = this.client.getLastSessionFilename();
    if (last != null)
      this.fcSession.setSelectedFile(new File(last));
    int returnVal = fcSession.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File sessionFile = this.fcSession.getSelectedFile();
      String path = sessionFile.getAbsolutePath();
      if (!path.endsWith("." + FileFormat.FILE_SESSION_EXTENSION))
        path += "." + FileFormat.FILE_SESSION_EXTENSION;
      if (!this.client.saveSession(path))
        showAndLogError(GUI.CW_DG_MSG_KO_SAVE_SSS, GUI.CW_DG_KO_SAVE_SSS);
    }
  }

  void showTPSLog(){
    this.tpsLogWindow.display();
  }

  /**
   * <ol>
   * <li> Retrieves the Results from the RPP
   * <li> Opens a JFileChooser to save the Results to a File
   * <li> Shows the Results
   * </ol>
   */
  void getResults() {
    String drf = this.client.getLastSessionFilename();
    if (drf != null) {
      if (drf.endsWith("." + FileFormat.FILE_SESSION_EXTENSION))
        drf = drf.substring(0, drf.length() - (FileFormat.FILE_SESSION_EXTENSION.length() + 1));
      drf += "." + FileFormat.FILE_RESULTS_EXTENSION;
      this.fcResults.setSelectedFile(new File(drf));
    }
    int returnVal = this.fcResults.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File results = this.fcResults.getSelectedFile();
      String path = results.getAbsolutePath();
      if (!path.endsWith("." + FileFormat.FILE_RESULTS_EXTENSION))
        path += "." + FileFormat.FILE_RESULTS_EXTENSION;
      try {
        this.client.communicationAskResults(path);
        showResults(results);
      } catch (MessageException e) {
        showAndLogError(GUI.CW_DG_MSG_KO_SAVE_RES, GUI.CW_DG_KO_SAVE_RES, e);
      }
    }
  }

  /**
   * <ol>
   * <li> Opens a JFileChooser to loads a Results File
   * <li> Shows the Results
   * </ol>
   */
  private void loadResults() {
    int returnVal = this.fcResults.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File results = this.fcResults.getSelectedFile();
      showResults(results);
    }
  }

  /**
   * Opens a ResultsPane to show the Results (@see fr.inserm.u1078.tludwig.privas.gui.ResultsPane)
   *
   * @param results the Results File to Display
   */
  private void showResults(File results) {
    try {
      this.resultsPane.setResults(results);
      this.resultsPane.display();
    } catch (ParsingException e) {
      showAndLogError(MSG.cat(GUI.CW_DG_MSG_KO_LOAD_RES, results.getAbsolutePath()), GUI.CW_DG_KO_LOAD_RES, e);
    }
  }

  public void postTPStatus(TPStatus tpStatus){
    this.tpsLogWindow.add(tpStatus);
  }

  /**
   * Sends the Data to the RPP. As the transfer might take time, a ProgressDialog is opened
   */
  void sendData() {
    final ProgressDialog pd = new ProgressDialog(this, GUI.CW_EXTRACTING_TITLE, GUI.CW_EXTRACTING_NORTH, GUI.CW_EXTRACTING_SOUTH);
    pd.setWorker(new SwingWorker<Object, Object>() {
      @Override
      protected Object doInBackground() {
        //pd.setVisible(true);
        boolean ext = client.extractData(pd);
        pd.done();
        if (!ext) {
          showAndLogError(MSG.cat(GUI.CW_DG_MSG_KO_EXTRACT, client.getLastError()), GUI.CW_DG_KO_EXTRACT);
          return null;
        }
        //sending data
        doSendData();
        saveSession();
        return null;
      }
    });
  }

  /**
   * The actual sending of the Data
   */
  private void doSendData() {
    final ProgressDialog pd = new ProgressDialog(this, GUI.CW_SENDING_TITLE, GUI.CW_SENDING_NORTH, GUI.CW_SENDING_SOUTH);
    pd.setWorker(new SwingWorker<Object, Object>() {
      @Override
      protected Object doInBackground() {
        try {
          client.communicationSendData(pd);
          pd.done();
          showMessage(GUI.CW_DG_MSG_OK_SEND, GUI.CW_DG_OK_SEND);
        } catch (MessageException| IOException e) {
          pd.done();
          showAndLogError(MSG.cat(GUI.CW_DG_MSG_KO_SEND, client.getLastError()), GUI.CW_DG_KO_SEND, e);
        }
        return null;
      }
    });
  }

  /**
   * Gets the Client Instance associated to this Window
   *
   * @return the Client attached to this ClientWindow
   */
  public Client getClient() {
    return this.client;
  }

  FileExtensionChooser getBedFileDialog() { return this.fcBedFile; }

  FileExtensionChooser getQCParametersDialog() { return this.fcQCParam; }

  FileExtensionChooser getExclusionDialog() { return this.fcExclusion; }

  /**
   * Quits the Window and the Program
   */
  private void quit() {
    if ("".equals(this.client.getSessionId()))
      this.client.quit();
    if (this.client.isSessionSaved())
      this.client.quit();
    switch (JOptionPane.showConfirmDialog(this, GUI.CW_DG_MSG_QUIT, GUI.CW_DG_QUIT, JOptionPane.YES_NO_CANCEL_OPTION)) {
      case JOptionPane.YES_OPTION:
        this.saveSession();
      case JOptionPane.NO_OPTION:
        this.client.quit();
    }
  }

  /**
   * Tells the Client to start monitoring the RPP Status
   */
  void monitorRPP() {
    try {
      this.client.monitorRPP();
    } catch (MonitoringException ex) {
      //Ignore
    }
  }

  /**
   * Enables/Disables (Grays out) Menu Items according to the Session current State
   *
   * @param state the RPPStatus.State to consider when updating the menus ?
   */
  void updateMenuItems(RPPStatus.State state) {
    this.saveSession.setEnabled(true);
    this.loadResults.setEnabled(true);
    this.loadQCedVCFAndAnnotate.setEnabled(false);
    this.loadQCedAnnotatedGenotypes.setEnabled(false);
    this.getResults.setEnabled(false);

    switch (state) {
      case UNKNOWN:
        this.loadQCedVCFAndAnnotate.setEnabled(true);
        this.loadQCedAnnotatedGenotypes.setEnabled(true);
        break;
      case NO_SESSION:
        this.loadQCedVCFAndAnnotate.setEnabled(true);
        this.loadQCedAnnotatedGenotypes.setEnabled(true);
        this.saveSession.setEnabled(false);
        break;
      case RESULTS_AVAILABLE:
        this.getResults.setEnabled(true);
        break;
      case NEW_SESSION:
      case WAITING_BOTH:
      case WAITING_CLIENT:
      case WAITING_RPP:
      case TPS_SENDING:
      case TPS_PENDING:
      case TPS_RUNNING:
      case TPS_DONE:
      case EXPIRED:
    }
  }

  public void showAndLogError(String message, String title, Throwable e){
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    this.client.logError(e);
  }

  public void showAndLogError(String message, String title){
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public void showMessage(String message, String title){
    JOptionPane.showMessageDialog(this,message, title, JOptionPane.INFORMATION_MESSAGE);
  }
}

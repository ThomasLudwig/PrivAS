package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.instances.Client;
import fr.inserm.u1078.tludwig.privas.instances.RPPStatus;
import fr.inserm.u1078.tludwig.privas.gui.ResultsPane.ParsingException;
import fr.inserm.u1078.tludwig.privas.instances.MessageException;
import fr.inserm.u1078.tludwig.privas.instances.MonitoringException;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * The Main Window of the GUI (Client Application)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-04
 *
 * Javadoc Complete on 2019-08-09
 */
public class ClientWindow extends JFrame {

  private final JMenuItem loadVCF;
  private final JMenuItem loadGenotype;
  private final JMenuItem loadSession;
  private final JMenuItem saveSession;
  private final JMenuItem loadResults;
  private final JMenuItem quit;
  private final JMenuItem connect;
  private final JMenuItem startSession;
  private final JMenuItem getResults;

  private final LoggingPanel loggingPanel;
  private final ConnectionPane connectionPane;
  private final CriteriaPane criteriaPane;
  private final ResultsPane resultsPane;
  private final FileExtensionChooser fcVCF;
  private final FileExtensionChooser fcGenotypes;
  private final FileExtensionChooser fcPRIVG;
  private final FileExtensionChooser fcRESULTS;
  private final FileExtensionChooser fcBedFile;
  private final FileExtensionChooser fcExclusion;
  private final SessionPanel sessionPanel;
  private final Client client;

  /**
   * Constructor
   *
   * @param startDir the default directory to open when loading/saving files
   * @param client   the client
   * @throws Exception
   */
  public ClientWindow(String startDir, Client client) throws Exception {
    super();
    this.client = client;
    this.sessionPanel = new SessionPanel(this.client.getSession(), this);
    this.loggingPanel = new LoggingPanel();
    this.client.addLogListener(loggingPanel);
    this.loadVCF = new JMenuItem(GUI.CW_MI_LOAD_VCF);
    this.loadGenotype = new JMenuItem(GUI.CW_MI_LOAD_GENO);
    this.loadSession = new JMenuItem(GUI.CW_MI_LOAD_SSS);
    this.saveSession = new JMenuItem(GUI.CW_MI_SAVE_SSS);
    this.loadResults = new JMenuItem(GUI.CW_MI_LOAD_RES);
    this.quit = new JMenuItem(GUI.CW_MI_QUIT);
    this.connect = new JMenuItem(GUI.CW_MI_CONNECT);
    this.startSession = new JMenuItem(GUI.CW_MI_NEW_SSS);
    this.getResults = new JMenuItem(GUI.CW_MI_GET_RESULTS);
    this.connectionPane = new ConnectionPane();
    this.criteriaPane = new CriteriaPane(this);
    this.resultsPane = new ResultsPane(this);
    this.fcVCF = new FileExtensionChooser(FileFormat.FILE_VCF_EXTENSION, true, startDir);
    this.fcGenotypes = new FileExtensionChooser(FileFormat.FILE_GENO_EXTENSION, true, startDir);
    this.fcPRIVG = new FileExtensionChooser(FileFormat.FILE_SESSION_EXTENSION, false, startDir);
    this.fcRESULTS = new FileExtensionChooser(FileFormat.FILE_RESULTS_EXTENSION, false, startDir);
    this.fcBedFile = new FileExtensionChooser(FileFormat.FILE_BED_EXTENSION, true, startDir);
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
    file.add(this.loadVCF);
    file.add(this.loadGenotype);
    file.add(this.loadSession);
    file.add(this.saveSession);
    file.add(this.loadResults);
    file.add(this.quit);
    bar.add(file);
    rpp.add(this.connect);
    rpp.add(this.startSession);
    rpp.add(this.getResults);
    bar.add(rpp);

    this.loadVCF.addActionListener(e -> loadVCF());
    this.loadGenotype.addActionListener(e -> loadGenotypes());
    this.loadSession.addActionListener(e -> loadSession());
    this.saveSession.addActionListener(e -> saveSession());
    this.loadResults.addActionListener(e -> loadResults());
    this.quit.addActionListener(e -> quit());
    this.connect.addActionListener(e -> connect());
    this.startSession.addActionListener(e -> askSession());
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
        JOptionPane.showMessageDialog(this, GUI.CW_DG_OK_MSG_CONNECT(this.connectionPane.getAddress(), this.connectionPane.getPort()), GUI.CW_DG_OK_CONNECT, JOptionPane.INFORMATION_MESSAGE);
      else
        JOptionPane.showMessageDialog(this, GUI.CW_DG_KO_MSG_CONNECT(this.connectionPane.getAddress(), this.connectionPane.getPort()), GUI.CW_DG_KO_CONNECT, JOptionPane.ERROR_MESSAGE);
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
   * @return
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
      JOptionPane.showMessageDialog(this, GUI.CW_DG_MSG_NO_GENO, GUI.CW_DG_NO_GENO, JOptionPane.ERROR_MESSAGE);
      return;
    }

    while (!this.client.isConnected()) {
      int doConnect = JOptionPane.showConfirmDialog(this, GUI.CW_DG_MSG_NOT_CONNECTED, GUI.CW_DG_NOT_CONNECTED, JOptionPane.ERROR_MESSAGE);
      if (doConnect == JOptionPane.OK_OPTION)
        this.connect();
      else
        return;
    }

    int result = this.criteriaPane.display();
    if (result == JOptionPane.OK_OPTION)
      this.waitForSession();
  }

  ProgressDialog newSessionPD;

  public void waitForSession() {
    newSessionPD = new ProgressDialog(this, GUI.CW_CREATING_SESSION_TITLE, GUI.CW_CREATING_SESSION_NORTH, GUI.CW_CREATING_SESSION_SOUTH);
    newSessionPD.setWorker(new SwingWorker() {
      @Override
      protected Object doInBackground() {
        try {
          client.setAlgorithm(criteriaPane.getAlgorithm());
          client.communicationAskSession(criteriaPane.getDataset(), criteriaPane.getMAF(), criteriaPane.getMAFNFE(), criteriaPane.getCsq(), criteriaPane.getLimitToSNVs(), criteriaPane.getBedFileName(), criteriaPane.getExcludedVariantsFilename());
          newSessionPD.done();
          setTitle(GUI.CW_TITLE(client.getSessionId()));
          JOptionPane.showMessageDialog(ClientWindow.this, MSG.cat(GUI.CW_DG_NEW_SESSION, client.getSessionId()), GUI.CW_DG_OK_SESSION, JOptionPane.INFORMATION_MESSAGE);
          client.communicationStartSession(client.getSessionId());
        } catch (MessageException e) {
          newSessionPD.done();
          JOptionPane.showMessageDialog(ClientWindow.this, client.getLastError(), GUI.CW_DG_KO_SESSION, JOptionPane.ERROR_MESSAGE);
        }
        return null;
      }
    });
  }

  public void sessionOKKO() {
    newSessionPD.done();
  }

  /**
   * Opens a JFileChooser to load a VCF File
   */
  private void loadVCF() {
    int returnVal = this.fcVCF.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File vcfFile = this.fcVCF.getSelectedFile();
      int doConvert = JOptionPane.showConfirmDialog(this, GUI.CW_DG_MSG_CONVERT(vcfFile.getAbsolutePath()), GUI.CW_DG_CONVERT, JOptionPane.ERROR_MESSAGE);
      if (doConvert == JOptionPane.OK_OPTION) {
        final ProgressDialog pd = new ProgressDialog(this, GUI.CW_CONVERTING_TITLE, GUI.CW_CONVERTING_NORTH, GUI.CW_CONVERTING_SOUTH);
        pd.setWorker(new SwingWorker() {
          @Override
          protected Object doInBackground() {
            //dlg.setVisible(true);
            boolean success = client.convert(vcfFile.getAbsolutePath());
            pd.done();
            if (success)
              JOptionPane.showMessageDialog(ClientWindow.this, GUI.CW_DG_MSG_OK_CONVERT, GUI.CW_DG_OK_CONVERT, JOptionPane.INFORMATION_MESSAGE);
            else
              JOptionPane.showMessageDialog(ClientWindow.this, MSG.cat(GUI.CW_DG_MSG_KO_CONVERT, client.getLastError()), GUI.CW_DG_KO_CONVERT, JOptionPane.ERROR_MESSAGE);
            return null;
          }
        });
      }
    }
  }

  /**
   * Opens a JFileChooser to load a Genotype File
   */
  private void loadGenotypes() {
    int returnVal = this.fcGenotypes.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File Genotype = this.fcGenotypes.getSelectedFile();

      final ProgressDialog pd = new ProgressDialog(this, GUI.CW_LOADING_TITLE, GUI.CW_LOADING_NORTH, GUI.CW_LOADING_SOUTH);
      pd.setWorker(new SwingWorker() {
        @Override
        protected Object doInBackground() throws Exception {
          client.setGenotypeFilename(Genotype.getAbsolutePath());
          Thread.sleep(Parameters.LOAD_GENOTYPE_DELAY);
          pd.done();
          criteriaPane.setNbVariants(client.getGenotypeFileSize());
          return null;
        }
      });
    }
  }

  /**
   * Opens a JFileChooser to load a Session File
   */
  private void loadSession() {
    int returnVal = this.fcPRIVG.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File sessionFile = this.fcPRIVG.getSelectedFile();
      if (!this.client.loadSession(sessionFile.getAbsolutePath()))
        JOptionPane.showMessageDialog(this, GUI.CW_DG_KO_LOAD_SSS, GUI.CW_DG_MSG_KO_LOAD_SSS, JOptionPane.ERROR_MESSAGE);
    }
    this.pack();
  }

  /**
   * Opens a JFileChooser to save the Session to a File
   */
  private void saveSession() {
    String last = this.client.getLastSessionFilename();
    if (last != null)
      this.fcPRIVG.setSelectedFile(new File(last));
    int returnVal = fcPRIVG.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File sessionFile = this.fcPRIVG.getSelectedFile();
      String path = sessionFile.getAbsolutePath();
      if (!path.endsWith("." + FileFormat.FILE_SESSION_EXTENSION))
        path += "." + FileFormat.FILE_SESSION_EXTENSION;
      if (!this.client.saveSession(path))
        JOptionPane.showMessageDialog(this, GUI.CW_DG_MSG_KO_SAVE_SSS, GUI.CW_DG_KO_SAVE_SSS, JOptionPane.ERROR_MESSAGE);
    }
  }

  public void alertError(String title, String message){
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public void alertInfo(String title, String message){
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
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
      this.fcRESULTS.setSelectedFile(new File(drf));
    }
    int returnVal = this.fcRESULTS.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File results = this.fcRESULTS.getSelectedFile();
      String path = results.getAbsolutePath();
      if (!path.endsWith("." + FileFormat.FILE_RESULTS_EXTENSION))
        path += "." + FileFormat.FILE_RESULTS_EXTENSION;
      try {
        this.client.communicationAskResults(path);
        showResults(results);
      } catch (MessageException e) {
        JOptionPane.showMessageDialog(this, GUI.CW_DG_MSG_KO_SAVE_RES, GUI.CW_DG_KO_SAVE_RES, JOptionPane.ERROR_MESSAGE);
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
    int returnVal = this.fcRESULTS.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File results = this.fcRESULTS.getSelectedFile();
      showResults(results);
    }
  }

  /**
   * Opens a ResultsPane to show the Results (@see fr.inserm.u1078.tludwig.privas.gui.ResultsPane)
   *
   * @param results
   */
  private void showResults(File results) {
    try {
      this.resultsPane.setResults(results);
      this.resultsPane.display();
    } catch (ParsingException e) {
      JOptionPane.showMessageDialog(this, MSG.cat(GUI.CW_DG_MSG_KO_LOAD_RES, results.getAbsolutePath()), GUI.CW_DG_KO_LOAD_RES, JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Sends the Data to the RPP. As the transfert might take time, a ProgressDialog is opened
   */
  void sendData() {
    final ProgressDialog pd = new ProgressDialog(this, GUI.CW_EXTRACTING_TITLE, GUI.CW_EXTRACTING_NORTH, GUI.CW_EXTRACTING_SOUTH);
    pd.setWorker(new SwingWorker() {
      @Override
      protected Object doInBackground() {
        //pd.setVisible(true);
        boolean ext = client.extractData(pd);
        pd.done();

        if (!ext) {
          JOptionPane.showMessageDialog(ClientWindow.this, MSG.cat(GUI.CW_DG_MSG_KO_EXTRACT, client.getLastError()), GUI.CW_DG_KO_EXTRACT, JOptionPane.ERROR_MESSAGE);
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
    pd.setWorker(new SwingWorker() {
      @Override
      protected Object doInBackground() {
        try {
          client.communicationSendData(pd);
          pd.done();
          JOptionPane.showMessageDialog(ClientWindow.this, GUI.CW_DG_MSG_OK_SEND, GUI.CW_DG_OK_SEND, JOptionPane.INFORMATION_MESSAGE);
        } catch (MessageException e) {
          pd.done();
          JOptionPane.showMessageDialog(ClientWindow.this, MSG.cat(GUI.CW_DG_MSG_KO_SEND, client.getLastError()), GUI.CW_DG_KO_SEND, JOptionPane.ERROR_MESSAGE);
        }
        return null;
      }
    });
  }

  /**
   * Gets the Client Instance associated to this Window
   *
   * @return
   */
  public Client getClient() {
    return this.client;
  }

  FileExtensionChooser getBedFileDialog() {
    return this.fcBedFile;
  }

  FileExtensionChooser getExclusionDialog() {
    return this.fcExclusion;
  }

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
   * @param state
   */
  void updateMenuItems(RPPStatus.State state) {
    this.saveSession.setEnabled(true);
    this.loadResults.setEnabled(true);
    this.loadVCF.setEnabled(false);
    this.loadGenotype.setEnabled(false);
    this.getResults.setEnabled(false);

    switch (state) {
      case UNKNOWN:
        this.loadVCF.setEnabled(true);
        this.loadGenotype.setEnabled(true);
        break;
      case NO_SESSION:
        this.loadVCF.setEnabled(true);
        this.loadGenotype.setEnabled(true);
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
}

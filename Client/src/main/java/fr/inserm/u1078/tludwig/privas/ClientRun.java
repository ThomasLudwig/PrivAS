package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.documentation.ClientDocumentation;
import fr.inserm.u1078.tludwig.privas.gui.ClientWindow;
import fr.inserm.u1078.tludwig.privas.gui.LookAndFeel;
import fr.inserm.u1078.tludwig.privas.instances.Client;
import fr.inserm.u1078.tludwig.privas.listener.StandardErrorLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Executable Class for the Client Module
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-01
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class ClientRun {

  private ClientRun() {
    //This class may not be instantiated
  }

  /**
   * Parses the command line arguments and starts the appropriate command
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    //String devComputer = "DESKTOP-VRJB1QR";
    String dir = MSG.GUI_DEFAULT_DIRECTORY;
    String devDir = "C:\\Users\\user\\Documents\\Projet\\PrivGene\\privas";
    File file = new File(devDir);
    if(file.exists() && file.isDirectory())
      dir = devDir;

    System.err.println(MSG.MSG_WELCOME);

    if (args.length < 1) {
      launchGUI(dir);
      return;
    }

    if(args[0].startsWith("-")){
      switch (args[0].toLowerCase()) {
        case MSG.ARG_CONVERT_VCF:
          Main.convertVCF(args, true);
          break;
        case MSG.ARG_DOC:
          doc(args);
          break;
        case MSG.ARG_QC:
          Main.qc(args, true);
          break;
        case MSG.ARG_EXTRACT_HASH:
          Main.extractAndHash(args, false);
          break;
        default:
          usage();
      }
    } else {
      launchGUI(args[0]);
    }
  }

  public static void doc(String[] args) throws Exception {
    String outFile = "Client.rst";
    PrintWriter out = new PrintWriter(new FileWriter(outFile));
    out.println(ClientDocumentation.getClientDocumentation());
    out.close();
  }

  /**
   * Prints the various usages of this program
   */
  public static String getUsage() {
    char N = '\n';
    StringBuilder ret = new StringBuilder(MSG.MSG_USAGE).append(N);
    ret.append(usageGUI(false)).append(N);
    ret.append(MSG.MSG_QC).append(N);
    ret.append(Main.usageQualityControl(false, true)).append(N);
    ret.append(MSG.MSG_TOOLS).append(N);
    ret.append(Main.usageConvertVCF(false, true));
    ret.append(N);
    ret.append(Main.usageExtractAndHash(false, true));
    return ret.toString();
  }

  public static void usage(){
    System.err.println(getUsage());
  }

  /**
   * Prints the usage message for launching an instance of GUI client
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static String usageGUI(boolean prefix) {
    StringBuilder ret = new StringBuilder();
    if (prefix)
      ret.append(MSG.MSG_USAGE).append("\n");
    ret.append(MSG.MSG_DESC_GUI).append("\n");
    ret.append(MSG.MSG_CMD_GUI).append("\n");
    return ret.toString();
  }

  /**
   * Starts a GUI Client
   *
   * @param dir - the working directory (default directory the browser when loading/saving files)
   */
  private static void launchGUI(String dir) throws Exception {
    try {

      LookAndFeel.setup();
      Client client = new Client();
      ClientWindow clientWindow = new ClientWindow(dir, client);
      client.setWindow(clientWindow);
      clientWindow.getClient().addLogListener(new StandardErrorLogger());
    } catch (Exception e) {
      System.err.println(MSG.MSG_FAIL_GUI + e.getMessage());
      throw e;
    }
  }
}

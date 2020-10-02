package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.gui.ClientWindow;
import fr.inserm.u1078.tludwig.privas.gui.LookAndFeel;
import fr.inserm.u1078.tludwig.privas.instances.Client;
import fr.inserm.u1078.tludwig.privas.listener.StandardErrorLogger;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
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
   * Parses the command line arguments and starts the approriate command
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    System.err.println(MSG.MSG_WELCOME);

    if (args.length < 1) {
      launchGUI(MSG.GUI_DEFAULT_DIRECTORY);
      return;
    }

    if(args[0].startsWith("-")){
      switch (args[0].toLowerCase()) {
        case MSG.ARG_CONVERT_VCF:
          Main.convertVCF(args);
          break;
        case MSG.ARG_IDE:
          launchGUI("C:\\Users\\user\\Documents\\Projet\\PrivGene\\privas");
          break;
        default:
          usage();
      }
    } else {
      launchGUI(args[0]);
    }
  }

  /**
   * Prints the various usages of this program
   */
  private static void usage() {
    System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_INSTANCES);
    usageGUI(false);
  }

  /**
   * Prints the usage message for launching an instance of GUI client
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static void usageGUI(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_GUI);
    System.err.println(MSG.MSG_CMD_GUI);
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

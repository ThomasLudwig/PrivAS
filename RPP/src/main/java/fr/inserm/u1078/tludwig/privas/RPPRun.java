package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.instances.RPP;


/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-01
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class RPPRun {

  private RPPRun() {
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
      usage();
      return;
    }

    if(args[0].startsWith("-")){
      switch (args[0].toLowerCase()) {
        case MSG.ARG_CONVERT_VCF:
          Main.convertVCF(args);
          break;
        default:
          usage();
      }
    } else {
        launchRPPServer(args[0]);
    }
  }

  /**
   * Prints the various usages of this program
   */
  private static void usage() { //TODO limit to RPP operations
    System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_INSTANCES);
    usageRPP(false);
    System.err.println();
    System.err.println(MSG.MSG_TOOLS);
    Main.usageConvertVCF(false);
  }

  /**
   * Prints the usage message for launching an instance of RPP server
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static void usageRPP(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_RPP);
    System.err.println(MSG.MSG_CMD_RPP);
    if (prefix)
      System.err.println(MSG.MSG_EXTRA_RPP);
  }

  /**
   * Launching an instance of RPP server
   *
   * @param configFile - configuration file
   */
  private static void launchRPPServer(String configFile) throws Exception {
    try {
      System.err.println("Starting RPP");
      RPP rpp = new RPP(configFile);
      System.err.println("RPP started [" + rpp + "]");
    } catch (Exception ex) {
      System.err.println(MSG.MSG_FAIL_RPP + ex.getMessage());
      throw ex;
    }
  }
}

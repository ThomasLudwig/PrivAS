package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.documentation.RPPDocumentation;
import fr.inserm.u1078.tludwig.privas.instances.RPP;

import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * Executable class for RPP
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
   * Parses the command line arguments and starts the appropriate command
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
          Main.convertVCF(args, false);
          break;
        case MSG.ARG_DOC:
          doc(args);
          break;
        case MSG.ARG_QC:
          Main.qc(args, false);
          break;
        case MSG.ARG_QC_CONV:
          Main.qcAndConvert(args, false);
          break;
        case MSG.ARG_EXTRACT_HASH:
          Main.extractAndHash(args, false);
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
  public static String getUsage() {
    char N = '\n';
    StringBuilder ret = new StringBuilder(MSG.MSG_USAGE).append(N);
    ret.append(MSG.MSG_INSTANCES).append(N);
    ret.append(usageRPP(false)).append(N);
    ret.append(N);
    ret.append(MSG.MSG_QC).append(N);
    ret.append(Main.usageQualityControl(false, false)).append(N);
    ret.append(MSG.MSG_TOOLS).append(N);
    ret.append(N);
    ret.append(Main.usageConvertVCF(false, false));
    ret.append(N);
    ret.append(Main.usageExtractAndHash(false, false));
    return ret.toString();
  }

  public static void doc(String[] args) throws Exception {
    String outFile = "RPP.rst";
    PrintWriter out = new PrintWriter(new FileWriter(outFile));
    out.println(RPPDocumentation.getDocumentation());
    out.close();
  }

  private static void usage() {
    System.err.println(getUsage());
  }

  /**
   * Prints the usage message for launching an instance of RPP server
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static String usageRPP(boolean prefix) {
    StringBuilder ret = new StringBuilder();
    if (prefix)
      ret.append(MSG.MSG_USAGE).append("\n");
    ret.append(MSG.MSG_DESC_RPP).append("\n");
    ret.append(MSG.MSG_CMD_RPP).append("\n");
    if (prefix)
      ret.append(MSG.MSG_EXTRA_RPP);
    return ret.toString();
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

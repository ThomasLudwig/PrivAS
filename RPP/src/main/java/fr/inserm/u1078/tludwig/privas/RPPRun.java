package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.documentation.RPPDocumentation;
import fr.inserm.u1078.tludwig.privas.instances.RPP;

import java.io.FileWriter;
import java.io.IOException;
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
  public static void main(String[] args) {
    Main.CommandExecutor cmd = new Main.CommandExecutor(args, Main.Party.RPP);
    if (args.length < 1) {
      cmd.getInstance().logInfo(Main.getUsage(Main.Party.RPP));
      return;
    }

    if(args[0].startsWith("-")){
      switch (cmd.getCommand()) {
        case QC2GENOTYPES:
          Main.convertVCF(cmd);
          break;
        case DOC:
          doc(cmd);
          break;
        case VCF2QC:
          Main.qc(cmd);
          break;
        case VCF2GENOTYPES:
          Main.qcAndConvert(cmd);
          break;
        case EXTRACT_HASH:
          Main.extractAndHash(cmd);
          break;
        case GNOMAD:
          Main.extractGnomAD(cmd);
          break;
        default:
          cmd.getInstance().logError(MSG.cat(MSG.UNKNOWN_COMMAND, args[0]));
          cmd.getInstance().logInfo(Main.getUsage(Main.Party.RPP));
      }
    } else {
        launchRPPServer(cmd);
    }
  }

  public static void doc(Main.CommandExecutor cmd) {
    try{
      PrintWriter out = new PrintWriter(new FileWriter(FileFormat.FILE_RPP_DOC));
      out.println(RPPDocumentation.getDocumentation());
      out.close();
    } catch(IOException | RuntimeException e) {
      cmd.fail(e);
    }
  }

  /**
   * Launching an instance of RPP server

   */
  private static void launchRPPServer(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String configFile = args[1];
      cmd.getInstance().logInfo(MSG.RPP_STARTING);
      RPP rpp = new RPP(configFile);
      cmd.getInstance().logInfo(MSG.cat(MSG.RPP_STARTED, rpp.toString()));
    } catch (Exception e) {
      cmd.fail(e);
    }
  }
}

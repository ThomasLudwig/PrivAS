package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.documentation.ClientDocumentation;
import fr.inserm.u1078.tludwig.privas.gui.ClientWindow;
import fr.inserm.u1078.tludwig.privas.gui.LookAndFeel;
import fr.inserm.u1078.tludwig.privas.instances.Client;
import fr.inserm.u1078.tludwig.privas.listener.StandardErrorLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
  public static void main(String[] args) {
    String dir = MSG.GUI_DEFAULT_DIRECTORY;
    String devDir = "C:\\Users\\user\\Documents\\Projet\\PrivGene\\privas\\moyamoya";
    File file = new File(devDir);
    if(file.exists() && file.isDirectory())
      dir = devDir;

    String[] newArgs = args.length < 1 ? new String[]{dir} : args;
    Main.CommandExecutor cmd = new Main.CommandExecutor(newArgs, Main.Party.CLIENT);

    if(newArgs[0].startsWith("-")){
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
        case EXTRACT_HASH:
          Main.extractAndHash(cmd);
          break;
        case GNOMAD:
          Main.extractGnomAD(cmd);
          break;
        default:
          cmd.getInstance().logError(MSG.cat(MSG.UNKNOWN_COMMAND, args[0]));
          cmd.getInstance().logInfo(Main.getUsage(Main.Party.CLIENT));
      }
    } else {
      launchGUI(cmd);
    }
  }

  public static void doc(Main.CommandExecutor cmd) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(FileFormat.FILE_CLIENT_DOC));
      out.println(ClientDocumentation.getClientDocumentation());
      out.close();
    } catch(IOException | RuntimeException e) {
      cmd.fail(e);
    }
  }

  /**
   * Starts a GUI Client
   */
  private static void launchGUI(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String dir = args[1];
      LookAndFeel.setup();
      Client client = new Client();
      ClientWindow clientWindow = new ClientWindow(dir, client);
      client.setWindow(clientWindow);
      clientWindow.getClient().addLogListener(new StandardErrorLogger());
    } catch (RuntimeException e) {
      cmd.fail(e);
    }
  }
}

package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.algorithms.Utils;
import fr.inserm.u1078.tludwig.privas.algorithms.wss.WSS;
import fr.inserm.u1078.tludwig.privas.algorithms.wss.WSSHandler;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.documentation.TPSDocumentation;
import fr.inserm.u1078.tludwig.privas.instances.ThirdPartyServer;
import fr.inserm.u1078.tludwig.privas.listener.StandardErrorLogger;

import java.io.*;
import java.util.Random;

/**
 * Executable class for TPS
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-01
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class TPSRun {
  private TPSRun() {
    //This class may not be instantiated
  }

  /**
   * Parses the command line arguments and starts the appropriate command
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Main.CommandExecutor cmd = new Main.CommandExecutor(args, Main.Party.TPS);
    if (args.length < 1) {
      cmd.getInstance().logInfo(Main.getUsage(Main.Party.TPS));
      return;
    }

    if(args[0].startsWith("-")) {
      switch (cmd.getCommand()) {
        case DEBUG:
          debugTPServer(cmd);
          break;
        case DOC:
          doc(cmd);
          break;
        case KEYGEN:
          generateTPSKeyPair(cmd);
          break;
        case WSS:
          computeWSS(cmd);
          break;
        case RANKSUM:
          computeWSSRanksum(cmd);
          break;
        default:
          cmd.getInstance().logError(MSG.cat(MSG.UNKNOWN_COMMAND, args[0]));
          cmd.getInstance().logInfo(Main.getUsage(Main.Party.TPS));
      }
    } else {
      launchTPServer(cmd);
    }
  }

  public static void doc(Main.CommandExecutor cmd) {
    try {
      String outFile = FileFormat.FILE_TPS_DOC;
      PrintWriter out = new PrintWriter(new FileWriter(outFile));
      out.println(TPSDocumentation.getDocumentation());
      out.close();
    } catch(IOException | RuntimeException e) {
      cmd.fail(e);
    }
  }

  /**
   * Launching an instance of TPS Server
   */
  private static void launchTPServer(Main.CommandExecutor cmd) {
    String sessionId;
    String workingDir;
    int nbCore;
    long randomSeed;
    String[] args = cmd.getArgs();
    try {
      sessionId = args[1];
      workingDir = args[2];
      nbCore = new Integer(args[3]);
      String seed = args[4];
      if (MSG.TPS_RANDOM.equals(seed.toLowerCase()))
        randomSeed = (new Random()).nextLong();
      else
        randomSeed = new Long(seed);
    } catch (RuntimeException e) {
      cmd.fail(e);
      return;
    }
    ThirdPartyServer tps = new ThirdPartyServer(sessionId, workingDir, randomSeed);
    tps.addLogListener(new StandardErrorLogger());
    try {
      tps.start(nbCore);
    } catch (RuntimeException | IOException e) {
      tps.statusError(cmd.getCommand().getFail() + e.getMessage());
      cmd.fail(e);
    }
  }

  private static void debugTPServer(Main.CommandExecutor cmd) {
    String sessionId;
    String workingDir;
    int nbCore;
    long randomSeed;
    String[] args = cmd.getArgs();
    try {
      sessionId = args[1];
      workingDir = args[2];
      nbCore = new Integer(args[3]);
      String seed = args[4];
      if (MSG.TPS_RANDOM.equalsIgnoreCase(seed))
        randomSeed = (new Random()).nextLong();
      else
        randomSeed = new Long(seed);
    } catch (RuntimeException e) {
      cmd.fail(e);
      return;
    }
    ThirdPartyServer tps = new ThirdPartyServer(sessionId, workingDir, randomSeed);
    tps.addLogListener(new StandardErrorLogger());
    try {
      tps.debug(nbCore);
    } catch (RuntimeException |IOException e) {
      cmd.fail(e);
    }
  }

  /**
   * Generates an RSA key pair,
   * Displayed the public key (to be read by the calling RPP)
   * Stores the private key in a file
   * The Private key will be deleted the first time it is used, ensuring that it is not reusable
   */
  private static void generateTPSKeyPair(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String dir = args[1];
      String session = args[2];
      ThirdPartyServer.generateKeyPair(dir, session, cmd.getInstance());
    } catch (IOException | RuntimeException e) {
      cmd.fail(e);
    }
  }

  /**
   * Compute a WSS association test (locally)
   */
  private static void computeWSS(Main.CommandExecutor cmd) {
    String resultFile;
    int nbThreads;
    long randomSeed;
    String genotypeListFilename;
    String phenotypeFilename;
    String[] args = cmd.getArgs();
    try {
      genotypeListFilename = args[1];
      phenotypeFilename = args[2];
      nbThreads = new Integer(args[3]);
      randomSeed = new Long(args[4]);
      resultFile = args[5];
      try {
        WSSHandler wss = new WSSHandler(nbThreads, randomSeed, cmd.getInstance());
        FileOutputStream out = new FileOutputStream(resultFile);
        byte[] results = wss.start(genotypeListFilename, phenotypeFilename);
        out.write(results);
        out.close();
      } catch (Exception e1) {
        cmd.fail(e1);
      }
    } catch (RuntimeException e) {
      cmd.fail(e);
    }
  }

  private static void computeWSSRanksum(Main.CommandExecutor cmd){
    String[] args = cmd.getArgs();
    try {
      String gene = args[1];
      String genotypeFile = args[2];
      boolean[] phenotypes = Utils.parsePhenotypes(args[3]);

      int affected = 0;
      for (boolean phenotype : phenotypes)
        if (phenotype)
          affected++;
      int unaffected = phenotypes.length - affected;

      WSS wss = new WSS(gene, phenotypes, genotypeFile, cmd.getInstance());
      cmd.getInstance().logDebug(gene+"\tphenotype="+Utils.getTextPhenotype(phenotypes)+"\taffected="+affected+"\tunaffected="+unaffected+"\tranksum="+wss.start(phenotypes)+"\toriginal="+wss.testUnoptimized(phenotypes));
    } catch (RuntimeException | IOException e) {
      cmd.fail(e);
    }
  }
}

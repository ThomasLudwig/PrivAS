package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.algorithms.Utils;
import fr.inserm.u1078.tludwig.privas.algorithms.wss.WSS;
import fr.inserm.u1078.tludwig.privas.algorithms.wss.WSSHandler;
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
  public static void main(String[] args) throws Exception {
    System.err.println(MSG.MSG_WELCOME);
    if (args.length < 1) {
      usage();
      return;
    }

    if(args[0].startsWith("-")) {
      switch (args[0].toLowerCase()) {
        case MSG.ARG_DEBUG:
          debugTPServer(args);
          break;
        case MSG.ARG_DOC:
          doc(args);
          break;
        case MSG.ARG_KEYGEN:
          generateTPSKeyPair(args);
          break;
        case MSG.ARG_WSSKEY:
          computeWSS(args);
          break;
        case MSG.ARG_RANKSUMKEY:
          computeWSSRanksum(args);
          break;
        default:
          usage();
      }
    } else {
      launchTPServer(args);
    }
  }

  /**
   * Prints the various usages of this program
   */
  private static void usage() {
    System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_INSTANCES);
    usageTPS(false);
    System.err.println();
    System.err.println(MSG.MSG_TOOLS);
    usageKeygen(false);
    usageComputeWSS(false);
    usageComputeWSSRanksum(false);
  }

  public static void doc(String[] args) throws Exception {
    String outFile = "TPS.rst";
    PrintWriter out = new PrintWriter(new FileWriter(outFile));
    out.println(TPSDocumentation.getDocumentation());
    out.close();
  }

  /**
   * Prints the usage message for launching an instance of TPS server
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static void usageTPS(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_TPS);
    System.err.println(MSG.MSG_CMD_TPS);
  }

  /**
   * Launching an instance of TPS Server
   *
   * @param args - the command line arguments
   */
  private static void launchTPServer(String[] args) throws Exception {
    String sessionId;
    String workingDir;
    int nbCore;
    long randomSeed;
    try {
      sessionId = args[0];
      workingDir = args[1];
      nbCore = new Integer(args[2]);
      String seed = args[3];
      if (MSG.TPS_RANDOM.equals(seed.toLowerCase()))
        randomSeed = (new Random()).nextLong();
      else
        randomSeed = new Long(seed);
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
      usageTPS(true);
      return;
    }
    ThirdPartyServer tps = new ThirdPartyServer(sessionId, workingDir, randomSeed);
    tps.addLogListener(new StandardErrorLogger());
    try {
      tps.start(nbCore);
    } catch (Exception e) {
      try {
        tps.statusError(MSG.MSG_FAIL_TPS + e.getMessage());
      } catch (IOException ex) {
        //Ignore
      }
      System.err.println(MSG.MSG_FAIL_TPS + e.getMessage());
      throw e;
    }
  }

  private static void debugTPServer(String[] args) throws Exception {
    String sessionId;
    String workingDir;
    int nbCore;
    long randomSeed;
    try {
      sessionId = args[1];
      workingDir = args[2];
      nbCore = new Integer(args[3]);
      String seed = args[4];
      if (MSG.TPS_RANDOM.equals(seed.toLowerCase()))
        randomSeed = (new Random()).nextLong();
      else
        randomSeed = new Long(seed);
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
      usageTPS(true);
      return;
    }
    ThirdPartyServer tps = new ThirdPartyServer(sessionId, workingDir, randomSeed);
    tps.addLogListener(new StandardErrorLogger());
    try {
      tps.debug(nbCore);
    } catch (Exception e) {
      try {
        tps.statusError(MSG.MSG_FAIL_TPS + e.getMessage());
      } catch (IOException ex) {
        //Ignore
      }
      System.err.println(MSG.MSG_FAIL_TPS + e.getMessage());
      throw e;
    }
  }

  /**
   * Generates an RSA key pair,
   * Displayed the public key (to be read by the calling RPP)
   * Stores the private key in a file
   * The Private key will be deleted the first time it is used, ensuring that it is not reusable
   */
  private static void generateTPSKeyPair(String[] args) {
    try {
      String dir = args[1];
      String session = args[2];
      ThirdPartyServer.generateKeyPair(dir, session);
    } catch (ArrayIndexOutOfBoundsException e) {
      usageKeygen(true);
    }
  }

  /**
   * Prints the usage message for generating an RSA keypair
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static void usageKeygen(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_KEYGEN);
    System.err.println(MSG.MSG_CMD_KEYGEN);
  }

  /**
   * Prints the usage message for compute a WSS association test
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  private static void usageComputeWSS(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_WSS);
    System.err.println(MSG.MSG_CMD_WSS);
  }

  private static void usageComputeWSSRanksum(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_RANKSUM);
    System.err.println(MSG.MSG_CMD_RANKSUM);
  }

  /**
   * Compute a WSS association test (locally)
   *
   * @param args
   */
  private static void computeWSS(String[] args) {
    String resultFile;
    int nbThreads;
    long randomSeed;
    String genotypeListFilename;
    String phenotypeFilename;
    try {
      genotypeListFilename = args[1];
      phenotypeFilename = args[2];
      nbThreads = new Integer(args[3]);
      randomSeed = new Long(args[4]);
      resultFile = args[5];
      try {
        WSSHandler wss = new WSSHandler(nbThreads, randomSeed);
        FileOutputStream out = new FileOutputStream(resultFile);
        byte[] results = wss.start(genotypeListFilename, phenotypeFilename);
        out.write(results);
        out.close();
      } catch (Exception e1) {
        System.err.println(MSG.MSG_FAIL_WSS + e1.getMessage());
        e1.printStackTrace();
      }
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
      usageComputeWSS(true);
      e.printStackTrace();
    }
  }

  private static void computeWSSRanksum(String[] args){
    try {
      String gene = args[1];
      String genotypeFile = args[2];
      boolean[] phenotypes = Utils.parsePhenotypes(args[3]);

      int affected = 0;
      for(int i = 0 ; i < phenotypes.length; i++)
        if(phenotypes[i])
          affected++;
      int unaffected = phenotypes.length - affected;

      WSS wss = new WSS(gene, phenotypes, genotypeFile);
      System.out.println(gene+"\tpheno="+Utils.getTextPhenotype(phenotypes)+"\taff="+affected+"\tunaff="+unaffected+"\tranksum="+wss.start(phenotypes)+"\toriginal="+wss.testUnoptimized(phenotypes));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}

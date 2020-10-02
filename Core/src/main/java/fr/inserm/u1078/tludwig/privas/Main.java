package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler;

import java.io.IOException;

/**
 * Main Class, entry point of the program. Only static fields and methods
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-23
 */
public final class Main {
  
  public static final boolean DEBUG = true;
  
  //TODO  propose Datasets in GRCh37 and GRCh38
  //DONE  results must be scrambled : they are written as soon as the p-value is available
  //DONE  TEST : Client OK + TPS Running/Done + RPP Restarted
  //DONE  TEST : Client Running, Client Ask new session 
  //DONE  RPP : thread removing old sessions (every hour), place tag expired
  //REJC  replace files.ok by in memory flags | in case of rpp crash, memory is lost
  //DONE  Pong message has a list of reference data to choose from, that will be used in the criteria panel and the Session object
  //DONE  Message are generating a lot of errors since switching to subclasses
  //REJC  Server AskTPS delay should be dynamic depending on ETA with a ceil and floor

  private Main() {
    //This class may not be instantiated
  }

  /**
   * Prints the usage message for preparing RPP data files
   *
   * @param prefix - should print the "Usage :" prefix ?
   */
  public static void usageConvertVCF(boolean prefix) {
    if (prefix)
      System.err.println(MSG.MSG_USAGE);
    System.err.println(MSG.MSG_DESC_PREPARE);
    System.err.println(MSG.MSG_CMD_PREPARE);
  }

  /**
   * Prepares an RPP data file by converting from a vep annotated VCF file
   *
   * @param args
   */
  public static void convertVCF(String[] args) {
    try {
      String vcfFile = args[1];
      String output = GenotypesFileHandler.vcfFilename2GenotypesFilename(vcfFile);
      GenotypesFileHandler.convertVCF2Genotypes(vcfFile, output);
    } catch (GenotypesFileHandler.GenotypeFileException | IOException e) {
      System.err.println(MSG.MSG_FAIL_PREPARE + e.getMessage());
      usageConvertVCF(true);
    }
  }
}

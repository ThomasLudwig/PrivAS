package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.FileUtils;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCException;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;
import fr.inserm.u1078.tludwig.privas.utils.qc.QualityControl;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

  public static String getVersion(){
    try {
      String line;
      InputStream is = Main.class.getResourceAsStream("/CHANGELOG.md");
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      while ((line = in.readLine()) != null) {
        if (line.toLowerCase().startsWith("## "))
          return line.substring(3);
      }
    } catch (IOException e) {
      //Ignore
    }
    return "v?.?.?(????-??-??)";
  }

  /**
   * Generic method to get usage String
   * @param prefix should print the "Usage :" prefix ?
   * @param client who is calling ? true for the Client, false for RPP
   * @param desc the description for this command
   * @param cmdClient the client command line for this command
   * @param cmdRpp the rpp command line for this command
   * @return the complete usage string
   */
  private static String usageGeneric(boolean prefix, boolean client, String desc, String cmdClient, String cmdRpp){
    StringBuilder ret = new StringBuilder();
    if (prefix)
      ret.append(MSG.MSG_USAGE).append("\n");
    ret.append(desc).append("\n");
    ret.append(client ? cmdClient : cmdRpp);
    return ret.toString();
  }

  /**
   * Prints the usage message for preparing RPP data files
   *
   * @param prefix should print the "Usage :" prefix ?
   *
   */
  public static String usageConvertVCF(boolean prefix, boolean client) {
    return usageGeneric(prefix, client, MSG.MSG_DESC_PREPARE, MSG.MSG_CMD_PREPARE_CLIENT, MSG.MSG_CMD_PREPARE_RPP);
  }

  /**
   * Prints the usage message for performing Quality Control
   *
   * @param prefix should print the "Usage :" prefix ?
   */
  public static String usageQualityControl(boolean prefix, boolean client) {
    return usageGeneric(prefix, client, MSG.MSG_DESC_QC, MSG.MSG_CMD_QC_CLIENT, MSG.MSG_CMD_QC_RPP);
  }

  public static String usageQualityControlAndConvert(boolean prefix, boolean client) {
    return usageGeneric(prefix, client, MSG.MSG_DESC_QC_CONV, MSG.MSG_CMD_QC_CONV_CLIENT, MSG.MSG_CMD_QC_CONV_RPP);
  }

  public static String usageExtractAndHash(boolean prefix, boolean client) {
    return usageGeneric(prefix, client, MSG.MSG_DESC_EXTRACT_HASH, MSG.MSG_CMD_EXTRACT_HASH_CLIENT, MSG.MSG_CMD_EXTRACT_HASH_RPP);
  }

  /**
   * Prepares an RPP data file by converting from a vep annotated VCF file
   *
   * @param args
   */
  public static void convertVCF(String[] args, boolean client) {
    try {
      String vcfFile = args[1];
      String output = GenotypesFileHandler.vcfFilename2GenotypesFilename(vcfFile);
      GenotypesFileHandler.convertVCF2Genotypes(vcfFile, output);
    } catch (GenotypesFileHandler.GenotypeFileException | IOException e) {
      System.err.println(MSG.MSG_FAIL_PREPARE + e.getMessage());
      usageConvertVCF(true, client);
    }
  }

  public static void qc(String[] args, boolean client){
    try {
      String inVCF = args[1];
      String qcParamFile = args[2];
      QCParam qcParam = new QCParam(qcParamFile);
      QualityControl.applyQC(inVCF, qcParam);
    } catch (QCException | IOException | ArrayIndexOutOfBoundsException e) {
      System.err.println(MSG.MSG_FAIL_QC + e.getClass() + " " + e.getMessage());
      usageQualityControl(true, client);
    }
  }

  public static void qcAndConvert(String[] args, boolean client) {
    QCParam qcParam = new QCParam();
    String inVCF;
    try{
      inVCF = args[1];
      QualityControl.applyQC(inVCF, qcParam);
    } catch(Exception e) {
      System.err.println(MSG.MSG_FAIL_QC + e.getClass() + " " + e.getMessage());
      usageQualityControlAndConvert(true, client);
      return;
    }
    String outVCF = FileUtils.getQCVCFFilename(inVCF, qcParam);
    String genotypeFilename = FileUtils.getQCGenotypeFilename(inVCF, qcParam);
    try {
      GenotypesFileHandler.convertVCF2Genotypes(outVCF, genotypeFilename);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (GenotypesFileHandler.GenotypeFileException e) {
      System.err.println(MSG.MSG_FAIL_PREPARE + e.getMessage());
      usageQualityControlAndConvert(true, client);
    }
    File file = new File(outVCF);
    file.delete();
  }

  public static void extractAndHash(String[] args, boolean client){
    try {
      String inVCF = args[1];
      String outVCFFilename = args[2];
      String outGeneFilename = args[3];
      String hashingKey = args[4];
      GenotypesFileHandler.extractCanonicalAndHash(inVCF, outVCFFilename, outGeneFilename, hashingKey);
    } catch (IOException | ArrayIndexOutOfBoundsException | InvalidKeyException | NoSuchAlgorithmException | GenotypesFileHandler.GenotypeFileException e) {
      System.err.println(MSG.MSG_FAIL_EXTRACT_HASH + e.getClass() + " " + e.getMessage());
      usageQualityControl(true, client);
    }
  }
}

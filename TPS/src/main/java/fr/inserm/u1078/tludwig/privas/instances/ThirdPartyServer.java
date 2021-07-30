package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import fr.inserm.u1078.tludwig.privas.utils.Crypto;
import fr.inserm.u1078.tludwig.privas.algorithms.wss.WSSHandler;
import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.FisherExactTest;
import fr.inserm.u1078.tludwig.privas.utils.VariantExclusionSet;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * The ThirdPartyServer is the instance of the program that will be launched on the Third Party Server
 * <p>
 * Its purpose is to perform the computation of the selected algorithm with the selected parameters
 * <p>
 * To do so, it has to:
 * <p><ol>
 * <li>decrypt some of the data
 * <li>Merge data from the RPP and the Client dataset
 * <li>Perform the computation
 * <li>encrypt the results
 * </ol><p>
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-03-29
 *
 * Javadoc complete on 2019-08-06
 */
public class ThirdPartyServer extends Instance {

  public static final int IDX_VARIANT = 0;
  public static final int IDX_GENE = 1;
  public static final int IDX_GENOTYPE = 2;

  /**
   * The directory containing the session's file
   */
  private final String sessionDir;  
  /**
   * The name of the file containing the private RSA key
   */
  private final String privateKeyFilename;
  /**
   * The name of the file containing the AES key
   */
  private final String aesFilename;
  /**
   * The name of the file containing the algorithm and its parameters
   */
  private final String algorithmFilename;
  /**
   * The name of the file containing the aes encrypted and hashed client data
   */
  private final String clientFilename;
  /**
   * The name of the file containing the hashed RPP data
   */
  private final String rppFilename;
  /**
   * The name of the file containing the variants excluded by the client
   */
  private final String clientExcludedVariantsFilename;
  /**
   * The name of the file containing the variants excluded by RPP
   */
  private final String rppExcludedVariantsFilename;
  /**
   * The name of the file where the AES encrypted results will be written
   */
  private final String resultFilename;
  /**
   * The name of the file where the status of this TPS will be written
   */
  private final String statusFilename;
  /**
   * The AES encryption Key
   */
  private String aesKey;
  /**
   * Number of affected individuals
   */
  private int nbAffected = 0;
  /**
   * Number of unaffected individuals
   */
  private int nbUnaffected = 0;
  /**
   * timestamp of the start of the computation
   */
  private long started;
  /**
   * The random seed
   */
  private final long randomSeed;
  
  private int failedFisher = 0;

  /**
   * Constructs an instance of ThirdPartyServer
   *
   * @param directory  the path to the directory containing the sessions
   * @param session    the ID of the session
   * @param randomSeed the random seed
   */
  public ThirdPartyServer(String session, String directory, long randomSeed) {
    this.sessionDir = directory + File.separator + FileFormat.DIRECTORY_SESSIONS + File.separator + session;
    this.aesFilename = sessionDir + File.separator + FileFormat.FILE_AES_KEY;
    this.privateKeyFilename = sessionDir + File.separator + FileFormat.FILE_PRIVATE_RSA_KEY;
    this.algorithmFilename = sessionDir + File.separator + FileFormat.FILE_ALGORITHM;
    this.clientFilename = sessionDir + File.separator + FileFormat.FILE_ENCRYPTED_CLIENT_DATA;
    this.rppFilename = sessionDir + File.separator + FileFormat.FILE_RPP_DATA;
    this.clientExcludedVariantsFilename = sessionDir + File.separator + FileFormat.FILE_ENCRYPTED_CLIENT_EXCLUDED_VARIANTS;
    this.rppExcludedVariantsFilename = sessionDir + File.separator + FileFormat.FILE_RPP_EXCLUDED_VARIANTS;
    this.resultFilename = sessionDir + File.separator + FileFormat.FILE_RESULTS;
    String tmpDir = directory + File.separator + FileFormat.DIRECTORY_TMP;
    File dir = new File(tmpDir);
    if (!dir.exists() && !dir.mkdirs())
      System.err.println(MSG.cat(MSG.FAIL_MKDIR, dir.getAbsolutePath()));
    this.statusFilename = sessionDir + File.separator + FileFormat.FILE_TPS_STATUS;
    this.randomSeed = randomSeed;
  }

  /**
   * Generates and stores a keypair for the current session
   * The public key will be displayed, so as to be retrieved by the calling RPP (through ssh -t)
   * The file containing the private key will be deleted the first time it is accessed, the the key will not be reusable
   *
   * @param directory the path to the directory containing the sessions
   * @param session   the ID of the session
   */
  public static void generateKeyPair(String directory, String session) {
    try {
      KeyPair kp = Crypto.generateRSAKeyPair();
      String sessionDir = directory + File.separator + session;
      File file = new File(sessionDir);
      if (!file.exists() && !file.mkdirs())
        throw new IOException(MSG.cat(MSG.FAIL_MKDIR, sessionDir));
      String privateFile = sessionDir + File.separator + FileFormat.FILE_PRIVATE_RSA_KEY;
      String publicFile = sessionDir + File.separator + FileFormat.FILE_PUBLIC_RSA_KEY;
      Crypto.savePrivateRSAKey(kp, privateFile);
      System.err.println("Saving private [" + privateFile + "]");
      Crypto.savePublicRSAKey(kp, publicFile);
      System.err.println("Saving public [" + publicFile + "]");
      System.out.println(Crypto.getPublicRSAKeyAsString(kp));
    } catch (IOException | NoSuchAlgorithmException e) {
      System.err.println(MSG.cat(MSG.MSG_FAIL_KEYGEN, e));
    }
  }

  /**
   * Starts the computation
   * The selected algorithm will be performed with its provided parameters
   *
   * @param nbThreads the maximum number of cores to use
   * @throws Exception
   */
  public void start(int nbThreads) throws Exception { //DONE trycatch all errors coming from the algorithm and propagate through TPStatus.ERROR
    this.started = new Date().getTime();
    this.statusStarted(MSG.TPS_STARTED, false);
    PrivateKey privateKey = Crypto.readAndDeletePrivateRSAKey(privateKeyFilename);
    this.statusStarted(MSG.TPS_RSA, false);
    UniversalReader in = new UniversalReader(aesFilename);
    aesKey = Crypto.decryptRSA(privateKey, in.readLine());
    this.statusStarted(MSG.TPS_AES, false);
    in.close();
    in = new UniversalReader(algorithmFilename);
    String algorithm = in.readLine();
    this.statusStarted(MSG.cat(MSG.TPS_ALGO, algorithm), false);
    in.close();

    String[] algo = algorithm.split(":");

    switch (algo[0].toLowerCase()) {
      case Constants.ALGO_WSS:
        long permutation = -1;
        double frqThreshold = 1;
        try {
          permutation = new Long(algo[1]);
          frqThreshold = new Double(algo[2]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
          //Nothing
        }
        if (permutation != -1) {
          HashMap<String, ArrayList<String>> genotypes = prepareWSSData(frqThreshold);
          WSSHandler wssHandler = new WSSHandler(permutation, nbThreads, randomSeed);
          wssHandler.setThirdPartyServer(this);
          if (genotypes == null) {
            System.err.println(MSG.WSS_NO_COMMON_GENE);
            //this.done();
            return;
          }

          byte[] resultsArray = wssHandler.start(genotypes, nbAffected, nbUnaffected);
          StringBuilder results = new StringBuilder();
          BufferedReader in2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resultsArray)));
          in2.readLine(); //Skip header
          String line;
          while ((line = in2.readLine()) != null) 
            results.append("\n").append(line);          
          in2.close();
          
          PrintWriter out = new PrintWriter(new FileWriter(this.resultFilename));
          out.print(Crypto.encryptAES(aesKey, results.substring(1)));
          out.flush();
          out.close();
        }
        break;

      default:
        this.statusError("Unexpected Algorithm ["+algo[0]+"]");
        break;
    }
    this.statusDone();
  }
  
  public void debug(int nbThreads) throws Exception {
    this.started = new Date().getTime();
    this.statusStarted(MSG.TPS_STARTED, false);
    this.statusStarted(MSG.TPS_RSA, false);
    UniversalReader in = new UniversalReader(this.sessionDir + File.separator + "aes.key");
    aesKey = in.readLine();
    this.statusStarted(MSG.TPS_AES, false);
    in.close();
    in = new UniversalReader(algorithmFilename);
    String algorithm = in.readLine();
    this.statusStarted(MSG.cat(MSG.TPS_ALGO, algorithm), false);
    in.close();

    String[] algo = algorithm.split(":");

    switch (algo[0].toLowerCase()) {
      case Constants.ALGO_WSS:
        long permutation = -1;
        double frqThreshold = 1;
        try {
          permutation = new Long(algo[1]);
          frqThreshold = new Double(algo[2]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
          //Nothing
        }
        if (permutation != -1) {
          HashMap<String, ArrayList<String>> genotypes = prepareWSSData(frqThreshold);
          WSSHandler wssHandler = new WSSHandler(permutation, nbThreads, randomSeed);
          wssHandler.setThirdPartyServer(this);
          if (genotypes == null) {
            System.err.println(MSG.WSS_NO_COMMON_GENE);
            //this.done();
            return;
          }

          byte[] resultsArray = wssHandler.start(genotypes, nbAffected, nbUnaffected);
          StringBuilder results = new StringBuilder();
          BufferedReader in2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resultsArray)));
          in2.readLine(); //Skip header
          String line;
          while ((line = in2.readLine()) != null) 
            results.append("\n").append(line);          
          in2.close();
          
          PrintWriter out = new PrintWriter(new FileWriter(this.resultFilename));
          out.println(results.substring(1));
          out.close();
        }
        break;

      default:
        this.statusError("Unexpected Algorithm ["+algo[0]+"]");
        break;
    }
    this.statusDone();
  }

  /**
   * Updates the ThirdPartyServer status
   *
   * @param key         Key of the Status
   * @param msg         Message of the Status
   * @param addDuration do we add elapsed time since start ?
   * @throws IOException
   */
  private void updateStatusFile(TPStatus.State key, String msg, boolean addDuration) throws IOException {
    String details = msg;
    if (addDuration) {
      long diff = new Date().getTime() - started;
      String duration = (diff / 1000d) + "s";
      details += " (in " + duration + ")";
    }
    TPStatus tpStatus = new TPStatus(new Date().getTime(), key, details);
    System.err.println(tpStatus);

    PrintWriter out = new PrintWriter(new FileWriter(this.statusFilename, true));
    out.println(tpStatus);
    out.close();
  }

  /**
   * Sets the ThirdPartyServer status as RUNNING
   *
   * @param msg         Message of the Status
   * @param addDuration do we add elapsed time since start ?
   * @throws IOException
   */
  public void statusRunning(String msg, boolean addDuration) throws IOException {
    this.updateStatusFile(TPStatus.State.RUNNING, msg, addDuration);
  }
  
  /**
   * Sets the ThirdPartyServer status as STARTED
   *
   * @param msg         Message of the Status
   * @param addDuration do we add elapsed time since start ?
   * @throws IOException
   */
  public void statusStarted(String msg, boolean addDuration) throws IOException {
    this.updateStatusFile(TPStatus.State.STARTED, msg, addDuration);
  }
  
  /**
   * Sets the ThirdPartyServer status as ERROR
   * 
   * @param msg Message of the Status
   * @throws IOException 
   */
  public void statusError(String msg) throws IOException{
    this.updateStatusFile(TPStatus.State.ERROR, msg, false);
  }

  /**
   * Sets the ThirdPartyServer status as DONE
   *
   * @throws IOException
   */
  private void statusDone() throws IOException {
    this.updateStatusFile(TPStatus.State.DONE, MSG.TPS_DONE, true);
  }
  
  private static int countVariants(HashMap<String, ArrayList<String>> data){
    int total = 0;
    for(ArrayList<String> list : data.values())
      total += list.size();
    return total;
  }

  /**
   * Prepare the WSS Input Data by parsing the session's files
   *
   * @return Map of genotypes for each genomic region (gene), the first columns are relative to the affected individuals, the last ones to the unaffected
   * @throws Exception
   */
  private HashMap<String, ArrayList<String>> prepareWSSData(double frqThreshold) throws Exception {
    UniversalReader in = new UniversalReader(this.rppExcludedVariantsFilename);
    VariantExclusionSet rppExcludedVariants = VariantExclusionSet.deserialize(in.readLine());
    in.close();
    
    in = new UniversalReader(this.clientExcludedVariantsFilename);
    VariantExclusionSet clientExcludedVariants = VariantExclusionSet.deserialize(Crypto.decryptAES(aesKey, in.readLine()));
    in.close();
    rppExcludedVariants.add(clientExcludedVariants);

    HashMap<String, ArrayList<String>> clientData = readClientData(rppExcludedVariants);
    HashMap<String, ArrayList<String>> rppData = readRPPData(rppExcludedVariants);
    int clientGenes = clientData.keySet().size();
    int clientVariants = countVariants(clientData);
    int rppGenes = rppData.keySet().size();
    int rppVariants = countVariants(rppData);    
    statusStarted(MSG.cat(MSG.WSS_CLIENT_VARIANTS, clientVariants), false);
    statusStarted(MSG.cat(MSG.WSS_CLIENT_GENES, clientGenes), false);
    
    statusStarted(MSG.cat(MSG.WSS_RPP_VARIANTS, rppVariants), false);
    statusStarted(MSG.cat(MSG.WSS_RPP_GENES, rppGenes), false);

    //Here we limit the tests to common genes (genes with at least 1 variant in both sets)
    ArrayList<String> genes = getCommonGenes(clientData, rppData);
    statusStarted(MSG.cat(MSG.WSS_COMMON_GENES, genes.size()), false);

    if (genes.size() > 0) {
      //here we get the number of affected/unaffected samples
      //and create a line of missing genotypes
      nbAffected = clientData.get(genes.get(0)).get(0).split("\t").length - 2;
      nbUnaffected = rppData.get(genes.get(0)).get(0).split("\t").length - 2;
      final String missingCases = missingLine(nbAffected);
      final String missingControl = missingLine(nbUnaffected);

      //here we populate the genotypes for each variant in each gene
      HashMap<String, ArrayList<String>> genotypes = new HashMap<>();
      HashMap<String, ArrayList<String>> variants = new HashMap<>();
      statusStarted(MSG.WSS_OK_PARSE, true);
      int pooledFrequencyFiltered = 0;
      failedFisher = 0;
      for (String gene : genes) {
        //key=variant, value=tab delimited genotypes
        HashMap<String, String> variantLineCase = extractVariantLines(clientData.get(gene));
        HashMap<String, String> variantLineControl = extractVariantLines(rppData.get(gene));

        ArrayList<String> merge = merge(variantLineCase, variantLineControl, missingCases, missingControl);
        PrintWriter out = null;;
        pooledFrequencyFiltered += removeAlleleFrequency(merge, frqThreshold);
        ArrayList<String> mergedGenotypes = new ArrayList<>();
        ArrayList<String> variantNames = new ArrayList<>();

        for(String line : merge){
          int i =  line.indexOf('\t');
          variantNames.add(line.substring(0, i));
          mergedGenotypes.add(line.substring(i+1));
        }

        if(!mergedGenotypes.isEmpty()) {
          genotypes.put(gene, mergedGenotypes);
          variants.put(gene, variantNames);
        }
      }
      statusStarted(MSG.cat(MSG.WSS_FILTERED_FREQUENCY, pooledFrequencyFiltered), false);
      statusStarted(MSG.cat(MSG.WSS_FILTERED_FISHER, failedFisher), false);
      statusStarted(MSG.WSS_OK_FILTER, true);

      //debug export input data !
      export(genotypes, variants);
      
      return genotypes;
    }
    statusRunning(MSG.WSS_NO_DATA, false);
    return null;
  }

  private boolean filterPooledFrequency(String line, double frq){
    String[] f=  line.split("\t");
    double an = 0;
    double ac = 0;

    for(int i = 1 ; i < f.length; i++){
      String g = f[i];
      int v = new Integer(g);
      if(v != -1){
        an += 2;
        ac += v;
      }
    }
    return ac/an > frq;
  }

  private int removeAlleleFrequency(ArrayList<String> lines, double frq){
    ArrayList<Integer> filtered = new ArrayList<>();
    for(int i = 0 ; i < lines.size(); i++)
      if(filterPooledFrequency(lines.get(i), frq))
        filtered.add(i);

    for(int i = filtered.size() - 1 ; i >= 0; i--)
      lines.remove(filtered.get(i));
    return filtered.size();
  }
  
  private void export(HashMap<String, ArrayList<String>> genotypes, HashMap<String, ArrayList<String>> variants) throws IOException{
    String debugDir = this.sessionDir + File.separator + "debug";
    new File(debugDir).mkdirs();
    
    for(String gene : genotypes.keySet()){
      PrintWriter out = new PrintWriter(new FileWriter(debugDir + File.separator + "debug."+gene+".geno"));
      ArrayList<String> vs = variants.get(gene);
      ArrayList<String> gs = genotypes.get(gene);
      for(int i = 0 ; i < vs.size(); i++){
        String v = vs.get(i);
        String g = gs.get(i);
        out.println(v + "\t" + g);
      }
      out.close();
    }
    PrintWriter out = new PrintWriter(new FileWriter(debugDir + File.separator +"debug.pheno"));
    StringBuilder sb = new StringBuilder();
    for(int i = 0 ; i < nbAffected; i++)
      sb.append("\t1");
    for(int i = 0 ; i < nbUnaffected; i++)
      sb.append("\t0");
    out.println(sb.substring(1));
    out.close();
  }

  /**
   * Merges the lines for a given genomic region (gene).
   * The first columns are relative to the affected individuals, the last ones to the unaffected
   *
   * @param vAffected         genotypes for the affected individuals
   * @param vUnaffected       genotypes for the unaffected individuals
   * @param missingAffected   String of nbAffected "-1" columns, if a variants is not present in the affected dataset
   * @param missingUnaffected String of nbUnaffected "-1" columns, if a variants is not present in the unaffected dataset
   * @return
   */
  private ArrayList<String> merge(HashMap<String, String> vAffected, HashMap<String, String> vUnaffected, String missingAffected, String missingUnaffected) {
    ArrayList<String> merge = new ArrayList<>();
    FisherExactTest fet = null;

    for (String variant : vAffected.keySet()) { //process all client variants
      String lCase = vAffected.get(variant);
      String lControl = vUnaffected.get(variant);
      if (lControl == null) //add if only on the client's side
        merge.add(variant+ "\t" + lCase + "\t" + missingUnaffected);
      else {  // if variant is common to both groups, perform a fisher test
        if(fet == null)
          fet = new FisherExactTest(lCase.split("\t").length + lControl.split("\t").length);
        if(checkCallrate(fet, lCase, lControl))
          merge.add(variant+ "\t" + lCase + "\t" + lControl);
        else
          failedFisher++;
      }
    }

    for (String variant : vUnaffected.keySet()) //process rpp variants that aren't common to client
      if (vAffected.get(variant) == null) 
        merge.add(variant+ "\t" + missingAffected + "\t" + vUnaffected.get(variant));
    
    return merge;
  }

  private static boolean checkCallrate(FisherExactTest fet, String lCase, String lControl){
    int missingCase = 0;
    int missingControl = 0;
    String[] fCase = lCase.split("\t");
    String[] fControl = lControl.split("\t");
    for(String s : fCase)
      if(new Integer(s) == -1)      
        missingCase++;

    for(String s : fControl)
      if(new Integer(s) == -1)      
        missingControl++;
    
    double pval = fet.twoTailed(fCase.length - missingCase, missingCase, fControl.length - missingControl, missingControl);
    return pval > 0.001;
  }

  /**
   * A String nb x "-1" columns, representation missing data for nb individuals
   *
   * @param nb number of missing individuals
   * @return
   */
  private static String missingLine(int nb) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < nb; i++)
      sb.append("\t-1");
    return sb.substring(1);
  }

  /**
   * Builds a Map associating a genomic region (gene) as Key to as line of genotypes as value
   *
   * @param lines lines from a Genotype File
   * @return
   */
  private static HashMap<String, String> extractVariantLines(ArrayList<String> lines) {
    HashMap<String, String> map = new HashMap<>();
    for (String line : lines) {
      String[] f = line.split("\t");
      StringBuilder sb = new StringBuilder();
      for (int j = IDX_GENOTYPE; j < f.length; j++)
        sb.append("\t").append(f[j]);
      map.put(f[IDX_VARIANT], sb.substring(1));
    }
    return map;
  }

  /**
   * Reads the data from the RPP
   * @param  excluded Variants Excluded by the Client and/or the Server in hashed values
   * @return A map of genotypes for each genomic region (gene)
   * @throws IOException
   */
  private HashMap<String, ArrayList<String>> readRPPData(VariantExclusionSet excluded) throws IOException {
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    String line;
    UniversalReader in = new UniversalReader(this.rppFilename);
    while ((line = in.readLine()) != null) {
      String[] f = line.split("\t");
      if(!excluded.contains(f[IDX_VARIANT])){
        ArrayList<String> lines = map.containsKey(f[IDX_GENE])
                ? map.get(f[IDX_GENE])
                : new ArrayList<>();

        lines.add(line);
        map.put(f[IDX_GENE], lines);
      }
    }
    in.close();
    return map;
  }

  /**
   * Reads the data from the Client
   * The input file the decrypted using the AES key
   * @param  excluded Variants Excluded by the Client and/or the Server in hashed values
   * @return A map of genotypes for each genomic region (gene)
   * @throws Exception
   */
  private HashMap<String, ArrayList<String>> readClientData(VariantExclusionSet excluded) throws Exception {
    UniversalReader in = new UniversalReader(this.clientFilename);

    HashMap<String, ArrayList<String>> map = new HashMap<>();
    for (String line : Crypto.decryptAES(aesKey, in.readLine()).split("\n")) {
      String[] f = line.split("\t");
      if(!excluded.contains(f[IDX_VARIANT])){
        ArrayList<String> lines = map.containsKey(f[IDX_GENE])
                ? map.get(f[IDX_GENE])
                : new ArrayList<>();
        lines.add(line);
        map.put(f[IDX_GENE], lines);
      }
    }
    return map;
  }

  /**
   * Gets a list of genomic regions (genes) that a present both in the RPP and client data
   *
   * @param clientData A Map where the key is a gene name
   * @param rppData    Another Map where the key is a gene name
   * @return The key that are present in both maps
   */
  private static ArrayList<String> getCommonGenes(HashMap<String, ArrayList<String>> clientData, HashMap<String, ArrayList<String>> rppData) {
    ArrayList<String> ret = new ArrayList<>();
    for (String gene : clientData.keySet())
      if (rppData.containsKey(gene))
        ret.add(gene);
    return ret;
  }
}

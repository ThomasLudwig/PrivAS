package fr.inserm.u1078.tludwig.privas.algorithms.wss;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.instances.ThirdPartyServer;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Class that will parse the data and launch WSS in parallel on all the genes
 * Sometimes the parallelization will be to sequentially launch WSS instances that are multi-threaded
 * Sometimes the parallelization will be to launch in parallel several instances of mono-threaded WSS
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-04-18
 *
 * Javadoc complete on 2019-08-06
 */
public class WSSHandler {
  //parameters
  /**
   * maximum number of cores to use
   */
  private final int nbThreads;
  /**
   * minimal k0 value (continue permutation as long as k0 < minK0)
   */
  private final int minK0;
  /**
   * maximum number of permutations
   */
  private final long maxK; //needs to be long, otherwise computation results might get negative (if >  maxint=2147483647)
  /**
   * if the p-value is above this value, the permutations stop, no need to continue
   */
  private final double rejectionPValue;
  /**
   * the permutations stop if k0 >= 1 + this.minPValueMinusLog + Math.log10(pvalue)
   */
  private final double minPValueMinusLog;
  /**
   * initial random seed
   */
  private final long randomSeed;

  //data
  /**
   * status of the sample (true when affected)
   */
  private boolean[] statuses;
  /**
   * number of affected samples
   */
  private int nbAffected;
  /**
   * number of unaffected samples
   */
  private int nbUnaffected;

  /**
   * all the instances of the WSS objects, one per genomic region (gene)
   */
  private ArrayList<WSS> wss;

  /**
   * The ThirdPartyServer that will execute the computation.
   * Only used to update the status of the TPS with the progression of the algorithm
   */
  private ThirdPartyServer tps = null;

  /**
   * Constructs a new WSSHandler
   *
   * @param nbThreads  maximum number of cores to use
   * @param randomSeed initial random seed
   */
  public WSSHandler(int nbThreads, long randomSeed) {
    this(nbThreads, Parameters.WSS_DEFAULT_MIN_K0, Parameters.WSS_DEFAULT_MAX_K, Parameters.WSS_DEFAULT_REJECTION_PVALUE, Parameters.WSS_DEFAULT_MIN_PVALUE_MINUS_LOG, randomSeed);
  }

  /**
   * Constructs a new WSSHandler
   *
   * @param maxPerm    maximum number of permutations
   * @param nbThreads  maximum number of cores to use
   * @param randomSeed initial random seed
   */
  public WSSHandler(long maxPerm, int nbThreads, long randomSeed) {
    this(nbThreads, Parameters.WSS_DEFAULT_MIN_K0, maxPerm, Parameters.WSS_DEFAULT_REJECTION_PVALUE, Parameters.WSS_DEFAULT_MIN_PVALUE_MINUS_LOG, randomSeed);
  }

  /**
   * Constructs a new WSSHandler
   *
   * @param nbThreads         maximum number of cores to use
   * @param minKo             minimal k0 value (continue permutation as long as k0 &lt; minK0)
   * @param kMax              maximum number of permutations
   * @param rejectionPValue   if the p-value is above this value, the permutations stop, no need to continue
   * @param minPValueMinusLog the permutations stop if k0 &ge; 1 + this.minPValueMinusLog + Math.log10(pvalue)
   * @param randomSeed        initial random seed
   */
  public WSSHandler(int nbThreads, int minKo, long kMax, double rejectionPValue, double minPValueMinusLog, long randomSeed) {
    this.nbThreads = nbThreads;
    this.minK0 = minKo;
    this.maxK = kMax;
    this.rejectionPValue = rejectionPValue;
    this.minPValueMinusLog = minPValueMinusLog;
    this.randomSeed = randomSeed;
  }

  /**
   * Creates the list of genes to process, loads phenotype data and start computing
   *
   * @param genotypeListFilename name of the file containing the genotypes
   * @param phenotypeFilename    name of the file containing the affected/unaffected statuses of the samples
   *
   * @return the results of WSS. A result file, in clear text, stored in a byte array
   * @throws IOException
   */
  public byte[] start(String genotypeListFilename, String phenotypeFilename) throws IOException {
    this.loadData(genotypeListFilename, phenotypeFilename);
    return this.run();
  }

  /**
   * Creates the list of genes to process, loads phenotype data and start computing
   *
   * @param mergedGenotypes Map of genotypes for each genomic region (gene), the first columns are relative to the affected individuals, the last ones to the
   *                        unaffected
   * @param nbAffected      number of affected individuals
   * @param nbUnaffected    number of unaffected individuals
   *
   * @return the results of WSS. A result file, in clear text, stored in a byte array
   * @throws IOException
   */
  public byte[] start(HashMap<String, ArrayList<String>> mergedGenotypes, int nbAffected, int nbUnaffected) throws IOException {
    if (mergedGenotypes == null) {
      statusError(MSG.WH_MAP_NULL);
      return null;
    }

    this.loadData(mergedGenotypes, nbAffected, nbUnaffected);
    return this.run();
  }

  /**
   * Sets the ThirdPartyServer that will receiver the status updates
   *
   * @param tps
   */
  public void setThirdPartyServer(ThirdPartyServer tps) {
    this.tps = tps;
  }

  /**
   * Updates the status of the ThirdPartyServer
   *
   * @param s the new status
   */
  private void statusRunning(String s) {
    if (tps != null)
      try {
        tps.statusRunning(s, false);
      } catch (IOException e) {
        //Nothing
      }
    else
      System.err.println(s);
  }

  private void statusStarted(String s) {
    if (tps != null)
      try {
        tps.statusStarted(s, false);
      } catch (IOException e) {
        //Nothing
      }
    else
      System.err.println(s);
  }

  private void statusError(String s) {
    if (tps != null)
      try {
        tps.statusError(s);
      } catch (IOException e) {
        //Nothing
      }
    else
      System.err.println(s);
  }

  /**
   * Loads phenotype data, loads the list of genes
   *
   * @param genotypeListFilename name of the file containing the genotypes
   * @param phenotypeFilename    name of the file containing the affected/unaffected statuses of the samples
   * @throws IOException
   */
  private void loadData(String genotypeListFilename, String phenotypeFilename) throws IOException {
    //reading phenotype status
    UniversalReader in = new UniversalReader(phenotypeFilename);
    String[] f = in.readLine().split("\\s+");
    statuses = new boolean[f.length];
    try{
      for (int i = 0; i < f.length; i++)
        statuses[i] = Boolean.parseBoolean(f[i].replace("0", "false").replace("1", "true"));
    } catch(NumberFormatException nfe){
      System.err.println("Unable to parse boolean is phenotype file.\n"+nfe.getMessage());
    }
    in.close();

    //number of affected individuals in the dataset
    nbAffected = 0;
    for (boolean b : statuses)
      if (b)
        nbAffected++;
    //number of unaffected individuals in the dataset
    nbUnaffected = statuses.length - nbAffected;

    //TODO START OF DEBUG SECTION TO REMOVE
    statuses = new Shuffler(nbUnaffected, nbAffected, 1138L).getNext();
    //TODO END OF DEBUG SECTION TO REMOVE

    //for each file in the genotypes list, create a wss object
    in = new UniversalReader(genotypeListFilename);
    wss = new ArrayList<>();
    String line;
    while ((line = in.readLine()) != null) {
      f = line.split("\t");
      //f[0] - gene name /  f[1] - filename
      wss.add(new WSS(f[0], statuses, f[1]));
    }
    in.close();
    statusRunning(MSG.cat(MSG.WH_GENO_LIST_LOADED, wss.size()));
  }

  /**
   * Loads phenotype data, loads the list of genes
   *
   * @param mergedGenotypes Map of genotypes for each genomic region (gene), the first columns are relative to the affected individuals, the last ones to the
   *                        unaffected
   * @param nbAffected      number of affected individuals
   * @param nbUnaffected    number of unaffected individuals
   */
  private void loadData(HashMap<String, ArrayList<String>> mergedGenotypes, int nbAffected, int nbUnaffected) {

    //TODO for debugging purpose only remove before release !
   /*

   String id = "debug."+new Date().getTime();
    PrintWriter out = null;
    if(Main.DEBUG){
      out = new PrintWriter(new FileWriter("/PROJECTS/PrivGene/PrivAS/debug/"+id+".pheno"));
      StringBuilder sb = new StringBuilder();
      for(int i = 0 ; i < nbAffected; i++)
        sb.append("\t1");
      for(int i = 0 ; i < nbUnaffected; i++)
        sb.append("\t0");
      out.println(sb.substring(1));
      out.close();
      out = new PrintWriter(new FileWriter("/PROJECTS/PrivGene/PrivAS/debug/"+id+".geno"));
    }*/

    //number of affected individuals in the dataset
    this.nbAffected = nbAffected;
    this.nbUnaffected = nbUnaffected;
    //reading phenotype status
    statuses = new boolean[this.nbAffected + this.nbUnaffected];
    for (int i = 0; i < this.nbAffected + this.nbUnaffected; i++)
      statuses[i] = i < this.nbAffected;
    wss = new ArrayList<>();
    for (String gene : mergedGenotypes.keySet()) {
      ArrayList<String> genotypes = mergedGenotypes.get(gene);
      if (genotypes == null)
        statusError(MSG.cat(MSG.WH_NO_GENOTYPE, gene));  //TODO exit ?
      /*else if(out != null)
        for(String ge : genotypes) 
          out.println(gene+"\t"+ge);*/
      wss.add(new WSS(gene, statuses, genotypes));
    }

    statusRunning(MSG.cat(MSG.WH_GENO_LIST_LOADED, wss.size()));
   /* if(out != null)
      out.close();*/
  }

  /**
   * First, compute WSS for each gene in the list in parallel mode (limited to the selected number of threads)
   * - compute reference ranksum
   * - perform first iteration (a set number of permutations)
   * <p>
   * Now we have rejected most of the genes (which are above the rejection p-value threshold)
   * we will iterate the genes sequentially, each gene will be processed on multiple threads
   * <p>
   *
   * @return the results of WSS. A result file, in clear text, stored in a byte array
   */
  private byte[] run() {
    long startComp = new Date().getTime();
    statusRunning(/*Constants.DF_TPS.format(new Date()) + */MSG.WH_START);
    //init
    Shuffler shuffler = new Shuffler(nbUnaffected, nbAffected, randomSeed);
    final long start = new Date().getTime();

    //print results header
    //PrintWriter out = new PrintWriter(new FileWriter(this.resultFile));
    ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(resultStream);
    out.println(Constants.WWS_HEADER);

    //compute rank sum
    //perform first iteration
    int minIteration = (int) Math.ceil(((Parameters.WSS_DEFAULT_LOOP_SIZE / this.rejectionPValue) + 1));
    while(minIteration % this.nbThreads != 0)
      minIteration++;
    //final int shuffleSize = minIteration / this.nbThreads;
    final boolean[][] shuffled = shuffler.getNext(minIteration);
    //k is the number of permutations done so far
    int k = minIteration;

    int totalGenes = wss.size();
    //minority of genes that are not above the rejection p-value after the 1st iteration
    ArrayList<WSS> keep = new ArrayList<>();

    for (WSS w : this.wss) {
      w.start(this.statuses);
      w.doPermutations(shuffled, this.nbThreads);
      if (w.isStopReached(this.minK0, this.maxK, this.rejectionPValue, this.minPValueMinusLog))
        out.println(w.getResults(start));
      else
        keep.add(w);
    }

    wss = keep;
    out.flush();
    statusRunning(MSG.WH_PROGRESS(k, wss.size(), totalGenes));
    int previousRemaining = wss.size();

    int previousK = k;
    long previousTime = new Date().getTime();
    //now we have rejected most of the genes (which are above the rejection p-value threshold)
    //we will iterate the genes sequentially, each gene will be processed on multiple threads
    int step = this.nbThreads * Parameters.WSS_DEFAULT_LOOP_SIZE;

    //second pass (same as the following, but timed
    while (!wss.isEmpty()) {
      ArrayList<WSS> toRemove = new ArrayList<>();
      //get shuffle
      final boolean[][] shuffled2 = shuffler.getNext(step);
      k += step;
      for (WSS w : wss) {
        w.doPermutations(shuffled2, this.nbThreads);
        if (w.isStopReached(this.minK0, this.maxK, this.rejectionPValue, this.minPValueMinusLog)) {
          out.println(w.getResults(start));
          toRemove.add(w);
        }
      }
      out.flush();
      for (WSS w : toRemove)
        wss.remove(w);

      if ((previousRemaining != wss.size()) || (k - previousK > 10000)) {
        long now = new Date().getTime(); //now
        double ms = now - previousTime;
        long nbIterDone = (k - previousK) * previousRemaining; //long in case it is more than maxint=2147483647 
        long nbIterLeft = (this.maxK - k) * wss.size(); //long in case it is more than maxint=2147483647                 
        double iterByMs = nbIterDone / ms;
        long msLeft = (long) (nbIterLeft / iterByMs);
        statusRunning(MSG.WH_PROGRESS(k, wss.size(), totalGenes, msLeft));
        //update values
        previousRemaining = wss.size();
        previousK = k;
        previousTime = now;
      }
    }
    Date endComp = new Date();
    statusRunning(MSG.WH_END(startComp, endComp.getTime()));
    statusRunning(MSG.WH_DONE);
    out.close();
    return resultStream.toByteArray();
  }
}
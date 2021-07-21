package fr.inserm.u1078.tludwig.privas.algorithms.wss;

import fr.inserm.u1078.tludwig.privas.algorithms.Utils;
import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * The actual computation of the WSS algorithm for a given gene
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-04-04
 *
 * Javadoc complete on 2019-08-06
 */
public class WSS {

  private static final String T = "\t";

  //parameters
  /**
   * The name of the genomic region (gene)
   */
  private final String gene;

  //input data
  /**
   * Genotypes for each position (first dim) and each sample (second dim)
   * Each field contains the number of variant allele (0, 1 or 2). Missing (-1)
   */
  private int[][] genotypes;
  /**
   * Total number of variants among the datasets
   */
  private int totalVariants;
  /**
   * Number of variants shared across the datasets
   */
  private int sharedVariants;

  /**
   * The most frequent genotype (REF or ALT)
   */
  private int mostFrequentGenotype;

  //current values
  /**
   * ranksum for the actual phenotypes
   */
  private double ranksum;

  /**
   * Number of permutations with ranksum at least as extreme as unscrambled data
   */
  private final AtomicInteger k0 = new AtomicInteger(0);

  /**
   * Total number of permutations
   */
  private final AtomicInteger k = new AtomicInteger(0);

  private double factor; //1/(2*unaffected + 2)

  /**
   * Builds a WSS from a file
   *
   * @param gene             name of the genomic region
   * @param phenotypes         phenotypes of the samples (true when affected)
   * @param genotypeFilename name of the file containing the genotypes
   * @throws IOException
   */
  public WSS(String gene, boolean[] phenotypes, String genotypeFilename) throws IOException {
    this.gene = gene;
    this.load(phenotypes, genotypeFilename);
  }

  /**
   * Builds a WSS from a list of lines
   *
   * @param gene     name of the genomic region
   * @param phenotypes phenotypes of the samples (true when affected)
   * @param lines    lines of genotypes (one column per sample with integer values from -1 to 2)
   */
  public WSS(String gene, boolean[] phenotypes, ArrayList<String> lines) {
    this.gene = gene;
    this.load(phenotypes, lines);
  }

  /**
   * Loads the real phenotypes and reads the genotypes from a file
   *
   * @param phenotypes         phenotypes of the samples (true when affected)
   * @param genotypeFilename name of the file containing the genotypes
   * @throws IOException
   */
  private void load(boolean[] phenotypes, String genotypeFilename) throws IOException {
    UniversalReader in = new UniversalReader(genotypeFilename);
    String line;
    ArrayList<String> lines = new ArrayList<>();
    while ((line = in.readLine()) != null)
      lines.add(line);
    this.load(phenotypes, lines);
  }

  /**
   * Loads the real phenotypes and reads the genotypes from a list of lines from a file
   *
   * @param phenotypes phenotypes of the sample (true when affected)
   * @param lines lines of genotypes (one column per sample with integer values from -1 to 2)
   */
  private void load(boolean[] phenotypes, ArrayList<String> lines) {
    int nbAffected = 0;
    for(boolean s : phenotypes)
      if(s)
        nbAffected++;
    int nbUnaffected = phenotypes.length - nbAffected;
    this.factor = 0.5/(nbUnaffected+1);

    this.totalVariants = lines.size();
    this.sharedVariants = 0;
    this.genotypes = new int[totalVariants][phenotypes.length];
    for (int i = 0; i < lines.size(); i++) {
      boolean hasAffected = false;
      boolean hasUnaffected = false;
      String[] f = lines.get(i).split("\\s+");
      int[] geno = replaceMissingWithMostFrequent(f); // No missing
      for (int j = 0; j < f.length; j++) {
        if (geno[j] != mostFrequentGenotype)
          if (phenotypes[j])
            hasAffected = true;
          else
            hasUnaffected = true;
      }
      genotypes[i] = geno;
      if (hasAffected && hasUnaffected)
        this.sharedVariants++;
    }
    if (totalVariants < 1)
      System.err.println("Unexpected : no variants for gene ["+this.gene+"]");
  }

  /**
   * Replaces missing genotypes with the most frequent non missing genotype between 0 (REF) and 2 (ALT)
   * @param f the original genotype array
   * @return the new genotype array
   */
  public int[] replaceMissingWithMostFrequent(String[] f){
    int[] ret = new int[f.length];
    int[] count = new int[3];

    //first loop steps missing to REF
    //if ALT > REF, second loop resets missing to ALT
    //this is more efficient than 1st loop count, 2nd loop sets
    //as (almost) everytime REF>ALT

    for(int i = 0 ; i < f.length; i++){
      int g = new Integer(f[i]);
      if(g > Constants.GENO_MISSING){
        count[g]++;
        ret[i] = g;
      } else
        ret[i] = Constants.GENO_REF;
    }
    this.mostFrequentGenotype = Constants.GENO_REF;
    if(count[Constants.GENO_ALT] > count[Constants.GENO_REF]) {
      this.mostFrequentGenotype = Constants.GENO_ALT;
      for (int i = 0; i < f.length; i++)
        if (f[i].equals("" + Constants.GENO_MISSING))
          ret[i] = Constants.GENO_ALT;
    }
    return ret;
  }

  /**
   * Compute the real ranksum
   *
   * @param phenotypes phenotypes of the samples (true when affected)
   * @return the computed ranksum
   */
  public double start(boolean[] phenotypes) {
    ranksum = xOptimizedNoMissing(phenotypes, this.genotypes, this.factor);
    return ranksum;
  }

  public double testUnoptimized(boolean[] phenotypes){
    return xOriginal(phenotypes, this.genotypes);
  }

  /**
   * count k0 for all the boolean arrays, on several cores
   *
   * @param shuffled  array of arrays of shuffled phenotypes
   * @param nbThreads number of core to use
   */
  public void doPermutations(final boolean[][] shuffled, final int nbThreads) {
    final int nbLoop = shuffled.length / nbThreads;
    //parallel processing
    IntStream.range(0, nbThreads).parallel().forEach(core -> doPermutations(shuffled, core * nbLoop, nbLoop));
  }

  /**
   * Partial count k0 for the boolean phenotypes from shuffled between [start;start+length[
   *
   * @param shuffled array of arrays of shuffled phenotypes
   * @param start    index of first boolean arrays to process
   * @param length   number of boolean arrays to process
   * @return number of time when x(boolean[] phenotypes) >= reference ranksum
   */
  private void doPermutations(final boolean[][] shuffled, final int start, final int length) {
    //k0 is the number of permutations that have a ranking sum at least as low as the real data
    for (int i = 0; i < length; i++)
      doPermutation(shuffled[start+i]);
  }

  /**
   * Increments k0 and
   *
   * @param shuffled
   */
  private void doPermutation(final boolean[] shuffled) {
    k.incrementAndGet();
    if (xOptimizedNoMissing(shuffled, this.genotypes, this.factor) >= ranksum)
      k0.incrementAndGet();
  }

  /**
   * Do we stop permutations ? yes if :
   * 1.k0 >= minK0
   * 2.k >= maxK
   * 3.pvalue > rejection Pvalue
   * 4.k0 >= 1 + this.minPValueMinusLog + Math.log10(pvalue)
   * k0 number of permutations where x >= reference ranksum
   * k total number of permutations
   *
   * @param minK0             minimum value for k0
   * @param maxK              maximum number of permutation
   * @param rejectionPValue   rejection pvalue
   * @param minPValueMinusLog number of permutation needed depending on the pvalue
   * @return true - if at least one of the condition is fulfilled
   */
  public boolean isStopReached(final int minK0, final long maxK, final double rejectionPValue, final double minPValueMinusLog) {
    if (k0.get() >= minK0)
      return true;
    if (k.get() >= maxK)
      return true;
    double pvalue = getPValue();
    if (pvalue > rejectionPValue)
      return true;
    return (k0.get() >= 1 + minPValueMinusLog + Math.log10(pvalue));
  }

  /**
   * sum of the ranks (on the genetic score) for affected individuals
   *
   * @param phenotypes phenotypes of the samples (true when affected)
   * @return
   */
  private static double xOriginal(boolean[] phenotypes, int[][] genotypes) {
    //weight of the variant v
    final double[] w = new double[genotypes.length];

    //compute mu,nu,n,q and w
    for (int v = 0; v < genotypes.length; v++) {
      //number of unaffected individuals genotyped for variant i
      int nu = 0;
      //number of mutant alleles observed for variant i in the unaffected individuals
      int mu = 0;
      //number of genotyped individuals (affected + unaffected)
      int n = 0;

      for (int i = 0; i < phenotypes.length; i++)
        if (genotypes[v][i] != Constants.GENO_MISSING){
          n++;
          if (!phenotypes[i]) {
            nu++;
            mu += genotypes[v][i];
          }
        }
      //mutant_unaffected + 1 / 2*genotyped_unaffected + 2 for variant v
      double q = (mu + 1.0) / (2 * nu + 2.0);
      w[v] = Math.sqrt(n * q * (1 - q)); //w == 0 if :n == 0 or q == 0 or q == 1
      //q == 0 -> impossible
      //n == 0 -> all samples are missing
      //q == 1 -> all genotyped unaffected individual are 2

      //System.err.println("q : "+q+" = ("+mu+" + 1) / (2 * "+nu+" + 2)");
      //System.err.println("Weight : "+w[v]+" = sqrt("+n+" x "+q+" x "+(1-q)+")"+")");
    }

    //compute gamma for affected and unaffected and add to sort
    //Arrays to sort/rank gammas
    RankedGammaList gammaList = new RankedGammaList();

    //genetic scores for individuals
//    HashMap<String, Integer> debug = new HashMap<>();
    for (int i = 0; i < phenotypes.length; i++) {
//      int nbMissing = 0;
      //     StringBuilder sb = new StringBuilder(i).append(" ").append(affected[i]);
      double gamma = 0.0;
      for (int v = 0; v < genotypes.length; v++)
        if (genotypes[v][i] != Constants.GENO_MISSING){
          gamma += genotypes[v][i] / w[v]; //in assotestR, one missing genotype set the whole gamma (genetic score) to 0 for the individual
          //        sb.append(" + ").append(genotypes[v][i]).append("/").append(w[v]);
        } else{
          System.err.println("There are missing genotypes, how could this be ?");
          //         sb.append(" + 0");
          //         nbMissing++;
        }
//      sb.append(" -> gamma=").append(gamma);
//      if(debug.containsKey(sb.toString()))
//        count += debug.get(sb.toString());
//      debug.put(sb.toString(), count);
      gammaList.add(gamma, phenotypes[i]/*, nbMissing*/);
    }
/*    for(String key : debug.keySet())
      System.err.println("["+debug.get(key)+"] -> "+key);
    for(Gamma gamma : gammaList.gammas)
      System.err.println("Gamma : "+gamma.gamma+" ["+gamma.nbMissing+"] "+gamma.nbAffected+"/"+gamma.size());
    System.err.println("Ranking "+gammaList.getRanking());*/
    return gammaList.getRanking();
  }

  /**
   * sum of the ranks (on the genetic score) for affected individuals<br>/
   * this is an optimized version, that assumes no missing genotypes (as missing are replaced with the most frequent homo genotypes
   * @param phenotypes phenotypes of the samples (true when affected)
   * @return
   */
  private static double xOptimizedNoMissing(boolean[] phenotypes, int[][] genotypes, double factor) {
    //weight of the variant v
    final int N = phenotypes.length;
    final int V = genotypes.length;
    final double[] w = new double[V];
    //compute mu,nu,n,q and w
    for (int v = 0; v < V; v++) {
      //1 + number of mutant alleles observed for variant v in the unaffected individuals
      int mu = 1; //optimized, was mu = 0;
      for (int i = 0; i < N; i++)
        if (!phenotypes[i])
          mu += genotypes[v][i];
      //mutant_unaffected + 1 / 2*genotyped_unaffected + 2 for variant v
      double q = factor * mu; // optimised, was double q = (mu + 1.0) / (2 * nu + 2.0) with nu=0 et mu=0 at start
      w[v] = Math.sqrt(N * q * (1 - q)); //w == 0 if : N == 0 or q == 0 or q == 1
      //q == 0 -> impossible
      //q == 1 -> all genotyped unaffected individual are 2
    }
    //compute gamma for affected and unaffected and add to sort
    //Arrays to sort/rank gammas
    RankedGammaList gammaList = new RankedGammaList();
    //genetic scores for individuals
    for (int i = 0; i < N; i++) {
      double gamma = 0.0;
      for (int v = 0; v < V; v++)
        gamma += genotypes[v][i] / w[v];
      gammaList.add(gamma, phenotypes[i]);
    }

    //gammaList.printDebug();
    return gammaList.getRanking();
  }

  /**
   * p-value of the WSS test
   *
   * @return (k0-1)/(k-1)
   */
  double getPValue() {
    return (k0.get() + 1.0) / (k.get() + 1.0);//k0+1 / k+1 From Madsen Browning, to avoid pvalue=0
  }

  /**
   * Gets the results of the tests as a table line
   *
   * @param start timestamp of the start of the processing
   * @return formatted output
   */
  String getResults(long start) {
    return gene + T + getPValue() + T + k0.get() + T + k.get() + T + ranksum + T + totalVariants + T + sharedVariants + T + Utils.durationInSeconds(start) + "s";
  }
}

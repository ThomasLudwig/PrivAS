package fr.inserm.u1078.tludwig.privas.algorithms;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
   * Number of variants shared accross the datasets
   */
  private int sharedVariants;

  //currentvalues
  /**
   * ranksum for the actual status
   */
  private double ranksum;

  /**
   * Number of permutations with ranksum at least as extrem as unscrambled data
   */
  private final AtomicInteger k0 = new AtomicInteger(0);

  /**
   * Total number of permutations
   */
  private final AtomicInteger k = new AtomicInteger(0);

  /**
   * Builds a WSS from a file
   *
   * @param gene             name of the genomic region
   * @param affected         status of the sample (true when affeted)
   * @param genotypeFilename name of the file containing the genotypes
   * @throws IOException
   */
  public WSS(String gene, boolean[] affected, String genotypeFilename) throws IOException {
    this.gene = gene;
    this.load(affected, genotypeFilename);
  }

  /**
   * Builds a WSS from a list of lines
   *
   * @param gene     name of the genomic region
   * @param affected status of the sample (true when affeted)
   * @param lines    lines of genotypes (one column per sample with integer values from -1 to 2)
   */
  public WSS(String gene, boolean[] affected, ArrayList<String> lines) {
    this.gene = gene;
    this.load(affected, lines);
  }

  /**
   * Loads the real phenotypes (sample status) and reads the genotypes from a file
   *
   * @param affected         status of the sample (true when affeted)
   * @param genotypeFilename name of the file containing the genotypes
   * @throws IOException
   */
  private void load(boolean[] affected, String genotypeFilename) throws IOException {
    UniversalReader in = new UniversalReader(genotypeFilename);
    String line;
    ArrayList<String> lines = new ArrayList<>();
    while ((line = in.readLine()) != null)
      lines.add(line);
    this.load(affected, lines);
  }

  /**
   * Loads the real phenotypes (sample status) and reads the genotypes from a list of lines from a file
   *
   * @param affected status of the sample (true when affeted)
   * @param lines    lines of genotypes (one column per sample with integer values from -1 to 2)
   */
  private void load(boolean[] affected, ArrayList<String> lines) {
    this.totalVariants = lines.size();
    this.sharedVariants = 0;
    genotypes = new int[lines.size()][affected.length];
    for (int i = 0; i < lines.size(); i++) {
      boolean hasCase = false;
      boolean hasControl = false;
      String[] f = lines.get(i).split("\\s+");
      f = replaceMissingWithMostFrequent(f);
      int[] geno = new int[f.length];
      for (int j = 0; j < f.length; j++) {
        geno[j] = new Integer(f[j]);
        if (geno[j] != Constants.GENO_MISSING)
          if (affected[j])
            hasCase = true;
          else
            hasControl = true;
      }
      genotypes[i] = geno;
      if (hasCase && hasControl)
        this.sharedVariants++;
    }
    if (totalVariants < 1) 
      System.err.println("Unexcepted : 0 variants for gene ["+this.gene+"]");
  }
  
  public String[] replaceMissingWithMostFrequent(String[] f){
    String[] ret = new String[f.length];
    int[] count = new int[3];
    
    for(int i = 0 ; i < f.length; i++){
      int v = new Integer(f[i]);
      if(v > -1){
        count[v]++;
        ret[i] = f[i];
      } else
        ret[i] = 0+"";
    }
    
    int max = 0;
    if(count[1] > count[0])
      max= 1;
    if(count[2] > count[max])
      max = 2;
    if(max > 0)
      for(int i = 0 ; i < f.length; i++){
        if(f[i].equals("-1"))
          ret[i] = max+"";
      }
    return ret;    
  }

  /**
   * Compute the real ranksum
   *
   * @param affected status of the sample (true when affeted)
   * @return the computed ranksum
   */
  public double start(boolean[] affected) {
    ranksum = x(affected);
    return ranksum;
  }

  /**
   * count k0 for all the boolean arrays, on several cores
   *
   * @param shuffled  array of arrays of shuffled status
   * @param nbThreads number of core to use
   */
  public void doPermutations(final boolean[][] shuffled, final int nbThreads) {
    final int nbLoop = shuffled.length / nbThreads;
    //parallel processing
    IntStream.range(0, nbThreads).parallel().forEach(core -> doPermutations(shuffled, core * nbLoop, nbLoop));
  }

  /**
   * Partial count k0 for the boolean status from shuffled between [start;start+length[
   *
   * @param shuffled array of arrays of shuffled status
   * @param start    index of first boolean arrays to process
   * @param length   number of boolean arrays to process
   * @return number of time when x(boolean[] status) >= reference ranksum
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
    if (x(shuffled) >= ranksum)
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
   * @param affected status of the sample (true when affected)
   * @return
   */
  private double x(boolean[] affected) {
    //weight of the variant v
    final double[] w = new double[this.totalVariants];

    //compute mu,nu,n,q and w
    for (int v = 0; v < this.totalVariants; v++) {
      //number of unaffected individuals genotyped for variant i
      int nu = 0;
      //number of mutant alleles observed for variant i in the unaffected individuals
      int mu = 0;
      //number of genotyped individuals (affected + unaffected)
      int n = 0;

      for (int i = 0; i < affected.length; i++)
        if (genotypes[v][i] != Constants.GENO_MISSING){
          n++;
          if (!affected[i]) {
            nu++;
            mu += genotypes[v][i];
          }
        }
      //mutant_unaffected + 1 / 2*genotyped_unaffected + 2 for variant v
      double q = (mu + 1.0) / (2 * nu + 2.0);
      w[v] = Math.sqrt(n * q * (1 - q)); //w == 0 if :n == 0 or q == 0 or q == 1 
      //q == 0 -> impossible
      //n == 0 -> all samples are missing //TODO check if this is filtered
      //q == 1 -> all genotyped unaffected individual are 2 //TODO check
      
      //System.err.println("q : "+q+" = ("+mu+" + 1) / (2 * "+nu+" + 2)");
      //System.err.println("Weight : "+w[v]+" = sqrt("+n+" x "+q+" x "+(1-q)+")"+")");
    }

    //compute gamma for affected and unaffected and add to sort
    //Arrays to sort/rank gammas
    RankedGammaList gammaList = new RankedGammaList();

    //genetic scores for individuals
//    HashMap<String, Integer> debug = new HashMap<>();
    for (int i = 0; i < affected.length; i++) {
//      int nbMissing = 0;
 //     StringBuilder sb = new StringBuilder(i).append(" ").append(affected[i]);
      double gamma = 0.0;
      for (int v = 0; v < this.totalVariants; v++)
        if (genotypes[v][i] != Constants.GENO_MISSING){
          gamma += genotypes[v][i] / w[v]; //in assotestR, one missing genotype set the whole gamma (genetic score) to 0 for the individual
          
          
          
  //        sb.append(" + ").append(genotypes[v][i]).append("/").append(w[v]);
        } else{
 //         sb.append(" + 0");
 //         nbMissing++;
        }
      
//      sb.append(" -> gamma=").append(gamma);
      int count = 1;
//      if(debug.containsKey(sb.toString()))
//        count += debug.get(sb.toString());
//      debug.put(sb.toString(), count);
      gammaList.add(gamma, affected[i]/*, nbMissing*/);
    }
/*    for(String key : debug.keySet())
      System.err.println("["+debug.get(key)+"] -> "+key);
    
    for(Gamma gamma : gammaList.gammas)
      System.err.println("Gamma : "+gamma.gamma+" ["+gamma.nbMissing+"] "+gamma.nbAffected+"/"+gamma.size());
    
    System.err.println("Ranking "+gammaList.getRanking());*/
    return gammaList.getRanking();
  }

  /**
   * p-value of the WSS test
   *
   * @return (k0-1)/(k-1)
   */
  double getPValue() {
    return (k0.get() + 1) / (double) (k.get() + 1);//k0+1 / k+1 From Madsen Browning , to avoid pvalue=0
  }

  /**
   * Gets the results of the tests as a table line
   *
   * @param start timestamp of the start of the processing
   * @return formatted output
   */
  String getResults(long start) {
    return gene + T + getPValue() + T + k0.get() + T + k.get() + T + ranksum + T + totalVariants + T + sharedVariants + T + duration(start) + "s";
  }

  /**
   * Gets the durations in seconds.milliseconds elapses since the given timestamp
   *
   * @param start initial timestamp
   * @return
   */
  private static double duration(long start) {
    long ms = (new Date().getTime() - start);
    return ms / 1000d;
  }

  /**
   * A list of Gamma object, that automatically sorts elements on insertion (via insertion sort algorithm)
   * After benchmark, it is faster to do an insertion sort on insertion :
   * QuickSort and Parallel sort are in theory faster, but the list size is small and the overhead is bigger via these sorting strategies
   */
  private static class RankedGammaList {

    /**
     * The sorted gamma values
     */
    final ArrayList<Gamma> gammas;

    /**
     * Constructs a new RankedGammaList
     */
    private RankedGammaList() {
      this.gammas = new ArrayList<>();
    }

    /**
     * Inserts the double at the appropriate place in the list and increment the boolean counter
     *
     * @param d the gamma value
     * @param s is the individual affected ?
     */
    private void add(double d, boolean s/*, int nbMissing*/) {
      if (this.gammas.isEmpty()) {
        Gamma g = new Gamma(d, s/*, nbMissing*/);
        this.gammas.add(g);
        return;
      }
      Gamma tmp = new Gamma(d, s/*, nbMissing*/);
      
      int min = 0;
      int max = this.gammas.size() - 1;
      
      while (max - min > 1) {
        int c = (min + max) / 2;
        Gamma g = this.gammas.get(c);
        int compare = g.compare(tmp);
        if(compare == 0){//if (g.gamma == d) {
          g.add(s);
          return;
        }

        if(compare < 0)//if (g.gamma < d)
          min = c;
        else
          max = c;
      }
      
      int compareMin = tmp.compare(this.gammas.get(min));
      //before min
      if(compareMin < 0){
        this.gammas.add(min, tmp);
        return;
      }
      
      //equals min
      if(compareMin == 0){
        this.gammas.get(min).add(s);
        return;
      }
      
      int compareMax = tmp.compare(this.gammas.get(max));
      //between min and max
      if(compareMax < 0) {
        this.gammas.add(max, tmp);
        return;
      }

      //equals max
      if (compareMax == 0) {
        this.gammas.get(max).add(s);
        return;
      }

      //after max
      max++;
      this.gammas.add(max, tmp);
      /*
      this.gammas.add(max, new Gamma(d, s));
      */
    }

    /**
     * summing the rank done like in wilcoxon :
     * first is 1
     * when there are ex aequo, each is ranked as the mean of the ranks
     *
     * @return the rank sum
     */
    private double getRanking() {
      double x = 0;
      int size = 1;
      for (Gamma gamma : gammas) {
        //System.err.println("X="+x+" size="+size);
        x += gamma.getSum(size);
        size += gamma.size();
      }
      return x;
    }
  }

  /**
   * Object that stores a gamma value (double) and a counter of affected/unaffected individuals with this gamma value
   */
  static class Gamma {

    private final double gamma;
    private int nbAffected;
    private int nbUnaffected;

    /**
     * Constructor for Gamma object
     *
     * @param gamma  the gamma value
     * @param status is the first individual presenting this value affected ?
     */
    private Gamma(double gamma, boolean status) {
      this.gamma = gamma;
      if (status) {
        this.nbAffected = 1;
        this.nbUnaffected = 0;
      } else {
        this.nbAffected = 0;
        this.nbUnaffected = 1;
      }      
    }
    
    int compare(Gamma g){
      double compare = this.gamma - g.gamma;
      if(compare < 0)
        return -1;
      if(compare > 0)
        return 1;
      return 0;
    }
    
    /**
     * adding an individual for this value
     *
     * @param status is the individual affected ?
     */
    private void add(boolean status) {
      if (status)
        this.nbAffected++;
      else
        this.nbUnaffected++;
    }

    /**
     * Number of individuals with this gamma value
     *
     * @return sum of affected and unaffected individuals
     */
    private int size() {
      return this.nbAffected + this.nbUnaffected;
    }

    /**
     * The rank of the individuals presenting this value
     *
     * @param start the rank of the first individual, if he was alone
     * @return
     */
    private double getRanking(int start) {
      return start + (size() - 1) * .5;
    }

    /**
     * the partial ranking sum
     *
     * @param start the rank of the first individual presenting this gamma
     * @return number of affected individual * getRanking(start)
     */
    private double getSum(int start) {
      return this.nbAffected * this.getRanking(start);
    }
  }
}

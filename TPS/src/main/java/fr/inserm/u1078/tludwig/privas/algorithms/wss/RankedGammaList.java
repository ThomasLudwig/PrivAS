package fr.inserm.u1078.tludwig.privas.algorithms.wss;

import java.util.ArrayList;

/**
 * Ranked List of Gammas
 *
 * A list of Gamma object, that automatically sorts elements on insertion (via insertion sort algorithm)
 * After benchmark, it is faster to do an insertion sort on insertion :
 * QuickSort and Parallel sort are in theory faster, but the list size is small and the overhead is bigger via these sorting strategies
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-12
 * Checked for release on 2021-05-13
 * Unit Test defined on   XXXX-XX-XX
 */
class RankedGammaList {
  /**
   * The sorted gamma values
   */
  final ArrayList<Gamma> gammas;

  /**
   * Constructs a new RankedGammaList
   */
  RankedGammaList() {
    this.gammas = new ArrayList<>();
  }

  /**
   * Inserts the double at the appropriate place in the list and increment the boolean counter
   *
   * @param d the gamma value
   * @param phenotype is the individual affected ?
   */
  @SuppressWarnings("UnusedReturnValue")
  boolean add(double d, boolean phenotype) {
    Gamma gamma = new Gamma(d, phenotype);

    //first known element
    if (this.gammas.isEmpty())
      return this.gammas.add(gamma);

    //insertion sort
    //Cut array in halves and work recursively on the good half
    int min = 0;
    int max = this.gammas.size() - 1;
    while (max - min > 1) {
      int c = (min + max) / 2;
      Gamma current = this.gammas.get(c);
      double compare = current.compare(gamma);
      if(compare == 0)//if (current.gamma == d) {
        return current.add(phenotype);

      if(compare < 0)//if (current.gamma < d)
        min = c;
      else
        max = c;
    }


    //here max == min || max == min + 1

    double compareMin = gamma.compare(this.gammas.get(min));
    //before min
    if(compareMin < 0)
      return this.add(min, gamma);

    //equals min
    if(compareMin == 0) //checked, it happens often
      return this.add(min, phenotype);

    if(min != max) {
      double compareMax = gamma.compare(this.gammas.get(max));
      //between min and max
      if (compareMax < 0)
        return this.add(max, gamma);

      //equals max
      if (compareMax == 0) //checked, it happens often
        return this.add(max, phenotype);
    }

    //after max
    max++;
    return this.add(max, gamma);
  }

  /**
   * summing the rank done like in wilcoxon :
   * first is 1
   * when there are ex aequo, each is ranked as the mean of the ranks
   *
   * @return the rank sum
   */
  double getRanking() {
    double x = 0;
    int start = 1;
    for (Gamma gamma : gammas) {
      x += gamma.getSum(start);
      start += gamma.size();
    }
    return x;
  }

  private boolean add(int index, boolean phenotype){
    return this.gammas.get(index).add(phenotype);
  }

  private boolean add(int index, Gamma gamma){
    this.gammas.add(index, gamma);
    return true;
  }
}

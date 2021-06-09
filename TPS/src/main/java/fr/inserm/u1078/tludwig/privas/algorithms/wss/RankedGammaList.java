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
   * @param status is the individual affected ?
   */
  void add(double d, boolean status) {
    Gamma gamma = new Gamma(d, status);

    if (this.gammas.isEmpty()) {
      this.gammas.add(gamma);
      return;
    }

    int min = 0;
    int max = this.gammas.size() - 1;

    while (max - min > 1) {
      int c = (min + max) / 2;
      Gamma current = this.gammas.get(c);
      double compare = current.compare(gamma);
      if(compare == 0){//if (current.gamma == d) {
        current.add(status);
        return;
      }

      if(compare < 0)//if (current.gamma < d)
        min = c;
      else
        max = c;
    }

    double compareMin = gamma.compare(this.gammas.get(min));
    //before min
    if(compareMin < 0){
      this.gammas.add(min, gamma);
      return;
    }

    //equals min
    if(compareMin == 0){ //checked, it happens often
      this.gammas.get(min).add(status);
      return;
    }

    double compareMax = gamma.compare(this.gammas.get(max));
    //between min and max
    if(compareMax < 0) {
      this.gammas.add(max, gamma);
      return;
    }

    //equals max
    if (compareMax == 0) { //checked, it happens often
      this.gammas.get(max).add(status);
      return;
    }

    //after max
    max++;
    this.gammas.add(max, gamma);
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
}

package fr.inserm.u1078.tludwig.privas.algorithms.wss;

/**
 * Gamma computed in the WSS algorithm <br/>
 * Object that stores a gamma value (double) and a counter of affected/unaffected individuals with this gamma value
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-12
 * Checked for release on 2021-05-13
 * Unit Test defined on   XXXX-XX-XX
 */
class Gamma {
  private final double gamma;
  private int nbAffected;
  private int nbUnaffected;

  /**
   * Constructor for Gamma object
   *
   * @param gamma  the gamma value
   * @param status is the first individual presenting this value affected ?
   */
  Gamma(double gamma, boolean status) {
    this.gamma = gamma;
    this.nbAffected = status ? 1 : 0;
    this.nbUnaffected = 1 - this.nbAffected;
  }

  /**
   * Compares two gamma values
   * @param g the gamma object to compare
   * @return this.gamma - g.gamma
   */
  double compare(Gamma g){
    return this.gamma - g.gamma;
  }

  /**
   * adding an individual for this value
   *
   * @param status is the individual affected ?
   */
  void add(boolean status) {
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
  int size() {
    return this.nbAffected + this.nbUnaffected;
  }

  /**
   * The rank of the individuals presenting this value
   *
   * @param start the rank of the first individual, if he was alone
   * @return start + 0.5 * (size() - 1)
   */
  private double getRanking(int start) {
    return start + 0.5 * (size() - 1);
  }

  /**
   * the partial ranking sum
   *
   * @param start the rank of the first individual presenting this gamma
   * @return number of affected individual * getRanking(start)
   */
  double getSum(int start) {
    return this.nbAffected * this.getRanking(start);
  }
}

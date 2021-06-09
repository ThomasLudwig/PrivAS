package fr.inserm.u1078.tludwig.privas.algorithms.wss;

import java.util.Random;

/**
 * Provides shuffles of (un)Affected status
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on 2021-05-21
 */
class Shuffler {
  private final Random random;
  private final int nbUnaffected;
  private final int nbAffected;

  /**
   * Constructs a new Shuffler
   *
   * @param nbUnaffected number of unaffected individuals
   * @param nbAffected   number of affected individuals
   * @param seed         random seed
   */
  Shuffler(int nbUnaffected, int nbAffected, long seed) {
    this.nbUnaffected = nbUnaffected;
    this.nbAffected = nbAffected;
    this.random = new Random(seed);
  }

  /**
   * produces SIZE random boolean arrays with nbUnaffected FALSE values and nbAffected TRUE values
   *
   * @param size number of arrays to generate
   * @return array of size boolean arrays
   */
  boolean[][] getNext(int size) {
    boolean[][] b = new boolean[size][];
    for (int i = 0; i < size; i++)
      b[i] = getNext();
    return b;
  }

  /**
   * produces a random boolean array with nbUnaffected FALSE values and nbAffected TRUE values
   *
   * @return random boolean array
   */
  private boolean[] getNext() {
    int nbFalse = this.nbUnaffected;
    int nbTrue = this.nbAffected;
    boolean[] shuffled = new boolean[nbFalse + nbTrue];//All initialized to false
    for (int i = 0; i < shuffled.length; i++) {
      int r = random.nextInt(nbFalse + nbTrue) + 1;
      if (r > nbFalse) {
        shuffled[i] = true;
        nbTrue--;
      } else {
        //shuffled[i] = false;//Already initialized to false
        nbFalse--;
      }
    }
    return shuffled;
  }
}

package fr.inserm.u1078.tludwig.privas.utils;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-10-08
 */
public class FisherExactTest {

  private final int maxN;
  private final double[] f;

  public FisherExactTest(int maxN) {
    this.maxN = maxN;
    f = new double[maxN + 1];
    f[0] = 0;
    for (int i = 1; i <= maxN; i++)
      f[i] = f[i - 1] + Math.log10(i);
  }

  private double test(int a, int b, int c, int d) {
    int n = a + b + c + d;
    if (n > maxN)
      return Double.NaN;
    double p = (f[a + b] + f[c + d] + f[a + c] + f[b + d]) - (f[a] + f[b] + f[c] + f[d] + f[n]);
    //System.out.println((a+"\t"+b+"\t"+c+"\t"+d+"\t"+Math.pow(10, p)).replace(".", ","));
    return Math.pow(10, p);
  }

  private double test(int[] v) {
    return test(v[0], v[1], v[2], v[3]);
  }

  public double twoTailed(int a, int b, int c, int d) {
    int[] v = rotate(a, b, c, d);
    double ref = test(v);
    double sum = ref;
    while (v[0] > 0) { //v[0] is the smallest value, while it's >= 0 //one side of the bell
      v[0]--;
      v[1]++;
      v[2]++;
      v[3]--;
      double t = test(v);
      if (t < ref)
        sum += t;
    }

    //second tail
    v = rotate(a, b, c, d);

    int pivot = v[1];
    v[0] += pivot;
    v[1] -= pivot; //=0
    v[2] -= pivot;
    v[3] += pivot;

    while (v[1] < pivot) { //v[2] >= v[1] so v[2] is always >= 0
      double val = test(v);
      if (val < ref)
        sum += val;
      v[0]--;
      v[1]++;
      v[2]++;
      v[3]--;
    }

    //Message.debug("Fisher("+a+", "+c+", "+b+", "+d+") => "+sum);
    return sum;
  }

  /**
   * Orders the values so that :<br/>
   * [0] is the smallest values S, [1] its smallest neighbour N (no the opposite value N != S, N-S != 2
   * <p>
   * examples :
   *
   * 17 5 2 13 --> 2, 5, 17, 13 ??
   *
   *
   * @return
   */
  private int[] rotate(int a, int b, int c, int d) {
    int[] r = new int[]{a, b, c, d};
    int min = Integer.MAX_VALUE;
    int idx = 0;

    for (int i = 0; i < 4; i++)
      if (r[i] < min) {
        min = r[i];
        idx = i;
      }

    switch (idx) {
      case 1:
        swap(r, 0, 1);
        swap(r, 2, 3);
        break;
      case 2:
        swap(r, 0, 2);
        swap(r, 1, 3);
        break;
      case 3:
        swap(r, 0, 3);
        break;
    }

    if (r[1] > r[2])
      swap(r, 1, 2);

    if (r[0] == r[1] && r[2] > r[3])
      swap(r, 2, 3);

    return r;
  }

  private static void swap(int[] t, int a, int b) {
    int swap = t[a];
    t[a] = t[b];
    t[b] = swap;
  }
}

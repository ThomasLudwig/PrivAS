package fr.inserm.u1078.tludwig.privas.utils;

/**
 * Canonical Representation of a Variant
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-31
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class CanonicalVariant implements Comparable<CanonicalVariant> {
  public static final CanonicalVariant NULL = new CanonicalVariant(0, 0, 0, "-");
  public static final String DEL = "-";
  //QUESTION migrate everything from c:p+l:a to c:p:l:a or c:p:a:l ?

  private final int chrom;
  private final int pos;
  private final int length;
  private final String alt;

  /**
   * Build Canonical Variant From Values
   * @param chrom     the chromosome of the variant
   * @param position  the position of the variant from the VCF file
   * @param reference the reference allele from the VCF file
   * @param alternate the alternate allele from the VCF file
   */
  public CanonicalVariant(String chrom, String position, String reference, String alternate){
    int tmpPos = new Integer(position);
    int prefix = commonPrefix(reference, alternate).length();
    int suffix = commonSuffix(reference, alternate).length();
    int x;
    int l;
    String a;

    if ((prefix > 0) || suffix == 0) { //prefix+suffix, prefix alone, nothing
      x = tmpPos + prefix;
      l = reference.length() - prefix;
      a = alternate.substring(prefix);

    } else { //suffix alone (suffix > 0) && (prefix == 0)
      x = tmpPos;
      l = reference.length() - suffix;
      a = alternate.substring(0, alternate.length() - suffix);
    }

    if (a.isEmpty() || a.equals("."))
      a = DEL;

    this.chrom = getChrAsNumber(chrom);
    this.pos = x;
    this.length = l;
    this.alt = a;
  }

  public CanonicalVariant(int chrom, int pos, int length, String alt) {
    this.chrom = chrom;
    this.pos = pos;
    this.length = length;
    this.alt = alt;
  }

  /**
   * Parse a Canonical Variant from a serialized value
   * @param string the serialized value of the variant
   */
  public CanonicalVariant(String string) {
    String[] f = string.split("\\+");
    String[] g = f[0].split(":");
    String[] h = f[1].split(":");
    this.chrom = Integer.parseInt(g[0]);
    this.pos = Integer.parseInt(g[1]);
    this.length = Integer.parseInt(h[0]);
    this.alt = h[1];
  }

  public int getChrom() {
    return chrom;
  }

  public int getPos() {
    return pos;
  }

  public int getLength() {
    return length;
  }

  public String getAlt() {
    return alt;
  }

  @Override
  public String toString() {
    return chrom  + ":" + pos + "+" + length + ":" + alt;
  }

  public int compareTo(CanonicalVariant that){
    if(this.chrom != that.chrom)
      return this.chrom - that.chrom;
    if(this.pos != that.pos)
      return this.pos - that.pos;
    if(this.length != that.length)
      return this.length - that.length;
    return this.alt.compareTo(that.alt);
  }

  /**
   * Returns true if the the variant is a SVN
   * @return true if the the variant is a SVN
   */
  public boolean isSNV(){
    return length == 1 && !DEL.equals(alt);
  }

  /**
   * true if Canonical Variant is of NULL value
   * @return true if chrom == 0 and pos == 0
   */
  public boolean isNull(){
    return this.chrom == NULL.chrom && this.pos == NULL.pos;
  }

  /**
   * Gets all the variants in canonical form
   * @param chr the chromosome
   * @param pos the position
   * @param ref the reference allele
   * @param alts the alternate alleles
   * @return arrays of Canonical Variants
   */
  public static CanonicalVariant[] getVariants(String chr, String pos, String ref, String[] alts){
    CanonicalVariant[] ret = new CanonicalVariant[alts.length];
    for(int i = 0; i < ret.length; i++)
      ret[i] = new CanonicalVariant(chr, pos, ref, alts[i]);
    return ret;
  }

  /**
   * Gets the longest prefix common to two String a and b
   *
   * @param a one String
   * @param b another String
   * @return the longest prefix common to two String a and b
   */
  private static String commonPrefix(String a, String b) {
    String ret = "";
    for (int i = 0; i < Math.min(a.length(), b.length()); i++)
      if (a.charAt(i) == b.charAt(i))
        ret += a.charAt(i);
      else
        break;
    return ret;
  }

  /**
   * Gets the longest suffix common to two String a and b
   *
   * @param a one String
   * @param b another String
   * @return the longest suffix common to two String a and b
   */
  private static String commonSuffix(String a, String b) {
    String ret = "";
    int aL = a.length() - 1;
    int bL = b.length() - 1;
    for (int i = 0; i < Math.min(a.length(), b.length()); i++)
      if (a.charAt(aL - i) == b.charAt(bL - i))
        ret = a.charAt(aL - i) + ret;
      else
        break;
    return ret;
  }

  /**
   * Gets the chromosome number
   * @param s the chromosome name
   * @return removes "chr" prefix, X->23, Y -> 24, M/MT -> 25 other non-number -> -1
   */
  public static int getChrAsNumber(String s){
    String chr = s.toLowerCase().replace("chr", "");
    try {
      return new Integer(chr);
    } catch(NumberFormatException e){
      if(chr.equals("x"))
        return 23;
      if(chr.equals("y"))
        return 24;
      if(chr.equals("m") || chr.equals("mt"))
        return 25;
      return -1;
    }
  }
}

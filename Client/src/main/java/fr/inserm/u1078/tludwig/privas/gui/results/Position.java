package fr.inserm.u1078.tludwig.privas.gui.results;

import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.utils.CanonicalVariant;

/**
 * Class representing a Genomic Position (chromosome + position)
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class Position implements  Comparable<Position> {
  private final int chr;
  private final int pos;

  /**
   * Constructor
   *
   * @param str the position in form chr:pos
   */
  Position(String str) {
    String[] f = str.split(":");
    this.chr = CanonicalVariant.getChrAsNumber(f[0]);
    this.pos = new Integer(f[1]);
  }

  /**
   * Gets the Chromosome name
   *
   * @return this position's chromosome name
   */
  private String getChromosomeName() {
    if (chr > 0 && chr < 23)
      return chr + "";
    if (chr == 23)
      return GUI.RP_CHR_X;
    if (chr == 24)
      return GUI.RP_CHR_Y;
    if (chr == 25)
      return GUI.RP_CHR_MT;
    return GUI.RP_CHR_UNKNOWN;
  }

  @Override
  public String toString() {
    String chromosome = getChromosomeName();
    return chromosome + ':' + pos;
  }

  /**
   * Compare another position to this one
   *
   * @param p the other position
   * @return 0 - if both position are the same, negative if the other position is before this one, positive otherwise
   */
  @Override
  public int compareTo(Position p) {
    if (this.chr == p.chr)
      return this.pos - p.pos;
    return this.chr - p.chr;
  }
}

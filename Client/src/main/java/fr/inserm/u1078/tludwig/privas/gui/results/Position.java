package fr.inserm.u1078.tludwig.privas.gui.results;

import fr.inserm.u1078.tludwig.privas.constants.GUI;

/**
 * Class representing a Genomic Position (chromosome + position)
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class Position {
  private final int chr;
  private final int pos;

  /**
   * Constructor
   *
   * @param str the position in form chr:pos
   */
  Position(String str) {
    String[] f = str.split(":");
    String c = f[0]
            .replace(GUI.RP_CHR_PREFIX, "")
            .replace(GUI.RP_CHR_X, "23")
            .replace(GUI.RP_CHR_Y, "24")
            .replace(GUI.RP_CHR_MT, "25")
            .replace(GUI.RP_CHR_M, "25");
    String p = f[1];
    int ch = -1;
    try {
      ch = new Integer(c);
    } catch (NumberFormatException e) {
      //Ignore
    }

    this.chr = ch;
    this.pos = new Integer(p);
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
  int compareTo(Position p) {
    if (this.chr == p.chr)
      return this.pos - p.pos;
    return this.chr - p.chr;
  }
}

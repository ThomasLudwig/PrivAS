package fr.inserm.u1078.tludwig.privas.utils;

import java.util.HashMap;

/**
 * Class containing two dictionaries
 * From the hashed value to the genomic region (gene) clear text name
 * From the genomic region (gene) to the position of it's first variant
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class HashAndPosition {
  private final HashMap<String, String> hash2gene;
  private final HashMap<String, String> gene2position;

  /**
   * Empty Constructor
   */
  HashAndPosition() {
    hash2gene = new HashMap<>();
    gene2position = new HashMap<>();
  }

  /**
   * Adds values to the dictionary
   *
   * @param gene    the genomic region name
   * @param hashed  the hashed value for the genomic region
   * @param variant the first variant in the genomic region
   */
  void add(String gene, String hashed, String variant) {
    this.hash2gene.put(hashed, gene);
    this.gene2position.put(gene, variant.split("\\+")[0]);
  }

  /**
   * Gets the dictionary from the hashed value to the genomic region (gene) clear text name
   *
   * @return embedded hash/gene dictionary
   */
  public HashMap<String, String> getHash2gene() {
    return hash2gene;
  }

  /**
   * Gets the dictionary from the genomic region (gene) to the position of the first variant (chr:pos)
   *
   * @return embedded gene/position dictionary
   */
  public HashMap<String, String> getGene2position() {
    return gene2position;
  }
}
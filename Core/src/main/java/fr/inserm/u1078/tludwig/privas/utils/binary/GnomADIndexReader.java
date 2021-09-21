package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.instances.Instance;
import fr.inserm.u1078.tludwig.privas.utils.CanonicalVariant;

import java.io.IOException;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * Class that parses GnomAD index files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-09
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADIndexReader {

  private final String filename;
  private GnomADIndexHeader header;
  private final TreeMap<Integer, TreeMap<Integer, Long>> exomeIndices;
  private final TreeMap<Integer, TreeMap<Integer, Long>> genomeIndices;

  private final Cache exomeCache;
  private final Cache genomeCache;

  public GnomADIndexReader(String filename, Instance log) throws IOException {
    this.filename = filename;
    exomeIndices = new TreeMap<>();
    genomeIndices = new TreeMap<>();
    loadIndex(log);
    exomeCache = new Cache(exomeIndices);
    genomeCache = new Cache(genomeIndices);
  }

  @SuppressWarnings("unused")
  public String getFilename() {
    return filename;
  }

  public GnomADIndexHeader getHeader() { return header;}

  /**
   * Loads the data from the index file
   * @throws IOException  if an I/O error occurs when reading from the input files.
   */
  @SuppressWarnings("UnusedReturnValue")
  private long loadIndex(Instance log) throws IOException {
    BinaryFileReader in = new BinaryFileReader(filename+".idx");
    header = GnomADIndexHeader.parseHeader(in.readString(), log);
    log.logDebug("Header for "+filename);
    log.logDebug(header.toString());
    long re = loadIndices(exomeIndices, in);
    log.logDebug("Read ["+re+"] indices for exome");
    //printSpan(exomeIndices, log);
    long rg = loadIndices(genomeIndices, in);
    log.logDebug("Read ["+rg+"] indices for exome");
    //printSpan(genomeIndices, log);
    in.close();
    return re+rg;
  }

  private static long loadIndices(TreeMap<Integer, TreeMap<Integer, Long>> indices, BinaryFileReader in) throws IOException {
    long r = 0;
    TreeMap<Integer, Long> chromMap;
    Index index;
    while(!(index = readIndex(in)).isNull()){
      r++;
      chromMap = indices.computeIfAbsent(index.chrom, k -> new TreeMap<>());
      chromMap.put(index.pos, index.offset);
    }
    return r;
  }

  /**
   * Gets the GnomAD Annotation from the GnomAD Files for a given variant
   * @param canonical the canonical representation of the variant
   * @return the GnomAD Annotation or null if non is found
   * @throws IOException  if an I/O error occurs when reading from the GnomAD File.
   */
  public GnomADLine[] fetch(CanonicalVariant canonical) throws IOException {
    return new GnomADLine[]{exomeCache.fetch(canonical), genomeCache.fetch(canonical)};
  }

  public static final String CHR = "Chr";
  public static final String FROM = "From";
  public static final String TO = "to";

  public static void printSpan(TreeMap<Integer, TreeMap<Integer, Long>> map, Instance log){
    for(Integer chr : map.navigableKeySet()){
      log.logDebug(CHR + " " +chr);
      TreeMap<Integer, Long> chromMap = map.get(chr);
      NavigableSet<Integer> positions = chromMap.navigableKeySet();
      if(positions.size() > 0){
        int first = positions.iterator().next();
        int last = positions.descendingIterator().next();
        log.logDebug(String.join(" ", FROM, first+"", TO, last+""));
      }
    }
  }

  private static Index readIndex(BinaryFileReader in) throws IOException {
    int chrom = in.readInt1();
    int pos = in.readInt4();
    long offset = in.readLong8();
    return new Index(chrom, pos, offset);
  }

  private static class Index {
    private final int chrom;
    private final int pos;
    private final long offset;

    Index(int chr, int pos, long offset) {
      this.chrom = chr;
      this.pos = pos;
      this.offset = offset;
    }

    boolean isNull() {
      return chrom == 0 && pos == 0 && offset == 0;
    }
  }

  /**
   * The avoid accessible the GnomAD file for each request, the neighbouring results are stored
   */
  private class Cache {
    private final TreeMap<Integer, TreeMap<Integer, Long>> indices;
    private final TreeMap<CanonicalVariant, GnomADLine> variants;

    private int chrom;
    private int first;
    private int last;

    Cache(TreeMap<Integer, TreeMap<Integer, Long>> indices){
      this.indices = indices;
      variants = new TreeMap<>();
      init(-1);
    }

    private void init(int chrom){
      this.chrom = chrom;
      this.first = Integer.MAX_VALUE;
      this.last = Integer.MAX_VALUE;
    }

    private boolean contains(int chrom, int pos){
      return  chrom == this.chrom &&
              this.first <= pos &&
              pos < this.last;
    }

    private void load(int chrom, int pos) throws IOException {
      variants.clear();

      TreeMap<Integer, Long> index = indices.get(chrom);
      if(index == null) { //No variant for this chrom, so the empty set covers to whole chrom
        this.first = 1;
        this.last = Integer.MAX_VALUE;
        return;
      }
      init(chrom);

      for(int i : index.navigableKeySet()){
        if(i <= pos)
          first = i;
        else {
          last = i;
          break;
        }
      }

      //here first = min(i, MAX_INT) and last = min(i, MAX_INT)
      if(first == Integer.MAX_VALUE) //position before first index, return empty set
        return;
      //if(last == max_values) it means that we are in the last section for this chrom

      long offset = index.get(first);
      GnomADFileReader in = new GnomADFileReader(filename, offset);
      GnomADLine gnomADLine;
      while(!(gnomADLine = in.readGnomADLine()).isNull()){
        CanonicalVariant variant = gnomADLine.getCanonicalVariant();
        if(variant.getChrom() != chrom || variant.getPos() > last) { //stop if greater than last, or change chrom
          break;
        }
        variants.put(variant, gnomADLine);
      }
      in.close();
    }

    GnomADLine fetch(CanonicalVariant canonical) throws IOException {
      if(!contains(canonical.getChrom(), canonical.getPos()))
        load(canonical.getChrom(), canonical.getPos());
      return this.variants.get(canonical);
    }
  }
}

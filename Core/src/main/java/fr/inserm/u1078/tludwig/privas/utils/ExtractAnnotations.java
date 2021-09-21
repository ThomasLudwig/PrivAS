package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.instances.Instance;
import fr.inserm.u1078.tludwig.privas.utils.binary.*;

import java.io.*;
import java.util.Date;
import java.util.TreeMap;

/**
 * Toolkit to extract annotations from reference files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-07-30
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class ExtractAnnotations {

  public static void export(String inputList, GnomADFileWriter out, Instance log) throws IOException {
    long read = 0;
    String line;
    SortingBuffer sb = new SortingBuffer(out);
    UniversalReader inList = new UniversalReader(inputList);
    String inputGnomadVCF;
    while((inputGnomadVCF = inList.readLine()) != null) { //FIXME everywhere, not startWith("#"), cut -f1 -d'#" and trim
      log.logInfo(MSG.cat(MSG.EXT_PROCESS_GNOMAD, inputGnomadVCF));
      UniversalReader in = new UniversalReader(inputGnomadVCF);
      while ((line = in.readLine()) != null)
        if (!line.startsWith("#")) {
          for(GnomADLine l : GnomADLine.parseVCFLine(line))
            if(!l.hasNullFreq())
              sb.add(l);
          read++;
          if (read % 100000 == 0)
            log.logInfo(MSG.cat(MSG.EXT_LINES, read+""));
        }
      in.close();
    }
    inList.close();
    sb.flush();
    out.writeGnomADLine(GnomADLine.NULL);//As a separator
    log.logInfo(MSG.cat(MSG.EXT_LINES, read+""));
  }

  private static String[] getList(String filename) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    UniversalReader in = new UniversalReader(filename);
    while((line = in.readLine()) != null)
      sb.append(",").append(new File(line).getAbsolutePath());
    String s = sb.length() == 0 ? "" : sb.substring(1);
    return s.split(",");
  }

  public static void convertGnomAD(String version, String gnomADExomeVCFList, String gnomADGenomeVCFList, String binaryOutput, Instance log) throws IOException {
    GnomADFileWriter out = new GnomADFileWriter(binaryOutput);
    Date start = new Date();
    GnomADFileHeader header = new GnomADFileHeader(version,
            getList(gnomADExomeVCFList),
            getList(gnomADGenomeVCFList),
            start);
    out.writeGnomADFileHeader(header);

    export(gnomADExomeVCFList, out, log);
    Date endExome = new Date();
    log.logInfo(MSG.EXT_DURATION_EXOME(start, endExome));

    export(gnomADGenomeVCFList, out, log);
    Date endGenome = new Date();
    log.logInfo(MSG.EXT_DURATION_GENOME(endExome, endGenome));
    out.close();

    buildIndex(binaryOutput, log);

    Date endIndex = new Date();
    log.logInfo(MSG.EXT_DURATION_INDEX(endGenome, endIndex));
    GnomADIndexReader rdx = new GnomADIndexReader(binaryOutput, log);
    log.logInfo(MSG.EXT_HEADER+"\n"+rdx.getHeader());
  }

  private static IndexData buildIndex(GnomADFileReader in, int bufferSize, Instance log) throws IOException {
    final TreeMap<Integer, TreeMap<Integer, Long>> indices = new TreeMap<>();
    final long offset = in.getBytesRead();

    GnomADLine line;
    long read = 0;
    long nb = 0;
    int prevPos = -1;
    long lineOffset = in.getBytesRead();
    while (!(line = in.readGnomADLine()).isNull()) {
      read++;
      CanonicalVariant canonicalVariant = line.getCanonicalVariant();

      TreeMap<Integer, Long> indexChrom = indices.computeIfAbsent(canonicalVariant.getChrom(), k -> new TreeMap<>());
      if(indexChrom.isEmpty()){//new chromosome
        prevPos = -1;
        nb = 0;
      }

      if(canonicalVariant.getPos() < prevPos)
        log.logWarning(MSG.EXT_UNSORTED(canonicalVariant, prevPos));

      if (prevPos != canonicalVariant.getPos()) {//ignore multiallelic, just add the first index // Always false on new chrom
        prevPos = canonicalVariant.getPos();
        if(nb % bufferSize == 0)
          /*if(indexChrom.containsKey(canonicalVariant.getPos()))
            nb--;// since it is sorted, can be removed
          else*/
            indexChrom.put(canonicalVariant.getPos(), lineOffset);//indexChrom.put(line.getPos(), line.getOffset());
        nb++; //++ after, or the first won't be added
      }
      lineOffset = in.getBytesRead();
    }

    return new IndexData(read, indices, lineOffset - offset);//return new IndexData(read, indices, line.getOffset() - offset);
  }

  public static void buildIndex(String inputFile, int bufferSize, Instance log) throws IOException {
    GnomADFileReader in = new GnomADFileReader(inputFile);
    GnomADFileHeader head = in.readGnomADFileHeader();
    IndexData exome = buildIndex(in, bufferSize, log);
    IndexData genome = buildIndex(in, bufferSize, log);

    GnomADIndexWriter idx = new GnomADIndexWriter(inputFile+".idx");
    GnomADIndexHeader header = new GnomADIndexHeader(
            head.getVersion(),
            head.getExomePath(),
            head.getGenomePath(),
            head.getDate(),
            exome.getNbVariants(),
            genome.getNbVariants(),
            exome.getNbBytes(),
            genome.getNbBytes(),
            bufferSize
    );

    idx.writeGnomADIndexHeader(header);
    idx.writeIndices(exome);
    idx.writeIndices(genome);
    idx.close();
  }

  public static void buildIndex(String inputFile, Instance log) throws IOException {
    buildIndex(inputFile, GnomADIndexWriter.DEFAULT_BUFFER_SIZE, log);
  }

  /**
   * Utility class to store results of extractions
   */
  public static class IndexData {
    private final long nbVariants;
    private final TreeMap<Integer, TreeMap<Integer, Long>> indices;
    private final long nbBytes;

    public IndexData(long nbVariants, TreeMap<Integer, TreeMap<Integer, Long>> indices, long nbBytes) {
      this.nbVariants = nbVariants;
      this.indices = indices;
      this.nbBytes = nbBytes;
    }

    public long getNbVariants() {
      return nbVariants;
    }

    public TreeMap<Integer, TreeMap<Integer, Long>> getIndices() {
      return indices;
    }

    public long getNbBytes() {
      return nbBytes;
    }
  }
}

package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.Constants;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.instances.Instance;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;
import fr.inserm.u1078.tludwig.privas.utils.binary.GnomADIndexReader;
import fr.inserm.u1078.tludwig.privas.utils.binary.GnomADLine;

import java.nio.charset.StandardCharsets;

/**
 * Class to Handle Genotype Files
 * Genotype File have the following structure :
 * One line per variant/gene (if a variant affects several genes, there is one line for the variant for each affected gene
 * Each line is composed of the following columns :
 * 1. Variant in canonical notation : chr:pos+length.allele (as described in the documentation)
 * 2. Allele Frequency (in GnomAD)
 * 3. Consequence of the Variant on the gene (from variant effect predictor)
 * 4. Affected gene
 * This is followed by one column for each sample. Those columns contain the number of time this allele is present for the given individual (0, 1 or 2; -1 for
 * missing data)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-30
 *
 * Javadoc complete on 2019-08-06
 */
public class GenotypesFileHandler {

  private static final String T = "\t";
  private static final String N = "\n";

  private static final String VEP_PREFIX = "##INFO=<ID=CSQ";
  private static final String HEADER_PREFIX = "#CHROM";
  private static final String CSQ = "CSQ=";
  private static final String VEP_CSQ = "Consequence";
  public static final String VEP_GENE = "SYMBOL";
  public static final String VEP_SOURCE = "SOURCE";
  public static final String VEP_ALLELE_NUM = "ALLELE_NUM";

  private static final String SOURCE_ENSEMBL = "Ensembl";
  private static final String[] VEP_CONSEQUENCES = {
          "intergenic_variant",
          "feature_truncation",
          "regulatory_region_variant",
          "feature_elongation",
          "regulatory_region_amplification",
          "regulatory_region_ablation",
          "TF_binding_site_variant",
          "TFBS_amplification",
          "TFBS_ablation",
          "downstream_gene_variant",
          "upstream_gene_variant",
          "non_coding_transcript_variant",
          "NMD_transcript_variant",
          "intron_variant",
          "non_coding_transcript_exon_variant",
          "3_prime_UTR_variant",
          "5_prime_UTR_variant",
          "mature_miRNA_variant",
          "coding_sequence_variant",
          "synonymous_variant",
          "stop_retained_variant",
          "incomplete_terminal_codon_variant",
          "splice_region_variant",
          "protein_altering_variant",
          "missense_variant",
          "inframe_deletion",
          "inframe_insertion",
          "transcript_amplification",
          "start_lost",
          "stop_lost",
          "frameshift_variant",
          "stop_gained",
          "splice_donor_variant",
          "splice_acceptor_variant",
          "transcript_ablation"};
  
  private static final int GENO_VARIANT = 0;
  private static final int GENO_CSQ = 1;
  private static final int GENO_GENE = 2;
  private static final int GENO_GNOMAD_E = 3;
  private static final int GENO_GNOMAD_G = GENO_GNOMAD_E + 12;
  private static final int GENO_GENOTYPES = GENO_GNOMAD_G + 12;
  
  private static final int VCF_CHR = 0;
  private static final int VCF_POS = 1;
  private static final int VCF_ID = 2;
  private static final int VCF_REF = 3;
  private static final int VCF_ALT = 4;
  private static final int VCF_INFO = 7;
  @SuppressWarnings("unused")
  private static final int VCF_FORMAT = 8;
  private static final int VCF_GENO = 9;

  public static final String ZERO = "0.0";
  public static final String[] ZEROS = {ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO};
  
  private GenotypesFileHandler() {
    //This class cannot be instantiated
  }

  /**
   * Converts a VCF File to a Genotype File
   * <p>
   * The input VCF file must contain the following Variant effect predictor annotations :
   * 1. prefixed with "CSQ="
   * 2. containing "Consequence"
   * 3. containing "SYMBOL"
   * 4. containing "gnomAD_AF"
   * 5. containing "ALLELE_NUM"
   *
   * @param vcfFilename           the name of the VCF file to convert
   * @param gnomADFilename  the name of the GnomAD binary File
   * @param log the instance that will log events
   * @throws IOException          if there are problems while reading the file
   * @throws GenotypesFileHandler.GenotypeFileException if the file is not in the exception format
   */
  public static GenotypesFile convertVCF2Genotypes(String vcfFilename, String gnomADFilename, Instance log) throws IOException, GenotypeFileException {

    GnomADIndexReader bin = new GnomADIndexReader(gnomADFilename, log);
    String gnomADVersion = bin.getHeader().getVersion();
    String genotypeFilename = FileUtils.addGnomADToQCedVCFFilename(vcfFilename, gnomADVersion);

    int idxCsq = -1;
    int idxGene = -1;
    int idxSource = -1;
    int idxAN = -1;
    String vepString = null;
    String header = null;
    UniversalReader in = new UniversalReader(vcfFilename);

    String line;
    long nbLines = 0;
    PrintWriter out = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(genotypeFilename)), StandardCharsets.UTF_8));
    out.println("#"+FileFormat.GENOPTYES_HEADER_GNOMAD_FILENAME+"\t"+gnomADFilename);
    while ((line = in.readLine()) != null)
      if (line.startsWith("#")) {
        if (line.startsWith(VEP_PREFIX)) {
          vepString = line;
          int idx = line.lastIndexOf(' ');
          String[] f = line.substring(idx, line.length() - 2).split("\\|", -1);
          for (int i = 0; i < f.length; i++) {
            if (VEP_CSQ.equals(f[i]))
              idxCsq = i;
            if (VEP_GENE.equals(f[i]))
              idxGene = i;
            if (VEP_SOURCE.equals(f[i]))
              idxSource = i;
            if (VEP_ALLELE_NUM.equals(f[i]))
              idxAN = i;
          }
          if (idxCsq == -1)
            throw new GenotypeFileException("Unable to find " + VEP_CSQ + " in the VEP header");
          if (idxGene == -1)
            throw new GenotypeFileException("Unable to find " + VEP_GENE + " in the VEP header");
          if (idxSource == -1)
            throw new GenotypeFileException("Unable to find " + VEP_SOURCE + " in the VEP header");
          if (idxAN == -1)
            throw new GenotypeFileException("Unable to find " + VEP_ALLELE_NUM + " in the VEP header");
          log.logDebug(VEP_CSQ+" --> "+idxCsq);
          log.logDebug(VEP_GENE+" --> "+idxGene);
          log.logDebug(VEP_SOURCE+" --> "+idxSource);
          log.logDebug(VEP_ALLELE_NUM+" --> "+idxAN);
        } else if (line.startsWith(HEADER_PREFIX))
          header = line;
      } else {
        if (vepString == null)
          throw new GenotypeFileException("Your VCF file does not seem to contain VEP annotations. Can't proceed.");
        if (header == null)
          throw new GenotypeFileException("Your VCF file seems to be badly formatted. Missing header.");

        nbLines += convertLine2Genotypes(line, idxCsq, idxGene, idxSource, idxAN, bin, out);
        if(nbLines % 10000 == 0)
          log.logInfo(MSG.cat(MSG.GNFH_CONVERTED_LINES, nbLines));
      }
    
    in.close();
    out.close();
    out = new PrintWriter(new FileWriter(genotypeFilename + "." + FileFormat.FILE_GENO_SIZE_EXTENSION));
    out.println(nbLines);
    out.close();
    return new GenotypesFile(genotypeFilename, nbLines);
  }

  /**
   * Utility class to store a Genotype File name and its size
   */
  public static class GenotypesFile {
    private final String filename;
    private final long size;

    public GenotypesFile(String filename, long size) {
      this.filename = filename;
      this.size = size;
    }

    public String getFilename() {
      return filename;
    }

    public long getSize() {
      return size;
    }
  }

  /**
   * Write the conversion of a VCF line to a Genotype File
   *
   * @param line      the VCF line to convert
   * @param idxCsq    index of the Consequence value in the vep annotations
   * @param idxGene   index of the SYMBOL value in the vep annotations
   * @param idxSource index of the SYMBOL_SOURCE value in the vep annotations
   * @param idxAN     index of the ALLELE_NUM value in the vep annotations
   * @param bin       Gnomad Binary File
   * @param out       the PrintWriter to the Genotype File
   * @return          the number of lines written in the Genotype File (several lines for multi-allelic and/or multiple annotations)
   * @throws IOException  If an I/O error occurs while reading the GnomAD File or its index
   * @throws GenotypesFileHandler.GenotypeFileException if the line doesn't have a VEP annotation
   */
  private static int convertLine2Genotypes(String line, int idxCsq, int idxGene, int idxSource, int idxAN, GnomADIndexReader bin, PrintWriter out) throws IOException, GenotypeFileException {
    String[] f = line.split(T);
    String[] alts = f[VCF_ALT].split(",", -1);
    int nb = alts.length;
    int nbLines = 0;

    String[] annotations = null;
    String[] tmp = f[VCF_INFO].split(";");
    for (String tm : tmp)
      if (tm.startsWith(CSQ)) {
        annotations = tm.substring(CSQ.length()).split(",");
        break;
      }

    if (annotations == null)
      throw new GenotypeFileException("Missing Annotation " + CSQ + " for line\n" + line);

    for (int a = 1; a <= nb; a++) {
      CanonicalVariant canonical = new CanonicalVariant(f[VCF_CHR], f[VCF_POS], f[VCF_REF], alts[a - 1]);
      String[] gnomADExomeFrequencies = ZEROS.clone();
      String[] gnomADGenomeFrequencies = ZEROS.clone();

      GnomADLine[] cacheLines = bin.fetch(canonical);
      GnomADLine exomeLine = cacheLines[0];
      GnomADLine genomeLine = cacheLines[1];
      if(exomeLine != null)
        for(int i = 0; i < 12 ; i++)
          gnomADExomeFrequencies[i] = "" + exomeLine.getFrequencies()[i];
      if(genomeLine != null)
        for(int i = 0; i < 12 ; i++)
          gnomADGenomeFrequencies[i] = "" + genomeLine.getFrequencies()[i];

      ArrayList<String> csqGene = getCsqGene(a, idxCsq, idxGene, idxSource, idxAN, annotations);
      int[] genotypes = getGenotypes(a, f);
      for (String csq : csqGene) {
        StringBuilder sb = new StringBuilder(canonical.toString());
        sb.append(T).append(csq);
        sb.append(T).append(String.join(T, gnomADExomeFrequencies));
        sb.append(T).append(String.join(T, gnomADGenomeFrequencies));
        for (int geno : genotypes)
          sb.append(T).append(geno);
        out.println(sb);
        nbLines++;
      }
    }
    return nbLines;
  }

  /**
   * Converts a VCF Genotype Block into an Array of integer (number of variant allele for each individual :0, 1 pr 2; -1 for missing data)
   *
   * @param a the number of the alternate allele to consider
   * @param f the Genotype Block from the VCF File columns after FORMAT
   * @return Array of integer (number of variant allele for each individual :0, 1 pr 2; -1 for missing data)
   */
  private static int[] getGenotypes(int a, String[] f) {
    int[] ret = new int[f.length - 9];
    for (int i = VCF_GENO; i < f.length; i++) {
      String geno = f[i].split(":")[0];
      int g = -1;
      if (geno.charAt(0) != '.') {
        g = 0;
        for (String s : geno.replace("/", "|").split("\\|", -1))
          if (s.equals(a + ""))
            g++;
      }
      ret[i - 9] = g;
    }
    return ret;
  }

  /**
   * Gets the Consequences for the given allele
   *
   * @param a       the alternate allele number
   * @param idxCsq  index of the Consequence value in the vep annotations
   * @param idxGene index of the SYMBOL value in the vep annotations
   * @param idxSource index of the SYMBOL_SOURCE value in the vep annotations
   * @param idxAN   index of the ALLELE_NUM value in the vep annotations
   * @param annotations    the list of annotations for the considered variant
   * @return ArrayList of consequences+" "+gene
   */
  private static ArrayList<String> getCsqGene(int a, int idxCsq, int idxGene, int idxSource, int idxAN, String[] annotations) {
    HashMap<String, Integer> geneCsqs = new HashMap<>();
    for (String ans : annotations) {
      String[] an = ans.split("\\|", -1);
      if (an[idxAN].equals("" + a) && an[idxSource].equals(SOURCE_ENSEMBL)) {
        String gene = an[idxGene];
        if(gene == null || gene.isEmpty())
          gene = Constants.SS_NO_GENE;
        int csq = getConsequenceLevel(an[idxCsq]);
        Integer oldCsq = geneCsqs.get(gene);
        if (oldCsq != null)
          csq = Math.max(csq, oldCsq);
        geneCsqs.put(gene, csq);
      }
    }

    ArrayList<String> ret = new ArrayList<>();
    for (String gene : geneCsqs.keySet())
      ret.add(geneCsqs.get(gene) + T + gene);
    return ret;
  }

  /**
   * Gets the highest Consequence Level for the given Consequence String
   *
   * @param csq the String from the VCF file containing the "Consequence" value from vep. It can contain several consequences
   * @return highest Consequence Level for the given Consequence String
   */
  private static int getConsequenceLevel(String csq) {
    int ret = -1;
    for(String c : csq.toLowerCase().split("&"))
      for (int i = VEP_CONSEQUENCES.length - 1; i >= 0; i--)
        if (c.equals(VEP_CONSEQUENCES[i]))
          ret = Math.max(ret, i);
    return ret;
  }

  private final static int STEP = 1000;

  /**
   * Get the number of lines in a Genotype File
   * If the file has its associated .size file, the value is simply read from this file
   * Otherwise the line are counted and the .size file the created
   *
   * @param filename the name of the Genotype File
   * @return number of lines in a Genotype File
   * @throws IOException If an I/O error occurs while reading the Genotype file
   */
  public static int getNumberOfLinesGenotypes(String filename) throws IOException {
    if (filename == null)
      return -1;
    try {
      UniversalReader in = new UniversalReader(filename + "." + FileFormat.FILE_GENO_SIZE_EXTENSION);
      int totalLines = new Integer(in.readLine());
      in.close();
      return totalLines;
    } catch (IOException | NumberFormatException e) {
      int nb = 0;

      UniversalReader ur = new UniversalReader(filename);
      while (ur.readLine() != null)
        nb++;
      ur.close();

      try {
        PrintWriter out = new PrintWriter(new FileWriter(filename + "." + FileFormat.FILE_GENO_SIZE_EXTENSION));
        out.println(nb);
        out.close();
        return nb;
      } catch (IOException ex) {
        return -1;
      }
    }
  }

  /**
   * Sets the name of the GnomAD File that was used to annotate this Genotypes File
   * @param filename the current Genotypes File
   * @return the name of the GnomAD File that was used to annotate this file (stored in the header)
   * @throws IOException If an I/O error occurs while reading the Genotype file
   */
  public static String getGnomADFilename(String filename) throws IOException {
    if(filename == null)
      return null;
    UniversalReader in = new UniversalReader(filename);
    String line;
    while((line = in.readLine()) != null && line.startsWith("#")){ // scan header
      String[] kv = line.substring(1).trim().split("\t");
      if(FileFormat.GENOPTYES_HEADER_GNOMAD_FILENAME.equals(kv[0]))
        return kv[1];
    }
    in.close();
    return "";
  }

  /**
   * Updates the ProgressListener (while extract lines from a Genotype File that pass a set of Filters)
   *
   * @param progressListener the ProgressListener
   * @param percent          the the progress of the extraction in percent
   */
  private static void progress(ProgressListener progressListener, int percent, Instance instance) {
    if (progressListener != null)
      progressListener.progressChanged(percent);
    else
      instance.logInfo("Progress : " + percent + "%");
  }

  //format
  //chr:pos+length.allele   maf csq gene    g1  g2  g3 ...  gn
  //chr17:12345891231+1.C 0.02    23  CFTR    -1   0   1   ... 2

  /**
   * Extracts and Hashed lines from a Genotype file according to a set of filters
   *
   * @param genotypeFilename  the name of the genotype file
   * @param totalLines        the total number of line in the Genotype File
   * @param maxMAF            the maximum GnomAD_AF allowed
   * @param subpop            the selected GnomAD subpopulation
   * @param maxMAFSubpop      the maximum GnomAD_Subpop_AF allowed
   * @param minCSQ            the least severe consequence allowed
   * @param limitToSNVs       is the extraction limited to SNVs ?
   * @param bed               list of all well covered positions
   * @param hash              the hash salt
   * @param progress          the ProgressListener to update during the extraction
   * @return                  all the extracted and hashed line as a single String (containing \n)
   * @throws IOException  If an I/O error occurs while reading the Genotype File or writing to the extracted file
   * @throws GenotypesFileHandler.GenotypeFileException if the provided least severe consequence is not valid
   */
  public static String extractGenotypes(String genotypeFilename, int totalLines, double maxMAF, String subpop, double maxMAFSubpop, String minCSQ, boolean limitToSNVs, BedFile bed, String hash, Instance instance, ProgressListener progress) throws GenotypeFileException, IOException {
    ArrayList<String> lines = extractGenotypesAsList(genotypeFilename, totalLines, maxMAF, subpop, maxMAFSubpop, minCSQ, limitToSNVs, bed, hash, instance, progress);
    instance.logInfo(MSG.cat(MSG.GNFH_EXTRACTED_LINES, lines.size()));
    StringBuilder res = new StringBuilder();
    progress(progress, 99, instance);
    for (String line : lines) {
      res.append(N);
      res.append(line);
    }
    progress(progress, 100, instance);
    if(res.length() > 1)
      return res.substring(1); 
    return "";
  }

  /**
   * Extracts and Hashed lines from a Genotype file according to a set of filters
   *
   * @param genotypeFilename  the name of the genotype file
   * @param outFilename       the output Hashed Genotype File containing the filtered lines
   * @param totalLines        the total number of line in the Genotype File
   * @param maxMAF            the maximum GnomAD_AF allowed
   * @param subpop            the selected GnomAD subpopulation
   * @param maxMAFSubpop      the maximum GnomAD_Subpop_AF allowed
   * @param minCSQ            the least severe consequence allowed
   * @param limitToSNVs       is the extraction limited to SNVs ?
   * @param bed               list of all well covered positions
   * @param hash              the hash salt   
   * @param progress          the ProgressListener to update during the extraction
   * @return number of lines written
   * @throws IOException  If an I/O error occurs while reading the Genotype File or writing to the extracted file
   * @throws GenotypesFileHandler.GenotypeFileException  if the provided least severe consequence is not valid
   */
  public static int extractGenotypesToFile(String genotypeFilename, String outFilename, long totalLines, double maxMAF, String subpop, double maxMAFSubpop, String minCSQ, boolean limitToSNVs, BedFile bed, String hash, Instance instance, ProgressListener progress) throws IOException, GenotypeFileException {
    ArrayList<String> lines = extractGenotypesAsList(genotypeFilename, totalLines, maxMAF, subpop, maxMAFSubpop, minCSQ, limitToSNVs, bed, hash, instance, progress);
    progress(progress, 99, instance);
    PrintWriter out = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outFilename)), StandardCharsets.UTF_8));
    for (String line : lines)
      out.println(line);
    out.close();
    progress(progress, 100, instance);
    return lines.size();
  }

  //DONE  Rows are Scrambled
  //DONE  Scrambled columns
  /**
   * Extracts and Hashed lines from a Genotype file according to a set of filters
   *
   * @param genotypeFilename  the name of the genotype file
   * @param totalLines        the total number of line in the Genotype File
   * @param maxMAF            the maximum GnomAD_AF allowed
   * @param subpop            the selected GnomAD subpopulation
   * @param maxMAFSubpop      the maximum GnomAD_Subpop_AF allowed
   * @param minCSQ            the least severe consequence allowed
   * @param limitToSNVs       is the extraction limited to SNVs ?
   * @param bed               list of all well covered positions
   * @param hash              the hash salt   
   * @param progress          the ProgressListener to update during the extraction
   * @return                  all the extracted and hashed line as an ArrayList of Strings (the lines and genotypes columns are shuffled)
   * @throws IOException      If an I/O error occurs while reading the Genotype file
   * @throws GenotypesFileHandler.GenotypeFileException if the provided least severe consequence is not valid
   */
  private static ArrayList<String> extractGenotypesAsList(String genotypeFilename, long totalLines, double maxMAF, String subpop, double maxMAFSubpop, String minCSQ, boolean limitToSNVs, BedFile bed, String hash, Instance instance, ProgressListener progress) throws IOException, GenotypeFileException {
    int subpopIndex = Constants.getSubpopIndex(subpop);
    Random random = new Random(681074832L);
    int minCSQIdx = getConsequenceLevel(minCSQ);
    if(minCSQIdx < 0)
      throw new GenotypeFileException("Can't find level for consequence ["+minCSQ+"]");
    long read = 0;
    UniversalReader in = new UniversalReader(genotypeFilename);
    String line;
    int previous = -1;
    int percent = 0;
    progress(progress, percent, instance);

    ArrayList<String> output = new ArrayList<>();
    ArrayList<Integer> columnOrder = null;
    while ((line = in.readLine()) != null) //TODO can be parallelized
      if(!line.startsWith("#")){
        read++;
        if (read % STEP == 0) {
          percent = (int) (100 * read / totalLines);
          if (percent != previous) {
            progress(progress, percent, instance);
            previous = percent;
          }
        }
        //At the first line, create the column order
        if (columnOrder == null) {
          columnOrder = new ArrayList<>();
          for (int n = GENO_GENOTYPES; n < line.split(T).length; n++) {
            //columnOrder.add(n);//
            addAtRandomPosition(columnOrder, n, random); //DONE scramble data, the scrambling isn't always the same, even with a static seed. Corrected
          }
        }
        String extracted = extractLine(line, maxMAF, subpopIndex, maxMAFSubpop, minCSQIdx, hash, columnOrder, limitToSNVs, bed);
        if (extracted != null)
          addAtRandomPosition(output, extracted, random);
      }

    in.close();

    instance.logInfo("Extraction of file ["+genotypeFilename+"] complete. Lines kept ["+output.size()+"/"+read+"]");

    return output;
  }

  /**
   * Adds an object at a Random Position in a List
   * @param list    the list of shuffled objects
   * @param object  the object to add 
   * @param random  the random generator
   */
  private static <E> void addAtRandomPosition(ArrayList<E> list, E object, Random random){
    list.add(random.nextInt(list.size() + 1), object);
  }
  
  /**
   * Extracts and Hashed a line from a Genotype file according to a set of filters
   *
   * @param line          the Genotype line
   * @param maxMAF        the maximum GnomAD_AF allowed
   * @param subpopColumn  the column of the selected GnomAD subpopulation
   * @param maxMAFSubpop  the maximum GnomAD_Subpop_AF allowed
   * @param minCSQ        the least severe consequence allowed
   * @param hash          the hash salt
   * @param order         the new order of the samples columns (the samples are shuffled)
   * @param limitToSNVs   is the extraction limited to SNVs ?
   * @param bed           list of all well covered positions
   * @return              the hashed extracted line, if the passes the filters, null otherwise
   */
  private static String extractLine(String line, double maxMAF, int subpopColumn, double maxMAFSubpop, int minCSQ, String hash, ArrayList<Integer> order, boolean limitToSNVs, BedFile bed) {
    String[] f = line.split(T);
    CanonicalVariant canonicalVariant = new CanonicalVariant(f[GENO_VARIANT]);
    if(limitToSNVs && !canonicalVariant.isSNV())
      return null;

    //DONE pass excluded list to TPS has hashed. And ignore there, so as not to leak data
    //1) Client does not fetch exclusion list from server
    //2) Client send AES(list(hash(excludedClient))) with the data
    //3) RPP Send list(hash(excludedRPP)) with the data
    //4) TPS merges list(excludedClient)+list(excludedRPP)
    //5) TPS ignores variants that belong in the list


    if(!bed.isEmpty() && !bed.overlaps(canonicalVariant))//DONE overlaps or contains ? maybe the whole position must be contained ? -> overlaps should be enough, for indels it would be to complicated ... also if bed is empty every position is kept
      return null;

    double mafExome = 0;
    double mafSubpopExome = 0;
    double mafGenome = 0;
    double mafSubpopGenome = 0;

    try { mafExome = Double.parseDouble(f[GENO_GNOMAD_E]); } catch (NumberFormatException ignore) { }

    try { mafSubpopExome = Double.parseDouble(f[GENO_GNOMAD_E + subpopColumn]); } catch (NumberFormatException ignore) { }

    try { mafGenome = Double.parseDouble(f[GENO_GNOMAD_G]); } catch (NumberFormatException ignore) { }

    try { mafSubpopGenome = Double.parseDouble(f[GENO_GNOMAD_G + subpopColumn]); } catch (NumberFormatException ignore) { }

    int csq = -1;
    try {
      csq = Integer.parseInt(f[GENO_CSQ]);
    } catch (NumberFormatException ignore) {
      //ignore
    }

    //DONE: AND or OR ??? ---> here it's AND

    if(csq < minCSQ)
      return null;
    if(mafExome > maxMAF || mafGenome > maxMAF)
      return null;
    if(mafSubpopExome > maxMAFSubpop || mafSubpopGenome > maxMAFSubpop)
      return null;

    StringBuilder res = new StringBuilder();
    res.append(Crypto.hashSHA256(hash, f[GENO_VARIANT]));
    String hashedGene = Crypto.hashSHA256(hash, f[GENO_GENE]);
    res.append(T);
    res.append(hashedGene);
    for (Integer i : order) {
      res.append(T);
      res.append(f[i]);
    }
    return res.toString();
  }

  /**
   * Returns a HashAndPosition set of dictionaries for the given genotype file
   *
   * @param genotypeFilename the name of the Genotype File
   * @param hash             the hash salt
   * @return the HashAndPosition set of dictionaries
   * @throws IOException If an I/O error occurs while reading the Genotype file
   */
  public static HashAndPosition buildHashDictionaryAndPosition(String genotypeFilename, String hash) throws IOException {
    HashAndPosition hashPos = new HashAndPosition();
    ArrayList<String> genes = new ArrayList<>();
    UniversalReader in = new UniversalReader(genotypeFilename);
    String line;
    while ((line = in.readLine()) != null)
      if(!line.startsWith("#")) {//skip header
        String[] f = line.split(T);
        String gene = f[GENO_GENE];
        String pos = f[GENO_VARIANT];
        if (!genes.contains(gene)) {
          genes.add(0, gene);
          hashPos.add(gene, Crypto.hashSHA256(hash, gene), pos);
        }
      }
    in.close();
    return hashPos;
  }

  /**
   * Converts a VCF file into a TSV file containing the first 5 columns + the canonical + the hash canonical (one line per alt)
   * @param inputFilename   the name of the input vcf file
   * @param variantOutputFilename  the name of the output tsv file for variants
   * @param geneOutputFilename  the name of the output tsv file for genes
   * @param hashKey the key to use when hashing the canonical variants
   * @param log the instance that will log events
   * @throws IOException  when there is a problem reading/writing the files
   */
  public static void extractCanonicalAndHash(String inputFilename, String variantOutputFilename, String geneOutputFilename, String hashKey, Instance log) throws IOException, GenotypeFileException {
    UniversalReader in = new UniversalReader(inputFilename);
    PrintWriter out = new PrintWriter(new FileWriter(variantOutputFilename));
    SortedSet<String> genes = new TreeSet<>();

    int idxGene = -1;
    String vepString = null;
    String header = null;
    String line;
    int nbLines = 0;
    while ((line = in.readLine()) != null)
      if (line.startsWith("#")) {
        if (line.startsWith(VEP_PREFIX)) {
          vepString = line;
          int idx = line.lastIndexOf(' ');
          String[] f = line.substring(idx, line.length() - 2).split("\\|", -1);
          for (int i = 0; i < f.length; i++)
            if (VEP_GENE.equals(f[i]))
              idxGene = i;
          if (idxGene == -1)
            throw new GenotypeFileException("Unable to find " + VEP_GENE + " in the VEP header");
        } else if (line.startsWith(HEADER_PREFIX))
          header = line;
      } else {
        if (vepString == null)
          throw new GenotypeFileException("Your VCF file does not seem to contain VEP annotations. Can't proceed.");
        if (header == null)
          throw new GenotypeFileException("Your VCF file seems to be badly formatted. Missing header.");
        nbLines++;
        if(nbLines%10000 == 0)
          log.logInfo(MSG.GNFH_READ_LINES(inputFilename,nbLines));
        String[] f = line.split(T);
        String prefix = String.join(T, f[VCF_CHR], f[VCF_POS], f[VCF_ID], f[VCF_REF]);
        for (String alt :  f[VCF_ALT].split(",")) {
          String canon = new CanonicalVariant(f[VCF_CHR], f[VCF_POS], f[VCF_REF], alt).toString();
          String hashed = Crypto.hashSHA256(hashKey, canon);
          out.println(String.join(T, prefix, alt, canon, hashed));
        }
        for (String info : f[VCF_INFO].split(";")) {
          if (info.startsWith("CSQ=")) {
            for (String csq : info.split(",")) {
              genes.add(csq.split("\\|")[idxGene]);
            }
          }
        }
      }
    log.logInfo(MSG.GNFH_READ_LINES(inputFilename,nbLines));
    in.close();
    out.close();
    out = new PrintWriter(new FileWriter(geneOutputFilename));
    for(String gene : genes)
      out.println(gene + T + Crypto.hashSHA256(hashKey, gene));
    out.close();
  }

  /**
   * Exception throws when the VCF to convert is not the the expected format
   */
  public static class GenotypeFileException extends Exception {
    /**
     * GenotypeFileException from a message
     *
     * @param message the Exception message
     */
    public GenotypeFileException(String message) {
      super(message);
    }

    @SuppressWarnings("unused")
    public GenotypeFileException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

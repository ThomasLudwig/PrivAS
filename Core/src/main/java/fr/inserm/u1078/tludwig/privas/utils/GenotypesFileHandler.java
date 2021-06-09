package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import fr.inserm.u1078.tludwig.privas.instances.Instance;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;

import java.nio.charset.StandardCharsets;
import java.util.Random;

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
  private static final String VEP_GENE = "SYMBOL";
  private static final String VEP_MAX = "gnomAD_AF";
  private static final String VEP_MAX_NFE = "gnomAD_NFE_AF";
  private static final String VEP_ALLELE_NUM = "ALLELE_NUM";
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
  private static final int GENO_GNOMAD = 1;
  private static final int GENO_GNOMAD_NFE = 2;
  private static final int GENO_CSQ = 3;  
  private static final int GENO_GENE = 4;  
  private static final int GENO_GENOTYPES = 5;
  
  private static final int VCF_CHR = 0;
  private static final int VCF_POS = 1;
  private static final int VCF_ID = 2;
  private static final int VCF_REF = 3;
  private static final int VCF_ALT = 4;
  private static final int VCF_INFO = 7;
  private static final int VCF_FORMAT = 8;
  private static final int VCF_GENO = 9;
  
  private GenotypesFileHandler() {
    //This class cannot be instantiated
  }

  /**
   * Gets the defaults filename for a Genotype File converted from a VCF File
   *
   * @param vcfFilename the name of the VCF file
   * @return
   */
  public static String vcfFilename2GenotypesFilename(String vcfFilename) {
    String directory = FileUtils.getDirectory(new File(vcfFilename));
    String basename = FileUtils.getBasename(vcfFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + File.separator + basename + "." + FileFormat.FILE_GENO_EXTENSION + ".gz";
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
   * @param vcfFilename      the name of the VCF file to convert
   * @param genotypeFilename the name of the resulting Genotype file
   * @throws IOException                                                                     if there are problems while reading the file
   * @throws GenotypesFileHandler.GenotypeFileException if the file is not in the exception format
   */
  public static int convertVCF2Genotypes(String vcfFilename, String genotypeFilename) throws IOException, GenotypeFileException {
    int idxCsq = -1;
    int idxGene = -1;
    int idxMaf = -1;
    int idxMafNFE = -1;
    int idxAN = -1;
    String vepString = null;
    String header = null;
    UniversalReader in = new UniversalReader(vcfFilename);
    String line;
    int nbLines = 0;
    PrintWriter out = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(genotypeFilename)), StandardCharsets.UTF_8));
    while ((line = in.readLine()) != null)
      if (line.startsWith("#")) {
        if (line.startsWith(VEP_PREFIX)) {
          vepString = line;
          int idx = line.lastIndexOf(' ');
          String[] f = line.substring(idx, line.length() - 2).split("\\|", -1);
          for (int i = 0; i < f.length; i++) {
            System.err.println(i+" --> "+f[i]);
            if (VEP_CSQ.equals(f[i]))
              idxCsq = i;
            if (VEP_GENE.equals(f[i]))
              idxGene = i;
            if (VEP_MAX.equals(f[i]))
              idxMaf = i;
            if (VEP_MAX_NFE.equals(f[i]))
              idxMafNFE = i;
            if (VEP_ALLELE_NUM.equals(f[i]))
              idxAN = i;
          }
          if (idxCsq == -1)
            throw new GenotypeFileException("Unable to find " + VEP_CSQ + " in the VEP header");
          if (idxGene == -1)
            throw new GenotypeFileException("Unable to find " + VEP_GENE + " in the VEP header");
          if (idxMaf == -1)
            throw new GenotypeFileException("Unable to find " + VEP_MAX + " in the VEP header");
          if (idxMafNFE == -1)
            throw new GenotypeFileException("Unable to find " + VEP_MAX_NFE + " in the VEP header");
          if (idxAN == -1)
            throw new GenotypeFileException("Unable to find " + VEP_ALLELE_NUM + " in the VEP header");
        } else if (line.startsWith(HEADER_PREFIX))
          header = line;
      } else {
        if (vepString == null)
          throw new GenotypeFileException("Your VCF file does not seem to contain VEP annotations. Can't proceed.");
        if (header == null)
          throw new GenotypeFileException("Your VCF file seems to be badly formatted. Missing header.");
        nbLines += convertLine2Genotypes(line, idxCsq, idxGene, idxMaf, idxMafNFE, idxAN, out);
      }
    
    in.close();
    out.close();
    out = new PrintWriter(new FileWriter(genotypeFilename + ".size"));
    out.println(nbLines);
    out.close();
    return nbLines;
  }

  /**
   * Write the conversion of a VCF line to a Genotype File
   *
   * @param line    the VCF line to convert
   * @param idxCsq  index of the Consequence value in the vep annotations
   * @param idxGene index of the SYMBOL value in the vep annotations
   * @param idxMaf  index of the GnomAD_AF value in the vep annotations
   * @param idxMafNFE  index of the GnomAD_NFE_AF value in the vep annotations
   * @param idxAN   index of the ALLELE_NUM value in the vep annotations
   * @param out     the PrintWriter to the Genotype File
   * @return the number of lines written in the Genotype File (several lines for multi-allelic and/or multiple annotations)
   * @throws GenotypesFileHandler.GenotypeFileException
   */
  private static int convertLine2Genotypes(String line, int idxCsq, int idxGene, int idxMaf, int idxMafNFE, int idxAN, PrintWriter out) throws GenotypeFileException {
    String[] f = line.split(T);
    String[] alts = f[VCF_ALT].split(",", -1);
    int nb = alts.length;
    int nbLines = 0;

    String[] anns = null;
    String[] tmp = f[VCF_INFO].split(";");
    for (String tm : tmp)
      if (tm.startsWith(CSQ)) {
        anns = tm.substring(CSQ.length()).split(",");
        break;
      }

    if (anns == null)
      throw new GenotypeFileException("Missing Annotation " + CSQ + " for line\n" + line);

    for (int a = 1; a <= nb; a++) {
      String canonical = getCanonical(f[VCF_CHR], f[VCF_POS], f[VCF_REF], alts[a - 1]);
      double maf = getAF(a, idxMaf, idxAN, anns);
      double mafNFE = getAF(a, idxMafNFE, idxAN, anns);
      ArrayList<String> csqGene = getCsqGene(a, idxCsq, idxGene, idxAN, anns);
      int[] genos = getGenotypes(a, f);
      for (String csq : csqGene) {
        StringBuilder sb = new StringBuilder(canonical);
        sb.append(T).append(maf);
        sb.append(T).append(mafNFE);
        sb.append(T).append(csq);
        for (int geno : genos) {
          sb.append(T).append(geno);
        }
        out.println(sb);
        nbLines++;
      }
    }
    return nbLines;
  }

  /**
   * Converts a VCF Genotype Block into a Array of integer (number of variant allele for each individual :0, 1 pr 2; -1 for missing data)
   *
   * @param a the number of the alternate allele to consider
   * @param f the Genotype Block from the VCF File columns after FORMAT
   * @return
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
   * Gets the GnomAD_AF for the given allele
   *
   * @param a      the alternate allele number
   * @param idxMaf index of the GnomAD_AF/GnomAD_NFE_AF value in the vep annotations
   * @param idxAN  index of the ALLELE_NUM value in the vep annotations
   * @param anns   the list of annotations for the considered variant
   * @return
   */
  private static double getAF(int a, int idxMaf, int idxAN, String[] anns) {
    for (String ann : anns) {
      String[] ans = ann.split("\\|", -1);
      if (ans[idxAN].equals("" + a))
        try {
          return new Double(ans[idxMaf]);
        } catch (NumberFormatException e) {
          //Nothing
        }
    }
    return 0;
  }

  /**
   * Gets the Consequence for the given allele
   *
   * @param a       the alternate allele number
   * @param idxCsq  index of the Consequence value in the vep annotations
   * @param idxGene index of the SYMBOL value in the vep annotations
   * @param idxAN   index of the ALLELE_NUM value in the vep annotations
   * @param anns    the list of annotations for the considered variant
   * @return
   */
  private static ArrayList<String> getCsqGene(int a, int idxCsq, int idxGene, int idxAN, String[] anns) {
    HashMap<String, Integer> geneCsqs = new HashMap<>();
    for (String ann : anns) {
      String[] ans = ann.split("\\|", -1);
      if (ans[idxAN].equals("" + a)) {
        String gene = ans[idxGene];
        if(gene == null || gene.isEmpty())
          gene = Constants.SS_NO_GENE;
        int csq = getConsequenceLevel(ans[idxCsq]);
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
   * @return
   */
  private static int getConsequenceLevel(String csq) {
    int ret = -1;
    for(String c : csq.toLowerCase().split("&"))
      for (int i = VEP_CONSEQUENCES.length - 1; i >= 0; i--)
        if (c.equals(VEP_CONSEQUENCES[i]))
          ret = Math.max(ret, i);
    return ret;
  }

  /**
   * Gets the longest common prefix between two String a and b
   *
   * @param a one String
   * @param b another String
   * @return
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
   * Gets the longest common suffix between two String a and b
   *
   * @param a one String
   * @param b another String
   * @return
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
   * Gets the canonical notation of the variant
   *
   * @param chrom     the chromosome of the variant
   * @param position  the position of the variant from the VCF file
   * @param reference the reference allele from the VCF file
   * @param alternate the alternate allele from the VCF file
   * @return canonical notation has the change on the reference sequence as chr:position+length:sequence
   */
  public static String getCanonical(String chrom, String position, String reference, String alternate) {
    int pos = new Integer(position);
    int prefix = commonPrefix(reference, alternate).length();
    int suffix = commonSuffix(reference, alternate).length();
    int x;
    int l;
    String a;

    if ((prefix > 0) || suffix == 0) { //prefix+suffix, prefix alone, nothing
      x = pos + prefix;
      l = reference.length() - prefix;
      a = alternate.substring(prefix);

    } else { //suffix alone (suffix > 0) && (prefix == 0)
      x = pos;
      l = reference.length() - suffix;
      a = alternate.substring(0, alternate.length() - suffix);
    }

    if (a.isEmpty() || a.equals("."))
      a = "-";

    return getChrAsNumber(chrom) + ":" + x + "+" + l + ":" + a;
  }
  
  public static int getChrAsNumber(String s){
    String chr = s.toLowerCase().replace("chr", "");
    if(chr.equals("x"))
      return 23;
    if(chr.equals("y"))
      return 24;
    if(chr.equals("m") || chr.equals("mt"))
      return 25;
    return new Integer(chr);
  }

  private final static int STEP = 1000;

  /**
   * Get the number of lines in a Genotype File
   * If the file has its associated .size file, the value is simply read from this file
   * Otherwise the line are counted and the .size file the created
   *
   * @param filename the name of the Genotype File
   * @return
   * @throws IOException
   */
  public static int getNumberOfLinesGenotypes(String filename) throws IOException { //
    if (filename == null)
      return -1;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename + ".size"));
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
        PrintWriter out = new PrintWriter(new FileWriter(filename + ".size"));
        out.println(nb);
        out.close();
        return nb;
      } catch (IOException ex) {
        return -1;
      }
    }
  }

  /**
   * Updates the ProgressListener (while extract lines from a Genotype File that pass a set of Filters)
   *
   * @param progressListener the ProgressListener
   * @param percent          the the progress of the extraction in percent
   */
  private static void progress(ProgressListener progressListener, int percent) {
    if (progressListener != null)
      progressListener.progressChanged(percent);
    else
      System.err.println("Progress : " + percent + "%");
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
   * @param maxMAFNFE         the maximum GnomAD_NFE_AF allowed
   * @param minCSQ            the least severe consequence allowed
   * @param limitToSNVs       is the extraction limited to SNVs ?
   * @param bed               list of all well covered positions
   * @param hash              the hash salt   
   * @param progress          the ProgressListener to update during the extraction
   * @return                  all the extracted and hashed line as a single String (containing \n)
   * @throws Exception
   */
  public static String extractGenotypes(String genotypeFilename, int totalLines, double maxMAF, double maxMAFNFE, String minCSQ, boolean limitToSNVs, BedFile bed, String hash, Instance instance, ProgressListener progress) throws Exception {
    ArrayList<String> lines = extractGenotypesAsList(genotypeFilename, totalLines, maxMAF, maxMAFNFE, minCSQ, limitToSNVs, bed, hash, instance, progress);
    instance.logInfo("Extracted Lines : ["+lines.size()+"]");
    StringBuilder res = new StringBuilder();
    progress(progress, 99);
    for (String line : lines) {
      res.append(N);
      res.append(line);
    }
    progress(progress, 100);
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
   * @param maxMAFNFE            the maximum GnomAD_NFE_AF allowed
   * @param minCSQ            the least severe consequence allowed
   * @param limitToSNVs       is the extraction limited to SNVs ?
   * @param bed               list of all well covered positions
   * @param hash              the hash salt   
   * @param progress          the ProgressListener to update during the extraction
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws GenotypesFileHandler.GenotypeFileException
   * return number of lines written
   */
  public static int extractGenotypesToFile(String genotypeFilename, String outFilename, int totalLines, double maxMAF, double maxMAFNFE, String minCSQ, boolean limitToSNVs, BedFile bed, String hash, Instance instance, ProgressListener progress) throws IOException, NoSuchAlgorithmException, InvalidKeyException, GenotypeFileException {
    ArrayList<String> lines = extractGenotypesAsList(genotypeFilename, totalLines, maxMAF, maxMAFNFE, minCSQ, limitToSNVs, bed, hash, instance, progress);
    progress(progress, 99);
    PrintWriter out = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outFilename)), StandardCharsets.UTF_8));
    for (String line : lines)
      out.println(line);
    out.close();
    progress(progress, 100);
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
   * @param maxMAFNFE            the maximum GnomAD_NFE_AF allowed
   * @param minCSQ            the least severe consequence allowed
   * @param limitToSNVs       is the extraction limited to SNVs ?
   * @param bed               list of all well covered positions
   * @param hash              the hash salt   
   * @param progress          the ProgressListener to update during the extraction
   * @return                  all the extracted and hashed line as an ArrayList of Strings (the lines and genotypes columns are shuffled)
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  private static ArrayList<String> extractGenotypesAsList(String genotypeFilename, int totalLines, double maxMAF, double maxMAFNFE, String minCSQ, boolean limitToSNVs, BedFile bed, String hash, Instance instance, ProgressListener progress) throws IOException, NoSuchAlgorithmException, InvalidKeyException, GenotypeFileException {
    Random random = new Random(681074832L);
    int minCSQIdx = getCSQIndex(minCSQ);
    int read = 0;
    UniversalReader in = new UniversalReader(genotypeFilename);
    String line;
    int previous = -1;
    int percent = 0;
    progress(progress, percent);

    ArrayList<String> output = new ArrayList<>();
    ArrayList<Integer> columnOrder = null;
    while ((line = in.readLine()) != null) { //TODO can be parallelized
      read++;
      if (read % STEP == 0) {
        percent = (100 * read / totalLines);
        if (percent != previous) {
          progress(progress, percent);
          previous = percent;
        }
      }
      //At the first line, create the column order
      if (columnOrder == null) {
        columnOrder = new ArrayList<>();
        for (int n = GENO_GENOTYPES; n < line.split(T).length; n++){
          //columnOrder.add(n);//
          addAtRandomPosition(columnOrder, n, random); //DONE scramble data, the scrambling isn't always the same, even with a static seed. Corrected
        }          
      }
      String extracted = extractLine(line, maxMAF, maxMAFNFE, minCSQIdx, hash, columnOrder, limitToSNVs, bed);
      if (extracted != null) 
        addAtRandomPosition(output, extracted, random);
    }
    in.close();

    instance.logInfo("Extraction of file ["+genotypeFilename+"] complete. Lines kept ["+output.size()+"/"+read+"]");

    return output;
  }
  
  private static int getCSQIndex(String csq) throws GenotypeFileException {
    for(int i = 0 ; i < VEP_CONSEQUENCES.length; i++)
      if(VEP_CONSEQUENCES[i].equalsIgnoreCase(csq))
        return i;
    throw new GenotypeFileException("Can't find index for consequence ["+csq+"]");
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
   * Returns true if the given variant is a SNV
   * @param canonical
   * @return 
   */
  public static boolean isSNV(String canonical){
    String[] f = canonical.split("\\+")[1].split(":");
    return ("1".equals(f[0]) && f[1].length() == 1 && !f[1].equals("-"));
  }

  /**
   * Extracts and Hashed a line from a Genotype file according to a set of filters
   *
   * @param line        the Genotype line
   * @param maxMAF      the maximum GnomAD_AF allowed
   * @param maxMAFNFE   the maximum GnomAD_NFE_AF allowed
   * @param minCSQ      the least severe consequence allowed
   * @param hash        the hash salt
   * @param order       the new order of the samples columns (the samples are shuffled)
   * @param limitToSNVs is the extraction limited to SNVs ?
   * @param bed         list of all well covered positions
   * @return            the hashed extracted line, if the passes the filters, null otherwise
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  private static String extractLine(String line, double maxMAF, double maxMAFNFE, int minCSQ, String hash, ArrayList<Integer> order, boolean limitToSNVs, BedFile bed) throws NoSuchAlgorithmException, InvalidKeyException {
    String[] f = line.split(T);
    if(limitToSNVs && !isSNV(f[GENO_VARIANT]))
      return null;

    //DONE pass excluded list to TPS has hashed. And ignore there, so as not to leak data
    //1) Client does not fetch exclusion list from server
    //2) Client send AES(list(hash(excludedClient))) with the data
    //3) RPP Send list(hash(excludedRPP)) with the data
    //4) TPS merges list(excludedClient)+list(excludedRPP)
    //5) TPS ignores variants that belong in the list


    if(!bed.isEmpty() && !bed.overlaps(f[GENO_VARIANT]))//DONE overlaps or contains ? maybe the whole position must be contained ? -> overlaps should be enough, for indels it would be to complicated ... also if bed is empty every position is kept
      return null;

    double maf = 0;
    double mafNFE = 0;
    int csq = -1;
    try {
      maf = Double.parseDouble(f[GENO_GNOMAD]);
      mafNFE = Double.parseDouble(f[GENO_GNOMAD_NFE]); //TODO replace NFE with a selectable population
      csq = Integer.parseInt(f[GENO_CSQ]);
    } catch (NumberFormatException e) {
      //Nothing
    }

    if ((maf <= maxMAF || mafNFE <= maxMAFNFE) && csq >= minCSQ) {
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
    return null;
  }

  /**
   * Returns a HashAndPosition set of dictionaries for the given genotype file
   *
   * @param genotypeFilename the name of the Genotype File
   * @param hash             the hash salt
   * @return the HashAndPosition set of dictionaries
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  public static HashAndPosition buildHashDictionaryAndPosition(String genotypeFilename, String hash) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    HashAndPosition hashPos = new HashAndPosition();
    ArrayList<String> genes = new ArrayList<>();
    UniversalReader in = new UniversalReader(genotypeFilename);
    String line;
    while ((line = in.readLine()) != null) {
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
   * Exception throws when the VCF to convert is not the the expected format
   */
  public static class GenotypeFileException extends Exception {

    /**
     * Empty Constructor
     */
    public GenotypeFileException() {
    }

    /**
     * GenotypeFileException from a message
     *
     * @param message the Exception message
     */
    public GenotypeFileException(String message) {
      super(message);
    }
  }
}

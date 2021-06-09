package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

import java.io.File;

/**
 * Library for processing files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-25
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class FileUtils {

  public static final String S = File.separator;

  public static boolean exists(String filename){
    File f = new File(filename);
    return f.exists() && !f.isDirectory();
  }

  public static String getDirectory(File file){
    return getDirectory(file.getAbsolutePath());
  }

  public static String getDirectory(String filename) {
    int index = filename.lastIndexOf(S);
    if(index == -1)
      return "." + S;
    else
      return filename.substring(0, index) + S;
  }

  public static String getBasename(String filename, String ext) {
    int index = 1 + filename.lastIndexOf(S);
    String basename = filename.substring(index);
    basename = removeExtension(basename, "gz");
    basename = removeExtension(basename, ext);
    return basename;
  }

  public static String removeExtension(String filename, String ext) {
    if(filename.toLowerCase().endsWith("."+ext.toLowerCase())){
      return filename.substring(0, filename.length() - (1 + ext.length()));
    }
    return filename;
  }

  public static String getQCVCFFilename(String vcfFilename, QCParam qcParam) {
    return getQCVCFFilename(vcfFilename, qcParam.hashCode());
  }

  public static String getQCVCFFilename(String vcfFilename, int hashCode) {
    String directory = getDirectory(vcfFilename);
    String basename = getBasename(vcfFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + "QC"+hashCode+"."+basename +".vcf";
  }

  public static String getQCGenotypeFilename(String vcfFilename, QCParam qcParam) {
    return getQCGenotypeFilename(vcfFilename, qcParam.hashCode());
  }

  public static String getQCGenotypeFilename(String vcfFilename, int hashCode) {
    String directory = getDirectory(vcfFilename);
    String basename = getBasename(vcfFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + "QC"+hashCode+"."+basename +".genotype.gz";
  }

  public static boolean isQCFile(File file) {
    String filename = file.getName();
    String start = filename.split("\\.")[0];
    if(!start.startsWith("QC"))
      return false;
    if(start.length() < 3)
      return false;
    try {
      int hashCode = new Integer(start.substring(2));
      return true;
    } catch(NumberFormatException e){
      return false;
    }
  }

  public static String getExcludedVariantFilename(String filename, String ext) {
    String directory = getDirectory(filename);
    String basename = getBasename(filename, ext);
    return directory + basename +".lst";
  }

  public static String getExcludedVariantFilename(String vcfFilename, QCParam qcParam) {
    return getExcludedVariantFilename(vcfFilename, qcParam.hashCode());
  }

  public static String getExcludedVariantFilename(String vcfFilename, int hashCode) {
    String directory = getDirectory(vcfFilename);
    String basename = getBasename(vcfFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + "QC"+hashCode+"."+basename +".lst";
  }
}

package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
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
  public static final String D = ".";

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
      return D + S;
    else
      return filename.substring(0, index) + S;
  }

  public static String getBasename(String filename, String ext) {
    String basename = getBasenameIgnoreExt(filename);
    basename = removeExtension(basename, FileFormat.FILE_GZ_EXTENSION);
    basename = removeExtension(basename, ext);
    return basename;
  }

  public static String getBasenameIgnoreExt(String filename) {
    int index = 1 + filename.lastIndexOf(S);
    return filename.substring(index);
  }

  public static String removeExtension(String filename, String ext) {
    if(filename.toLowerCase().endsWith(D+ext.toLowerCase()))
      return filename.substring(0, filename.length() - (1 + ext.length()));
    return filename;
  }

  public static String addQCPrefixToVCFFilename(String vcfFilename, QCParam qcParam) {
    return addQCPrefixToVCFFilename(vcfFilename, qcParam.hashCode());
  }

  public static String addQCPrefixToVCFFilename(String vcfFilename, int hashCode) {
    String directory = getDirectory(vcfFilename);
    String basename = getBasename(vcfFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + FileFormat.QC_PREFIX+hashCode+D+basename +D+FileFormat.FILE_VCF_EXTENSION+D+FileFormat.FILE_GZ_EXTENSION;
  }

  public static String addGnomADAndQCToVCFFilename(String vcfFilename, QCParam qcParam, String gnomADVersion) {
    return addGnomADAndQCToVCFFilename(vcfFilename, qcParam.hashCode(), gnomADVersion);
  }

  public static String addGnomADAndQCToVCFFilename(String vcfFilename, int hashCode, String gnomADVersion) {
    return addGnomADToQCedVCFFilename(addQCPrefixToVCFFilename(vcfFilename, hashCode), gnomADVersion);
  }

  public static String addGnomADToQCedVCFFilename(String qcedVcfFilename, String gnomADVersion) {
    String directory = getDirectory(qcedVcfFilename);
    String basename = getBasename(qcedVcfFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + gnomADVersion + D + basename + D + FileFormat.FILE_GENO_EXTENSION + D + FileFormat.FILE_GZ_EXTENSION;
  }

  @SuppressWarnings("unused")
  public static boolean isQCedFile(String filename) {
    return isQCedFile(new File(filename));
  }

  public static boolean isQCedFile(File file) {
    String basename = getBasenameIgnoreExt(file.getName());
    return getQCIndex(basename) > -1;
  }

  public static int getQCIndex(String basename) {
    if(isQCPrefix(basename))
      return 0;
    for(int i = 1 ; i < basename.length(); i++)
      if(basename.charAt(i - 1) == '.' && isQCPrefix(basename.substring(i)))
        return i;
    return -1;
  }

  public static boolean isQCPrefix(String s){
    if(!s.startsWith(FileFormat.QC_PREFIX))
      return false;
    int idx = s.indexOf(D);
    if(idx < 3)
      return false;
    String hash = s.substring(FileFormat.QC_PREFIX.length(), idx);
    try{
      @SuppressWarnings("unused")
      int hashCode = new Integer(hash);
      return true;
    } catch(NumberFormatException e) {
      return false;
    }
  }

  public static String stripGnomADFromFilename(String filename, String ext) {
    return stripGnomadFromBasename(getBasename(filename, ext));
  }

  public static String stripGnomadFromBasename(String basename) {
    int index = getQCIndex(basename);
    if(index == -1)
      throw new IllegalArgumentException(MSG.cat(MSG.FU_STRIP_QC_KO, basename));
    return basename.substring(index);
  }

  public static String excludedVariantFromQCedVCF(String qcedVCFFFilename) {
    String directory = getDirectory(qcedVCFFFilename);
    String basename = getBasename(qcedVCFFFilename, FileFormat.FILE_VCF_EXTENSION);
    return directory + basename + D + FileFormat.FILE_EXCLUSION_EXTENSION;
  }

  public static String excludedVariantFromGnomADGenotypeFile(String genotypeFilename) {
    String directory = getDirectory(genotypeFilename);
    String basename = getBasename(genotypeFilename, FileFormat.FILE_GENO_EXTENSION);
    String strip = stripGnomadFromBasename(basename);
    return directory + strip + D + FileFormat.FILE_EXCLUSION_EXTENSION;
  }

  public static String getQCParamFromGenotypeFile(String genotypeFilename) {
    String directory = getDirectory(genotypeFilename);
    String basename = stripGnomADFromFilename(genotypeFilename, FileFormat.FILE_GENO_EXTENSION);
    return directory + basename.split("\\.")[0] + D + FileFormat.FILE_QC_PARAM_EXTENSION;
  }
}

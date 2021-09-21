package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.*;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

import java.io.File;
import java.io.IOException;

/**
 * Dataset parameters
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-23
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class RPPDataset {
  private final String name;
  private final String vcfFilename;
  private final String bedFilename;


  private RPPDataset(String name, String vcfFilename, String bedFilename) {
    this.name = name;
    this.vcfFilename = vcfFilename;
    this.bedFilename = bedFilename;
  }

  public static RPPDataset parse(String string) throws RPP.ConfigFileParsingException {
    String[] f = string.split(":");
    if(f.length != 4)
      throw new RPP.ConfigFileParsingException(MSG.RPP_DATASET_PARSING_EXCEPTION(string));
    //name
    String name = f[0];
    //vcf file
    String vcfFilename = f[1];
    checkFile(vcfFilename, FileFormat.FILETYPE_VCF);
    //bed file
    String bedFilename = f[3];
    checkFile(bedFilename, FileFormat.FILETYPE_BED);

    return new RPPDataset(name, vcfFilename, bedFilename);
  }

  public static void checkFile(String filename, String type) throws RPP.ConfigFileParsingException {
    File file = new File(filename);
    if(!file.exists())
      throw new RPP.ConfigFileParsingException(MSG.FILE_DOES_NOT_EXIST(type, filename));
    if(file.isDirectory())
      throw new RPP.ConfigFileParsingException(MSG.FILE_IS_DIRECTORY(type, filename));
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public VariantExclusionSet getVariantExclusionSet(String sessionHash, QCParam qcParam) throws IOException {
    return this.getVariantExclusionSet(sessionHash, qcParam.hashCode());
  }

  public VariantExclusionSet getVariantExclusionSet(String sessionHash, int qcHashCode) throws IOException {
    return new VariantExclusionSet(this.getExcludedVariantFilename(qcHashCode), sessionHash);
  }

  public BedFile getBedFile() throws IOException, BedRegion.BedRegionException {
    return new BedFile(getBedFilename());
  }

  public int getGenotypeSize(QCParam qcParam, String gnomADVersion) throws IOException {
    return getGenotypeSize(qcParam.hashCode(), gnomADVersion);
  }

  public int getGenotypeSize(int hashCode, String gnomADVersion) throws IOException {
    return GenotypesFileHandler.getNumberOfLinesGenotypes(this.getGenotypeFilename(hashCode, gnomADVersion));
  }

  public String getName() {
    return name;
  }

  public String getVCFFilename() {
    return vcfFilename;
  }

  public String getBedFilename() {
    return bedFilename;
  }

  public String getExcludedVariantFilename(QCParam qcParam){
    return FileUtils.excludedVariantFromQCedVCF(getQCVCFFilename(qcParam));
  }

  public String getExcludedVariantFilename(int hashCode){
    return FileUtils.excludedVariantFromQCedVCF(getQCVCFFilename(hashCode));
  }

  public String getQCVCFFilename(QCParam qcParam){
    return FileUtils.addQCPrefixToVCFFilename(this.getVCFFilename(), qcParam);
  }

  public String getQCVCFFilename(int hashCode){
    return FileUtils.addQCPrefixToVCFFilename(this.getVCFFilename(), hashCode);
  }

  public String getGenotypeFilename(QCParam qcParam, String gnomADVersion){
    return FileUtils.addGnomADAndQCToVCFFilename(this.getVCFFilename(), qcParam, gnomADVersion);
  }

  public String getGenotypeFilename(int hashCode, String gnomADVersion){
    return FileUtils.addGnomADAndQCToVCFFilename(this.getVCFFilename(), hashCode, gnomADVersion);
  }
}

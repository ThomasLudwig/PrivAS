package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.utils.*;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
      throw new RPP.ConfigFileParsingException("Could not parse dataset parameters from ["+string+"]. Expected : " +
              "dataset_name:" +
              "vcf_filename:" +
              "vcf_size:" +
              "well_covered_position_bed_filename:");
    //name
    String name = f[0];
    //vcf file
    String vcfFilename = f[1];
    checkFile(vcfFilename, "VCF File");
    //vcf size
   /* int vcfSize;
    try{
      vcfSize = Integer.parseInt(f[2]);
    } catch(NumberFormatException e){
      throw new RPP.ConfigFileParsingException("Could not read VCF size ["+f[2]+"].", e);
    }*/
    //bed file
    String bedFilename = f[3];
    checkFile(bedFilename, "Bed File");

    return new RPPDataset(name, vcfFilename, bedFilename);
  }

  public static void checkFile(String filename, String type) throws RPP.ConfigFileParsingException {
    File file = new File(filename);
    if(!file.exists())
      throw new RPP.ConfigFileParsingException(type+" ["+filename+"] does not exist.");
    if(file.isDirectory())
      throw new RPP.ConfigFileParsingException(type+" ["+filename+"] is a directory.");
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public VariantExclusionSet getVariantExclusionSet(String sessionHash, QCParam qcParam) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
    return this.getVariantExclusionSet(sessionHash, qcParam.hashCode());
  }

  public VariantExclusionSet getVariantExclusionSet(String sessionHash, int qcHashCode) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
    return new VariantExclusionSet(this.getExcludedVariantFilename(qcHashCode), sessionHash);
  }

  public BedFile getBedFile() throws IOException, BedRegion.BedRegionException {
    return new BedFile(getBedFilename());
  }

  public int getGenotypeSize(QCParam qcParam) throws IOException {
    return getGenotypeSize(qcParam.hashCode());
  }

  public int getGenotypeSize(int hashCode) throws IOException {
    return GenotypesFileHandler.getNumberOfLinesGenotypes(this.getGenotypeFilename(hashCode));
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
    return FileUtils.getExcludedVariantFilename(this.getVCFFilename(), qcParam);
  }

  public String getExcludedVariantFilename(int hashCode){
    return FileUtils.getExcludedVariantFilename(this.getVCFFilename(), hashCode);
  }

  public String getQCVCFFilename(QCParam qcParam){
    return FileUtils.getQCVCFFilename(this.getVCFFilename(), qcParam);
  }

  public String getQCVCFFilename(int hashCode){
    return FileUtils.getQCVCFFilename(this.getVCFFilename(), hashCode);
  }

  public String getGenotypeFilename(QCParam qcParam){
    return FileUtils.getQCGenotypeFilename(this.getVCFFilename(), qcParam);
  }

  public String getGenotypeFilename(int hashCode){
    return FileUtils.getQCGenotypeFilename(this.getVCFFilename(), hashCode);
  }
}

package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.utils.BedFile;
import fr.inserm.u1078.tludwig.privas.utils.BedRegion;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCException;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

/**
 * Message from the Client to the RPP Asking to start a new Session with a set of parameters
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class AskSession extends Message {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public AskSession() {
  }

  /**
   * Constructor from a set of parameters values
   *
   * @param clientPublicKey the Public RSA Key of the Client (as a one line pem String)
   * @param dataset         the name of the dataset chosen by the client (from the list of datasets previously sent by the RPP)
   * @param gnomADVersion   the version of GnomAD chosen by the client (from the list of datasets previously sent by the RPP)
   * @param maxAF           the maximum allele frequency from GnomAD that will be used to filter the variants
   * @param subpop          the GnomAD subpopulation
   * @param maxAFSubpop     the maximum allele frequency from GnomAD_NFE that will be used to filter the variants
   * @param minCSQ          the least severe vep consequence that will be used to filter the variants
   * @param limitToSNVs     is variant selection limited to SNVs ?
   * @param bed             list of all well covered positions
   * @param qcParam         the QC parameters
   * @throws EmptyParameterException if at least one of the values is null
   */
  public AskSession(String clientPublicKey, String dataset, String gnomADVersion, double maxAF, String subpop, double maxAFSubpop, String minCSQ, boolean limitToSNVs, BedFile bed, QCParam qcParam) throws EmptyParameterException {
    this.setClientPublicKey(clientPublicKey);
    this.setDataset(dataset);
    this.setGnomADVersion(gnomADVersion);
    this.setMaxAF(maxAF);
    this.setSubpopulation(subpop);
    this.setMaxAFSubpop(maxAFSubpop);
    this.setMinCSQ(minCSQ);
    this.setLimitToSNVs(limitToSNVs);
    this.setBedFile(bed);
    this.setQCParam(qcParam);
  }

  /**
   * Gets the Public RSA Key of the Client (as a one line pem String)
   *
   * @return the Public RSA Key of the Client (as a one line pem String)
   */
  public final String getClientPublicKey() {
    return this.getValue(Key.CLIENT_PUB_RSA);
  }

  /**
   * Sets the ClientPublicKey parameter's value
   *
   * @param clientPublicKey Public RSA Key of the Client (as a one line pem String)
   * @throws EmptyParameterException if the value is null
   */
  private void setClientPublicKey(String clientPublicKey) throws EmptyParameterException {
    this.set(Key.CLIENT_PUB_RSA, clientPublicKey);
  }

  /**
   * Gets the name of the dataset chosen by the client (from the list of datasets previously sent by the RPP)
   *
   * @return the name of the dataset chosen by the client (from the list of datasets previously sent by the RPP)
   */
  public final String getDataset() {
    return this.getValue(Key.DATASET);
  }

  /**
   * Sets the dataset parameter's value
   *
   * @param dataset the name of the dataset chosen by the client (from the list of datasets previously sent by the RPP)
   * @throws EmptyParameterException if the value is null
   */
  private void setDataset(String dataset) throws EmptyParameterException {
    this.set(Key.DATASET, dataset);
  }

  /**
   * Gets the least severe vep consequence that will be used to filter the variants
   *
   * @return the least severe vep consequence that will be used to filter the variants
   */
  public final String getMinCSQ() {
    return this.getValue(Key.MIN_CSQ);
  }

  /**
   * Sets the minCSQ parameter's value
   *
   * @param minCSQ the least severe vep consequence that will be used to filter the variants
   */
  private void setMinCSQ(String minCSQ) {
    try {
      this.set(Key.MIN_CSQ, minCSQ);
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }

  /**
   * Gets the maximum allele frequency that will be used to filter the variants
   *
   * @return the maximum allele frequency that will be used to filter the variants
   */
  public final double getMaxAF() {
    return Double.parseDouble(this.getValue(Key.MAX_MAF));
  }

  /**
   * Sets the maxAF parameter's value
   *
   * @param maxAF the maximum allele frequency that will be used to filter the variants
   */
  private void setMaxAF(double maxAF) {
    try {
      this.set(Key.MAX_MAF, maxAF + "");
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }
  
  /**
   * Gets the maximum allele frequency on the subpopulation that will be used to filter the variants
   *
   * @return the maximum allele frequency on the subpopulation that will be used to filter the variants
   */
  public final double getMaxAFSubpop() {
    return Double.parseDouble(this.getValue(Key.MAX_MAF_SUBPOP));
  }

  /**
   * Sets the maxAFNFE parameter's value
   *
   * @param maxAFSubpop the maximum allele frequency on the subpopulation that will be used to filter the variants
   */
  private void setMaxAFSubpop(double maxAFSubpop) {
    try {
      this.set(Key.MAX_MAF_SUBPOP, maxAFSubpop + "");
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }

  /**
   * Gets the version of GnomAD chosen by the client
   * @return the version of GnomAD chosen by the client
   */
  public final String getGnomADVersion() { return this.getValue(Key.GNOMAD_VERSION);}

  /**
   * Sets the version of GnomAD chosen by the client
   * @param gnomADVersion the version of GnomAD chosen by the client
   */
  private void setGnomADVersion(String gnomADVersion) {
    try {
      this.set(Key.GNOMAD_VERSION, gnomADVersion);
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }

  /**
   * Gets the GnomAD subpopulation
   * @return the GnomAD subpopulation
   */
  public final String getSubpopulation() { return this.getValue(Key.SUBPOPULATION);}

  /**
   * Sets the GnomAD subpopulation
   * @param subpopulation the GnomAD subpopulation
   */
  private void setSubpopulation(String subpopulation) {
    try {
      this.set(Key.SUBPOPULATION, subpopulation);
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }
  
  /**
   * Tells if variant extraction is limited to SNVs
   * 
   * @return true if variant extraction is limited to SNVs
   */
  public final boolean getLimitToSNVs(){
    return Boolean.parseBoolean(this.getValue(Key.LIMIT_TO_SNVS));
  }
  
  /**
   * Limits (or not) variant selection to SNVs
   * @param limitToSNVs true to limit variant selection to SNVs
   */
  private void setLimitToSNVs(boolean limitToSNVs){
    try {
      this.set(Key.LIMIT_TO_SNVS, limitToSNVs + "");
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }
  
  /**
   * Sets the list of positions that are well covered
   * @param bed  the list of positions that are well covered
   */
  public void setBedFile(BedFile bed) {
    try {
      this.set(Key.BED_FILE, bed.serialize());
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }

  /**
   * Gets the list of well covered position
   * @return the list of well covered position
   * @throws BedRegion.BedRegionException if there is a problem with the bed regions
   */
  public BedFile getBedFile() throws BedRegion.BedRegionException {
    return BedFile.deserialize(this.getValue(Key.BED_FILE));
  }

  /**
   * Sets the QC parameters
   * @param qcParam the QC parameters
   */
  public void setQCParam(QCParam qcParam){
    try {
      this.set(Key.QC_PARAM, qcParam.serialize());
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }

  /**
   * Gets the QC parameters
   * @return the QC parameters
   * @throws QCException if there is a problem with the QC parameters
   */
  public QCParam getQCParam() throws QCException {
    return QCParam.deserialize(this.getValue(Key.QC_PARAM));
  }
}

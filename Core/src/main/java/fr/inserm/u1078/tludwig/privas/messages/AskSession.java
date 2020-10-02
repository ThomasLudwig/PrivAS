package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.utils.BedFile;
import fr.inserm.u1078.tludwig.privas.utils.BedRegion;

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
  public AskSession() {
  }

  /**
   * Constructor from a set of parameters values
   *
   * @param clientPublicKey the Public RSA Key of the Client (as a one line pem String)
   * @param dataset         the name of the dataset chosen by the client (from the list of datasets previously sent by the RPP)
   * @param maxAF           the maximum allele frequency from GnomAD that will be used to filter the variants
   * @param maxAFNFE           the maximum allele frequency from GnomAD_NFE that will be used to filter the variants
   * @param minCSQ          the least severe vep consequence that will be used to filter the variants
   * @param limitToSNVs     is variant selection limited to SNVs ?
   * @param bed             list of all well covered positions
   * @throws EmptyParameterException if at least one of the values is null
   */
  public AskSession(String clientPublicKey, String dataset, double maxAF, double maxAFNFE, String minCSQ, boolean limitToSNVs, BedFile bed) throws EmptyParameterException {
    this.setClientPublicKey(clientPublicKey);
    this.setDataset(dataset);
    this.setMaxAF(maxAF);
    this.setMaxAFNFE(maxAFNFE);
    this.setMinCSQ(minCSQ);
    this.setLimitToSNVs(limitToSNVs);
    this.setBedFile(bed);
  }

  /**
   * Gets the Public RSA Key of the Client (as a one line pem String)
   *
   * @return
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
   * @return
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
   * @return
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
   * @return
   */
  public final double getMaxAF() {
    return Double.valueOf(this.getValue(Key.MAX_MAF));
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
   * Gets the maximum allele frequency that will be used to filter the variants
   *
   * @return
   */
  public final double getMaxAFNFE() {
    return Double.valueOf(this.getValue(Key.MAX_MAF_NFE));
  }

  /**
   * Sets the maxAFNFE parameter's value
   *
   * @param maxAFNFE the maximum allele frequency that will be used to filter the variants
   */
  private void setMaxAFNFE(double maxAFNFE) {
    try {
      this.set(Key.MAX_MAF_NFE, maxAFNFE + "");
    } catch (EmptyParameterException ex) {
      //Ignore, impossible
    }
  }
  
  /**
   * Tells if variant extraction is limited to SNVs
   * 
   * @return 
   */
  public final boolean getLimitToSNVs(){
    return Boolean.valueOf(this.getValue(Key.LIMIT_TO_SNVS));
  }
  
  /**
   * Limits (or not) variant selection to SNVs
   * @param limitToSNVs 
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
   * @param bed 
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
   * @return 
   * @throws BedRegion.BedRegionException
   */
  public BedFile getBedFile() throws BedRegion.BedRegionException {
    return BedFile.deserialize(this.getValue(Key.BED_FILE));
  }
}

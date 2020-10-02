package fr.inserm.u1078.tludwig.privas.listener;

/**
 * Interface for an observer to register to receive notifications of changes to a Session
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-11
 */
public interface SessionListener {

  /**
   * Gives notification that the Session ID has been updated
   *
   * @param id the new value of Session ID
   */
  void idUpdated(String id);

  /**
   * Gives notification that the Session Hash Salt has been updated
   *
   * @param hash the new value of the Session Hash Salt
   */
  void hashUpdated(String hash);

  /**
   * Gives notification that the Minor Allele Frequency Threshold has been updated
   *
   * @param maf the new value of the Minor Allele Frequency Threshold
   */
  void maxMAFUpdated(double maf);
  
  /**
   * Gives notification that the Minor Allele Frequency Threshold has been updated
   *
   * @param maf the new value of the Minor Allele Frequency Threshold
   */
  void maxMAFNFEUpdated(double maf);

  /**
   * Gives notification that the Consequence threshold has been updated
   *
   * @param csq the new value of the Consequence threshold
   */
  void leastSeverConsequenceUpdated(String csq);
  
  /**
   * Gives notification that the inclusion/exclusion of indel has been updated
   * @param limitToSNVs is variant extraction limited to SNVs ?
   */
  void limitToSNVsUpdated(boolean limitToSNVs);

  /**
   * Gives notification that the AES Key has been updated
   *
   * @param aes the new value of AES Key
   */
  void aesKeyUpdated(String aes);

  /**
   * Gives notification that the Client Genotype File name has been updated
   *
   * @param genotypeFilename
   * @param lines the new value of the Client Genotype File name
   */
  void clientGenotypeFilenameUpdated(String genotypeFilename, int lines);

  /**
   * Gives notification that the Selected Dataset has been updated
   *
   * @param dataset the new value of the Selected Dataset
   */
  void selectedDatasetUpdated(String dataset);

  /**
   * Gives notification that the Datasets Available have been updated
   *
   * @param datasets the new value of Available Datasets
   */
  void availableDatasetsUpdated(String datasets);

  /**
   * Gives notification that the Client Public Key has been updated
   *
   * @param publicKey the new value of the Client Public Key
   */
  void clientPublicKeyUpdated(String publicKey);

  /**
   * Gives notification that the Client Private Key has been updated
   *
   * @param privateKey the new value of the Client Private Key
   */
  void clientPrivateKeyUpdated(String privateKey);

  /**
   * Gives notification that the RPP Status has been updated
   *
   * @param status the new value of the RPP Status
   */
  void rppStatusUpdated(String status);

  /**
   * Gives notification that the RPP is ready to process the session
   */
  void sessionReady();

  /**
   * Gives notification that the Third Party Public Key has been updated
   *
   * @param thirdPartyKey the new value of Third Party Public Key
   */
  void thirdPartyPublicKeyUpdated(String thirdPartyKey);

  /**
   * Gives notification that the Third Party Public Name has been updated
   *
   * @param thirdPartyName the new value of Third Party Public Name
   */
  void thirdPartyPublicNameUpdated(String thirdPartyName);

  /**
   * Gives notification that the RPP address:port has been updated
   *
   * @param address     the new value of the RPP address
   * @param portNumber  the new value of the RPP port
   */
  void rppAddressUpdated(String address, int portNumber);

  /**
   * Gives notification that the selected Algorithm has been updated
   *
   * @param algorithm the new value of the selected Algorithm
   */
  void algorithmUpdated(String algorithm);
}

package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.utils.BedFile;
import fr.inserm.u1078.tludwig.privas.utils.BedRegion;

/**
 * Message from the RPP to the Client acknowledging the creation of a new Session.
 * The message also carries the ID of the Session as well as the Public RSA Key from the Third Party Server
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class SendSession extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  public SendSession() {
  }

  /**
   * Constructor from a set of parameters' values
   *
   * @param session             the Session ID
   * @param encryptedHashKey    the Hash Salt shared between the RPP and the Client
   * @param thirdPartyPublicKey the Public RSA Key from the Third Party Server
   * @param bed                 list of all well covered positions
   * @throws Message.EmptyParameterException if at least one of the parameters is null
   */
  public SendSession(String session, String encryptedHashKey, String thirdPartyPublicKey, BedFile bed) throws EmptyParameterException {
    super(session);
    this.setEncryptedHashKey(encryptedHashKey);
    this.setThirdPartyPublicKey(thirdPartyPublicKey);
    this.setBedFile(bed);
  }

  /**
   * Gets the Hash Salt shared between the RPP and the Client
   *
   * @return
   */
  public final String getEncryptedHashKey() {
    return this.getValue(Key.ENCRYPTED_HASH);
  }

  /**
   * Sets the encryptedHash parameter's value
   *
   * @param encryptedHash the Hash Salt shared between the RPP and the Client
   * @throws Message.EmptyParameterException if the value is null
   */
  private void setEncryptedHashKey(String encryptedHash) throws EmptyParameterException {
    this.set(Key.ENCRYPTED_HASH, encryptedHash);
  }

  /**
   * Gets the Public RSA Key from the Third Party Server
   *
   * @return
   */
  public final String getThirdPartyPublicKey() {
    return this.getValue(Key.THIRD_PUB_RSA);
  }

  /**
   * Sets the thirdPartyPublicKey parameter's value
   *
   * @param thirdPartyPublicKey the Public RSA Key from the Third Party Server
   * @throws Message.EmptyParameterException if the value is null
   */
  private void setThirdPartyPublicKey(String thirdPartyPublicKey) throws EmptyParameterException {
    this.set(Key.THIRD_PUB_RSA, thirdPartyPublicKey);
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

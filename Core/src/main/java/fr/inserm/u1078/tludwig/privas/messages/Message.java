package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.instances.MessageException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Message passed between two Instances (Client / Server)
 * <p>
 * Messages are of a given type (String) and can carry parameters (wrapped as Strings and referred to by keys
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-30
 *
 * Javadoc complete on 2019-08-07
 */
//DONE  Message : Message Types as subclasses 
public abstract class Message implements Serializable {

  /**
   * Possible parameter Keys
   */
  enum Key {
    DATASETS, //SendRPPConfiguration
    TPS_NAME, //SendRPPConfiguration
    GNOMAD_VERSIONS, //SendRPPConfiguration
    CLIENT_PUB_RSA, //AskSession
    DATASET, //AskSession
    GNOMAD_VERSION, //AskSession
    MAX_MAF, //AskSession
    SUBPOPULATION, //AskSession
    MAX_MAF_SUBPOP, //AskSession
    LIMIT_TO_SNVS, //AskSession
    MIN_CSQ, //AskSession
    QC_PARAM, //AskSession
    BED_FILE, //AskSession, SendSession
    SESSION, //SessionMessage
    ENCRYPTED_HASH, //SendSession
    THIRD_PUB_RSA, //SendSession
    ENCRYPTED_AES, //SendClientData
    ENCRYPTED_CLIENT_DATA, //SendClientData
    ENCRYPTED_CLIENT_EXCLUDED_VARIANT, //SendClientData
    ALGORITHM, //SendClientData
    STATUS, //SendRPPStatus, SendTPSStatus
    ERROR_MESSAGE, //SendError
    ENCRYPTED_RESULTS //SendResults
  }

  /**
   * Map to stores parameters
   */
  private final HashMap<Key, String> parameters;

  /**
   * Factory Type Message Builder
   *
   * @param type       the type of the Message
   * @param parameters parameters of the Message
   * @return the new Message
   * @throws MessageException if there if the message type is not recognized
   */
  public static Message buildMessage(String type, HashMap<String, String> parameters) throws MessageException {
    Class<?> clazz;
    try {
      clazz = Class.forName(type);  
    } catch (ClassNotFoundException e) {
      throw new MessageException(MSG.cat(MSG.MSG_UNKNOWN_TYPE, type), e);
    }
    
    try {
      Constructor<?> constructor;
      constructor = clazz.getConstructor();
      Message m = (Message) constructor.newInstance();
      for (String k : parameters.keySet())
        m.set(Key.valueOf(k), parameters.get(k));
      return m;
    } catch (EmptyParameterException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
      throw new MessageException(MSG.cat(MSG.cat(MSG.MSG_UNABLE_BUILD, type), e),e);
    }    
  }

  /**
   * Empty Constructor
   */
  Message() {
    this.parameters = new HashMap<>();
  }

  /**
   * Gets the type of the Message
   * The type is the full class name
   *
   * @return the type of Message
   */
  public final String getType() {
    return this.getClass().getName();
  }

  /**
   * Gets the names (Keys) of all the Message's parameters
   *
   * @return the names (Keys) of all the Message's parameters
   */
  public final String getKeys() {
    StringBuilder sb = new StringBuilder();
    boolean append = false;
    for (Key key : this.parameters.keySet()) {
      if (append)
        sb.append(",");
      sb.append(key);
      append = true;
    }
    return sb.toString();
  }

  /**
   * Sets a parameters
   *
   * @param key   the name of the parameter
   * @param value the value of the parameter
   * @throws Message.EmptyParameterException if value is null
   */
  void set(Key key, String value) throws EmptyParameterException {
    if (value == null)
      throw new EmptyParameterException(key);
    this.parameters.put(key, value);
  }

  /**
   * Gets a parameter's value
   *
   * @param key the name of the parameter
   * @return a value for the given key
   */
  String getValue(Key key) {
    return this.parameters.get(key);
  }
  
    
  public static String INTERRUPT(Runnable r){
    return "Thread "+r.toString()+" has been unexpectedly interrupted";
  }

  /**
   * Exception thrown when trying to affect a null value to a parameter
   */
  public static class EmptyParameterException extends Exception {

    /**
     * Constructor
     *
     * @param key the name of the parameter with a null value
     */
    private EmptyParameterException(Key key) {
      super(MSG.cat(MSG.MSG_MISSING_PARAMETER, key.toString()));
    }
  }
}

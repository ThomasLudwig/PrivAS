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
 * Messages are of a given type (String) and can carry parameters (wrapped as Strings and refered to by keys
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
    CLIENT_PUB_RSA, DATASET, MAX_MAF, MAX_MAF_NFE, LIMIT_TO_SNVS, MIN_CSQ, DATASETS, TPS_NAME, ENCRYPTED_AES, ENCRYPTED_CLIENT_DATA, ENCRYPTED_CLIENT_EXCLUDED_VARIANT, ALGORITHM, ERROR_MESSAGE, ENCRYPTED_RESULTS, ENCRYPTED_HASH, THIRD_PUB_RSA, STATUS, SESSION, BED_FILE, QC_PARAM, EXCLUDED_VARIANTS
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
   * @return
   * @throws MessageException
   */
  public static Message buildMessage(String type, HashMap<String, String> parameters) throws MessageException {
    Class<?> clazz;
    try {
      clazz = Class.forName(type);  
    } catch (ClassNotFoundException e) {
      throw new MessageException("Unknown Message Type ["+type+"]", e);
    }
    
    try {
      Constructor<?> ctor;
      ctor = clazz.getConstructor();
      Message m = (Message) ctor.newInstance();
      for (String k : parameters.keySet())
        m.set(Key.valueOf(k), parameters.get(k));
      return m;
    } catch (EmptyParameterException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
      throw new MessageException("Unable to build new message of type ["+type+"] ("+e.getClass().getSimpleName()+" : "+e.getMessage()+")",e);
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
   * @return
   */
  public final String getType() {
    return this.getClass().getName();
  }

  /**
   * Gets the names (Keys) of all the Message's parameters
   *
   * @return
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
   * @return
   */
  String getValue(Key key) {
    return this.parameters.get(key);
  }
  
    
  public static String INTERRUPT(Runnable r){
    return "Thread "+r.toString()+" has been unexcpectedly interrupted";
  }

  /**
   * Exception thrown when trying to affect a null value to a parameter
   */
  public class EmptyParameterException extends Exception {

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

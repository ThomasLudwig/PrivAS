package fr.inserm.u1078.tludwig.privas.utils.qc;

/**
 * Exception thrown during Quality Control
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
@SuppressWarnings("unused")
public class QCException extends Exception{

  public QCException() {
  }

  public QCException(String message) {
    super(message);
  }

  public QCException(String key, String value){
    this("Unexpected key ["+key+"] value {"+value+"}");
  }

  public QCException(String message, Throwable cause) {
    super(message, cause);
  }

  public QCException(Throwable cause) {
    super(cause);
  }

  public QCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

package fr.inserm.u1078.tludwig.privas.listener;

/**
 * Interface for an observer to register to receive notifications of changes to an operation's Progress
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-15
 */
public interface ProgressListener {

  /**
   * Gives notification that the operation's progress has been updated
   *
   * @param percent - the percent of completion of the operation
   */
  void progressChanged(int percent);
}

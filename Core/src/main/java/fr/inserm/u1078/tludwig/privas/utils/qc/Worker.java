package fr.inserm.u1078.tludwig.privas.utils.qc;

import javafx.util.Pair;

/**
 * QC Worker class
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
class Worker implements Runnable {

  private final Reader reader;
  private final QCParam qcParam;
  private final Consumer consumer;

  Worker(Reader r, QCParam qcParam, Consumer consumer) {
    this.reader = r;
    this.qcParam = qcParam;
    this.consumer = consumer;
  }

  @Override
  public void run() {
    Pair<Integer, String> read = reader.getNext();
    while (read.getValue() != null) {
      QCVariant v = new QCVariant(read.getValue());
      int filter =  v.filter(qcParam);
      this.consumer.pushOutput(read.getKey(), read.getValue(), filter);
      read = reader.getNext();
    }
    this.consumer.pushOutput(read.getKey(), QualityControl.END_MESSAGE, -1);
  }
}

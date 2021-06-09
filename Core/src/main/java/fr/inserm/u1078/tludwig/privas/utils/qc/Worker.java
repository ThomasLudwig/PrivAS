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
    //System.out.println("New Worker");
    this.reader = r;
    this.qcParam = qcParam;
    this.consumer = consumer;
  }

  @Override
  public void run() {
    //System.out.println("Worker started "+Thread.currentThread());
    Pair<Integer, String> read = reader.getNext();
    //System.out.println("Worker first "+Thread.currentThread());
    while (read.getValue() != null) {
      //System.err.println(Thread.currentThread()+" read");
      QCVariant v = new QCVariant(read.getValue());
      int filter =  v.filter(qcParam);
      this.consumer.pushOutput(read.getKey(), read.getValue(), filter);
      read = reader.getNext();
    }
    //System.out.println("Worker ending "+Thread.currentThread());
    this.consumer.pushOutput(read.getKey(), QualityControl.END_MESSAGE, -1);
  }
}

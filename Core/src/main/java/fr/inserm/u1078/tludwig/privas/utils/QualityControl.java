package fr.inserm.u1078.tludwig.privas.utils;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Quality Control on VCF
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-12
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class QualityControl {

  public static final int WORKERS = Math.max(1, Math.min(8, Runtime.getRuntime().availableProcessors() - 2));//Number of workers, there must be one consumer and one reader
  public static final String END_MESSAGE = "XXX_NO_MORE_LINES_XXX";
  public static final int STEP = 10000;

  public static int applyQC(String inputVCF, QCParam qcParam) throws IOException, QCException {
    String outputVCF = FileUtils.getQCVCFFilename(inputVCF, qcParam);
    String excludedVariants = FileUtils.getExcludedVariantFilename(inputVCF, qcParam);
    return applyQC(inputVCF, qcParam, outputVCF, excludedVariants, false);
  }

  public static int applyQC(String inputVCF, String qcParam, String outputVCF, String excludedVariants, boolean gzipped) throws QCException, IOException {
    return applyQC(inputVCF, new QCParam(qcParam), outputVCF, excludedVariants, gzipped);
  }


    public static int applyQC(String inputVCF, QCParam qcParam, String outputVCF, String excludedVariants, boolean gzipped) throws QCException, IOException {
    //Count header to skip
    int skipHeader = 0;
    UniversalReader in = new UniversalReader(inputVCF);
    PrintWriter out = new PrintWriter(new FileWriter(outputVCF)); //TODO gzipped
    PrintWriter exc = new PrintWriter(new FileWriter(excludedVariants)); //TODO gzipped
    String line;
    while((line = in.readLine()) != null && line.startsWith("#")) {
      out.println(line);
        skipHeader++;
    }
    in.close();

    Consumer consumer = new Consumer(out, exc);
    Reader reader = new Reader(new UniversalReader(inputVCF), skipHeader);

    ExecutorService threadPool = Executors.newFixedThreadPool(WORKERS + 1);
    System.out.println("Workers : "+WORKERS);
    threadPool.submit(consumer);
    for(int i = 0; i < WORKERS; i++)
      threadPool.submit(new Worker(reader, qcParam, consumer));
    threadPool.shutdown();
    try {
      threadPool.awaitTermination(100, TimeUnit.DAYS);
    } catch (InterruptedException ignore) {
      //Ignore
    }

    return consumer.filters[0];
  }

  public static class QCException extends Exception {
    public QCException() {
    }

    public QCException(String message) {
      super(message);
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

  public static class Reader {
    private final UniversalReader in;
    private int read = 0;
    private boolean closed = false;

    public Reader(UniversalReader in, int skipHeader) {
      this.in = in;
      for(int i = 0 ; i < skipHeader; i++) {
        try {
          in.readLine();
        } catch (IOException ignore) {
          //ignore
        }
      }
    }

    public synchronized Read getNext() {
      Read r = new Read(read++);
      if(closed)
        return r;
      try {
        String line = in.readLine();
        if(line == null) {
          close();
        } else
          r.line = line;
      } catch (IOException e) {
        e.printStackTrace();
      }
      return r;
    }

    private void close() throws IOException{
      if(!closed){
        closed = true;
        in.close();
      }
    }
  }

  public static class Read {
    private final int number;
    private String line;

    public Read(int number){
      this.number = number;
      this.line = null;
    }

  }

  public static class Worker implements Runnable {

    private final Reader reader;
    private final QCParam qcParam;
    private final Consumer consumer;

    public Worker(Reader r, QCParam qcParam, Consumer consumer) {
      //System.out.println("New Worker");
      this.reader = r;
      this.qcParam = qcParam;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      //System.out.println("Worker started "+Thread.currentThread());
      Read read = reader.getNext();
      //System.out.println("Worker first "+Thread.currentThread());
      while (read.line != null) {
        //System.err.println(Thread.currentThread()+" read");
        QCVariant v = new QCVariant(read.line);
        int filter =  v.filter(qcParam);
        this.consumer.pushOutput(read.number, read.line, filter);
        read = reader.getNext();
      }
      //System.out.println("Worker ending "+Thread.currentThread());
      this.consumer.pushOutput(read.number, END_MESSAGE, -1);
    }
  }

  public static class Output {

    public final int n;
    public final String line;
    public final int filter;

    public Output(int n, String line, int filter) {
      this.n = n;
      this.line = line;
      this.filter = filter;
    }
  }

  public static class Consumer extends Thread {

    private final PrintWriter out;
    private final PrintWriter exc;
    private final ArrayList<Output> desynchronizedPulledOutput;
    private final LinkedBlockingQueue<Output> outputLines;
    private long start;
    private int[] filters = new int[1024];

    public Consumer(PrintWriter out, PrintWriter exc) {
      this.out = out;
      this.exc = exc;
      this.desynchronizedPulledOutput = new ArrayList<>();
      outputLines = new LinkedBlockingQueue<>(20 * WORKERS);
    }

    public void pushOutput(int n, String line, int filter) {
      try {
        this.outputLines.put(new Output(n, line, filter));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    private boolean process(Output output) {
      //Process output
      if (END_MESSAGE.equals(output.line)) {
        out.close();
        exc.close();
        System.out.println(summary(filters));
        return false;
      }

      filters[output.filter]++;
      if(output.filter == 0)
        out.println(output.line);
      else
        exclude(output.line);
      if (output.n % STEP == 0) {
        System.out.println(summary(filters));
      }
      return true;
    }

    private void exclude(String line){
      String[] f = line.split("\t");
      String chr = f[0];
      String pos = f[1];
      String ref = f[3];
      String[] alts = f[4].split(",");

      for(String alt : alts)
        exc.println(GenotypesFileHandler.getCanonical(chr, pos, ref, alt));
    }

    private Output remove(int nb) {
      for (int i = 0; i < this.desynchronizedPulledOutput.size(); i++)
        if (this.desynchronizedPulledOutput.get(i).n == nb)
          return this.desynchronizedPulledOutput.remove(i);

      return null;
    }

    @Override
    public void run() {
      start = new Date().getTime();
      boolean running = true;

      int nb = 1;
      while (running)
        try {
          Output output = outputLines.take();
          if (output.n == nb) {
            if (!process(output))
              running = false;
            nb++;
          } else { //out.n > nb
            this.desynchronizedPulledOutput.add(output);

            Output lines;// = this.unqueuedOutput.remove(nb);
            while ((lines = remove(nb)) != null) {
              if (!process(lines))
                running = false;
              nb++;
            }
          }
        } catch (InterruptedException ignore) {
          //fatalAndDie("Consumer interrupted", e);
        }
    }

    public static String summary(int[] filters){
      int total = filters[0];
      int pass = filters[0];
      int qd = 0;
      int inbreeding = 0;
      int mqranksum = 0;
      int fs = 0;
      int sor = 0;
      int mq = 0;
      int readpos = 0;
      int abhet = 0;
      int callrate = 0;
      //int hq = 0;

      for(int i = 1; i < QCVariant.FILTER_NUMBER; i++){
        total += filters[i];
        if((i&QCVariant.FILTER_QD) == QCVariant.FILTER_QD)
          qd += filters[i];
        if((i&QCVariant.FILTER_INBREEDING) == QCVariant.FILTER_INBREEDING)
          inbreeding += filters[i];
        if((i&QCVariant.FILTER_MQRANKSUM) == QCVariant.FILTER_MQRANKSUM)
          mqranksum += filters[i];
        if((i&QCVariant.FILTER_FS) == QCVariant.FILTER_FS)
          fs += filters[i];
        if((i&QCVariant.FILTER_SOR) == QCVariant.FILTER_SOR)
          sor += filters[i];
        if((i&QCVariant.FILTER_MQ) == QCVariant.FILTER_MQ)
          mq += filters[i];
        if((i&QCVariant.FILTER_READPOSRANKSUM) == QCVariant.FILTER_READPOSRANKSUM)
          readpos += filters[i];
        if((i&QCVariant.FILTER_ABHETDEV) == QCVariant.FILTER_ABHETDEV)
          abhet += filters[i];
        if((i&QCVariant.FILTER_CALLRATE) == QCVariant.FILTER_CALLRATE)
          callrate += filters[i];
        /*if((i&QCVariant.FILTER_HQ) == QCVariant.FILTER_HQ)
          hq += filters[i];*/
      }
      return "Total["+total+"] "
              + "Pass["+pass+"] "
              + "QD["+qd+"] "
              + "Inbreeding["+inbreeding+"] "
              + "MQRS["+mqranksum+"] "
              + "FS["+fs+"] "
              + "SOR["+sor+"] "
              + "MQ["+mq+"] "
              + "ReadPos["+readpos+"] "
              + "ABHET["+abhet+"] "
              + "CallRate["+callrate+"] "
              //+ "HQ["+hq+"] "
              ;
    }
  }
}

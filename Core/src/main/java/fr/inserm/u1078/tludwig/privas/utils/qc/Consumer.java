package fr.inserm.u1078.tludwig.privas.utils.qc;

import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Quality Control Consumer
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class Consumer extends Thread {
  private final PrintWriter out;
  private final PrintWriter exc;
  private final ArrayList<Output> desynchronizedPulledOutput;
  private final LinkedBlockingQueue<Output> outputLines;
  private final int[] filters = new int[1024];
  private final int step;

  public Consumer(PrintWriter out, PrintWriter exc, int workers, int step) {
    this.out = out;
    this.exc = exc;
    this.step = step;
    this.desynchronizedPulledOutput = new ArrayList<>();
    outputLines = new LinkedBlockingQueue<>(20 * workers);
  }

  public void pushOutput(int n, String line, int filter) {
    try {
      this.outputLines.put(new Output(n, line, filter));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public int getFilter(int i) {
    return this.filters[i];
  }

  private boolean process(Output output) {
    //Process output
    if (QualityControl.END_MESSAGE.equals(output.line)) {
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
    if (output.n % step == 0) {
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
}

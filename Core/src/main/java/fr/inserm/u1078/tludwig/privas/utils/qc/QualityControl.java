package fr.inserm.u1078.tludwig.privas.utils.qc;

import fr.inserm.u1078.tludwig.privas.utils.FileUtils;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

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

  public static void applyQC(String inputVCF, QCParam qcParam) throws IOException, QCException {
    String outputVCF = FileUtils.getQCVCFFilename(inputVCF, qcParam);
    String excludedVariants = FileUtils.getExcludedVariantFilename(inputVCF, qcParam);
    applyQC(inputVCF, qcParam, outputVCF, excludedVariants, false);
  }

  public static void applyQC(String inputVCF, String qcParam, String outputVCF, String excludedVariants, boolean gzipped) throws QCException, IOException {
    applyQC(inputVCF, new QCParam(qcParam), outputVCF, excludedVariants, gzipped);
  }

  public static void applyQC(String inputVCF, QCParam qcParam, String outputVCF, String excludedVariants, boolean gzipped) throws IOException {
    //Count header to skip
    int skipHeader = 0;
    UniversalReader in = new UniversalReader(inputVCF);
    PrintWriter out = new PrintWriter(new GZIPOutputStream(new FileOutputStream(outputVCF)));//gzipped
    //PrintWriter exc = new PrintWriter(new GZIPOutputStream(new FileOutputStream(excludedVariants)));//gzipped -- unable to read back
    //PrintWriter out = new PrintWriter(new FileWriter(outputVCF));
    PrintWriter exc = new PrintWriter(new FileWriter(excludedVariants));
    String line;
    while((line = in.readLine()) != null && line.startsWith("#")) {
      out.println(line);
        skipHeader++;
    }
    in.close();

    Consumer consumer = new Consumer(out, exc, WORKERS, STEP);
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

    consumer.getFilter(0);
  }
}

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

  /**
   * Apply QC
   * @param inputVCF the VCF File on which to apply the QC
   * @param qcParam the QCParameters
   * @return the number of filtered out variants
   * @throws IOException If an I/O error occurs while return inputs of writing output
   */
  public static int applyQC(String inputVCF, QCParam qcParam) throws IOException {
    String outputVCF = FileUtils.addQCPrefixToVCFFilename(inputVCF, qcParam);
    String excludedVariants = FileUtils.excludedVariantFromQCedVCF(outputVCF);
    return applyQC(inputVCF, qcParam, outputVCF, excludedVariants);
  }
/*
  public static void applyQC(String inputVCF, String qcParam, String outputVCF, String excludedVariants) throws QCException, IOException {
    applyQC(inputVCF, new QCParam(qcParam), outputVCF, excludedVariants);
  }*/

  /**
   * Apply QC
   * @param inputVCF the VCF File on which to apply the QC
   * @param qcParam the QCParameters
   * @param outputVCF the filtered VCF
   * @param excludedVariants the List of Variants Excluded during the QC
   * @return the number of filtered out variants
   * @throws IOException If an I/O error occurs while return inputs of writing output
   */
  public static int applyQC(String inputVCF, QCParam qcParam, String outputVCF, String excludedVariants) throws IOException {
    //Count header to skip
    int skipHeader = 0;
    UniversalReader in = new UniversalReader(inputVCF);
    PrintWriter out = new PrintWriter(new GZIPOutputStream(new FileOutputStream(outputVCF)));//gzipped
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

    return consumer.getFilter(0);
  }
}

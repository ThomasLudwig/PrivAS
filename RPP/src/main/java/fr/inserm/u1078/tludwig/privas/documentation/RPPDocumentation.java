package fr.inserm.u1078.tludwig.privas.documentation;

import fr.inserm.u1078.tludwig.privas.Main;
import fr.inserm.u1078.tludwig.privas.constants.Command;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.Tag;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler;

/**
 * Generates an Up-to-date documentation for the RPP
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-28
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class RPPDocumentation extends Documentation {

  public static String getDocumentation() {
    LineBuilder doc = new LineBuilder();
    getTitle(doc);
    chooseSet(doc);
    configurationFile(doc);
    defaultQC(doc);
    getCommandLines(doc, Main.Party.RPP);
    return doc.toString();
  }

  public static void getTitle(LineBuilder doc){
    doc.rstChapter("Reference Panel Provider");
  }

  public static void chooseSet(LineBuilder doc){
    doc.rstSection("Choose a data set");
    doc.rstItemize(
            "a VCF File (must be VEP annotated, with the following annotations : "+code(GenotypesFileHandler.VEP_GENE)+", "+code(GenotypesFileHandler.VEP_SOURCE)+" and "+code(GenotypesFileHandler.VEP_ALLELE_NUM)+")",
            "a Bed of well covered position (capture kit - minus positions with low coverage)"
    );
    doc.newLine();
  }

  public static void defaultQC(LineBuilder doc){
    String qc = FileFormat.QC_PREFIX+"122186898";
    String prefix = "my_precious_data";
    doc.rstSection("Run the default QC");
    doc.append(paragraph("To speed-up session times for your Client, you can run the default QC prior to launching your server. " +
            "This has top be done for each of your GnomAD versions. "));
    doc.append(paragraph("In order for the Quality Control to work optimally, it is recommended to split multiallelic variants."));

    doc.rstCode(BASH, Command.VCF2GENOTYPES.getJavaCommand(Main.Party.RPP.getJar()));
    doc.rstTable(Command.VCF2GENOTYPES.getArguments(), false);
    doc.newLine();
    doc.newLine("This will produce the following files");
    doc.rstItemize(
            code(qc+"."+prefix+"."+ FileFormat.FILE_VCF_EXTENSION +"."+FileFormat.FILE_GZ_EXTENSION)+" "+italic("The VCF file of variants that PASS the QC"),
            code(qc+"."+prefix+"."+ FileFormat.FILE_EXCLUSION_EXTENSION)+" "+italic("The list of variants that FAIL the QC"),
            code("GnomAD.Version."+qc+"."+prefix+"."+ FileFormat.FILE_GENO_EXTENSION +"."+FileFormat.FILE_GZ_EXTENSION)+" "+italic("The variants in the "+FileFormat.FILE_GENO_EXTENSION+" file format, annotated with the frequencies from GnomAD")

    );
    doc.newLine();
    doc.rstNote("QC Parameters files are named according to the hash of their content. So the file containing the default parameters is named "+qc);
    doc.newLine();
    doc.newLine("If a Client runs a session with a new combination of QC Parameters / GnomAD Versions, this operation will be performed at the beginning of the session and the resulting files will be stored for future use.");
    doc.newLine();
  }

  public static void configurationFile(LineBuilder doc){

    doc.rstSection("The Server configuration file");
    doc.newLine("Each RPP server needs a TSV configuration file "+code("config.rpp")+" (or any other name), that has the following format:");
    doc.rstCode("text", Tag.getWholeCodeBlock());

    doc.newLine();
    doc.newLine("The content of this file is:");

    doc.rstTable(Tag.getWholeDocTable(), true);
    doc.newLine();
    doc.newLine("The following fields for each dataset description are separated by colons (:)");
    doc.rstItemize(
            "Dataset name (As informative as possible, especially in regard to the reference genome)",
            code("/path/to/file.vcf.gz")+" the VCF file annotated with vep",
            "number of variants sites in the VCF file",
            code("/path/to/coverage.bed.gz")+" the Bed file defining the well covered positions (any variants found in regions outside this scope will be ignored)"
    );
    doc.newLine();
  }
}

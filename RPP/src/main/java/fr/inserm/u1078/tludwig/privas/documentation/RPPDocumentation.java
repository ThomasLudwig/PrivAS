package fr.inserm.u1078.tludwig.privas.documentation;

import fr.inserm.u1078.tludwig.privas.Main;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;

import java.nio.MappedByteBuffer;

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
    defaultQC(doc);
    configurationFile(doc);

    return doc.toString();
  }

  public static void getTitle(LineBuilder doc){
    doc.rstChapter("Reference Panel Provider");
  }

  public static void chooseSet(LineBuilder doc){
    doc.rstSection("Choose a data set");
    doc.rstItemize(
            "a VCF File (must be VEP annotated with annotation : GnomAD frequencies, Symbol names and allele numbers)",
            "a Bed of well covered position (capture kit - minus badly coverage positions)"
    );
    doc.newLine();
  }

  public static void defaultQC(LineBuilder doc){
    String qc = "QC1352166484";
    String prefix = "my_precious_data";
    doc.rstSection("Run the default QC");
    doc.rstCode(BASH,
            "#Execute QC",
            "java -jar PrivAS.RPP.jar --defaultqc "+prefix+".vcf.gz "
    );
    doc.newLine();
    doc.newLine("This will produce the following files");
    doc.rstItemize(
            code(qc+"."+FileFormat.FILE_QC_PARAM_EXTENSION),
            code(qc+"."+prefix+"."+ FileFormat.FILE_GENO_EXTENSION +".gz"),
            code(qc+"."+prefix+"."+ FileFormat.FILE_EXCLUSION_EXTENSION)
    );
    doc.newLine();
    doc.rstNote("QC Parameters files are named according to the hash of their content. So the file containing the default parameters is named "+qc);
    doc.newLine();
    doc.newLine("When a Client ask for different parameters, a new QC will be automatically performed and the resulting files will be stored for future use.");
    doc.newLine();
  }

  public static void configurationFile(LineBuilder doc){
    int pad = 2+getMax(FileFormat.RPP_TAG_PORT, FileFormat.RPP_TAG_DATA, FileFormat.RPP_TAG_RPP_SESSION_DIR, FileFormat.RPP_TAG_RRP_EXPIRED_SESSION, FileFormat.RPP_TAG_TPS_NAME,
            FileFormat.RPP_TAG_TPS_ADDRESS, FileFormat.RPP_TAG_TPS_USER, FileFormat.RPP_TAG_TPS_LAUNCH_COMMAND, FileFormat.RPP_TAG_TPS_GETKEY_COMMAND, FileFormat.RPP_TAG_TPS_SESSION_DIR);
    doc.rstSection("The Server configuration file");
    doc.newLine("To launch an RPP server to need to execute the command:");
    doc.rstCode(BASH,
            "java -jar PrivAS.RPP.jar config.rpp"
    );
    doc.newLine();
    doc.newLine("where the TSV configuration file "+code("config.rpp")+" (or any other name) has the following format:");
    doc.rstCode("text",
            pad(FileFormat.RPP_TAG_PORT, pad)+"6666",
            pad(FileFormat.RPP_TAG_DATA, pad)+"Data_Name1:/path/to/file1.vcf.gz:65147:/path/to/coverage1.bed.gz,Data_Name2:/path/to/file2.vcf.gz:81791:/path/to/coverage2.bed.gz ",
            pad(FileFormat.RPP_TAG_RPP_SESSION_DIR, pad)+"/path/to/rpp_working_directory/sessions",
            pad(FileFormat.RPP_TAG_RRP_EXPIRED_SESSION, pad)+"/path/to/rpp_working_directory/expired.session.lst",
            pad(FileFormat.RPP_TAG_TPS_NAME, pad)+"The name of the TPS Super computer",
            pad(FileFormat.RPP_TAG_TPS_ADDRESS, pad)+"supercomputer.domain.ext",
            pad(FileFormat.RPP_TAG_TPS_USER, pad)+"username",
            pad(FileFormat.RPP_TAG_TPS_LAUNCH_COMMAND, pad)+"/path/to/PrivAS.TPS.sh",
            pad(FileFormat.RPP_TAG_TPS_GETKEY_COMMAND, pad)+"/path/to/PrivAS.getPublicKey.sh",
            pad(FileFormat.RPP_TAG_TPS_SESSION_DIR, pad)+"/path/to/tps_working_directory/sessions"
    );

    doc.newLine();
    doc.newLine("The content of this file is:");

    doc.rstTable(new String[][]{
            {"key", "description"},
            {code(FileFormat.RPP_TAG_PORT), "the port on which the RPP server will listen"},
            {code(FileFormat.RPP_TAG_DATA), "Comma-separated dataset description"},
            {code(FileFormat.RPP_TAG_RPP_SESSION_DIR), "the directory where RPP will store the files for each session"},
            {code(FileFormat.RPP_TAG_RRP_EXPIRED_SESSION), "the file where RPP will list expired sessions"},
            {code(FileFormat.RPP_TAG_TPS_NAME), "Name and description of the Third Party Server"},
            {code(FileFormat.RPP_TAG_TPS_ADDRESS), "fully qualified hostname.domain name or IP address"},
            {code(FileFormat.RPP_TAG_TPS_USER), "the SSH username that will be used on the TPS"},
            {code(FileFormat.RPP_TAG_TPS_LAUNCH_COMMAND), "the unix command execute by "+code(FileFormat.RPP_TAG_TPS_USER)+" on TPS to launch an Association Test"},
            {code(FileFormat.RPP_TAG_TPS_GETKEY_COMMAND), "tps_get_key_command: the unix command execute by "+code(FileFormat.RPP_TAG_TPS_USER)+" on TPS to generate of unique RSA keypair for each new session"},
            {code(FileFormat.RPP_TAG_TPS_SESSION_DIR), "the directory where TPS will store the files for each session"}
    }, true);
    doc.newLine();
    doc.newLine("The following fields for each dataset description are separated by colons (:)");
    doc.rstItemize(
            "Dataset name (As informative as possible, especially in regard to the reference genome)",
            code("/path/to/file.vcf.gz")+" the VCF file annotated with vep",
            "number of variants sites in the VCF file",
            code("/path/to/coverage.bed.gz")+" the Bed file defining the well covered positions (any variants found in regions outside this scope will be ignored)"
    );
  }
}

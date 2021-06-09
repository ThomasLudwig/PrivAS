package fr.inserm.u1078.tludwig.privas.documentation;

import fr.inserm.u1078.tludwig.privas.ClientRun;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.GUI;

/**
 * Generates an Up-to-date documentation for the Client
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-28
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class ClientDocumentation extends Documentation {

  public static String getClientDocumentation(){
    LineBuilder doc = new LineBuilder();
    getTitle(doc);
    getPreamble(doc);
    getLaunch(doc);
    getAssociationTests(doc);
    getMainWindows(doc);
    getCommandLines(doc);

    return doc.toString();

  }

  public static void getTitle(LineBuilder doc){
    doc.rstChapter("Client");
    doc.newLine();
    doc.newLine(".. role:: red");
    doc.newLine(".. role:: green");
    doc.newLine(".. role:: blue");
    doc.newLine();
  }

  public static void getPreamble(LineBuilder doc){
    doc.rstSection("Preamble");
    doc.append(paragraph("In order for the quality control to work optimally, it is recommended to split multiallelic variants."));
    doc.append(paragraph("To compare similar variants during the Association Tests, " +
            "input VCF files need to be annotated with vep (to added the consequence on genes and GnomAD frequencies). " +
            "The command line to do this (after having installed vep) is:"));
    doc.rstCode(BASH,
            "/path/to/vep " +
            "--cache --merged --offline --dir [/path/to/cache] " +
            "--fork 24 --buffer_size 25000 --species homo_sapiens --assembly GRCh37 " +
            "--use_given_ref --check_existing --allele_number --symbol --af_gnomad " +
            "--vcf -i input.vcf.gz -o annotated.vcf");
    doc.newLine();
  }

  public static void getLaunch(LineBuilder doc){
    doc.rstSection("Launch the Client GUI");
    doc.rstCode(BASH,
            "java -jar PrivAS.Client.jar [/path/to/data/directory]"
    );
    doc.newLine();
  }

  public static void getAssociationTests(LineBuilder doc){
    doc.rstSection("Performing Association Tests with PrivAS");
    int i = 1;
    image0(doc);

    doc.rstSubsection((i++)+". Load Variants");
    doc.newLine(
            "Load your annotated VCF file and perform a Quality Control on " +
            "the Variants "+menu(GUI.CW_MN_FILE,GUI.CW_MI_APPLY_QC_VCF)+". " +
            "Alternatively if you have already performed this operation for a previous session and you want the same QC parameters, " +
            "you can simple load the results "+code(FileFormat.FILE_GENO_EXTENSION)+" file, " +
            "this will be much quicker "+menu(GUI.CW_MN_FILE,GUI.CW_MI_LOAD_GENO)
            );
    image1a(doc);
    doc.rstItemize(
            "Choose your input VCF file",
            "Choose a previous QC Parameters file..."
    );
    image1b(doc);
    doc.newLine("...or create a new one");
    image1c(doc);
    doc.newLine("The variants are Loaded (as seen in the "+menu(GUI.SP_LABEL_GENOTYPE)+" field)");
    image1d(doc);

    doc.rstSubsection((i++) + ". Connect to a RPP Server");
    doc.newLine("Connect to a server "+ menu(GUI.CW_MN_SERVER,GUI.CW_MI_CONNECT));
    image2a(doc);
    doc.rstItemize(
            "Fill in the address",
            "Fill in the port number (default values point to a Server providing the FrEx dataset)"
    );
    image2b(doc);
    doc.newLine("You are connected (as seen in the "+menu(GUI.SP_LABEL_RPP)+" field)");
    image2c(doc);

    doc.rstSubsection((i++)+". Perform Association Tests");
    doc.newLine("Start a Session : " + menu(GUI.CW_MN_SERVER,GUI.CW_MI_NEW_SSS));
    image3a(doc);
    doc.rstItemize(
            "Choose one of the provided datasets (your data and RPP's data must share the same Reference Genome)",
            "Choose the least severe VEP Consequence",
            "Choose the maximal frequency in *GnomAD*",
            "Choose if you want to limit the tests to SNVs (more reliable)",
            "Choose the Bed file defining the well covered positions (any variants found outside those regions will be ignored)",
            "Choose the QC Parameters file used to extract the Client's variants, so that the RPP's variants will be filtered using the same criteria (should be automatically filled in)",
            "Choose the file listing the variants excluded by the QC (should be automatically filled in)",
            "Select the Association Tests Algorithm (only WSS is present at this time) and its parameters"
    );
    image3b(doc);
    doc.newLine("When prompted, save your session (this will allows you to keep track of the parameters you have selected, and to reconnected to the RPP if you have been disconnected).");
    doc.newLine();
    doc.rstSubsection((i++)+". Follow your Association Tests progress");
    doc.newLine("Follow the progress of the session in the "+menu(GUI.SP_LABEL_STATUS)+ " Bar for the RPP server and in the "+menu(GUI.TPS_TITLE)+" window");
    image4(doc);
    doc.newLine("Save your results (when prompted)");
    doc.newLine();
    doc.rstSubsection((i++)+". Visualize your results");
    image5(doc);
    doc.newLine("Save the the visualization (Table / Manhattan Plot) through the "+menu(GUI.RP_MN_EXPORT)+" menu");
    doc.newLine();
  }

  public static void getMainWindows(LineBuilder doc){
    doc.rstSection("The Main Window");
    doc.newLine("Description of the various information found in the Main Window");
    doc.rstGridTable(new String[][]{
            {"Field","Description"},
            {GUI.SP_LABEL_RPP, description(GUI.SP_TOOLTIP_RPP)},
            {GUI.SP_LABEL_THIRD_PARTY_NAME, description(GUI.SP_TOOLTIP_THIRD_PARTY_NAME)},
            {GUI.SP_LABEL_DATASET, description(GUI.SP_TOOLTIP_DATASET)},
            {GUI.SP_LABEL_MAF, description(GUI.SP_TOOLTIP_MAF)},
            {GUI.SP_LABEL_MAF_NFE, italic("Same as above, but for NFE frequencies")},
            {GUI.SP_LABEL_ID, description(GUI.SP_TOOLTIP_ID)},
            {GUI.SP_LABEL_CSQ, description(GUI.SP_TOOLTIP_CSQ)},
            {GUI.SP_LABEL_AES, description(GUI.SP_TOOLTIP_AES)},
            {GUI.SP_LABEL_LIMIT_SNV, description(GUI.SP_TOOLTIP_LIMIT_SNV)},
            {GUI.SP_LABEL_ALGORITHM, description(GUI.SP_TOOLTIP_ALGORITHM)},
            {GUI.SP_LABEL_GENOTYPE, description(GUI.SP_TOOLTIP_GENOTYPE)},
            {GUI.SP_LABEL_HASH, description(GUI.SP_TOOLTIP_HASH)},
            {GUI.SP_LABEL_PUBLIC, description(GUI.SP_TOOLTIP_PUBLIC)},
            {GUI.SP_LABEL_PRIVATE, description(GUI.SP_TOOLTIP_PRIVATE)},
            {GUI.SP_LABEL_THIRD_PARTY_KEY, description(GUI.SP_TOOLTIP_THIRD_PARTY_KEY)},
            {GUI.SP_LABEL_STATUS, description(GUI.SP_TOOLTIP_STATUS)},
            {GUI.LP_TITLE, description("Session Log.<ul><li>Messages are in black.</li><li>Successful operations are in green</li><li>Errors are in red</li></ul>")}
          },true);
  }

  public static final String RST_YOU = "the :green:`Client`";
  public static final String RST_RPP = "the :blue:`Reference Panel Provider`";
  public static final String RST_TPS = "the :red:`Third-Party Server`";

  public static String description(String html){
    String rst = html;
    rst = rst.replace(GUI.SP_HTML_YOU, RST_YOU);
    rst = rst.replace(GUI.SP_HTML_RPP, RST_RPP);
    rst = rst.replace(GUI.SP_HTML_TPS, RST_TPS);

    rst = rst.replace("</p>", "");
    rst = rst.replace("<ul>", "\n");
    rst = rst.replace("</ul>", "");
    rst = rst.replace("</li>", "");

    rst = rst.replace("<br/>", "\n\n");
    rst = rst.replace("<p>", "\n\n");
    rst = rst.replace("<li>", "\n- ");
    rst = rst.replace("<b>", "**");
    rst = rst.replace("</b>", "**");

    while(rst.startsWith("\n"))
      rst = rst.substring(1);

    return rst;
  }

  public static void getCommandLines(LineBuilder doc){
    doc.rstSection("PrivAS Client's Command Lines");
    String[] lines = ClientRun.getUsage().split("\n");
    for(String line: lines){
      if(line != null && !line.isEmpty()) {
        String theLine = line.trim();
        if(theLine.startsWith("java"))
          doc.rstCode(BASH, theLine).newLine();
        else if(theLine.startsWith("-"))
          doc.newLine(theLine);
        else
          doc.rstSubsection(theLine);
      }
    }
  }

  private static void image0(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/0.privas.empty.png", "PrivAS Main window");
  }

  private static void image1a(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/1a.privas.load.png", "Load menu");
  }

  private static void image1b(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/1b.privas.main-apply.png", "Choose VCF / QC parameters");
  }


  private static void image1c(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/1c.privas.main-qc.png", "Set QC parameters");
  }

  private static void image1d(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/1d.privas.main-loaded.png", "Variants Loaded");
  }

  private static void image2a(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/2a.privas.main-connect.png", "Connect menu");
  }

  private static void image2b(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/2b.privas.connect.png", "Connection dialog");
  }

  private static void image2c(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/2c.privas.connected.png", "Connected");
  }

  private static void image3a(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/3a.privas.start.png", "Start menu");
  }

  private static void image3b(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/3b.privas.criteria.png", "Set Variants selection criteria and Association Tests parameters");
  }

  private static void image4(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/4.privas.tps.png", "Third Party Log Window");
  }

  private static void image5(LineBuilder doc){
    doc.rstImage("http://lysine.univ-brest.fr/privas/screenshots/5.privas.results.full.png", "Results Visualization");
  }
}

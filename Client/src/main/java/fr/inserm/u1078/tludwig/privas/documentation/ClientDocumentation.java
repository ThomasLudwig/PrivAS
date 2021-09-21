package fr.inserm.u1078.tludwig.privas.documentation;

import fr.inserm.u1078.tludwig.privas.Main;
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
    getCommandLines(doc, Main.Party.CLIENT);
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
    int subsectionIndex = 1;
    Image.F0_START.addToDoc(doc);

    doc.rstSubsection((subsectionIndex++)+". Load Variants");
    doc.newLine(
            "Load your annotated VCF file and perform a Quality Control on " +
            "the Variants and Annotate with GnomAD frequencies "+menu(GUI.CW_MN_FILE,GUI.CW_MI_APPLY_QC_VCF_ANNOTATE)+". " +
            "Alternatively if you have already performed this operation for a previous session and you want the same QC parameters, " +
            "you can simple load the results "+code(FileFormat.FILE_GENO_EXTENSION)+" file, " +
            "this will be much quicker "+menu(GUI.CW_MN_FILE,GUI.CW_MI_LOAD_GENO)
            );
    Image.F1A_LOAD.addToDoc(doc);
    doc.rstItemize(
            "Choose your input VCF file",
            "Choose a previous QC Parameters file...",
            "Choose a GnomAD File"
    );
    Image.F1B_CHOOSE_1B.addToDoc(doc);
    doc.newLine("...or create a new QC Parameters file");
    Image.F1C_QC.addToDoc(doc);
    doc.newLine("The variants are Loaded (as seen in the "+menu(GUI.SP_LABEL_GENOTYPE)+" field)");
    Image.F1D_LOADED.addToDoc(doc);

    doc.rstSubsection((subsectionIndex++) + ". Connect to a RPP Server");
    doc.newLine("Connect to a server "+ menu(GUI.CW_MN_SERVER,GUI.CW_MI_CONNECT));
    Image.F2A_CONNECT_MENU.addToDoc(doc);
    doc.rstItemize(
            "Fill in the address",
            "Fill in the port number (default values point to a Server providing the FrEx datasets)"
    );
    Image.F2B_CONNECT_DIALOG.addToDoc(doc);
    doc.newLine("You are connected (as seen in the "+menu(GUI.SP_LABEL_RPP)+" field)");
    Image.F2C_CONNECTED.addToDoc(doc);

    doc.rstSubsection((subsectionIndex++)+". Perform Association Tests");
    doc.newLine("Start a Session : " + menu(GUI.CW_MN_SERVER,GUI.CW_MI_NEW_SSS));
    Image.F3A_START_SESSION.addToDoc(doc);
    doc.newLine("Choose:");
    doc.rstItemize(
            "one of the provided datasets (your data and RPP's data must share the same Reference Genome)",
            "the same GnomAD Version as the one you annotated your data with",
            "the least severe VEP Consequence",
            "the maximal frequency in *GnomAD*",
            "a *GnomAD* subpopulation (or "+code("None")+" to disable)",
            "the maximal frequency in the *GnomAD* subpopulation",
            "if you want to limit the tests to SNVs (as a INDEL calling is often less reliable)",
            "the Bed file defining the well covered positions (any variants found outside those regions will be ignored)",
            "the QC Parameters file used to extract the Client's variants, so that the RPP's variants will be filtered using the same criteria (should be automatically filled in)",
            "the file listing the variants excluded by the QC (should be automatically filled in)",
            "the Association Tests Algorithm (only WSS is present at this time) and its parameters"
    );
    Image.F3B_CRITERIA.addToDoc(doc);
    doc.newLine("When prompted, save your session (this will allows you to keep track of the parameters you have selected, and to reconnected to the RPP if you have been disconnected).");
    Image.F3C_CREATED.addToDoc(doc);
    doc.newLine();
    doc.rstSubsection((subsectionIndex++)+". Follow your Association Tests progress");
    doc.newLine("Follow the progress of the session in the "+menu(GUI.SP_LABEL_STATUS)+ " Bar for the RPP server and in the "+menu(GUI.TPS_TITLE)+" window");
    Image.F4_TPS_LOG.addToDoc(doc);
    doc.newLine("Save your results (when prompted)");
    doc.newLine();
    doc.rstSubsection((subsectionIndex++)+". Visualize your results");
    Image.F5_RESULTS.addToDoc(doc);
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
            {GUI.SP_LABEL_GNOMAD_VERSION, description(GUI.SP_TOOLTIP_GNOMAD_VERSION)},
            {GUI.SP_LABEL_MAF, description(GUI.SP_TOOLTIP_MAF)},
            {GUI.SP_LABEL_MAF_SUBPOP, italic("Same as above, but for frequencies in Selected Subpopulation")},
            {GUI.SP_LABEL_SUBPOP, italic(GUI.SP_TOOLTIP_SUBPOP)},
            {GUI.SP_LABEL_ID, description(GUI.SP_TOOLTIP_ID)},
            {GUI.SP_LABEL_CSQ, description(GUI.SP_TOOLTIP_CSQ)},
            {GUI.SP_LABEL_AES, description(GUI.SP_TOOLTIP_AES)},
            {GUI.SP_LABEL_LIMIT_SNV, description(GUI.SP_TOOLTIP_LIMIT_SNV)},
            {GUI.SP_LABEL_ALGORITHM, description(GUI.SP_TOOLTIP_ALGORITHM)},
            {GUI.SP_LABEL_GENOTYPE, description(GUI.SP_TOOLTIP_GENOTYPE)},
            {GUI.SP_LABEL_GNOMAD_FILENAME, description(GUI.SP_TOOLTIP_GNOMAD_FILENAME)},
            {GUI.SP_LABEL_BED_FILENAME, description(GUI.SP_TOOLTIP_BED_FILENAME)},
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

  private enum Image {
    F0_START("0.privas.empty", "PrivAS Main window"),
    F1A_LOAD("1a.privas.load", "Load menu"),
    F1B_CHOOSE_1B("1b.privas.main-apply", "Choose VCF / QC parameters"),
    F1C_QC("1c.privas.main-qc", "Set QC parameters"),
    F1D_LOADED("1d.privas.main-loaded", "Variants Loaded"),
    F2A_CONNECT_MENU("2a.privas.main-connect", "Connect menu"),
    F2B_CONNECT_DIALOG("2b.privas.connect", "Connection dialog"),
    F2C_CONNECTED("2c.privas.connected", "Connected"),
    F3A_START_SESSION("3a.privas.start", "Start menu"),
    F3B_CRITERIA("3b.privas.criteria", "Set Variants selection criteria and Association Tests parameters"),
    F3C_CREATED("3c.privas.created", "Session is created"),
    F4_TPS_LOG("4.privas.tps", "Third Party Log Window"),
    F5_RESULTS("5.privas.results.full", "Results Visualization");

    private final String filename;
    private final String alt;

    Image(String filename, String alt){
      this.filename = filename;
      this.alt = alt;
    }

    public String getURL(){
      return "http://lysine.univ-brest.fr/privas/screenshots/"+filename+".png";
    }

    public void addToDoc(LineBuilder doc){
      doc.rstImage(getURL(), alt);
    }
  }
}

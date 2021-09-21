package fr.inserm.u1078.tludwig.privas.constants;

import fr.inserm.u1078.tludwig.privas.Main;
import fr.inserm.u1078.tludwig.privas.documentation.Documentation;

/**
 * Command Line options
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-09-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public enum Command {
  //Top Level
  CLIENT_TOP_LEVEL(
          "",
          "Launch the Client GUI",
          new String[][]{
                  {"[Directory]", "initial working directory (Optional, default is current directory)"}
          },
          "",
          false, false, false,
          "Unable to start instance of GUI client"),
  RPP_TOP_LEVEL(
          "",
          "Launch the Reference Panel Provider Server",
          new String[][]{
                  {"ConfigFile.rpp", "RPP Configuration File"}
          },
          MSG.MSG_EXTRA_RPP,
          false, false, false,
          "Unable to start instance of RPP Server"),
  TPS_TOP_LEVEL("",
          "Launch the Third-Party Server (should be called by the provided script, to manage scheduler issues)",
          new String[][]{
                  {"Session_ID", "the session ID used to generate the key pair"},
                  {"Work_Directory", "The path to the directory where the keys will be stored"},
                  {"Integer", "Number of cores (actually threads) to use"},
                  {"Long", "Initial seed for the random number generator (or \""+MSG.TPS_RANDOM+"\" for random seed)"}},
          "",
          false, false, false,
          "Unable to start instance of Third Party Server"),
  //RPP & Client
  VCF2GENOTYPES(
          "vcf2genotypes",
          "Perform DEFAULT Quality Control on a VEP Annotated VCF file and convert the result to genotype file",
          new String[][]{
                  {"input."+FileFormat.FILE_VCF_GZ_EXTENSION, "The Input VCF file (must have been annotated with vep)"},
                  {"GnomADFile."+FileFormat.FILE_GNOMAD_EXTENSION, "The GnomAD file to use for the frequency annotation"}},
          "",
          true, true, false,
          "Unable to perform QC or convert to genotypes file"),
  VCF2QC(
          "vcf2qc",
          "Perform a Quality Control on a VCF file",
          new String[][]{
                  {"input."+FileFormat.FILE_VCF_GZ_EXTENSION, "The Input VCF file (must have been annotated with vep)"},
                  {"qc."+FileFormat.FILE_QC_PARAM_EXTENSION, "The file containing the QC parameters to apply"}},
          "",
          true, true, false,
          "Unable to perform QC on VCF file"),
  QC2GENOTYPES(
          "qc2genotypes",
          "Convert a QCed VEP Annotated VCF file to a genotypes file",
          new String[][]{
                  {"vep_annotated_QCed_file."+FileFormat.FILE_VCF_GZ_EXTENSION, "The input VCF File (must result from a PrivAS QC and thus is annotated with vep)"},
                  {"GnomADFile."+FileFormat.FILE_GNOMAD_EXTENSION, "The GnomAD file to use for the frequency annotation"}},
          "",
          true, true, false,
          "Unable to convert input vcf file to a genotype file"),
  GNOMAD(
          "gnomad",
          "Creates an annotation binary file from lists of GnomAD (exome/genome) VCF files",
          new String[][]{
                  {"gnomADVersion", "The name of the GnomAD Version"},
                  {"listExomeVCFFiles."+FileFormat.FILE_LIST_EXTENSION, "File listing input GnomAD Exome files (one path per line)"},
                  {"listGenomeVCFFiles."+FileFormat.FILE_LIST_EXTENSION, "File listing input GnomAD Genome files (one path per line)"},
                  {"output."+FileFormat.FILE_GNOMAD_EXTENSION, "The name of the resulting binary file"}},
          "",
          true, true, false,
          "Unable to convert GnomAD vcf file"),
  //TPS
  KEYGEN(
          "--keygen",
          "Generate a keypair for the Third-Party Server",
          new String[][]{
                  {"Work_Directory", "The path to the directory where the keys will be stored"},
                  {"Session_ID", "the session ID used to generate the key pair"}},
          "",
          false, false, true,
          "Unable to save generated keypair"),
  WSS(
          "--wss",
          "Compute a WSS Association Test (locally)",
          new String[][]{
                  {"genotype_files."+FileFormat.FILE_TSV_EXTENSION, "A TSV file that lists the genotypes files to used as input (Column1: Gene Name, Column2: /path/to/the/file."+FileFormat.FILE_GENO_EXTENSION+")"},
                  {"phenotypes.bool", "A TSV file that stores the phenotypes of the samples (0/false = unaffected, 1/true = affected"},
                  {"Integer", "Number of cores (actually threads) to use"},
                  {"Long", "Initial seed for the random number generator (or \""+MSG.TPS_RANDOM+"\" for random seed)"},
                  {"results."+FileFormat.FILE_TSV_EXTENSION, "TSV file that will store the result table"}},
          "",
          false, false, true,
          "Unable to start WSS computation"),
  RANKSUM(
          "--ranksum",
          "Compute a WSS Ranksum",
          new String[][]{
                  {"geneName", "The Name of the gene"},
                  {"input."+FileFormat.FILE_GENO_EXTENSION, "the path to the genotypes file to use as input"},
                  {"phenotypes.bool", "A TSV file that stores the phenotypes of the samples (0/false = unaffected, 1/true = affected"}},
          "",
          false, false, true,
          "Unable to compute a WSS Ranksum"),

  //hidden
  UNKNOWN(
          "",
          "",
          new String[][]{},
          "",
          false, false, false,
          ""
  ),
  DOC(
          "--doc",
          "Generate documentation",
          new String[][]{},
          "",
          false, false, false,
          "Failed to generate documentation"),
  DEBUG(
          "--debug",
          "Debugging Command",
          new String[][]{
                  {"Session_ID", "the session ID used to generate the key pair"},
                  {"Work_Directory", "The path to the directory where the keys will be stored"},
                  {"Integer", "Number of cores (actually threads) to use"},
                  {"Long", "Initial seed for the random number generator (or \""+MSG.TPS_RANDOM+"\" for random seed)"}},
          "",
          false, false, false,
          "Failed to launch debugging command"),
  EXTRACT_HASH(
          "--extract-hash",
          "Converts a VCF file into a TSV file containing the first 5 columns + the canonical + the hash canonical (one line per alt)",
          new String[][]{
                  {"input."+FileFormat.FILE_VCF_GZ_EXTENSION, "the name of the input vcf file"},
                  {"output.variants."+FileFormat.FILE_TSV_EXTENSION, "the name of the output tsv file for variants"},
                  {"output.genes"+FileFormat.FILE_TSV_EXTENSION, "the name of the output tsv file for genes"},
                  {"hash_key", "the key to use when hashing the canonical variants and genes"}
          },
          "",
          false, false, false,
          "Failed to Extract and Hash Data")
  ;

  private final String syntax;
  private final String description;
  private final String[][] arguments;
  private final String extra;
  private final boolean forClient;
  private final boolean forRPP;
  private final boolean forTPS;
  private final String fail;

  Command(String syntax, String description, String[][] arguments, String extra, boolean forClient, boolean forRPP, boolean forTPS, String fail) {
    this.syntax = syntax;
    this.description = description;
    this.arguments = arguments;
    this.extra = extra;
    this.forClient = forClient;
    this.forRPP = forRPP;
    this.forTPS = forTPS;
    this.fail = fail;
  }

  public static Command parseString(String arg){
    for(Command cmd : Command.values())
      if(cmd.getSyntax().equalsIgnoreCase(arg))
        return cmd;
    return UNKNOWN;
  }

  public String usage(String jar){
    int pad = 0;

    for(Command cmd : Command.values())
      for(String[] argument : cmd.arguments)
        pad = Math.max(pad, argument[0].length());
    pad += 3;

    StringBuilder sb = new StringBuilder("- ").append(description).append(":\n");
    sb.append("    ").append(getJavaCommand(jar));
    sb.append("\n").append("    Arguments:");
    for(String[] argument : arguments)
      sb.append("\n").append("      ").append(Documentation.pad(argument[0], pad)).append(argument[1]);
    if(!getExtra().isEmpty())
      sb.append("\n").append(getExtra());
    return sb.toString();
  }

  public String getJavaCommand(String jar){
    StringBuilder sb = new StringBuilder("java -jar ").append(jar).append("  ").append(syntax);
    for(String[] argument : arguments)
      sb.append(" ").append(argument[0]);
    return sb.toString().trim().replaceAll(" +", " ");
  }

  public static Command[] getClientCommands(){
    int n = 0;
    for(Command cmd : Command.values())
      if(cmd.forClient)
        n++;

    Command[] ret = new Command[n];
    n = 0;
    for(Command cmd : Command.values())
      if(cmd.forClient)
        ret[n++] = cmd;
    return ret;
  }

  public static Command[] getRPPCommands(){
    int n = 0;
    for(Command cmd : Command.values())
      if(cmd.forRPP)
        n++;

    Command[] ret = new Command[n];
    n = 0;
    for(Command cmd : Command.values())
      if(cmd.forRPP)
        ret[n++] = cmd;
    return ret;
  }

  public static Command[] getTPSCommands(){
    int n = 0;
    for(Command cmd : Command.values())
      if(cmd.forTPS)
        n++;

    Command[] ret = new Command[n];
    n = 0;
    for(Command cmd : Command.values())
      if(cmd.forTPS)
        ret[n++] = cmd;
    return ret;
  }

  public static Command getTopLevelCommand(Main.Party party){
    switch (party){
      case CLIENT : return CLIENT_TOP_LEVEL;
      case RPP : return RPP_TOP_LEVEL;
      case TPS : return TPS_TOP_LEVEL;
    }
    return UNKNOWN;
  }

  public static Command[] getCommands(Main.Party party){
    switch (party){
      case CLIENT: return getClientCommands();
      case RPP: return getRPPCommands();
      case TPS: return getTPSCommands();
    }
    return new Command[]{};
  }

  public String getFail(){
    return fail+": ";
  }

  public String[][] getArguments() {
    return arguments;
  }

  public String getSyntax() {
    return syntax;
  }

  public String getDescription() {
    return description;
  }

  public String getExtra() {
    return extra;
  }
}

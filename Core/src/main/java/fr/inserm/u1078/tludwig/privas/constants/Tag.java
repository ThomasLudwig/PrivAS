package fr.inserm.u1078.tludwig.privas.constants;

import fr.inserm.u1078.tludwig.privas.documentation.Documentation;

/**
 * Tags from the RPPConfigFile
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-09-16
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public enum Tag {
  PORT_NUMBER(
          "Port Number",
          "Integer",
          "6666",
          "the port on which the RPP server will listen"),
  DATA_FILE(
          "Data File",
          "Name1:datafile1(.gz):nbVariants1:Coverage1.bed,Name2:datafile2(.gz):nbVariants2:Coverage2.bed,...,NameN:datafileN(.gz):nbVariantsN:CoverageN.bed",
          "Data_Name1:/path/to/file1.vcf.gz:65147:/path/to/coverage1.bed.gz,Data_Name2:/path/to/file2.vcf.gz:81791:/path/to/coverage2.bed.gz",
          "Comma-separated dataset description"),//QUESTION is the dataset size needed ?
  GNOMAD(
          "GnomAD Files",
          "Version1:gnomad.v1.bin,Version2:gnomad.v2.bin,...,VersionN:gnomad.vN.bin",
          "Version1:gnomad.v1.bin,Version2:gnomad.v2.bin,...,VersionN:gnomad.vN.bin",
          "Command-separated GnomAD version:path"),
  RPP_SESSION_DIR(
          "RPP Session Directory",
          "/path/to/directory",
          "/path/to/rpp_working_directory/sessions",
          "the directory where RPP will store the files for each session"),
  RPP_EXPIRED_SESSION(
          "RPP Expired Session List",
          "/path/to/file",
          "/path/to/rpp_working_directory/expired.session.\"+FileFormat.FILE_EXCLUSION_EXTENSION",
          "the file where RPP will list expired sessions"),

  TPS_NAME(
          "Third-Party Server Name",
          "name of the server",
          "The name of the TPS Super computer",
          "Name and description of the Third Party Server"),
  TPS_ADDRESS(
          "Third-Party Server Address",
          "address.or.IP",
          "supercomputer.domain.com",
          "fully qualified hostname.domain name or IP address for the TPS"),
  TPS_USER(
          "Third-Party Server Username (ssh)",
          "username",
          "privasuser",
          "the SSH username that will be used on the TPS"),
  TPS_LAUNCH_COMMAND(
          "Third-Party Server Launch_command",
          "launch_command_or_path_to_script",
          "/path/to/PrivAS.TPS.sh",
          "the unix command executed by "+ Documentation.code(TPS_USER.toString().toLowerCase())+" on TPS to launch an Association Test"),
  TPS_GET_KEY_COMMAND(
          "Third-Party Server Get_Key_command",
          "get_rsa_public_key_command_or_path_to_script",
          "/path/to/PrivAS.getPublicKey.sh",
          "tps_get_key_command: the unix command executed by "+Documentation.code(TPS_USER.toString().toLowerCase())+" on TPS to generate of unique RSA keypair for each new session"),
  TPS_SESSION_DIR(
          "Third-Party Server session directory",
          "/path/to/directory",
          "/path/to/tps_working_directory/sessions",
          "the directory where TPS will store the files for each session"),

  WHITELIST(
          "List of whitelisted addresses or ranges",
          "comma-separated_addresses_or_ranges",
          "172.0.0.1,192.168.*.*,172.0.0.0,193.54.252.10-99",
          "a list of IP addresses/ranges that are always allowed to connect to the RPP server"),
  BLACKLIST(
          "List of blacklisted addresses or ranges",
          "comma-separated_addresses_or_ranges",
          "142.250.178.131,152.199.19.61",
          "a list of IP addresses/ranges that are never allowed to connect to the RPP server"),
  CONNECTION_LOG(
          "File containing the connection log",
          "/path/to/file",
          "/path/to/connection.log",
          "The file logging all the connections to the RPP"),
  MAX_PER_DAY(
          "Maximum number of connections per day from the same address",
          "number_of_connections (0 for unlimited)",
          "5",
          "Maximum number of connections per day from the same address"),
  MAX_PER_WEEK(
          "Maximum number of connections per week from the same address",
          "number_of_connections (0 for unlimited)",
          "10",
          "Maximum number of connections per week from the same address"),
  MAX_PER_MONTH(
          "Maximum number of connections per month from the same address",
          "number_of_connections (0 for unlimited)",
          "30",
          "Maximum number of connections per month from the same address");

  private final String description;
  private final String format;
  private final String example;
  private final String doc;

  Tag(String description, String format, String example, String doc){
    this.description = description;
    this.format = format;
    this.example = example;
    this.doc = doc;
  }

  public String getDescription() {
    return description;
  }

  public String getFormat() {
    return format;
  }

  public String getDoc() {
    return doc;
  }

  public String getSyntax(int pad){
    return Documentation.pad(this.toString().toLowerCase(), pad)+" <TAB> "+getFormat();
  }

  public static String MISSING_EXCEPTION(String configFile, Tag tag) {
    return MSG.cat(MSG.CL_EX_MISSING, tag.getDescription()) + " "
            + MSG.cat(MSG.CL_EX_IN, configFile) + "\n"
            + MSG.CL_EX_SYNTAX + " " + tag.getSyntax(tag.toString().length());
  }

  public static String[][] getWholeDocTable() {
    String[][] ret = new String[Tag.values().length+1][2];
    ret[0] = new String[]{"key", "description"};
    for(int i = 0; i < Tag.values().length; i++)
      ret[i+1] = Tag.values()[i].getDocTable();
    return ret;
  }

  public String[] getDocTable(){
    return new String[]{Documentation.code(this.toString().toLowerCase()), this.getDoc()};
  }

  public static String[] getAllSyntax(){
    int pad = 0;
    for(Tag tag : Tag.values())
      pad = Math.max(pad, tag.toString().length());
    String[] ret = new String[Tag.values().length];
    for(int i = 0; i < Tag.values().length; i++)
      ret[i] = Tag.values()[i].getSyntax(pad);
    return ret;
  }

  public static String[] getWholeCodeBlock(){
    int pad = 0;
    for(Tag tag : Tag.values())
      pad = Math.max(pad, tag.toString().length());
    pad += 2;

    String[] ret = new String[Tag.values().length];
    for(int i = 0; i < Tag.values().length; i++)
      ret[i] = Tag.values()[i].getCodeLine(pad);
    return ret;
  }

  public String getCodeLine(int pad){
    return Documentation.pad(this.toString().toLowerCase(), pad)+example;
  }
}

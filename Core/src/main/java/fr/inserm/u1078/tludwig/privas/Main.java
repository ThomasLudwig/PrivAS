package fr.inserm.u1078.tludwig.privas;

import fr.inserm.u1078.tludwig.privas.constants.Command;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.instances.CommandLineInstance;
import fr.inserm.u1078.tludwig.privas.instances.Instance;
import fr.inserm.u1078.tludwig.privas.utils.ExtractAnnotations;
import fr.inserm.u1078.tludwig.privas.utils.FileUtils;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCException;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;
import fr.inserm.u1078.tludwig.privas.utils.qc.QualityControl;

import java.io.*;

/**
 * Main Class, entry point of the program. Only static fields and methods
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-23
 */
public final class Main {
  public static final boolean DEBUG = true;
  
  //TODO  propose Datasets in GRCh37 and GRCh38
  //DONE  results must be scrambled : they are written as soon as the p-value is available
  //DONE  TEST : Client OK + TPS Running/Done + RPP Restarted
  //DONE  TEST : Client Running, Client Ask new session 
  //DONE  RPP : thread removing old sessions (every hour), place tag expired
  //REJC  replace files.ok by in memory flags | in case of rpp crash, memory is lost
  //DONE  Pong message has a list of reference data to choose from, that will be used in the criteria panel and the Session object
  //DONE  Message are generating a lot of errors since switching to subclasses
  //REJC  Server AskTPS delay should be dynamic depending on ETA with a ceil and floor

  private Main() {
    //This class may not be instantiated
  }

  public static String getVersion(){
    try {
      String line;
      InputStream is = Main.class.getResourceAsStream("/CHANGELOG.md");
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      while ((line = in.readLine()) != null) {
        if (line.toLowerCase().startsWith("## "))
          return line.substring(3);
      }
    } catch (IOException e) {
      //Ignore
    }
    return "v?.?.?(????-??-??)";
  }

  /**
   * Prepares an RPP data file by converting from a vep annotated VCF file
   */
  public static void convertVCF(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String vcfFile = args[1];
      String gnomADFilename = args[2];
      GenotypesFileHandler.convertVCF2Genotypes(vcfFile, gnomADFilename, cmd.getInstance());
    } catch (GenotypesFileHandler.GenotypeFileException | IOException e) {
      cmd.fail(e);
    }
  }

  public static void qc(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String inVCF = args[1];
      String qcParamFile = args[2];
      QCParam qcParam = new QCParam(qcParamFile);
      QualityControl.applyQC(inVCF, qcParam);
    } catch (QCException | IOException | ArrayIndexOutOfBoundsException e) {
      cmd.fail(e);
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void qcAndConvert(Main.CommandExecutor cmd) {
    QCParam qcParam = new QCParam();
    String inVCF;
    String[] args = cmd.getArgs();
    try{
      inVCF = args[1];
      QualityControl.applyQC(inVCF, qcParam);
    } catch(Exception e) {
      cmd.fail(e);
      return;
    }
    String outVCF = FileUtils.addQCPrefixToVCFFilename(inVCF, qcParam);
    String gnomADFilename = args[2];
    try {
      GenotypesFileHandler.convertVCF2Genotypes(outVCF, gnomADFilename, cmd.getInstance());
    } catch (GenotypesFileHandler.GenotypeFileException | IOException e) {
      cmd.fail(e);
    }
    File file = new File(outVCF);
    file.delete();
  }

  public static void extractGnomAD(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String gnomADVersion = args[1];
      String listExome = args[2];
      String listGenome = args[3];
      String output = args[4];
      ExtractAnnotations.convertGnomAD(gnomADVersion, listExome, listGenome, output, cmd.getInstance());
    } catch (Exception e) {
      cmd.fail(e);
    }
  }

  public static void extractAndHash(Main.CommandExecutor cmd) {
    String[] args = cmd.getArgs();
    try {
      String inVCF = args[1];
      String outVCFFilename = args[2];
      String outGeneFilename = args[3];
      String hashingKey = args[4];
      GenotypesFileHandler.extractCanonicalAndHash(inVCF, outVCFFilename, outGeneFilename, hashingKey, cmd.getInstance());
    } catch (IOException | ArrayIndexOutOfBoundsException | GenotypesFileHandler.GenotypeFileException e) {
      cmd.fail(e);
    }
  }

  /**
   * Prints the various usages of this program
   */
  public static String getUsage(Party party) {
    String N = "\n";
    String jar = party.getJar();
    StringBuilder out = new StringBuilder(MSG.MSG_USAGE + N
            + Command.getTopLevelCommand(party).usage(jar) + N);

    Command[] commands = Command.getCommands(party);
    if(commands.length > 0) {
      out.append(N).append(MSG.MSG_TOOLS);
      for (Command cmd : commands)
        out.append(N).append(cmd.usage(jar));
    }
    return out.toString();
  }

  public enum Party {
    CLIENT("Client"),
    RPP("RPP"),
    TPS("TPS");
    private final String name;

    Party(String name){
      this.name = name;
    }

    public String getJar() {
      return "PrivAS."+name+".jar";
    }

    public String getName(){
      return name;
    }
  }

  public static class CommandExecutor {
    private final Instance instance;
    private final Command command;
    private final String[] args;
    private final Party party;

    public CommandExecutor(String[] args, Party party) {
      this.instance = new CommandLineInstance();
      this.party = party;
      if(args.length > 0 && args[0].startsWith("-")) {
        this.args = args;
        command = Command.parseString(args[0]);
      } else {
        this.command = Command.getTopLevelCommand(party);
        this.args = new String[args.length + 1];
        this.args[0] = "";
        System.arraycopy(args, 0, this.args, 1, args.length);
      }
      instance.logInfo(MSG.MSG_WELCOME);
      this.check();
    }

    public void check(){
      if(command == Command.UNKNOWN)
        return;
      if(command.getArguments().length < args.length - 1)
        instance.logWarning("More arguments than expected for this command");
      if(command.getArguments().length > args.length  - 1)
        instance.logError("Not enough arguments for this command");
      if(command.getArguments().length != args.length - 1)
        if(command != Command.CLIENT_TOP_LEVEL && command != Command.RPP_TOP_LEVEL && command != Command.TPS_TOP_LEVEL)
          instance.logInfo(command.usage(party.getJar()));
    }

    public Instance getInstance() {
      return instance;
    }

    public Command getCommand() {
      return command;
    }

    public String[] getArgs() {
      return args;
    }

    public Party getParty() {
      return party;
    }

    public void fail(Exception e){
      instance.logError(command.getFail());
      if(e != null)
        instance.logError(e);
      instance.logInfo(command.usage(party.getJar()));
    }
  }
}

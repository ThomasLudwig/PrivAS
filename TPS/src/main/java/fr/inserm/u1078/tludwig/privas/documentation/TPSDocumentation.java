package fr.inserm.u1078.tludwig.privas.documentation;

import fr.inserm.u1078.tludwig.privas.Main;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;

/**
 * Generates an Up-to-date documentation for the TPS
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-28
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class TPSDocumentation extends Documentation {
  public static String getDocumentation() {
    LineBuilder doc = new LineBuilder();
    getTitle(doc);
    configuration(doc);
    getKey(doc);
    launch(doc);

    return doc.toString();
  }

  public static void getTitle(LineBuilder doc){
    doc.rstChapter("Third Party Server");
  }

  public static void configuration(LineBuilder doc){
    doc.rstSection("Configuration");
    doc.newLine("The Third Party Server must provide at least 2 scripts :");
    doc.rstItemize(
            "one to get a public RSA Key for a Session",
            "one to launch a series of Association Tests"
    );
    doc.newLine();
  }

  public static void getKey(LineBuilder doc){
    doc.rstSubsection("GetPublicRSAKey");
    doc.newLine("Example For Simple Unix based System:");
    doc.rstCode(BASH,
            "#!/bin/bash",
            "",
            "if [ $# -lt 1 ]",
            "then",
            "        echo -e \"usage :\\t$0 SessionID\";",
            "        exit 1;",
            "fi",
            "",
            "installDir=/path/to/PrivAS/WorkingDirectory",
            "jar=/path/to/PrivAS.TPS.VERSION.jar",
            "",
            "java -jar $jar --keygen $installDir/sessions $1"
    );
    doc.newLine();
  }



  public static void launch(LineBuilder doc){
    doc.rstSubsection("LaunchAssociationTest");
    doc.newLine("Example For Simple Unix based System:");
    doc.rstCode(BASH,
            "#!/bin/bash",
                    "",
                    "if [ $# -lt 1 ]",
                    "then",
                    "        echo -e \"usage :\\t$0 SessionID\";",
                    "        exit 1;",
                    "fi",
                    "",
                    "session=$1",
                    "installDir=/path/to/PrivAS/WorkingDirectory",
                    "jar=/path/to/PrivAS.TPS.VERSION.jar",
                    "core=24",
                    "seed=\"random\" #random seed for Production",
                    "#seed=\"123456789\" #fixed seed for Debuging",
                    "d=`date +\"%Y-%m-%d\"`;",
                    "log=$installDir/log/$d.log",
                    "mkdir -p $installDir/sessions/$session;",
                    "status=$installDir/sessions/$session/tps.status;",
                    "epoch=`date +%s`000;",
                    "echo -e \"$epoch\\tPENDING\\tJob submitted from $HOSTNAME\" >> $status;",
                    "",
                    "",
                    "java -jar $jar \"$session\" \"$installDir\" \"$core\" \"$seed\" &>> $log"
    );
    doc.newLine();
  }


}

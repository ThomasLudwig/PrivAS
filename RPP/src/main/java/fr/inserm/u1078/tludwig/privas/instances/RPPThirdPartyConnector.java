package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Inner Class responsible of the communication with the Third Party Server
 *
 * * @author Thomas E. Ludwig (INSERM - U1078) 2020-01-17
 */
public class RPPThirdPartyConnector {

  /**
   * the RPP to which the connector is attached
   */
  private final RPP rpp;
  /**
   * the Address of the Third Party Server
   */
  private final String address;
  /**
   * the user login (ssh) used to access the Third Party Server
   */
  private final String login;
  /**
   * the Script used to launch jobs on the Third Party Server
   */
  private final String launchScript;
  /**
   * the Script used to generate a new RSA Key Pair on the Third Party Server
   */
  private final String getKeyScript;
  /**
   * the path to the Session Directory on the Third Party Server
   */
  private final String sessionDir;

  /**
   * Constructor
   *
   * @param address      the Address of the Third Party Server
   * @param login        the user login (ssh) used to access the Third Party Server
   * @param launchScript the Script used to launch jobs on the Third Party Server
   * @param getKeyScript the Script used to generate a new RSA Key Pair on the Third Party Server
   * @param sessionDir   the path to the Session Directory on the Third Party Server
   */
  RPPThirdPartyConnector(RPP rpp, String address, String login, String launchScript, String getKeyScript, String sessionDir) {
    this.rpp = rpp;
    this.address = address;
    this.login = login;
    this.launchScript = launchScript;
    this.getKeyScript = getKeyScript;
    this.sessionDir = sessionDir;
  }

  /**
   * Executes a unix command
   *
   * @param command
   */
  private void exec(String command) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(command.split("\\s+"));
    p.waitFor();
  }

  /**
   * Gets the Third Party Server's Public RSA key as a one-line pem String (through ssh)
   *
   * @param sessionId the Session ID
   * @return
   */
  String getTPSPublicKey(String sessionId) {
    rpp.logInfo(MSG.RPP_INF_TPS_KEY);
    try {
      String[] command = {
        "ssh",
        "-t",
        login + "@" + address,
        getKeyScript + " '" + sessionId + "'"
      };
      ProcessBuilder p = new ProcessBuilder(command);
      Process proc = p.start();
      BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      String key = in.readLine();
      in.close();
      return key;
    } catch (IOException e) {
      rpp.logError(MSG.RPP_ERR_TPS_KEY);
      rpp.logError(e);
      rpp.setStatus(sessionId, RPPStatus.tpsError(e.getMessage()));
    }
    return null;
  }

  /**
   * Sends Data to the Third Party Server an launches the job (through scp/ssh)
   *
   * @param sessionId the Session ID
   */
  void sendDataAndStartJob(String sessionId) {
    rpp.submitNow(() -> {
      rpp.logInfo(MSG.cat(MSG.RPP_INF_SENDING, rpp.getFilenameFor(sessionId, FileFormat.FILE_AES_KEY)));

      String sendInput = "scp -r " + rpp.getFilenameFor(sessionId, null/* directory */) + " " + login + "@" + address + ":" + sessionDir;
      try {
        exec(sendInput);
      } catch (IOException | InterruptedException ex) {
        rpp.setStatus(sessionId, RPPStatus.tpsError(ex.getMessage()));
        
      }
      //TODO how to check that all files were received ? Maybe checksum, download tps.checksum and compare to rpp.checksum
      rpp.logInfo(MSG.cat(MSG.RPP_INF_STARTING, launchScript));
      //exec(launchJob);
      try {
        String[] command = {
          "ssh",
          "-t",
          login + "@" + address,
          launchScript + " '" + sessionId + "'"
        };
        ProcessBuilder p = new ProcessBuilder(command);
        p.start();
      } catch (IOException e) {
        rpp.logError(e);
        rpp.setStatus(sessionId, RPPStatus.tpsError(e.getMessage()));
      }
      rpp.logInfo(MSG.RPP_INF_STARTED);
    });
  }

  /**
   * Gets the TPSStatus of the Third Party Server (through scp)
   *
   * @param sessionId the Session ID
   * @return
   */
  List<TPStatus> getStatuses(String sessionId, int last) throws Exception{
    ArrayList<TPStatus> statuses = new ArrayList<>();

    String dest = rpp.getFilenameFor(sessionId, FileFormat.FILE_TPS_STATUS);
    String cmd = "scp " + login + "@" + address + ":" + sessionDir + "/" + sessionId + "/" + FileFormat.FILE_TPS_STATUS + " " + dest;
   /* try {*/
      exec(cmd);
   /*   try {*/
        UniversalReader in = new UniversalReader(dest);
        String line;
        try{
          for(int i = 0; i < last; i++)
            in.readLine();
          while((line = in.readLine()) != null) {
            if (!line.isEmpty()) {
              String[] f = line.split("\t", -1);
              statuses.add(new TPStatus(new Long(f[0]), TPStatus.State.valueOf(f[1]), f[2]));
            }
          }
        } catch(EOFException ignore) {
          //ignored - TPS is writing in the file, while RPP is reading, sometimes an incomplete line is read.
        }
        try {
          in.close();
        } catch(IOException ignore){
          //don't care
        }
   /*   } catch(FileNotFoundException ex) { //TODO this will completely mess up the offset !!
        //The file doesn't yet exist on TPS
        statuses.add(new TPStatus(new Date().getTime(), TPStatus.State.PENDING, ""));
      } catch (IOException e) {
        statuses.add(new TPStatus(new Date().getTime(), TPStatus.State.UNREACHABLE, e.getClass().getSimpleName()));
      } catch(Exception e){
        statuses.add(new TPStatus(new Date().getTime(), TPStatus.State.UNKNOWN, "RPP has been unable to get TPS's status"));
      }
    } catch (IOException | InterruptedException ex) {
      rpp.setStatus(sessionId, RPPStatus.tpsError(ex.getMessage()));
      statuses.add(new TPStatus(new Date().getTime(), TPStatus.State.UNREACHABLE, ex.getClass().getSimpleName()));
    }*/
    return statuses;
  }

  /**
   * Gets the Results File from the Third Party Server (through scp)
   *
   * @param sessionId the Session ID
   */
  void getResults(String sessionId) {
    String dest = rpp.getFilenameFor(sessionId, FileFormat.FILE_RESULTS);
    String cmd = "scp " + login + "@" + address + ":" + sessionDir + "/" + sessionId + "/" + FileFormat.FILE_RESULTS + " " + dest;
    try {
      exec(cmd);
    } catch (IOException | InterruptedException ex) {
      rpp.setStatus(sessionId, RPPStatus.tpsError(ex.getMessage()));
    }
    String validDest = rpp.getFilenameFor(sessionId, FileFormat.FILE_RESULTS_OK);
    try {
      PrintWriter out = new PrintWriter(new FileWriter(validDest));
      out.println(Constants.OK);
      out.close();
    } catch (IOException e) {
      rpp.setStatus(sessionId, RPPStatus.tpsError(e.getMessage()));
      rpp.logError(e);
    }
  }
}

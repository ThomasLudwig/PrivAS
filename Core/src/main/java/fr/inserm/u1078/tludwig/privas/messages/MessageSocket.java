package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.instances.MessageException;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;
import fr.inserm.u1078.tludwig.privas.utils.CompressedBlockInputStream;
import fr.inserm.u1078.tludwig.privas.utils.CompressedBlockOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Socket between two instances (Client and RPP) that serves to transfert Message object between both parties
 * There should be one instance of MessageSocket per Message, except for Monitoring Messages that will keep the socket open
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-01
 *
 * Javadoc complete on 2019-08-07
 */
public class MessageSocket {

  /**
   * The actual socket
   */
  private final Socket socket;
  /**
   * The DataInputStream from which the message will be read
   */
  private final DataInputStream is;
  /**
   * The DataOutputStream where the Message will be written
   */
  private final DataOutputStream os;

  /**
   * Actual Constructor
   *
   * @param socket the embeded socket
   * @throws IOException
   */
  private MessageSocket(Socket socket) throws IOException {
    this.socket = socket;
    this.os = new DataOutputStream(new CompressedBlockOutputStream(this.socket.getOutputStream()));
    this.is = new DataInputStream(new CompressedBlockInputStream(this.socket.getInputStream()));
  }

  /**
   * Constructs a MessageSocket from a ServerSocket
   * <p>
   * A Server (RPP) creates this MessageSocket in order to received a Message from a Client, then send a Reply (another Message)
   *
   * @param serverSocket the Server Socket is part of the server instance (RPP), it listen of the selected port
   * @throws IOException
   */
  public MessageSocket(ServerSocket serverSocket) throws IOException {
    this(serverSocket.accept());
  }

  /**
   * Constructs a MessageSocket to an IP address and Port number
   * <p>
   * A Client creates this MessageSocket in order to send a Message to a Server, then received a Reply (another Message)
   *
   * @param ip   the IP address (or name) of the server
   * @param port the port of the server
   * @throws IOException
   */
  public MessageSocket(String ip, int port) throws IOException {
    this(new Socket(ip, port));
  }

  /**
   * Gets the Remote party's Address (Used by the server to log connections)
   *
   * @return
   */
  public String getClientIP() {
    try {
      return this.socket.getRemoteSocketAddress().toString();
    } catch (Exception e) {
      return Constants.IP_UNKNOWN;
    }
  }

  /**
   * Writes a Message to the Socket
   *
   * @param message the message
   * @throws IOException
   */
  public void writeMessage(Message message) throws IOException {
    this.writeMessage(message, null);
  }

  /**
   * Updates the task progression on a optionnal ProgressDialog
   *
   * @param progressListener the ProgressListener to update
   * @param percent          the Progression in percent
   */
  private static void setPercent(ProgressListener progressListener, int percent) {
    if (progressListener != null)
      progressListener.progressChanged(percent);
  }

  /**
   * Writes a Message to the Socket
   *
   * @param message          the message
   * @param progressListener an optionnal ProgressListener to keep updated of the writting progression
   * @throws IOException
   */
  public void writeMessage(Message message, ProgressListener progressListener) throws IOException {
    os.writeUTF(message.getType());
    String keyList = message.getKeys();
    os.writeUTF(keyList);
    setPercent(progressListener, 0);
    int nbPackets = 2;
    int nbSent = 2;
    if (keyList.length() > 1) {
      String[] keys = keyList.split(",");
      int[] blocks = new int[keys.length];
      StringBuilder lengths = new StringBuilder();
      for (int i = 0; i < keys.length; i++) {
        lengths.append(",");
        blocks[i] = (int) Math.ceil(message.getValue(Message.Key.valueOf(keys[i])).length() / Parameters.SOCKET_BLOCK_SIZE);
        nbPackets += blocks[i];
        lengths.append(blocks[i]);
      }
      os.writeUTF(lengths.substring(1));
      for (int i = 0; i < keys.length; i++)
        for (int j = 0; j < blocks[i]; j++) {
          String block;
          int start = (int) (j * Parameters.SOCKET_BLOCK_SIZE);
          if (j < blocks[i] - 1) {
            int end = (int) (start + Parameters.SOCKET_BLOCK_SIZE);
            block = message.getValue(Message.Key.valueOf(keys[i])).substring(start, end);
          } else
            block = message.getValue(Message.Key.valueOf(keys[i])).substring(start);
          os.writeUTF(block);
          nbSent++;
          setPercent(progressListener, (nbSent * 100) / nbPackets);
        }
    }
    setPercent(progressListener, 100);
    os.flush();
  }

  /**
   * Reads a Message from the MessageSocket
   *
   * @return the Message
   * @throws IOException
   * @throws MessageException
   */
  public Message readMessage() throws IOException, MessageException {
    String type = is.readUTF();
    HashMap<String, String> kv = new HashMap<>();
    String keyList = is.readUTF();
    try {
      if (keyList.length() > 1) {
        String[] keys = keyList.split(",");
        int[] blocks = new int[keys.length];
        String[] blockSizes = is.readUTF().split(",");
        for (int i = 0; i < keys.length; i++)
          blocks[i] = new Integer(blockSizes[i]);
        for (int i = 0; i < keys.length; i++) {
          StringBuilder sb = new StringBuilder();
          for (int j = 0; j < blocks[i]; j++)
            sb.append(is.readUTF());
          kv.put(keys[i], sb.toString());
        }
      }
      return Message.buildMessage(type, kv);
    } catch(IOException | MessageException e){
      throw e;
    } catch(Exception e1){ //Something else (NumberFormatException | NullPointer | ArrayIndexOutOfBounds...
      throw new IOException("Unable to read Message("+e1.getClass().getSimpleName()+")", e1);
    }
  }

  /**
   * Closes this MessageSocket (and its underlying Socket and DataInput/OutputStream
   *
   * @throws IOException
   */
  public void close() throws IOException {
    this.is.close();
    this.os.close();
    this.socket.close();
  }
}

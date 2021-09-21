package test;

import fr.inserm.u1078.tludwig.privas.gui.LookAndFeel;
import fr.inserm.u1078.tludwig.privas.gui.TPSLogWindow;
import fr.inserm.u1078.tludwig.privas.instances.TPStatus;

import java.util.Date;

/**
 * Testing Class to try the Client's GUI
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-03
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class Testgui {

  public static void main(String[] args) {
    LookAndFeel.setup();

    TPSLogWindow logWindow = new TPSLogWindow();
    long offset = -1000*60*60*24;
    for(int i = 0; i < 200; i++) {
      logWindow.add(new TPStatus(new Date().getTime()+offset, getRandomState(), getRandomMessage()));
      offset += 1000*60*60;
      try {
        Thread.sleep(2000);
      } catch (InterruptedException ignore) {
        //ignore
      }
    }
  }

  public static String getRandomMessage(){
    double newWord = .95;
    StringBuilder message = new StringBuilder(getRandomWord());
    while(Math.random() < newWord)
      message.append(" ").append(getRandomWord());
    return message.toString();
  }

  public static String getRandomWord(){
    double newLetter = 0.65;
    StringBuilder word = new StringBuilder(""+getRandomLetter());
    while(Math.random()< newLetter)
      word.append(getRandomLetter());
    return word.toString();
  }

  public static char getRandomLetter(){
    return (char)(97 + (int)(26*Math.random()));
  }

  public static TPStatus.State getRandomState(){
    int s = (int)(Math.random()* STATES.length);
    return STATES[s];
  }

  public static final TPStatus.State[] STATES = {TPStatus.State.DONE,TPStatus.State.ERROR,TPStatus.State.PENDING,TPStatus.State.RUNNING,TPStatus.State.STARTED,TPStatus.State.UNKNOWN,TPStatus.State.UNREACHABLE};
}

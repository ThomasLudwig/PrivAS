package test;

import fr.inserm.u1078.tludwig.privas.gui.LookAndFeel;
import fr.inserm.u1078.tludwig.privas.gui.TPSLogWindow;
import fr.inserm.u1078.tludwig.privas.instances.TPStatus;

import java.util.Date;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-03
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class Testgui {

  public static final void main(String[] args) throws Exception {
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
    String message = getRandomWord();
    while(Math.random() < newWord)
      message += " " + getRandomWord();
    return message;
  }

  public static String getRandomWord(){
    double newLetter = 0.65;
    String word = ""+getRandomLetter();
    while(Math.random()< newLetter)
      word += getRandomLetter();
    return word;
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

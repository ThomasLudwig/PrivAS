package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.GUI;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;

/**
 * A Dialog with a Progress Bar to inform on an operation progression
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-05
 * 
 * Javadoc Complete on 2019-08-09
 */
public class ProgressDialog implements ProgressListener {

  /**
   * The actual dialog
   */
  private final JDialog dlg;
  /**
   * The actual ProgressBar
   */
  private final JProgressBar pb;
  /**
   * the Frame owning (calling) this Dialog
   */
  private final Frame owner;
  private SwingWorker<Object, Object> worker;
  /**
   * the Text shown at the top of the Dialog
   */
  private final String northText;
  /**
   * the Text shown at the bottom of the Dialog
   */
  private final String southText;

  /**
   * Constructor
   * @param owner the Frame owning (calling) this Dialog
   * @param title the Title of the Dialog
   * @param northText the Text shown at the top of the Dialog
   * @param southText the Text shown at the bottom of the Dialog
   */
  public ProgressDialog(Frame owner, String title, String northText, String southText) {
    this.dlg = new JDialog(owner, title, true);
    this.owner = owner;
    pb = new JProgressBar(0, 100);
    this.northText = northText;
    this.southText = southText;
  }

  /**
   * Initializes the Dialog
   * @param northText the Text shown at the top of the Dialog
   * @param southText the Text shown at the bottom of the Dialog
   */
  private void init(String northText, String southText) {
    pb.setMinimumSize(GUI.PD_DIM);
    pb.setMaximumSize(GUI.PD_DIM);
    pb.setPreferredSize(GUI.PD_DIM);
    pb.setIndeterminate(true);
    pb.setStringPainted(false);
    dlg.add(BorderLayout.NORTH, new JLabel(northText));
    dlg.add(BorderLayout.CENTER, pb);
    dlg.add(BorderLayout.SOUTH, new JLabel(southText));
    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    dlg.setSize(GUI.PD_SIZE);
    dlg.setLocationRelativeTo(owner);
    dlg.pack();
    dlg.setResizable(false);
    this.activate();
  }

  /**
   * Sets the SwingWorker with the task being performed
   * @param worker the SwingWorker with the task being performed
   */
  public void setWorker(SwingWorker<Object, Object> worker) {
    this.worker = worker;
    this.init(northText, southText);
  }

  /**
   * Activates this ProgressDialog's SwingWorker and start the worker of the actual Task
   */
  private void activate() {
    SwingWorker<Object, Object> t = new SwingWorker<Object, Object>(){
      @Override
      protected Object doInBackground() {
        dlg.setVisible(true);
        return null;
      }
    };
    t.execute();
    worker.execute();
  }

  /**
   * When the task is done, this dialog stops showing
   */
  public void done() {
    dlg.setVisible(false);
  }

  @Override
  public void progressChanged(int percent) {
    SwingUtilities.invokeLater(() -> {
      try {
        if (percent < 0) {
          pb.setIndeterminate(true);
          pb.setStringPainted(false);
        } else {
          pb.setIndeterminate(false);
          pb.setValue(percent);
          pb.setStringPainted(true);
          pb.setString(percent + "%");
        }
      } catch (Exception e) {
        //Nothing
      }
    });
  }
}

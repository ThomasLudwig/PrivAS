package fr.inserm.u1078.tludwig.privas.gui.helper;

import fr.inserm.u1078.tludwig.privas.constants.GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.io.File;

/**
 * JTextField that check on edit for validity of Document
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public abstract class CheckedTextField extends JTextField implements DocumentListener {

  enum Type {FILE, LONG, DOUBLE}

  private Type type;
  private double max = Double.POSITIVE_INFINITY;
  private double min = -Double.NEGATIVE_INFINITY;

  public CheckedTextField() {
    this.getDocument().addDocumentListener(this);
  }

  public CheckedTextField(String text) {
    super(text);
    this.getDocument().addDocumentListener(this);
  }

  public CheckedTextField(int columns) {
    super(columns);
    this.getDocument().addDocumentListener(this);
  }

  public CheckedTextField(String text, int columns) {
    super(text, columns);
    this.getDocument().addDocumentListener(this);
  }

  public CheckedTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    this.getDocument().addDocumentListener(this);
  }

  public void setText(String text) {
    super.setText(text);
  }

  private void setType(Type type) {
    this.type = type;
  }

  private void setMax(double max) {
    this.max = max;
  }

  private void setMin(double min) {
    this.min = min;
  }

  void setFile(){
    this.setType(Type.FILE);
  }

  void setLong(long min, long max){
    this.setType(Type.LONG);
    this.setMin(min);
    this.setMax(max);
  }

  void setDouble(double min, double max){
    this.setType(Type.DOUBLE);
    this.setMin(min);
    this.setMax(max);
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    this.changedUpdate(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    this.changedUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    boolean check = false;
    String text = getText();
    switch (type) {
      case FILE:
        check = checkFile(text);
        break;
      case LONG:
        check = checkLong(text);
        break;
      case DOUBLE:
        check = checkDouble(text);
        break;
    }

    setBackground(check ? GUI.BACKGROUND_COLOR_OK : GUI.BACKGROUND_COLOR_KO);
    if (check)
      whenOK();
    else
      whenKO();
  }

  boolean checkFile(String text) {
    return new File(text).exists();
  }

  boolean checkLong(String text) {
    try {
      long l = Long.parseLong(text);
      return min <= l && l <= max;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  boolean checkDouble(String text) {
    try {
      double d = Double.parseDouble(text);
      return min <= d && d <= max;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public void whenOK() {

  }

  public void whenKO() {

  }
}

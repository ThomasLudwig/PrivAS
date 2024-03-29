package fr.inserm.u1078.tludwig.privas.gui;

import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import java.awt.Color;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2020-02-05
 */
public class LookAndFeel {
  
  public static final int TEXT_BG_RED = 60;
  public static final int TEXT_BG_GREEN = 60;
  public static final int TEXT_BG_BLUE = 70;
  
  public static final ColorUIResource LOG_BG_COLOR = new ColorUIResource(new Color(210, 210, 210));
  public static final ColorUIResource TEXT_BG_COLOR = new ColorUIResource(new Color(TEXT_BG_RED, TEXT_BG_GREEN, TEXT_BG_BLUE));

  public static void setup() {
    try {
      Properties p = new Properties();
      p.setProperty("logoString", "PrivAS");
      p.setProperty("backgroundPattern", "off");
      p.setProperty("userTextFont", "Arial PLAIN 12);");

      HiFiLookAndFeel.setCurrentTheme(p);
      UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
      UIManager.put("TextPane.background", LOG_BG_COLOR);
      UIManager.put("TextPane.inactiveBackground", LOG_BG_COLOR);

      String[] keys = {
              "TextArea.background",
              "TextArea.inactiveBackground",

              "TextField.background",
              "TextField.disabledBackground",
              "TextField.inactiveBackground",

              "EditorPane.background",

              "FormattedTextField.background",
              "FormattedTextField.inactiveBackground",

              "PasswordField.background"
      };

      for (String key : keys) {
        UIManager.put(key, TEXT_BG_COLOR);
      }
    } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
      throw new RuntimeException("Unable to Load LookAndFeel", e);
    }
  }

  public static Color getBackgroundTextColor() {
    return TEXT_BG_COLOR;
  }
}

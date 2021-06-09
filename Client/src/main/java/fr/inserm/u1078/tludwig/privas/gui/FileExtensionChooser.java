package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * A JFileChooser that possesses an Inner FileFilter to show only files of certain extensions
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-06
 * 
 * Javadoc Complete on 2019-08-09
 */
public class FileExtensionChooser extends JFileChooser {

  /**
   * Constructor
   * @param ext the extension allowed
   * @param withGZ also allow gzipped files (FILE.ext.gz) ?
   * @param startDir the current directory of the JFileChooser
   */
  public FileExtensionChooser(String ext, boolean withGZ, String startDir) {
    super();
    FileExtensionFilter ff = new FileExtensionFilter(ext, withGZ);
    File dir = (startDir == null) ? new File(".") : new File(startDir);
    this.setCurrentDirectory(dir);
    this.setFileFilter(ff);
  }

  /**
   * A FileFilter to show only files of certain extensions
   */
  private static class FileExtensionFilter extends FileFilter {
    private final String name;
    private final String extension;
    private final String gzExtension;

    /**
     * Constructor
     * @param type file type
     * @param withGZ also allow gzipped files (FILE.ext.gz) ?
     */
    FileExtensionFilter(String type, boolean withGZ) {
      this.name = type.toUpperCase();
      this.extension = "." + type.toLowerCase();
      this.gzExtension = withGZ ? this.extension + "." + FileFormat.FILE_GZ_EXTENSION : null;
    }

    @Override
    public boolean accept(File f) {
      return f.isDirectory()
              || f.getName().toLowerCase().endsWith(extension)
              || (gzExtension != null && f.getName().toLowerCase().endsWith(gzExtension));
    }

    @Override
    public String getDescription() {
      StringBuilder desc = new StringBuilder();
      desc.append(name);
      desc.append(" ");
      desc.append(GUI.FEC_FILES);
      desc.append(" (*");
      desc.append(extension);
      if (gzExtension != null) {
        desc.append(", *");
        desc.append(gzExtension);
      }
      desc.append(")");
      return desc.toString();
    }
  }
}
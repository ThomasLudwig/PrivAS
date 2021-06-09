package fr.inserm.u1078.tludwig.privas.documentation;

/**
 * Class to generate the project's documentation
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-28
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class Documentation {
  public static final String BASH = "bash";

  public static String bold(String string){
    return "**"+string+"**";
  }

  public static String italic(String string){
    return "*"+string+"*";
  }

  public static String menu(String... strings){
    return menu(String.join("/", strings));
  }

  public static String menu(String string){
    return code("["+string+"]");
  }

  public static String code(String string){
    return ":code:`"+string+"`";
  }

  public static String paragraph(String string){
    return string+"\n";
  }

  public static String pad(String string, int pad){
    StringBuilder ret = new StringBuilder(string);
    while(ret.length() < pad)
      ret.append(" ");
    return ret.toString();
  }

  public static int getMax(String... strings){
    int max = 0;
    for(String string : strings)
      max = Math.max(max, string.length());
    return max;
  }

  public static String code(String[] strings, String language, boolean numbered){
    StringBuilder ret = new StringBuilder("```");
    if(language != null)
      ret.append(language);
    if(numbered)
      ret.append("=");
    ret.append("\n");
    for(String string : strings)
      ret.append(string).append("\n");
    ret.append("```\n\n");
    return ret.toString();
  }

  public static String table(String[] titles, String[][] rows){
    StringBuilder ret = new StringBuilder("|");
    for(String title: titles)
      ret.append(" ").append(title).append(" |");
    ret.append("\n|");
    for(String title: titles)
      ret.append(" --- |");
    for(String[] row : rows){
      ret.append("\n|");
      for(String cell : row)
        ret.append(" ").append(cell).append(" |");
    }
    return ret.toString();
  }

  public static String hr(){
    return "---";
  }

  public static String link(String title, String url){
    return "["+title+"]{"+url+"}";
  }

  public static String image(String address, String altText){
    String url = address.startsWith("http") ? address : "http://lysine.univ-brest.fr/privas/screenshots/"+address;
    return "\n!["+altText+"]("+url+")\n";
  }

  public static String blockquote(String string){
    return "> "+string;
  }

  public static String orderedList(String string, int num){
    return num+". "+string+"\n";
  }

  public static String orderedList(String[] strings){
      return orderedList(strings, 1);
  }

  public static String orderedList(String[] strings, int start){
    StringBuilder ret = new StringBuilder();
    for(int i = 0; i < strings.length; i++)
      ret.append(orderedList(strings[i], i+start));
    return ret.toString();
  }

  public static String unorderedList(String string, int level){
    StringBuilder pad = new StringBuilder();
    for(int i = 0 ; i < level; i++)
      pad.append("\t");
    pad.append("- ").append(string).append("\n");
    return pad.toString();
  }

  public static String unorderedList(String[] strings, int level){
    StringBuilder ret = new StringBuilder();
    for (String string : strings)
      ret.append(unorderedList(string, level));
    return ret.toString();
  }

  public static String htmlUnorderedList(String... strings){
    StringBuilder ret = new StringBuilder("<ul>");
    for(String string : strings)
      ret.append("<li>").append(string).append("</li>");
    ret.append("</ul>");
    return ret.toString();
  }
}

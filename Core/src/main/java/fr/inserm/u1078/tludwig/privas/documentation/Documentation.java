package fr.inserm.u1078.tludwig.privas.documentation;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
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
    String ret = string;
    while(ret.length() < pad)
      ret += " ";
    return ret;
  }

  public static int getMax(String... strings){
    int max = 0;
    for(String string : strings)
      max = Math.max(max, string.length());
    return max;
  }

  public static String code(String[] strings, String language, boolean numbered){
    String ret = "```";
    if(language != null)
      ret+= language;
    if(numbered)
      ret += "=";
    ret += "\n";
    for(String string : strings)
      ret += string + "\n";
    ret += "```\n\n";
    return ret;
  }

  public static String table(String[] titles, String[][] rows){
    String ret = "|";
    for(String title: titles)
      ret += " "+title+" |";
    ret += "\n|";
    for(String title: titles)
      ret += " --- |";
    for(String[] row : rows){
      ret += "\n|";
      for(String cell : row)
        ret += " "+cell+" |";
    }
    return ret;
  }

  public static String hr(){
    return "---";
  }

  public static String link(String title, String url){
    return "["+title+"]{"+url+"}";
  }

  public static String image(String address, String altText){
    String url = address.startsWith("http") ? address : "https://lysine.univ-brest.fr/privas/screenshots/"+address;
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
    String ret = "";
    for(int i = 0; i < strings.length; i++)
      ret += orderedList(strings[i], i+start);
    return ret;
  }

  public static String unorderedList(String string, int level){
    String pad = "";
    for(int i = 0 ; i < level; i++)
      pad += "\t";
    return pad+"- "+string+"\n";
  }

  public static String unorderedList(String[] strings, int level){
    String ret = "";
    for(int i = 0; i < strings.length; i++)
      ret += unorderedList(strings[i], level);
    return ret;
  }

  public static String htmlUnorderedList(String... strings){
    String ret = "<ul>";
    for(String string : strings)
      ret += "<li>"+string+"</li>";
    return ret+"</ul>";
  }
}

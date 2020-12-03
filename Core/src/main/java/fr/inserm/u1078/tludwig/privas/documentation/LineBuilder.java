package fr.inserm.u1078.tludwig.privas.documentation;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Extensions of StringBuilder with custom methods
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on 2020-09-17
 * Checked for release on xxxx-xx-xx
 * Unit Test defined on xxxx-xx-xx
 */
public class LineBuilder {
  private static final String N = "\n";
  private static final String T = "\t";
  private static final String S = " ";
  private static final String DEFAULT_INDENT = "  ";
  public static final String REGEX_EVERY_CHAR = ".";
  public static final int TAB_INDENT = -1;
  private final StringBuilder stringBuilder;
  private final String indent;

  public LineBuilder() {
    this.stringBuilder = new StringBuilder();
    this.indent = DEFAULT_INDENT;
  }

  public LineBuilder(int indent) {
    this.stringBuilder = new StringBuilder();
    this.indent = getIndent(indent);
  }

  public LineBuilder(String s){
    this.stringBuilder = new StringBuilder(s);
    this.indent = DEFAULT_INDENT;
  }

  public LineBuilder (String s, int indent){
    this.stringBuilder = new StringBuilder(s);
    this.indent = getIndent(indent);
  }

  public static String getIndent(int n){
    if(n == TAB_INDENT)
      return T;
    String r = "";
    for(int i = 0 ; i < n; i++)
      r += " ";
    return r;
  }

  public LineBuilder newLine(){
    return this.append(N);
  }

  public LineBuilder newLine(LineBuilder lb){
    return this.append(lb).newLine();
  }

  public LineBuilder newLine(String str){
    return this.append(str).newLine();
  }

  public LineBuilder addColumn() {
    return this.append(T);
  }

  public LineBuilder addColumn(String str) {
    return this.addColumn().append(str);
  }

  public LineBuilder addColumn(char c) {
    return this.addColumn().append(c);
  }

  public LineBuilder addColumn(boolean b) {
    return this.addColumn().append(b);
  }

  public LineBuilder addColumn(double d) {
    return this.addColumn().append(d);
  }

  public LineBuilder addColumn(char[] c) {
    return this.addColumn().append(c);
  }

  public LineBuilder addColumn(CharSequence s) {
    return this.addColumn().append(s);
  }

  public LineBuilder addColumn(float f) {
    return this.addColumn().append(f);
  }

  public LineBuilder addColumn(int  i) {
    return this.addColumn().append(i);
  }

  public LineBuilder addColumn(long l) {
    return this.addColumn().append(l);
  }

  public LineBuilder addColumn(Object o) {
    return this.addColumn().append(o);
  }

  public LineBuilder openHTML(String tag){
    return openHTML(tag, (String)null, new String[][]{});
  }

  public LineBuilder openHTML(String tag, String clas){
    return openHTML(tag, clas, new String[][]{});
  }

  public LineBuilder openHTML(String tag, String[]... extra){
    return openHTML(tag, (String)null, extra);
  }

  public LineBuilder openHTML(String tag, String clas, String[]... extra){
    this.append("<").append(tag);
    if(clas != null)
      this.addSpace("class=\"").append(clas).append("\"");

    for(String[] ext : extra)
      this.addSpace(ext[0]).append("=\"").append(ext[1]).append("\"");
    return this.append(">");
  }

  public LineBuilder openCloseHTML(String tag){
    return openCloseHTML(tag, (String)null, new String[][]{});
  }

  public LineBuilder openCloseHTML(String tag, String clas){
    return openCloseHTML(tag, clas, new String[][]{});
  }

  public LineBuilder openCloseHTML(String tag, String[]... extra){
    return openCloseHTML(tag, (String)null, extra);
  }

  public LineBuilder openCloseHTML(String tag, String clas, String[]... extra){
    this.append("<").append(tag);
    if(clas != null)
      this.addSpace("class=\"").append(clas).append("\"");
    if(extra != null)
      for(String[] ext : extra)
        this.addSpace(ext[0]).append("=\"").append(ext[1]).append("\"");
    return this.append("/>");
  }

  public LineBuilder closeHTML(String tag){
    return this.append("</").append(tag).append(">");
  }

  public LineBuilder addSpace() {
    return this.append(S);
  }

  public LineBuilder addSpaces(int n){
    for(int i = 0; i < n; i++)
      this.addSpace();
    return this;
  }

  public LineBuilder addSpaces(int n, String str){
    return this.addSpaces(n).append(str);
  }

  public LineBuilder newLine(int n, String str){
    for(int i = 0; i < n; i++)
      this.append(indent);
    return this.newLine(str);
  }

  public LineBuilder addSpace(String str) {
    return this.addSpace().append(str);
  }

  public LineBuilder rstHorizontalLine(){
    return this.append("\n----------\n");
  }

  public LineBuilder rstHeader(String title){
    return this.append(".. _").append(title.toLowerCase()).newLine(":").newLine();
  }

  public LineBuilder rstChapter(String title){
    return this.rstTitle(title, '*');
  }

  public LineBuilder rstSection(String title){
    return this.rstTitle(title, '=');
  }

  public LineBuilder rstSubsection(String title){
    return this.rstTitle(title, '-');
  }

  public LineBuilder rstSubsubsection(String title){
    return this.rstTitle(title, '.');
  }

  public LineBuilder rstTitle(String title, char underline){
    String line = title.replaceAll(REGEX_EVERY_CHAR, ""+underline);
    return this.newLine(title)
            .newLine(line)
            .newLine();
  }

  public LineBuilder rstGridTable(String[][] table, boolean hasHeader){
    if(table.length == 0)
      return this.newLine();

    ArrayList<String>[][] multiLines = new ArrayList[table.length][table[0].length];
    for(int i = 0 ; i < table.length; i++)
      for(int j = 0 ; j < table[i].length; j++)
        multiLines[i][j] = new ArrayList<>(Arrays.asList(table[i][j].split("\n")));

    multiLines = trimAndPad(multiLines);
    LineBuilder del = new LineBuilder();
    del.append("+");
    for(int col = 0; col < multiLines[0].length; col++){
      del.append("-");
      del.append(multiLines[0][col].get(0).replaceAll(LineBuilder.REGEX_EVERY_CHAR, "-"));
      del.append("-");
      del.append("+");
    }
    String hDel = del.toString();
    if(hasHeader)
      hDel = hDel.replace("-","=");

    this.newLine();
    this.newLine(del);
/*
    System.out.println("DEBUG");
    for(int l = 0 ; l < multiLines.length; l++){
      System.out.println("LINE["+l+"]");
      for(int c = 0; c < multiLines[l].length; c++){
        System.out.println("\tCOL["+l+","+c+"]");
        for(String s : multiLines[l][c]){
          System.out.println("\t\t{"+s+"}");
        }
      }
    }
*/

    for(int line = 0; line < multiLines.length; line++){
      String[][] sameHeight = sameHeight(multiLines[line]);
      int nbLines = sameHeight[0].length;
      int nbCols = sameHeight.length;
      for(int l = 0 ; l < nbLines; l++) {
        this.append("|");
        for (int col = 0; col < nbCols; col++) {
          this.append(" ");
          this.append(sameHeight[col][l]);
          this.append(" |");
        }
        this.newLine();
      }
      if(line == 0 && multiLines.length > 1)
        this.newLine(hDel);
      else
        this.newLine(del);
    }
    return this.newLine();
  }

  public String[][] sameHeight(ArrayList<String>[] cols){
    int nbLine = 0;
    for(ArrayList<String> col : cols)
      nbLine = Math.max(nbLine, col.size());
    String[][] ret = new String[cols.length][nbLine];
    for(int c = 0; c < cols.length; c++){
      String pad = "";
      for(int l = 0; l < ret[c].length; l++){
        if(l < cols[c].size()){
          ret[c][l] = cols[c].get(l);
          pad = ret[c][l].replaceAll(LineBuilder.REGEX_EVERY_CHAR, " ");
        } else
          ret[c][l] = pad;
      }
    }

    return ret;
  }

  public LineBuilder rstTable(String[][] table, boolean hasHeader){
    if(table.length == 0)
      return this.newLine();

    String[][] t = trimAndPad(table);
    LineBuilder del = new LineBuilder();
    for(int col = 0; col < t[0].length; col++){
      if(col > 0)
        del.addSpaces(2);
      del.append(t[0][col].replaceAll(LineBuilder.REGEX_EVERY_CHAR, "="));
    }
    this.newLine();
    this.newLine(del);
    for(int line = 0; line < t.length; line++){
      for(int col = 0; col < t[line].length; col++){
        if(col > 0)
          this.addSpaces(2);
        this.append(t[line][col]);
      }
      this.newLine();
      if(line == 0 && hasHeader && t.length > 1)
        this.newLine(del);
    }
    this.newLine(del);
    return this;
  }

  public LineBuilder rstImage(String address, String alt){
    this.newLine();
    this.newLine(".. image:: "+address);
    this.newLine("  :width: "+600);
    this.newLine("  :alt: "+alt);
    this.newLine();
    return this;
  }

  public static ArrayList<String>[][] trimAndPad(ArrayList<String>[][] t){
    ArrayList<String>[][] ret = new ArrayList[t.length][t[0].length];
    int[] width = new int[t[0].length];
    for(int row = 0 ; row < t.length; row++)
      for(int col = 0 ; col < t[row].length; col++){
        for(String s : t[row][col]){
          width[col] = Math.max(width[col], s.trim().length());
        }
      }

    for(int row = 0 ; row < t.length; row++)
      for(int col = 0 ; col < t[row].length; col++){
        ret[row][col] = new ArrayList<>();
        for(String s : t[row][col]){
          ret[row][col].add(pad(s.trim(), width[col]));
        }
      }

    return ret;
  }

  public static String[][] trimAndPad(String[][] t){
    String[][] ret = new String[t.length][t[0].length];
    int[] width = new int[t[0].length];
    for(int line = 0; line < t.length; line++)
      for(int col = 0 ; col < t[line].length; col++){
        ret[line][col] = t[line][col].trim();
        width[col] = Math.max(width[col], ret[line][col].length());
      }
    for(int line = 0; line < t.length; line++)
      for(int col = 0 ; col < t[line].length; col++)
        ret[line][col] = pad(ret[line][col], width[col]);

    return ret;
  }

  public static String pad(String s, int width){
    String ret = s;
    while(ret.length() < width)
      ret += " ";
    return ret;
  }

  public LineBuilder rstColumns(String... columns){
    LineBuilder del = new LineBuilder();
    LineBuilder col = new LineBuilder();
    for (int i = 0; i < columns.length; i++) {
      String c = columns[i];
      String d = c.replaceAll(REGEX_EVERY_CHAR, "=");
      if (i > 0) {
        del.addSpaces(2);
        col.addSpaces(2);
      }
      col.append(" ").append(c).append(" ");
      del.append("=").append(d).append("=");
    }
    this.newLine();
    this.newLine(del);
    this.newLine(col);
    this.newLine(del);
    return this;
    //return "\n" + del + "\n" + col + "\n" + del + "\n";
  }

/*
      StringBuilder sb = new StringBuilder("\n");
    sb.append(".. code-block:: bash\n");
    for (String s : list)
      sb.append("\n   ").append(s);
    return sb.toString() + "\n";

  */

  public LineBuilder rstBold(String s){
    return this.append("**").append(s).append("**");
  }

  public LineBuilder rstInlineCode(String line){
    return this.append(":code:`").append(line).append("`");
  }

  public LineBuilder rstCode(String language, String... lines){
    this.newLine();
    this.append(".. code-block::").addSpace(language).newLine();
    this.newLine();
    for (String s : lines)
      this.addSpaces(3, s).newLine();
    return this;
  }

  public LineBuilder rstEnum(String... items){
    this.rstBeginItemize();
    for (int i = 0; i < items.length; i++)
      this.rstAddEnum(i+1, items[i]);
    return this;
  }

  public LineBuilder rstItemize(String... items){
    this.rstBeginItemize();
    for (String item : items)
      this.rstAddItem(item);
    return this;
  }

  public LineBuilder rstBeginItemize(){
    return this.newLine();
  }

  public LineBuilder rstAddItem(String item){
    return this.append("-").addSpace(item).newLine();
  }

  public LineBuilder rstAddEnum(int i, String item){
    return this.append(i).append(".").addSpace(item).newLine();
  }

  public LineBuilder rstWarning(String... warnings){
    this.newLine();
    this.newLine(".. warning::");
    for(String warning : warnings)
      this.addSpaces(3, warning).newLine().newLine();
    return this;
  }

  public LineBuilder rstNote(String... notes){
    this.newLine();
    this.newLine(".. note::");
    for(String note : notes)
      this.addSpaces(3, note).newLine().newLine();
    return this;
  }

  public String minus(int n){
    int l = this.length();
    return this.substring(0, l-n);
  }

  //simulate inheritant from StringBuilder
  public LineBuilder append(boolean b){	stringBuilder.append(b); return this;}
  public LineBuilder append(char c){	stringBuilder.append(c); return this;}
  public LineBuilder append(char[] str){	stringBuilder.append(str); return this;}
  public LineBuilder append(char[] str, int offset, int len){	stringBuilder.append(str, offset, len); return this;}
  public LineBuilder append(CharSequence s){	stringBuilder.append(s); return this;}
  public LineBuilder append(CharSequence s, int start, int end){	stringBuilder.append(s, start, end); return this;}
  public LineBuilder append(double d){	stringBuilder.append(d); return this;}
  public LineBuilder append(float f){	stringBuilder.append(f); return this;}
  public LineBuilder append(int i){	stringBuilder.append(i); return this;}
  public LineBuilder append(long lng){	stringBuilder.append(lng); return this;}
  public LineBuilder append(Object obj){	stringBuilder.append(obj); return this;}
  public LineBuilder append(String str){	stringBuilder.append(str); return this;}
  public LineBuilder append(StringBuffer sb){	stringBuilder.append(sb); return this;}
  public LineBuilder append(LineBuilder lb){	stringBuilder.append(lb.stringBuilder); return this;}
  public LineBuilder appendCodePoint(int codePoint){	stringBuilder.append(codePoint); return this;}
  public int capacity(){	return stringBuilder.capacity();}
  public char charAt(int index){	return stringBuilder.charAt(index);}
  public int codePointAt(int index){	return stringBuilder.codePointAt(index);}
  public int codePointBefore(int index){	return stringBuilder.codePointBefore(index);}
  public int codePointCount(int beginIndex, int endIndex){	return stringBuilder.codePointCount(beginIndex, endIndex);}
  public StringBuilder delete(int start, int end){	return stringBuilder.delete(start, end);}
  public StringBuilder deleteCharAt(int index){	return stringBuilder.deleteCharAt(index);}
  public void ensureCapacity(int minimumCapacity){	stringBuilder.ensureCapacity(minimumCapacity);}
  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin){	stringBuilder.getChars(srcBegin, srcEnd, dst, dstBegin);}
  public int indexOf(String str){	return stringBuilder.indexOf(str);}
  public int indexOf(String str, int fromIndex){	return stringBuilder.indexOf(str, fromIndex);}
  public LineBuilder insert(int offset, boolean b){	stringBuilder.insert(offset, b); return this;}
  public LineBuilder insert(int offset, char c){	stringBuilder.insert(offset, c); return this;}
  public LineBuilder insert(int offset, char[] str){	stringBuilder.insert(offset, str); return this;}
  public LineBuilder insert(int index, char[] str, int offset, int len){	stringBuilder.insert(index, str, offset, len); return this;}
  public LineBuilder insert(int dstOffset, CharSequence s){	stringBuilder.insert(dstOffset, s); return this;}
  public LineBuilder insert(int dstOffset, CharSequence s, int start, int end){	stringBuilder.insert(dstOffset, s, start, end); return this;}
  public LineBuilder insert(int offset, double d){	stringBuilder.insert(offset,d); return this;}
  public LineBuilder insert(int offset, float f){	stringBuilder.insert(offset, f); return this;}
  public LineBuilder insert(int offset, int i){	stringBuilder.insert(offset, i); return this;}
  public LineBuilder insert(int offset, long l){	stringBuilder.insert(offset, l); return this;}
  public LineBuilder insert(int offset, Object obj){	stringBuilder.insert(offset, obj); return this;}
  public LineBuilder insert(int offset, String str){	stringBuilder.insert(offset, str); return this;}
  public int lastIndexOf(String str){	return stringBuilder.lastIndexOf(str);}
  public int lastIndexOf(String str, int fromIndex){	return stringBuilder.lastIndexOf(str, fromIndex);}
  public int length(){	return stringBuilder.length();}
  public int offsetByCodePoints(int index, int codePointOffset){	return stringBuilder.offsetByCodePoints(index, codePointOffset);}
  public LineBuilder replace(int start, int end, String str){	stringBuilder.replace(start, end, str); return this;}
  public LineBuilder reverse(){	stringBuilder.reverse(); return this;}
  public void setCharAt(int index, char ch){	stringBuilder.setCharAt(index, ch);}
  public void setLength(int newLength){	stringBuilder.setLength(newLength);}
  public CharSequence subSequence(int start, int end){	return stringBuilder.subSequence(start, end);}
  public String substring(int start){	return stringBuilder.substring(start);}
  public String substring(int start, int end){	return stringBuilder.substring(start, end);}
  @Override
  public String toString(){	return stringBuilder.toString();}
  public void trimToSize(){	stringBuilder.trimToSize();}
}

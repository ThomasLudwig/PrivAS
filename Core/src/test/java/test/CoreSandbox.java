package test;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-09-14
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class CoreSandbox {
  public static void main(String[] args){
    /*String s = "dfsdf";
    try{
      Double.parseDouble(s);
    } catch(NumberFormatException e){
      System.out.println(e.getClass().getSimpleName() + " " + e.getMessage());
      e.printStackTrace();
    }*/
    boolean ok = false;
    test(ok = true);
    test(ok);
    test(ok = false);
    test(ok);
  }

  public static void test(boolean b){
    System.out.println("VALUES ["+b+"]");
  }
}

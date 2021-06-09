package test;

import fr.inserm.u1078.tludwig.privas.documentation.ClientDocumentation;

/**
 * Testing Class to try the Client's documentation
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-29
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class TestDocClient {
  public static void main(String[] args){
    System.err.println("Before");
    System.err.println(ClientDocumentation.getClientDocumentation());
    System.err.println("After");
  }
}

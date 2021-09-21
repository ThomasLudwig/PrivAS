package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.listener.StandardErrorLogger;

/**
 * A CommandLine instance that logs on stdout
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-09-15
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class CommandLineInstance extends Instance {
  public CommandLineInstance() {
    this.addLogListener(new StandardErrorLogger());
  }
}

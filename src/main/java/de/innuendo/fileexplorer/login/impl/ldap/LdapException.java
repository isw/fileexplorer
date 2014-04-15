package de.innuendo.fileexplorer.login.impl.ldap;

public class LdapException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public LdapException(String message) {
    super(message);
  }

  public LdapException(String message, Throwable cause) {
    super(message, cause);
  }

}

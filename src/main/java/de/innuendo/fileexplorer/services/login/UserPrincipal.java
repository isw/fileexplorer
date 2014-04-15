package de.innuendo.fileexplorer.services.login;

import java.io.Serializable;
import java.security.Principal;

public class UserPrincipal implements Principal, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String user;
  
  public UserPrincipal (String u) {
    this.user = u;
  }
  
  @Override
  public String getName() {
    return this.user;
  }

}

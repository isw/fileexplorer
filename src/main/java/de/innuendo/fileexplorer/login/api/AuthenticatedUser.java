package de.innuendo.fileexplorer.login.api;

import java.security.Principal;

public class AuthenticatedUser implements Principal {
  private String name;
  private String scheme;
  private String[] groups;
  
  public AuthenticatedUser (String scheme, String n, String[] grps) {
    this.name = n;
    this.scheme = scheme;
    this.groups = grps;
  }
  
  @Override
  public String getName() {
    return this.name;
  }

  public String[] getGroups() {
    return groups;
  }

  public String getScheme() {
    return scheme;
  }

}

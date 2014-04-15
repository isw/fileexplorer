package de.innuendo.fileexplorer.login.impl.ldap;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.AuthenticationException;
import de.innuendo.fileexplorer.login.api.IUserAuthentication;
import de.innuendo.fileexplorer.service.api.ComponentBaseImpl;

public class LDAPAuthentication extends ComponentBaseImpl implements IUserAuthentication {
  private LdapBean accessData;
  private String description;
  
  @Override
  public AuthenticatedUser authenticate(String usr, String pwd)
      throws AuthenticationException {
    try {
      LdapUser u = LdapAccess.auth (usr, pwd, false, this.getAccessData());
      return new AuthenticatedUser (this.getComponentName(), u.getName(), u.getGroups());
    }
    catch (Exception e) {
      throw new AuthenticationException (e);
    }
  }

  public LdapBean getAccessData() {
    return accessData;
  }

  public void setAccessData(LdapBean accessData) {
    this.accessData = accessData;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}

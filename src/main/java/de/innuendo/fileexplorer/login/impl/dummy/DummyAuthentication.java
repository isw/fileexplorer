package de.innuendo.fileexplorer.login.impl.dummy;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.AuthenticationException;
import de.innuendo.fileexplorer.login.api.IUserAuthentication;
import de.innuendo.fileexplorer.service.api.ComponentBaseImpl;

public class DummyAuthentication extends ComponentBaseImpl implements IUserAuthentication  {
  
  private static class User {
    private String pwd;
    private String[] roles;
    
    public User (String p, String[] rolz) {
      this.pwd = p;
      this.roles = rolz;
    }

    public String getPwd() {
      return pwd;
    }

    public String[] getRoles() {
      return roles;
    }

  }
  
  private String description;
  private Map<String, User> users = new HashMap<>();
  
  @Override
  public AuthenticatedUser authenticate(String usr, String pwd)
      throws AuthenticationException {
    User u = this.users.get(usr);
    // nur ein Dummy! ein richtiger Authenticator dürfte niemals
    // rausgeben ob UID oder PWD falsch sind!
    
    if (u==null)
      throw new AuthenticationException ("Ungültige Kennung");
    
    if (!u.getPwd().equals(pwd))
      throw new AuthenticationException ("Ungültiges Passwort");
    
    return new AuthenticatedUser(this.getComponentName(), usr, u.getRoles());
  }



  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setUserCredentials (String[] users) {
    for (String u : users) {
      StringTokenizer toks = new StringTokenizer(u, ",");
      String uid = toks.nextToken();
      String pwd = toks.nextToken();
      String[] rolz = toks.nextToken().split("\\|");
      this.users.put(uid, new User(pwd,rolz));
    }
  }
}

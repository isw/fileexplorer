package de.innuendo.fileexplorer.services.login;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.AuthenticationException;
import de.innuendo.fileexplorer.login.api.IAuthentication;
import de.innuendo.fileexplorer.login.api.IUserAuthentication;
import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.rpc.api.CallResult.RC;

public class LoginService extends AbstractRemoteService {
  
  @Inject
  private Set<IUserAuthentication> authenticators;
  
  @Inject
  private IAuthentication authentication;
  
  public IUserAuthentication getAuthentication (String scheme) {
    for (IUserAuthentication c : this.authenticators) {
      if (c.getComponentName().equals(scheme))
        return c;
    }
    throw new RuntimeException("no authenticator '"+scheme+"' found");
  }
  
  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp,
      Object... objects) {
    String user = (String)objects[0];
    String password = (String)objects[1];
    String scheme = (String)objects[2];
    
    if (password == null || password.trim().equals(""))
      return new CallResult(RC.ERROR, "Fehler beim Login: Leeres Passwort", null);
    
    try {
      IUserAuthentication auth = this.getAuthentication(scheme);
      AuthenticatedUser usr = auth.authenticate(user, password);
      this.authentication.setCurrentUser(rq, usr);
      return new CallResult (usr);
    }
    catch (AuthenticationException e) {
      return new CallResult(RC.ERROR, e.getMessage(), null);
    }
  }

}

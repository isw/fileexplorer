package de.innuendo.fileexplorer.services.login;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.login.api.IUserAuthentication;
import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.rpc.api.CallResult.RC;

public class GetAuthenticators extends AbstractRemoteService {

  
  @Inject
  private Set<IUserAuthentication> authenticators;

  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp,
      Object... objects) {
    if (authenticators == null)
      return new CallResult (RC.ERROR,"no registered Authenticators", null);
    
    AuthenticatorScheme[] res = new AuthenticatorScheme[this.authenticators.size()];
    int i=0;
    for (IUserAuthentication c : this.authenticators) {
      res[i++] = new AuthenticatorScheme(c.getComponentName(),c.getDescription());
    }
    return new CallResult(res);
  }

}

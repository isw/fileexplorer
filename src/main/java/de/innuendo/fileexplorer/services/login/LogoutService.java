package de.innuendo.fileexplorer.services.login;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.login.api.IAuthentication;
import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;

public class LogoutService extends AbstractRemoteService {

  @Inject
  private IAuthentication authentication;
  
  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp,
      Object... objects) {
    
    this.authentication.setCurrentUser(rq, null);
    return new CallResult(Boolean.TRUE);
  }
}

package de.innuendo.fileexplorer.services.login;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.IAuthentication;
import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.rpc.api.CallResult.RC;

public class GetUser extends AbstractRemoteService {

  @Inject
  private IAuthentication authentication;

  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp,
      Object... objects) {
    AuthenticatedUser u = this.authentication.getCurrentUser(rq);
    return new CallResult(RC.OK, null, u);
  }

}

package de.innuendo.fileexplorer.services.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;

public class Ping extends AbstractRemoteService {
  
  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp,
      Object... objects) {
    rq.getSession().setAttribute("___dummy___", System.currentTimeMillis());
    return new CallResult(Boolean.TRUE);
  }

}

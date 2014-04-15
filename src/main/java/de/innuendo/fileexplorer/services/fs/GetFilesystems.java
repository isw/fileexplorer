package de.innuendo.fileexplorer.services.fs;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.services.fs.api.IDirectory;

public class GetFilesystems extends AbstractRemoteService {

  @Inject
  private Set<IDirectory> directories;
  
  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp,
      Object... objects) {
    
    return new CallResult(directories);
  }

}

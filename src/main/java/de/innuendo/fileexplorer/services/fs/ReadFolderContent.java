package de.innuendo.fileexplorer.services.fs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.login.api.IAuthentication;
import de.innuendo.fileexplorer.message.api.IMessageProvider;
import de.innuendo.fileexplorer.rpc.api.AbstractRemoteService;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.rpc.api.CallResult.RC;
import de.innuendo.fileexplorer.services.fs.api.IDirectory;
import de.innuendo.fileexplorer.services.fs.impl.IOFetcher;
import de.innuendo.fileexplorer.services.fs.impl.Util;

public class ReadFolderContent extends AbstractRemoteService {

  @Inject
  private IMessageProvider messageprovider;

  @Inject
  private IAuthentication authenticator;
  
  @Inject
  private Set<IDirectory> directories;

  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp, Object... objects) {
    String parent = null;
    String fsdir = (String)objects[0];
    if (objects.length > 1)
      parent = (String) objects[1];
    
    IDirectory fsroot= Util.findById(fsdir, this.directories);
    Path root = Paths.get(fsroot.getPath());
    Path dir = parent == null? root : root.resolve(parent);
    if (dir.normalize().compareTo(root.normalize()) < 0)
      return new CallResult (RC.ERROR, this.messageprovider.getMessage("illegal path", parent),null);
    
    return IOFetcher.fetchContent(
        fsroot.isShowHiddenFiles(),
        this.authenticator,
        this.authenticator.getCurrentUser(rq),
        root,
        dir, true, true);
  }
  
}

package de.innuendo.fileexplorer.services.fs;

import java.nio.charset.Charset;
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

public class ReadFileContents extends AbstractRemoteService {

  @Inject
  private IAuthentication authenticator;
  
  @Inject
  private IMessageProvider messageprovider;
  
  @Inject
  private Set<IDirectory> directories;
 
  
  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp, Object... objects) {
    // parameter 0: fsdir
    // parameter 1: pfad
    // parameter 2: als attachment oder nicht
    // parameter 3: charset
    // parameter 4: zipped
    // parameter 5: filter
    
    if (objects.length != 6)
      return new CallResult (RC.ERROR, this.messageprovider.getMessage("filecontents-wrong-args"),null);
    
    String fs = (String)objects[0];
    String file = (String)objects[1];
    boolean attach = (Boolean)objects[2];
    Charset cs = Charset.forName((String)objects[3]);
    boolean zipped = (Boolean)objects[4];
    String filter = (String)objects[5];
    
    IDirectory fsdir = Util.findById(fs, this.directories);
    Path root = Paths.get(fsdir.getPath());
    Path dir = root.resolve(file);
    if (dir.normalize().compareTo(root.normalize()) < 0)
      return new CallResult (RC.ERROR, this.messageprovider.getMessage("illegal path", file),null);
    return IOFetcher.fetchFileContent(
        fsdir.isShowHiddenFiles(),
        this.authenticator,
        this.authenticator.getCurrentUser(rq),    
        root,
        file, dir, attach, cs, zipped, filter, rsp);
  }
  
}

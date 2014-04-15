package de.innuendo.fileexplorer.services.fs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
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

public class ZipFiles extends AbstractRemoteService {
  
  @Inject
  private IAuthentication authenticator;
  
  @Inject
  private IMessageProvider messageprovider;

  @Inject
  private Set<IDirectory> directories;

  void checkForOverlapping (Set<Path> paths, Path p) {
    Path pn = p.normalize();
    for (Path pa : paths) {      
      Path pp = pa.normalize();
      if (pp.toString().startsWith(pn.toString())) {
        // ersetzen: der neue ist mehr toplevel!
        paths.remove(pa);
        paths.add(p);
      }
      else if (pn.toString().startsWith(pa.toString())) {
        // wir haben schon einen höheren oder gleichen pfad, direkt raus
        return;
      }
    }
    if (!paths.contains(p))
      paths.add(p);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public CallResult call(HttpServletRequest rq, HttpServletResponse rsp, Object... objects) {
    // parameter 0: fsroot
    // parameter 1: files
    
    if (objects.length != 2)
      return new CallResult (RC.ERROR, this.messageprovider.getMessage("filecontents-wrong-args"),null);
    
    String fs = (String)objects[0];
    List<String> sfiles = (List<String>)objects[1];
    
    IDirectory fsroot = Util.findById(fs, this.directories);
    Path root = Paths.get(fsroot.getPath());
    
    Set<Path>files = new HashSet<>();
    for (int i=0; i<sfiles.size(); i++) {
      Path fl = root.resolve(sfiles.get(i));
      if (fl.normalize().compareTo(root.normalize())<0)
        return new CallResult (RC.ERROR, this.messageprovider.getMessage("illegal path", sfiles.get(i)),null);
      this.checkForOverlapping(files, fl);
    }
    
    return IOFetcher.fetchFilesAsZip(fsroot.isShowHiddenFiles(), this.authenticator, this.authenticator.getCurrentUser(rq), root, files.toArray(new Path[0]), rsp);
  }
  
}

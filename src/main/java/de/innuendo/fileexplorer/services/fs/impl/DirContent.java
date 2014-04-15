package de.innuendo.fileexplorer.services.fs.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.IAuthentication;

/**
 * glob:tom**=AG101
 * 
 * @author uschreiner
 *
 */
public class DirContent {  
  public static interface IAccessible {
    boolean path (Path p);
  }
  
  private static class Entry {
    private String match;
    private String[] roles;
    
    public Entry (String s, String[] r) {
      this.match = s;
      this.roles = r;
    }
    
    public String[] getRoles () {
      return this.roles;
    }
    
    public boolean matches (Path root, Path p) {
      Path rel = root.relativize(p);
      return p.getFileSystem().getPathMatcher(this.match).matches(rel);
    }
  }
  
  private List<Entry> enries = new ArrayList<>();
  
  public DirContent parseContent (String[] cnt) {
    for (String ln :cnt) {
      if (ln == null || ln.trim().length()==0 || ln.startsWith("#"))
        continue;
      
      try (Scanner s = new Scanner(ln).useDelimiter("=")) {    	  
	      String key = s.next();
	      String[] vals = s.next().split(":");
	      Entry e = new Entry (key, vals);
	      this.enries.add (e);
      }
    }
    return this;
  }

  public IAccessible can (
      final Path root, 
      final IAuthentication auth, 
      final AuthenticatedUser u) {
    return new IAccessible () {
      @Override
      public boolean path(Path p) {
        // alle entries durchgehen, bis einer den zugriff erlaubt oder am ende keine erlaubnis
        // erteilt
        for (Entry e : DirContent.this.enries) {
          if (e.matches(root, p) && auth.isAllowed(u, e.getRoles())) 
            return true;
        }
        return false;
      }};
  }
  
}

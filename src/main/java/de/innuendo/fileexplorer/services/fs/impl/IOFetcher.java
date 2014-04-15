package de.innuendo.fileexplorer.services.fs.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.IAuthentication;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.rpc.api.CallResult.RC;

public class IOFetcher {
  private static final String XPLORER = ".xplorer";

  public static void addFile (ZipOutputStream zos, Path f, final boolean showhidden, final DirContent xcontent, final Path absoluteRoot, final IAuthentication auth,final AuthenticatedUser usr) throws IOException {
    if (Files.isDirectory(f)) {
      DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
        public boolean accept(Path file) throws IOException {
          if (!xcontent.can(absoluteRoot, auth, usr).path(file))
            return false;
          if (Files.isHidden(file) && !showhidden)
            return false;
          return true;
        }
      };

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(f, filter)) {
        for (Path entry : stream) {
          if (Files.isDirectory(entry))
            addFile (zos, entry, showhidden, xcontent, absoluteRoot, auth, usr);
          else {
            ZipEntry ze = new ZipEntry(absoluteRoot.relativize(entry).toString());
            zos.putNextEntry(ze);
            Files.copy(entry, zos);
            zos.closeEntry();            
          }            
        }
      }
    }
    else {
      ZipEntry ze = new ZipEntry(absoluteRoot.relativize(f).toString());
      zos.putNextEntry(ze);
      Files.copy(f, zos);
      zos.closeEntry();                  
    }
  }
  public static CallResult fetchFilesAsZip(final boolean showhidden,
      final IAuthentication auth, final AuthenticatedUser usr,
      Path absoluteRoot, Path[] files, HttpServletResponse rsp) {
    try {
      String fname = "download.zip";
      if (files != null && files.length == 1)
        fname = files[0].getFileName().toString() + ".zip";
      rsp.setHeader("Content-Disposition", "attachment; filename=" + fname);
      rsp.setContentType("application/octet-stream");
      OutputStream os = rsp.getOutputStream();
      ZipOutputStream gzout = new ZipOutputStream(os);

      for (Path f : files) {
        if (Files.isHidden(f) && !showhidden) {
          return new CallResult(RC.ERROR, "unknown File: " + f.getFileName(),
              null);
        }
        DirContent cnt = getAllowed(absoluteRoot, f);
        if (!cnt.can(absoluteRoot, auth, usr).path(f))
          return new CallResult(RC.ERROR, "Insufficient Rights for: "
              + f.getFileName(), null);

        addFile(gzout, f, showhidden, cnt, absoluteRoot, auth, usr);

      }
      gzout.close();
      os.flush();
    } catch (IOException io) {
      return new CallResult(new String[] { "error when zipping files" });
    }
    return null;
  }

  public static CallResult getAbsolutePath (final boolean showhidden,
      final IAuthentication auth, final AuthenticatedUser usr,
      Path absoluteRoot, Path f, HttpServletResponse rsp) {
    try {
      if (Files.isHidden(f) && !showhidden) {
        return new CallResult(RC.ERROR, "unknown File: " + f.getFileName(),
            null);
      }
      DirContent cnt = getAllowed(absoluteRoot, f);
      if (!cnt.can(absoluteRoot, auth, usr).path(f))
        return new CallResult(RC.ERROR, "Insufficient Rights for: "
            + f.getFileName(), null);
      
      return new CallResult (f.toString());
    } catch (IOException e) {
      return new CallResult (RC.ERROR, "cannot access file:"+f,null);
    }    
  }
  
  public static CallResult fetchFileContent(final boolean showhidden,
      final IAuthentication auth, final AuthenticatedUser usr,
      Path absoluteRoot, String fp, Path f, boolean attach, Charset cs,
      boolean zipped, String filter, HttpServletResponse rsp) {
    try {
      if (Files.isHidden(f) && !showhidden) {
        return new CallResult(RC.ERROR, "unknown File: " + f.getFileName(),
            null);
      }
      DirContent cnt = getAllowed(absoluteRoot, f);
      if (!cnt.can(absoluteRoot, auth, usr).path(f.resolve(fp)))
        return new CallResult(RC.ERROR, "Insufficient Rights for: "
            + f.resolve(fp).getFileName(), null);

      if (attach) {
        String fn = f.getFileName().toString();
        if (zipped)
          fn = fn + ".zip";
        rsp.setHeader("Content-Disposition", "attachment; filename=" + fn);
        rsp.setContentType("application/octet-stream");
        long len = Files.size(f);
        if (!zipped) {
          rsp.setContentLength((int) len);
          OutputStream os = rsp.getOutputStream();
          Files.copy(f, os);
          os.flush();
        } else {
          OutputStream os = rsp.getOutputStream();
          ZipOutputStream gzout = new ZipOutputStream(os);
          ZipEntry ze = new ZipEntry(f.getFileName().toString());
          gzout.putNextEntry(ze);
          Files.copy(f, gzout);
          gzout.closeEntry();
          gzout.close();
          os.flush();
        }
        return null;
      } else {
        // String ct = Files.probeContentType(f);
        // if (ct != null && ct.startsWith ("text")) {
        Pattern p = null;
        if (filter != null)
          p = Pattern.compile(filter);
        if (!zipped) { // zipped in diesem fall als kennzeichner fuer hex
          //if (p == null)
            //rsp.setContentLength((int) Files.size(f));
          PrintWriter pw = new PrintWriter(rsp.getOutputStream());
          try (LineReader lr = new LineReader(f)) {
            pushLinesToClient(rsp, p, pw, lr);
          }
        } else {
          PrintWriter pw = new PrintWriter(rsp.getOutputStream());
          try (HexReader hr = new HexReader(f)) {
            pushLinesToClient(rsp, p, pw, hr);
          }
        }
        return null;
      }
    } catch (PatternSyntaxException pse) {
      return new CallResult(RC.ERROR, "Ungültiges Pattern: "
          + pse.getDescription(), null);
    } catch (IOException io) {
      return new CallResult(new String[] { "<" + fp + ">" });
    }

  }

  static void pushLinesToClient(HttpServletResponse rsp, Pattern p,
      PrintWriter pw, ILineReader lr) throws IOException {
    if (p != null) {
      for (String ln = lr.readLine(); ln != null; ln = lr.readLine()) {
        if (p.matcher(ln).find()) {
          pw.println(ln);
          pw.flush();
          rsp.flushBuffer();
        }
      }

    } else {
      for (String ln = lr.readLine(); ln != null; ln = lr.readLine()) {
        pw.println(ln);
        pw.flush();
        rsp.flushBuffer();
      }
    }
  }

  public static CallResult fetchContent(final boolean showhidden,
      final IAuthentication auth, final AuthenticatedUser usr,
      final Path absoluteRoot, Path root, final boolean withDirs,
      final boolean withFiles) {
    FileObject result = new FileObject(toElements(absoluteRoot, root), true);
    ArrayList<FileObject> res = new ArrayList<FileObject>();

    final DirContent xcontent = getAllowed(absoluteRoot, root);

    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
      public boolean accept(Path file) throws IOException {
        if (!xcontent.can(absoluteRoot, auth, usr).path(file))
          return false;
        if (Files.isHidden(file) && !showhidden)
          return false;
        if (withDirs && Files.isDirectory(file))
          return true;
        if (withFiles && Files.isRegularFile(file))
          return true;
        return false;
      }
    };

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, filter)) {
      SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS",
          Locale.GERMANY);

      for (Path entry : stream) {
        FileObject fo = new FileObject(toElements(absoluteRoot, entry),
            Files.isDirectory(entry));
        fo.setUser(Files.getOwner(entry).getName());
        fo.setSize(Files.size(entry));
        try {
          fo.setGroup(Files.getAttribute(entry, "unix:group").toString());
          fo.setRights(permissions(Files.getPosixFilePermissions(entry)));
        } catch (UnsupportedOperationException e) {
          // wenn wir nicht unter unix laufen, dann gehen die beiden felder
          // nicht
          // --> exception wird absichtlich ignoriert
        }
        Date t = new Date(Files.getLastModifiedTime(entry).toMillis());
        fo.setLastaccess(df.format(t));
        res.add(fo);
      }
    } catch (IOException e) {
      return new CallResult(CallResult.RC.ERROR, e.getMessage(), null);
    }

    Collections.sort(res, new Comparator<FileObject>() {
      @Override
      public int compare(FileObject o1, FileObject o2) {
        if (o1.isDirectory() != o2.isDirectory())
          return o1.isDirectory() ? -1 : 1;
        return o1.getLabel().compareToIgnoreCase(o2.getLabel());
      }
    });
    result.setChildren(res.toArray(new FileObject[0]));
    return new CallResult(result);

  }

  static DirContent getAllowed(final Path absoluteRoot, Path root) {
    String[] allowed = new String[0];
    try {
      Path rootdir = root;
      Path xpfile = rootdir.resolve(XPLORER);
      while (!Files.exists(xpfile)
          && !(xpfile.normalize().compareTo(absoluteRoot.normalize()) < 0)) {
        rootdir = rootdir.getParent();
        xpfile = rootdir.resolve(XPLORER);
      }
      allowed = Files.readAllLines(xpfile, Charset.forName("utf-8")).toArray(
          new String[0]);
    } catch (IOException e) {
      // kein xplorer-file, einfach ein leeres annehmen
    }
    final DirContent xcontent = new DirContent().parseContent(allowed);
    return xcontent;
  }

  public static String[] toElements(Path root, Path p) {
    Path rel = root.relativize(p);
    String[] res = new String[rel.getNameCount()];
    for (int i = 0; i < res.length; i++)
      res[i] = rel.getName(i).toString();
    return res;
  }

  public static String permissions(Set<PosixFilePermission> perms) {
    StringBuilder b = new StringBuilder();
    String[] chars = new String[] { "r", "w", "x" };

    PosixFilePermission[] p = { PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
        PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
        PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_READ,
        PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE, };

    for (int i = 0; i < p.length; i++) {
      if (perms.contains(p[i]))
        b.append(chars[i % 3]);
      else
        b.append("-");
    }
    return b.toString();
  }
}

package de.innuendo.fileexplorer.services.fs.impl;

import de.innuendo.fileexplorer.services.fs.api.IDirectory;

public class Util {
  public static IDirectory findById (String dir, Iterable<IDirectory> dirs) {
    for (IDirectory d : dirs)
      if (d.getId().equals(dir)) return d;
    throw new RuntimeException("illegal directory: "+dir);
  }
}

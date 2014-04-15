package de.innuendo.fileexplorer.services.fs.impl;

public class FileObject {
  private static FileObject[] EMPTY = new FileObject[0];

  private String[] path;
  private String label;
  private FileObject[] children;
  private String user;
  private String group;
  private String rights;
  private String lastaccess;
  private long size;
  
  private boolean directory;

  public FileObject(String[] path, FileObject[] children) {
    this.path = path;
    this.children = children;
    this.directory = true;
    this.label = this.path[this.path.length-1];
  }

  public FileObject(String[] path, boolean isdir) {
    this(path, EMPTY);
    this.directory = isdir;

  }

  public String[] getPath() {
    return path;
  }

  public FileObject[] getChildren() {
    return children;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getRights() {
    return rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  public String getLastaccess() {
    return lastaccess;
  }

  public void setLastaccess(String lastaccess) {
    this.lastaccess = lastaccess;
  }

  public static FileObject[] getEMPTY() {
    return EMPTY;
  }

  public boolean isDirectory() {
    return directory;
  }

  public void setChildren(FileObject[] children) {
    this.children = children;
  }

  public String getLabel() {
    return label;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }
}

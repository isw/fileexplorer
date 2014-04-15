package de.innuendo.fileexplorer.services.fs.impl;

import de.innuendo.fileexplorer.services.fs.api.IDirectory;
import static de.innuendo.fileexplorer.util.Functions.replaceEnv;;

public class Directory implements IDirectory {
  private String id;
  private String label;
  private boolean showHiddenFiles;
  private String path;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getLabel() {
    return label;
  }
  public void setLabel(String label) {
    this.label = label;
  }
  public boolean isShowHiddenFiles() {
    return showHiddenFiles;
  }
  public void setShowHiddenFiles(boolean showHiddenFiles) {
    this.showHiddenFiles = showHiddenFiles;
  }
  @Override
  public String getComponentName() {
    return "directory."+this.id;
  }
  public String getPath() {
    return replaceEnv(path);
  }
  public void setPath(String path) {
    this.path = path;
  }
}

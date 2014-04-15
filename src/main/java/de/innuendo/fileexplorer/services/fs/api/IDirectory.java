package de.innuendo.fileexplorer.services.fs.api;

import de.innuendo.fileexplorer.service.api.IComponent;

public interface IDirectory extends IComponent {
  public String getLabel();
  public boolean isShowHiddenFiles();
  public String getId();
  public String getPath();
}

package de.innuendo.fileexplorer.service.api;

public interface IRegistry {
  public <T> T getByName (String componentName);
}

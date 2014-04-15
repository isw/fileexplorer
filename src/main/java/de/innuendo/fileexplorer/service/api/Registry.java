package de.innuendo.fileexplorer.service.api;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class Registry implements IRegistry {

  @Inject
  private Map<String,IComponent> components = new HashMap<>();

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getByName(String componentName) {
    return (T)this.components.get(componentName);
  }

}

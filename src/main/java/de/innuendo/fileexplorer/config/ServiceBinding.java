package de.innuendo.fileexplorer.config;

import java.util.ArrayList;
import java.util.List;

import de.innuendo.fileexplorer.service.api.IComponent;

public class ServiceBinding {
  private String id;
  private String instanceInterface;
  private List<IComponent> implementations = new ArrayList<>();
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getInstanceInterface() {
    return instanceInterface;
  }
  public void setInstanceInterface(String instanceInterface) {
    this.instanceInterface = instanceInterface;
  }
  public List<IComponent> getImplementations() {
    return implementations;
  }
  public void setImplementations(List<IComponent> implementations) {
    this.implementations.addAll(implementations);
  }
    
  public void addImplementation (IComponent impl) {
    this.implementations.add (impl);
  }
  
  @SuppressWarnings("unchecked")
  public Class<IComponent> getInterface () throws ClassNotFoundException {
    return (Class<IComponent>)Class.forName(this.instanceInterface, true, this.getClass().getClassLoader());
  }
}

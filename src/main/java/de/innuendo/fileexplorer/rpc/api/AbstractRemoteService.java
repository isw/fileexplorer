package de.innuendo.fileexplorer.rpc.api;


public abstract class AbstractRemoteService implements IRemoteService {

  private String name;
  
  @Override
  public String getComponentName() {
    return this.getName();
  }

  @Override
  public String getName() {
    return this.name;
  }

  public void setName (String n) {
    this.name = n;
  }

}

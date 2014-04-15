package de.innuendo.fileexplorer.service.api;

/**
 * Kann als Basisklasse genutzt werden, msus aber nicht
 * @author ize0h88
 *
 */
public abstract class ComponentBaseImpl implements IComponent {
  private String name;

  public void setComponentName(String n) {
    this.name = n;
  }

  @Override
  public String getComponentName() {
    return this.name;
  }

}

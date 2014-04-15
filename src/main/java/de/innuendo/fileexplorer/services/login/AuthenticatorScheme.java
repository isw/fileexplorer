package de.innuendo.fileexplorer.services.login;

public class AuthenticatorScheme {
  private String componentName;
  private String description;
  public AuthenticatorScheme () {}
  public AuthenticatorScheme (String n, String d) {
    this.componentName = n;
    this.description = d;
  }
  public String getComponentName() {
    return componentName;
  }
  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
}

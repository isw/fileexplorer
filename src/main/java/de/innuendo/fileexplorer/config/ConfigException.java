package de.innuendo.fileexplorer.config;

public class ConfigException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String key;
  
  public ConfigException (String k, String m) {
    super (m);
    this.key = k;
  }
  
  public String getKey () {
    return this.key;
  }
}

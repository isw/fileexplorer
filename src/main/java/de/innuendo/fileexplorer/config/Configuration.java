package de.innuendo.fileexplorer.config;

import java.util.List;
import java.util.Map;

public class Configuration {
  private Map<String, ?> configdata;
  
  public Configuration (Map<String, ?> d) {
    this.configdata = d;
  }
  
  void checkKey (String k) throws ConfigException {
    if (!this.configdata.containsKey(k))
      throw new ConfigException (k, "kein Configkey: "+k);
  }
  public String get (String k) throws ConfigException {
    this.checkKey(k);
    return (String)this.configdata.get(k);
  }
  
  public boolean getBoolean (String k) throws ConfigException {
    this.checkKey(k);
    return (Boolean)this.configdata.get(k);
  }
  public List<?> getList (String k) throws ConfigException {
    this.checkKey(k);
    return (List<?>)this.configdata.get(k);
  }
  @SuppressWarnings("unchecked")
  public Map<String,?> getObject (String k) throws ConfigException {
    this.checkKey(k);
    return (Map<String,?>)this.configdata.get(k);
  }
}

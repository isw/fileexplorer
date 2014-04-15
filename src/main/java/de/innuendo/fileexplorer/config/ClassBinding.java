package de.innuendo.fileexplorer.config;

public class ClassBinding<T> {
  private String id;
  private String from;
  private String to;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getFrom() {
    return from;
  }
  public void setFrom(String from) {
    this.from = from;
  }
  public String getTo() {
    return to;
  }
  public void setTo(String to) {
    this.to = to;
  }
  
  @SuppressWarnings("unchecked")
  public Class<T> getFromClass () throws ClassNotFoundException {
    return ((Class<T>) Class.forName(this.from, true, this.getClass().getClassLoader()));
  }
  @SuppressWarnings("unchecked")
  public Class<? extends T> getToClass () throws ClassNotFoundException {
    return (Class<? extends T>) Class.forName(this.to, true, this.getClass().getClassLoader());
  }
}

package de.innuendo.fileexplorer.servlets;

public class CallData {
	private String service;

	private Object[] parameter;
	
	public Object[] getParameter() {
		return parameter;
	}
	public void setParameter(Object[] parameter) {
		this.parameter = parameter;
	}
  public String getService() {
    return service;
  }
  public void setService(String service) {
    this.service = service;
  }
}

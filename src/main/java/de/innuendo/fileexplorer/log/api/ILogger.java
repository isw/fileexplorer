package de.innuendo.fileexplorer.log.api;

public interface ILogger {
	public void info (Exception e, String msg, Object... par);
	public void warn (Exception e, String msg, Object... par);
	public void error (Exception e, String msg, Object... par);	
}

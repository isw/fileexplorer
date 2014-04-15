package de.innuendo.fileexplorer.log.impl;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import de.innuendo.fileexplorer.log.api.ILogger;

public class ServletLogger implements ILogger {

	@Inject
	private ServletContext context;
	
	@Override
	public void error(Exception e, String msg, Object... par) {
		log("ERROR : ",e, msg, par);
	}

	private void log(String prefix, Exception e, String msg, Object... par) {
		String message = msg;
		if (par != null)
			message =  String.format (msg, par);
		if (e != null)
			this.context.log(prefix+message, e);
		else
			this.context.log(prefix+message);
	}

	@Override
	public void info(Exception e, String msg, Object... par) {
		log("INFO : ",e, msg, par);
	}

	@Override
	public void warn(Exception e, String msg, Object... par) {
		log("WARN : ",e, msg, par);
	}

}

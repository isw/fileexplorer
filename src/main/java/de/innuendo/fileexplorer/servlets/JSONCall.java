package de.innuendo.fileexplorer.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import de.innuendo.fileexplorer.log.api.ILogger;
import de.innuendo.fileexplorer.message.api.IMessageProvider;
import de.innuendo.fileexplorer.rpc.api.CallResult;
import de.innuendo.fileexplorer.rpc.api.IRemoteService;

@Singleton
public class JSONCall extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ILogger logger;
	
	@Inject
	private IMessageProvider messageprovider;
	
	@Inject
	private Set<IRemoteService> services;
	
	@Inject
	private Gson gson;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String calldata = req.getParameter( "calldata" );
		CallData cd = this.gson.fromJson(calldata, CallData.class);
		
		CallResult result = null;
		if (calldata == null) {
			result = new CallResult (CallResult.RC.ERROR, messageprovider.getMessage("empty-calldata"), null);
		}
		else {
		  IRemoteService srv = this.findService(cd);
		  if (srv == null)
		    result = new CallResult (CallResult.RC.ERROR, messageprovider.getMessage("unknown-service", cd.getService()),null);
		  else
			result = srv.call(req, resp, cd.getParameter());
		}
		resp.setContentType("application/json;utf-8");
		if (result != null) // jemand kann auch direkt in den outputstream schreiben
		  this.dumpToWriter(this.renderResult(result), resp.getWriter());
	}

	IRemoteService findService (CallData cd) {
	  for (IRemoteService srv : this.services)
	    if (srv.getName().equals(cd.getService())) return srv;
	  throw new RuntimeException ("unknown JSON Service: "+cd.getService());
	}
	
	String renderResult (CallResult cr) {
		return this.gson.toJson(cr);
	}
	
	void dumpToWriter (String s, PrintWriter pw) {
		pw.println (s);
		pw.flush ();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();
		pw.println ("json");
		pw.flush ();
		this.logger.info(null, "json test");
	}

}

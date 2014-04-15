package de.innuendo.fileexplorer.rpc.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.innuendo.fileexplorer.service.api.IComponent;

public interface IRemoteService extends IComponent {
	public String getName ();
	public CallResult call (HttpServletRequest rq, HttpServletResponse rsp, Object...objects);
}

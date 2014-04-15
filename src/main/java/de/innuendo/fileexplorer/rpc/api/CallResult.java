package de.innuendo.fileexplorer.rpc.api;

public class CallResult {
	public static enum RC {
		OK, ERROR
	}
	
	private RC status;
	private String message;
	private Object result;

	public CallResult (RC s, String m, Object r) {
		this.status = s;
		this.message = m;
		this.result = r;
	}
	
	public CallResult (Object r) {
		this (RC.OK, null, r);
	}
	
	public RC getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public Object getResult() {
		return result;
	}

}

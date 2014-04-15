package de.innuendo.fileexplorer.login.impl.ldap;

import java.io.Serializable;

public class LdapBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ldapServer;
	private String initialFactory;
	private String base;
	private String techUser;
	private String techPassword;
	private String groupsQuery;
	private String userQuery;
	
	public String getLdapServer() {
		return ldapServer;
	}
	public void setLdapServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getTechUser() {
		return techUser;
	}
	public void setTechUser(String techUser) {
		this.techUser = techUser;
	}
	public String getTechPassword() {
		return techPassword;
	}
	public void setTechPassword(String techPassword) {
		this.techPassword = techPassword;
	}
	public String getGroupsQuery() {
		return groupsQuery;
	}
	public void setGroupsQuery(String groupsQuery) {
		this.groupsQuery = groupsQuery;
	}
	public String getInitialFactory() {
		return initialFactory;
	}
	public void setInitialFactory(String initialFactory) {
		this.initialFactory = initialFactory;
	}
	public String getUserQuery() {
		return userQuery;
	}
	public void setUserQuery(String userQuery) {
		this.userQuery = userQuery;
	}
	
}
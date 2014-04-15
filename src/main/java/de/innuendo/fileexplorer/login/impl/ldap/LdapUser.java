package de.innuendo.fileexplorer.login.impl.ldap;
import java.io.Serializable;

public class LdapUser implements Serializable {
	/**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String name;
	private String[] groups;
	
	public LdapUser (String n, String[] grps) {
		this.name = n;
		this.groups = grps;
	}
	
	public String getName() {
		return name;
	}
	public String[] getGroups() {
		return groups;
	}
}
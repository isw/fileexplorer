package de.innuendo.fileexplorer.login.impl.ldap;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapName;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LdapAccess {
	private static boolean checkUser(LdapBean lb, String usr, String userdn, String pwd) throws LdapException {
		try {
			InitialLdapContext ctx = getContext(lb, userdn, pwd);
			ctx.close ();
			return true;
		}
		catch (NamingException ne) {
			throw new LdapException("Kennung '"+usr+"' kann nicht authentifiziert werden!", ne);
		}
	}

	private static String findUserDN(LdapBean lb, String uid) throws NamingException {
		InitialLdapContext ctx = getContext(lb, lb.getTechUser(), lb.getTechPassword());
		try {
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			sc.setReturningAttributes(new String[] { "cn" });
			NamingEnumeration ne = ctx.search(lb.getBase(), String.format(lb.getUserQuery(),uid), sc);
			if (ne != null && ne.hasMore()) {
				SearchResult sr = (SearchResult) ne.next();
				return sr.getNameInNamespace();
			}
			return null;
		}
		finally {
			ctx.close ();
		}
	}

  private static String[] findUserGroups(LdapBean lb, String uid) throws NamingException {
		InitialLdapContext ctx = getContext(lb, lb.getTechUser(), lb.getTechPassword());
		try {
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			sc.setReturningAttributes(new String[] { "cn" });
			NamingEnumeration ne = ctx.search(lb.getBase(), String.format(lb.getGroupsQuery(), uid), sc);
			ArrayList<String> res = new ArrayList<String>();
			while (ne != null && ne.hasMore()) {
				SearchResult sr = (SearchResult) ne.next();
				LdapName ln = new LdapName(sr.getNameInNamespace());
				res.add(String.valueOf(ln.getRdn(ln.size() - 1).getValue()));
			}
			return res.toArray(new String[0]);
		}
		finally {
			ctx.close ();
		}
	}

  private static InitialLdapContext getContext(LdapBean lb, String user, String password)
			throws NamingException {
		Hashtable h = new Hashtable();
		h.put(Context.INITIAL_CONTEXT_FACTORY, lb.getInitialFactory());
		h.put(Context.PROVIDER_URL, lb.getLdapServer());
		h.put("java.naming.security.authentication", "simple");
		h.put(Context.SECURITY_PRINCIPAL, user);
		h.put(Context.SECURITY_CREDENTIALS, password);

		return new InitialLdapContext(h, null);
	}

	public static LdapUser auth(String u, String p, boolean preAuth, LdapBean lb) throws NamingException, LdapException {
		String userdn = findUserDN(lb, u);
		if (userdn == null) throw new LdapException("Kennung '"+u+"' nicht gefunden!");
		if (!preAuth) checkUser(lb, u, userdn, p);
		String[] groups = findUserGroups(lb, userdn);
		LdapUser lu = new LdapUser(u, groups);
		
		return lu;
	}
}

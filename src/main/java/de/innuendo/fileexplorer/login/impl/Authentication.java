package de.innuendo.fileexplorer.login.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.innuendo.fileexplorer.login.api.AuthenticatedUser;
import de.innuendo.fileexplorer.login.api.IAuthentication;

public class Authentication implements IAuthentication {
  private static final String USER = "__auth__user__";

  private String godrole;
  
  public AuthenticatedUser getCurrentUser(HttpServletRequest request) {
    HttpSession s = request.getSession(true);
    return (AuthenticatedUser) s.getAttribute(USER);
  }

  public void setCurrentUser(HttpServletRequest req, AuthenticatedUser usr) {
    req.getSession(true).setAttribute(USER, usr);
  }
  
  @Override
  public boolean isAllowed(AuthenticatedUser u, String[] required) {
    Set<String> s1 = new HashSet<>(Arrays.asList(u.getGroups()));
    Set<String> s2 = new HashSet<>(Arrays.asList(required));
    
    if (this.godrole != null && s1.contains(this.godrole)) return true; // user ist gott :-)
    
    s2.retainAll(s1);
    
    return !s2.isEmpty();
  }

  @Override
  public String getComponentName() {
    return "authentication";
  }

  public String getGodrole() {
    return godrole;
  }

  public void setGodrole(String godrole) {
    this.godrole = godrole;
  }  
}

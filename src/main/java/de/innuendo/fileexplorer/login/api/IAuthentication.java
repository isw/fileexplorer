package de.innuendo.fileexplorer.login.api;

import javax.servlet.http.HttpServletRequest;

import de.innuendo.fileexplorer.service.api.IComponent;

public interface IAuthentication extends IComponent {
  public AuthenticatedUser getCurrentUser(HttpServletRequest request);
  public void setCurrentUser(HttpServletRequest req, AuthenticatedUser usr);
  public boolean isAllowed(AuthenticatedUser u, String[] required);

}

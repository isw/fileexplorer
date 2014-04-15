package de.innuendo.fileexplorer.login.api;

import de.innuendo.fileexplorer.service.api.IComponent;

public interface IUserAuthentication extends IComponent {
  
  public String getDescription ();
  
  public AuthenticatedUser authenticate (String usr, String pwd)
    throws AuthenticationException;
  
}

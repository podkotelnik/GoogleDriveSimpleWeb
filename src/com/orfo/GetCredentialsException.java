package com.orfo;

/**
 * Exception thrown when an error occurred while retrieving credentials.
 */
public class GetCredentialsException extends Exception {

  protected String authorizationUrl;

  /**
   * Construct a GetCredentialsException.
   * 
   * @param authorizationUrl The authorization URL to redirect the user to.
   */
  public GetCredentialsException(String authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
  }

  /**
   * Set the authorization URL.
   */
  public void setAuthorizationUrl(String authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
  }

  /**
   * @return the authorizationUrl
   */
  public String getAuthorizationUrl() {
    return authorizationUrl;
  }
}

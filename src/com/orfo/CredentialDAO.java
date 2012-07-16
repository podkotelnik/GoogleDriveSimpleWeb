package com.orfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class CredentialDAO {

  private static GoogleAuthorizationCodeFlow flow                   = null;

  private static final String                REDIRECT_URI           = "https://mykaban1.appspot.com/googledrivesimple";

  private static final List<String>          SCOPES                 = Arrays.asList("https://www.googleapis.com/auth/userinfo.email",
                                                                                    "https://www.googleapis.com/auth/userinfo.profile",
                                                                                    "https://www.googleapis.com/auth/drive.readonly");

  private static final String                CLIENTSECRETS_LOCATION = "{\"web\": {\"client_id\": \"1013188646846.apps.googleusercontent.com\",\"client_secret\": \"PPRMtwbVUOO38GlmjOuY8mHO\",\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\"token_uri\": \"https://accounts.google.com/o/oauth2/token\"}}";

  /**
   * Exchange an authorization code for OAuth 2.0 credentials.
   * 
   * @param authorizationCode Authorization code to exchange for OAuth 2.0
   *          credentials.
   * @return OAuth 2.0 credentials.
   * @throws CodeExchangeException An error occurred.
   */
  public Credential exchangeCode(String authorizationCode) throws CodeExchangeException {
    try {
      GoogleAuthorizationCodeFlow flow = getFlow();
      GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                                         .setRedirectUri(REDIRECT_URI)
                                         .execute();
      return flow.createAndStoreCredential(response, null);
    } catch (IOException e) {
      System.err.println("An error occurred: " + e);
      throw new CodeExchangeException(null);
    }
  }

  /**
   * Build an authorization flow and store it as a static class attribute.
   * 
   * @return GoogleAuthorizationCodeFlow instance.
   * @throws IOException Unable to load client_secrets.json.
   */
  static GoogleAuthorizationCodeFlow getFlow() throws IOException {
    if (flow == null) {
      HttpTransport httpTransport = new NetHttpTransport();
      JacksonFactory jsonFactory = new JacksonFactory();
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                                                                   new ByteArrayInputStream(CLIENTSECRETS_LOCATION.getBytes()));
      // OAuthCredent.class.getResourceAsStream(CLIENTSECRETS_LOCATION));
      flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
                                                     jsonFactory,
                                                     clientSecrets,
                                                     SCOPES).setAccessType("offline")
                                                            .setApprovalPrompt("force")
                                                            .build();
    }
    return flow;
  }
}

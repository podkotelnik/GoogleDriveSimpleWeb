package com.orfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.*;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@SuppressWarnings("serial")
public class GoogleDriveSimpleServlet extends HttpServlet {
  /** The name of the Oauth code URL parameter */
  public static final String CODE_URL_PARAM_NAME  = "code";

  /** The name of the OAuth error URL parameter */
  public static final String ERROR_URL_PARAM_NAME = "error";

  /** The URL suffix of the servlet */
  public static final String URL_MAPPING          = "/oauth2callback";

  HttpTransport              httpTransport        = new NetHttpTransport();

  JacksonFactory             jsonFactory          = new JacksonFactory();

  CredentialDAO              credentialDAO        = new CredentialDAO();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Getting the "error" URL parameter
    String[] error = request.getParameterValues(ERROR_URL_PARAM_NAME);

    // Checking if there was an error such as the user denied access
    if (error != null && error.length > 0) {
      response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "There was an error: \"" + error[0]
          + "\".");
      return;
    }

    // Getting the "code" URL parameter
    String[] code = request.getParameterValues(CODE_URL_PARAM_NAME);

    // Checking conditions on the "code" URL parameter
    if (code == null || code.length == 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                         "The \"code\" URL parameter is missing");
      return;
    }

    PrintWriter out = response.getWriter();

    Credential credential = null;
    try {
      credential = credentialDAO.exchangeCode(code[0]);
    } catch (CodeExchangeException e) {
      e.printStackTrace();
    }

    Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential).build();
    
    out.print("<!DOCTYPE html PUBLIC  \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
    out.print("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
    out.print("<head>");
    out.print("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
    out.print("<title>Google Drive</title>");
    out.print("</head>");
    out.print("<body>");
    out.print("<style> p { text-indent: 30px;}</style>");

    List<File> listFile = WorkWithDriveData.retrieveAllFiles(drive);
    for (int i = 0; i < listFile.size() && i < 5; i++) {
      File one = listFile.get(i);
      out.print("Title = " + "<a href=" + one.getAlternateLink() + " target=\"_blank\">"
          + one.getTitle() + "</a>" + "<p>Owner names = " + one.getOwnerNames()
          + "<p>Last modified data = " + one.getModifiedDate() + "<p>Mime type = "
          + one.getMimeType() + "<br><br>");
    }
    out.print("</body></html>");
  }
}

/**
 * 
 */

package cd.go.common.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author brigl
 *
 */
public class HttpClient implements Closeable {

  private final URL               url;
  private final HttpURLConnection conn;

  /**
   * Creates a new {@link HttpClient}
   * 
   * @param url
   * @param method
   */
  private HttpClient(String url, String method) throws IOException {
    this(new URL(url), method);
  }

  /**
   * Creates a new {@link HttpClient}
   * 
   * @param url
   * @param method
   */
  private HttpClient(URL url, String method) throws IOException {
    this.url = url;
    this.conn = (HttpURLConnection) url.openConnection();
    this.conn.setRequestMethod(method);
  }

  /**
   * Get the {@link URL}.
   */
  public final URL getUrl() {
    return this.url;
  }

  /**
   * Get the response status code.
   */
  public final int getResponseCode() throws IOException {
    return this.conn.getResponseCode();
  }

  /**
   * Get the response status message.
   */
  public final String getResponseText() throws IOException {
    return this.conn.getResponseMessage();
  }

  /**
   * Get the response status message.
   */
  public final String getHeader(String name) {
    return this.conn.getHeaderField(name);
  }

  /**
   * Get the response status message.
   */
  public final String getContentType() {
    return this.conn.getContentType();
  }

  /**
   * Get the response status message.
   */
  public final int getContentLength() {
    return this.conn.getContentLength();
  }


  public final void storeTo(File destination) throws IOException {
    byte[] buffer = new byte[4096];
    // opens input stream from the HTTP connection
    try (InputStream inputStream = conn.getInputStream()) {
      // opens an output stream to save into file
      try (FileOutputStream outputStream = new FileOutputStream(destination)) {
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }
      }
    }
  }

  @Override
  public final void close() throws IOException {
    this.conn.disconnect();
  }

  /**
   * Get a HEAD request.
   * 
   * @param url
   */
  public static HttpClient head(String url) throws IOException {
    HttpClient client = new HttpClient(url, "HEAD");
    client.conn.connect();
    return client;
  }

  /**
   * Get a GET request.
   * 
   * @param url
   */
  public static HttpClient get(String url) throws IOException {
    HttpClient client = new HttpClient(url, "GET");
    client.conn.connect();
    return client;
  }
}

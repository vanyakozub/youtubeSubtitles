/**
 * Sample Java code for youtube.captions.download
 * See instructions for running these code samples locally:
 * https://developers.google.com/explorer-help/guides/code_samples#java
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.CaptionSnippet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ApiExample {
    private static final String CLIENT_SECRETS= "client_secret.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws Exception {
        // Load client secrets.
        

        InputStream in = ApiExample.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, Exception, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        String videoId ="fJ9rUzIMcZQ";
        // TODO: Replace "YOUR_FILE" with the location where
        //       the downloaded content should be written.
        OutputStream output = new FileOutputStream("sub.txt");

        CaptionListResponse captionListResponse = youtubeService.captions().
                list("snippet", videoId).execute();

        List<Caption> captions = captionListResponse.getItems();
        // Print information from the API response.
        System.out.println("\n============ Returned Caption Tracks ============\n");
        CaptionSnippet snippet;
        String trackId = new String();
        for (Caption caption : captions) {
            snippet = caption.getSnippet();
            System.out.println("  - ID: " + caption.getId());
            trackId = caption.getId();
            System.out.println("  - Name: " + snippet.getName());
            System.out.println("  - Language: " + snippet.getLanguage());
            System.out.println("\n----------------------------------------------\n");
        }
        // Define and execute the API request
        YouTube.Captions.Download request = youtubeService.captions()
                .download(trackId);
        //HttpGet req = new HttpGet("www.googleapis.com/youtube/v3/captions/fJ9rUzIMcZQ");
        //CloseableHttpClient client = HttpClients.createDefault();
        //CloseableHttpResponse response = client.execute(req);
        request.getMediaHttpDownloader();
        request.executeMediaAndDownloadTo(output);
    }
}
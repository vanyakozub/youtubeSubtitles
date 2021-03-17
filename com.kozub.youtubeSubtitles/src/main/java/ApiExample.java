import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.CaptionSnippet;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ApiExample {


    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, Exception, GoogleJsonResponseException {

        /**
         * work
         */

       /* YouTube youtubeService = SubExtractor.getService();
        String videoId =args[0].substring(args[0].indexOf("v="));
        videoId = videoId.substring(2, videoId.length()-1);
        OutputStream output = new FileOutputStream(args[2]);
        CaptionListResponse captionListResponse = youtubeService.captions().
                list("snippet", videoId).execute();

        List<Caption> captions = captionListResponse.getItems();
        // Print information from the API response.
        String trackId ="";
        Caption caption = captions.get(0);
        trackId = caption.getId();

        YouTube.Captions.Download request = youtubeService.captions()
                .download(trackId);
        request.getMediaHttpDownloader();
        request.executeMediaAndDownloadTo(output);/*

        /**
         * до сюда
         */
        if ("text".equals(args[1])) {


            Stream<String> wordsStream = Files.lines(Paths.get(args[2]));

            Object[] content = wordsStream.toArray();

            ArrayList<String> subtitles = new ArrayList<>();
            ArrayList<Timing> timming = new ArrayList<>();

            String tmpBegin = "";
            String tmpEnd = "";
            String tmpHours = "";
            String tmpMinutes = "";
            String tmpSeconds = "";
            String tmpMilliseconds = "";
            String[] tmp;
            String[] tmpTimming1;
            String[] tmpTimming2;
            for (int i = 0; i < content.length - 1; i++) {
                if (i % 3 == 1) {
                    subtitles.add((String) content[i]);
                }
                if (i % 3 == 0) {
                    tmp = content[i].toString().split(",");
                    tmpBegin = tmp[0];
                    tmpEnd = tmp[1];
                    tmpTimming1 = tmpBegin.split(":");
                    tmpTimming2 = tmpEnd.split(":");
                    Duration begin = Duration.ofHours(Integer.parseInt(tmpTimming1[0]));
                    begin = begin.plusMinutes(Integer.parseInt(tmpTimming1[1]));
                    begin = begin.plusSeconds(Integer.parseInt(tmpTimming1[2].substring(0, tmpTimming1[2].indexOf("."))));
                    begin = begin.plusMillis(Integer.parseInt(tmpTimming1[2].substring(tmpTimming1[2].indexOf(".") + 1)));
                    Duration end = Duration.ofHours(Integer.parseInt(tmpTimming2[0]));
                    end = end.plusMinutes(Integer.parseInt(tmpTimming2[1]));
                    end = end.plusSeconds(Integer.parseInt(tmpTimming2[2].substring(0, tmpTimming2[2].indexOf("."))));
                    end = end.plusMillis(Integer.parseInt(tmpTimming2[2].substring(tmpTimming2[2].indexOf(".") + 1)));
                    timming.add(new Timing(begin, end));
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            BufferedWriter writer = new BufferedWriter(new FileWriter("TEXT.txt"));
            writer.append("\t");
            for (int i = 0; i < timming.size() - 1; i++) {
                long curDelay = timming.get(i).getEnd().toMillis() - timming.get(i + 1).getBegin().toMillis();
                System.out.println(curDelay);
                if (i % 6 == 0 && i > 0) {
                    writer.newLine();
                }
                writer.append(subtitles.get(i));
                if (curDelay < 0) {
                    writer.append(".");
                    writer.newLine();
                    writer.newLine();
                    writer.append("\t");
                }
                if (curDelay > 3300) {
                    writer.append(".");
                }
                writer.append(" ");
            }
            writer.append(subtitles.get(subtitles.size() - 1));



            for (Object str : content) {
                //System.out.println(str);
            }
            System.out.println(subtitles);
            System.out.println(timming);
        }
        RussianAnalyzer analyzer = new RussianAnalyzer();
        /*TokenStream stream = analyzer.tokenStream("field", content);
        stream.reset();
        while (stream.incrementToken()) {
            String lemma = stream.getAttribute(CharTermAttribute.class).toString();
            System.out.print(lemma + " ");
        }
        stream.end();
        stream.close();*/
    }
}
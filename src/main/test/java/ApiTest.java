import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ApiTest {
    @Test
    public void apiExampleTest(String[] args) throws Exception {

        YouTube youtubeService = SubExtractor.getService();
        String videoId = args[0].substring(args[0].indexOf("v="));
        videoId = videoId.substring(2, videoId.length() - 1);
        OutputStream output = new FileOutputStream(args[2]);
        CaptionListResponse captionListResponse = youtubeService.captions().
                list("snippet", videoId).execute();

        List<Caption> captions = captionListResponse.getItems();
        // Print information from the API response.
        String trackId = "";
        Caption caption = captions.get(0);
        trackId = caption.getId();

        YouTube.Captions.Download request = youtubeService.captions()
                .download(trackId);
        request.getMediaHttpDownloader();
        request.executeMediaAndDownloadTo(output);


        Stream<String> wordsStream = Files.lines(Paths.get(args[3]));

        Object[] content = wordsStream.toArray();

        ArrayList<String> subtitles = new ArrayList<>();
        ArrayList<Timing> timming = new ArrayList<>();

        String tmpBegin = "";
        String tmpEnd = "";
        String[] tmp;
        String[] tmpTimming1;
        String[] tmpTimming2;
        for (int i = 0; i < content.length; i++) {
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

        if ("text".equals(args[1])) {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TEXT.txt"));
            writer.append("\t");
            for (int i = 0; i < timming.size() - 1; i++) {
                long curDelay = timming.get(i).getEnd().toMillis() - timming.get(i + 1).getBegin().toMillis();
                //System.out.println(curDelay);
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
            writer.close();
        }
        if ("keyword".equals(args[1])) {
            StringBuilder stringBuilder1 = new StringBuilder();
            for (Object str : subtitles) {
                stringBuilder1.append(str + " ");
            }
            RussianAnalyzer analyzer = new RussianAnalyzer();
            TokenStream stream = analyzer.tokenStream("field", stringBuilder1.toString());
            stream.reset();
            BufferedWriter writer2 = new BufferedWriter(new FileWriter("lemmLibJava.txt"));
            while (stream.incrementToken()) {
                String lemma = stream.getAttribute(CharTermAttribute.class).toString();
                writer2.append(lemma + "\n");
            }
            stream.end();
            stream.close();
            ArrayList<String> lemmaSubtitles = new ArrayList<>();
            for (String curSub : subtitles) {
                stream = analyzer.tokenStream("field", curSub);
                stream.reset();
                StringBuilder curLem = new StringBuilder();
                while (stream.incrementToken()) {
                    curLem.append(stream.getAttribute(CharTermAttribute.class).toString());
                    curLem.append(" ");
                }
                lemmaSubtitles.add(curLem.toString());
                stream.end();
                stream.close();
            }

            stream.end();
            stream.close();
            /**
             * Создание таймкодов по порядку
             */

            BufferedReader bufferedReader = new BufferedReader(new FileReader(args[2]));
            StringBuilder key = new StringBuilder();
            String ExampleKeyLine = (bufferedReader.readLine());
            key.append(ExampleKeyLine);
            key.append("\n");
            while (ExampleKeyLine != null) {
                ExampleKeyLine = (bufferedReader.readLine());
                key.append(ExampleKeyLine);
                key.append("\n");
            }
            ArrayList<String> keywordsInRow = new ArrayList<String>(Arrays.asList(key.toString().split("\n")));
            if (keywordsInRow.get(keywordsInRow.size() - 1).equals("null")) {
                keywordsInRow.remove(keywordsInRow.size() - 1);
            }

            /**
             * запись леммы ключевых слов
             */
            ArrayList<String> lemmaKeywordsInRow = new ArrayList<>();
            for (String str : keywordsInRow) {
                stream = analyzer.tokenStream("field", str);
                stream.reset();
                StringBuilder curLem = new StringBuilder();
                while (stream.incrementToken()) {
                    curLem.append(stream.getAttribute(CharTermAttribute.class).toString());
                    curLem.append(" ");
                }
                lemmaKeywordsInRow.add(curLem.toString().trim());
                stream.end();
                stream.close();
            }

            ArrayList<Timing> timecodesInRow = Timecodes.getTimecodes(lemmaKeywordsInRow, lemmaSubtitles, timming);

            BufferedWriter writer3 = new BufferedWriter(new FileWriter(args[3]));
            for (Timing timing : timecodesInRow) {
                String curTimming = timing.toString().replaceAll("PT", "")
                        .replaceAll("H", ":").replaceAll("M", ":");
                curTimming = curTimming.substring(0, curTimming.indexOf("."));
                writer3.append(curTimming + " " + timing.getKeyword());
                writer3.newLine();
            }
            writer3.flush();
            writer3.close();


            /**
             * считывание образца
             */


            File file = new File("timecodesExample.txt");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            StringBuilder ex = new StringBuilder();
            String ExampleLine = (reader.readLine());
            ex.append(ExampleLine);
            ex.append("\n");
            while (ExampleLine != null) {
                ExampleLine = (reader.readLine());
                ex.append(ExampleLine);
                ex.append("\n");
            }

            String[] tCodeEx = ex.toString().split("\n");
            ArrayList<Timing> timingsEx = new ArrayList<>();
            for (int i = 0; i < tCodeEx.length - 1; i = i + 2) {
                tmp = tCodeEx[i].split(",");
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
                timingsEx.add(new Timing(begin, end));
            }
            ArrayList<String> keywordsEx = new ArrayList<>();
            for (int i = 1; i < tCodeEx.length - 1; i = i + 2) {
                keywordsEx.add(tCodeEx[i]);
            }
            System.out.println(tCodeEx);

            /**
             * сравнение по порядку
             */

            ArrayList<Timing> diff3 = new ArrayList<>();
            for (Timing timing : timecodesInRow) {
                Timing dif3 = new Timing();

                dif3.setBegin(timingsEx.get(timecodesInRow.indexOf(timing)).getBegin().minus(timing.getBegin()));
                dif3.setKeyword(timing.getKeyword());
                diff3.add(dif3);
                String tmpdif = dif3.toString().replace("PT", "");
                System.out.println(tmpdif);


            }


            /**
             * среднее значение и ско
             */


            double avgDif = Timecodes.getAverageDifference(diff3);
            System.out.println(avgDif);
            System.out.println(Timecodes.getSko(diff3, avgDif));
        }
    }

}

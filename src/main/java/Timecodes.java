import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Timecodes {
    public static ArrayList<Timing> getTimecodes (ArrayList<String> keywordsInRow, ArrayList<String> lemmaSubtitles, ArrayList<Timing> timming) {
        Timing tmpTimecodes = new Timing();
        ArrayList<Timing> timecodesInRow = new ArrayList<>();
        int indOfStart = 0;
        for (int i = 0; i < keywordsInRow.size() - 1; i++) {
            tmpTimecodes.setKeyword(keywordsInRow.get(i));
            tmpTimecodes.setOccurrence(0);
            for (int j = indOfStart; j < lemmaSubtitles.size(); j++) {
                if (lemmaSubtitles.get(j).contains(keywordsInRow.get(i + 1)) && tmpTimecodes.getOccurrence() > 0) {
                    timecodesInRow.add(tmpTimecodes);
                    indOfStart = j;
                    tmpTimecodes = new Timing();
                    tmpTimecodes.setKeyword(keywordsInRow.get(i + 1));
                    tmpTimecodes.setBegin(timming.get(j).getBegin());
                    tmpTimecodes.setOccurrence(0);
                    break;
                }
                if (lemmaSubtitles.get(j).contains(keywordsInRow.get(i))) {
                    if (tmpTimecodes.getBegin() == null) {
                        tmpTimecodes.setBegin(timming.get(j).getBegin());
                    }
                    tmpTimecodes.setEnd(timming.get(j).getEnd());
                    tmpTimecodes.setOccurrence(tmpTimecodes.getOccurrence() + 1);
                }
            }
        }
        tmpTimecodes = new Timing();
        tmpTimecodes.setKeyword(keywordsInRow.get(keywordsInRow.size() - 1));
        tmpTimecodes.setBegin(timming.get(indOfStart).getBegin());
        timecodesInRow.add(tmpTimecodes);
        tmpTimecodes.setOccurrence(0);
        return timecodesInRow;
    }

    public static double getAverageDifference(ArrayList<Timing> diff) {
        Long sumDif = 0l;
        for (Timing timing : diff) {
            sumDif = sumDif + Math.abs(timing.getBegin().toMillis());
        }
        double avgDif = (double) sumDif / 1000.0 / diff.size();
        return avgDif;
    }
    public static double getSko(ArrayList<Timing> diff, double avgDif) {
        Double sko = 0d;
        for (Timing timing : diff) {
            double cur = Math.abs(timing.getBegin().toMillis() / 1000.0);
            sko = sko + Math.pow(cur - avgDif, 2);
        }

        double resSko = sko / (diff.size());
        resSko = Math.sqrt(resSko);
        return resSko;
    }
}

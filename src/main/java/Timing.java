import java.time.Duration;

public class Timing {
    private Duration begin;
    private Duration end;
    private Integer occurrence;
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Integer occurrence) {
        this.occurrence = occurrence;
    }

    public Timing() {

    }
    public Timing(Duration begin, Duration end) {
        this.begin = begin;
        this.end = end;
        this.occurrence = 0;
    }

    public Duration getBegin() {
        return begin;
    }

    public void setBegin(Duration begin) {
        this.begin = begin;
    }

    public Duration getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return begin +
                " " + keyword + "\n";
    }

    public void setEnd(Duration end) {
        this.end = end;
    }
}

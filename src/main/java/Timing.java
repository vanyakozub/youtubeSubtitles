import java.time.Duration;

public class Timing {
    private Duration begin;
    private Duration end;

    public Timing(Duration begin, Duration end) {
        this.begin = begin;
        this.end = end;
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
        return "Timing{" +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }

    public void setEnd(Duration end) {
        this.end = end;
    }
}

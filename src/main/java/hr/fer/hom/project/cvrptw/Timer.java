package hr.fer.hom.project.cvrptw;

public class Timer {

    private long start;
    private long end;

    public Timer(int timeLimit){
        this.start = System.currentTimeMillis();
        this.end = this.start + timeLimit*60*1000;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}

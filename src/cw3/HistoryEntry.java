package cw3;

public class HistoryEntry {
    private final int movieId;
    private final long watchedAtEpochMillis;

    public HistoryEntry(int movieId, long watchedAtEpochMillis) {
        this.movieId = movieId;
        this.watchedAtEpochMillis = watchedAtEpochMillis;
    }
    public int getMovieId() { return movieId; }
    public long getWatchedAtEpochMillis() { return watchedAtEpochMillis; }
}
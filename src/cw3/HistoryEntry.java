package cw3;

public class HistoryEntry
{
    private final String movieId;
    private final long watchedAtEpochMillis;

    public HistoryEntry(String movieId, long watchedAtEpochMillis) {
        this.movieId = movieId;
        this.watchedAtEpochMillis = watchedAtEpochMillis;
    }
    public String getMovieId() { return movieId; }
    public long getWatchedAtEpochMillis() { return watchedAtEpochMillis; }
}
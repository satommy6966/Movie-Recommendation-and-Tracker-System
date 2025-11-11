package cw3;

import java.util.ArrayList;
import java.util.HashMap;

public class History {
    private final ArrayList<HistoryEntry> entries = new ArrayList<>();

    public void add(int movieId, long whenEpochMillis) {
        entries.add(new HistoryEntry(movieId, whenEpochMillis));
    }

    public ArrayList<HistoryEntry> list() {
        return new ArrayList<>(entries);
    }

    /**
     * 基于历史生成 genre->count 直方图
     */
    public HashMap<String, Integer> genreHistogram(MovieLibrary lib) {
        HashMap<String, Integer> map = new HashMap<>();
        for (HistoryEntry e : entries) {
            Movie m = lib.getById(e.getMovieId());
            if (m == null) continue;
            String g = m.getGenre();
            map.put(g, map.getOrDefault(g, 0) + 1);
        }
        return map;
    }
}

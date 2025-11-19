package cw3;

import java.util.*;

public class YearBasedStrategy implements RecommendationStrategy {
    @Override
    public ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN) {
        // 统计历史中最近观看电影的年份分布，简单偏好“较新”
        HashSet<String> watched = new HashSet<>();
        for (HistoryEntry e : user.getHistory().list()) watched.add(e.getMovieId());
        HashSet<String> inWatch = new HashSet<>(user.getWatchlist().list());
        ArrayList<Movie> candidates = new ArrayList<>();
        for (Movie m : lib.listAll()) {
            if (watched.contains(m.getId())) continue;
            if (inWatch.contains(m.getId())) continue;
            candidates.add(m);
        }
        candidates.sort((a, b) -> Integer.compare(b.getYear(), a.getYear()));
        ArrayList<Movie> out = new ArrayList<>();
        for (int i = 0; i < candidates.size() && i < topN; i++) out.add(candidates.get(i));
        return out;
    }
}
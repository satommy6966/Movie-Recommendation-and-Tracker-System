package cw3;

import java.util.*;

public class RatingBasedStrategy implements RecommendationStrategy {
    @Override
    public ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN) {
        HashSet<Integer> watched = new HashSet<>();
        for (HistoryEntry e : user.getHistory().list()) watched.add(e.getMovieId());
        HashSet<Integer> inWatch = new HashSet<>(user.getWatchlist().list());
        ArrayList<Movie> candidates = new ArrayList<>();
        for (Movie m : lib.listAll()) {
            if (watched.contains(m.getId())) continue;
            if (inWatch.contains(m.getId())) continue;
            candidates.add(m);
        }
        candidates.sort((a, b) -> {
            if (Double.compare(a.getRating(), b.getRating()) != 0) return Double.compare(b.getRating(), a.getRating());
            return Integer.compare(b.getYear(), a.getYear());
        });
        ArrayList<Movie> out = new ArrayList<>();
        for (int i = 0; i < candidates.size() && i < topN; i++) out.add(candidates.get(i));
        return out;
    }
}
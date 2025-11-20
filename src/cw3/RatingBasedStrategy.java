package cw3;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * RatingBasedStrategy：完全按评分排序推荐（高分优先）。
 */
public class RatingBasedStrategy {

    public ArrayList<Movie> recommend(User user, MovieLibrary lib, int topN) {
        HashSet<String> watched = new HashSet<String>();
        ArrayList<HistoryEntry> histList = user.getHistory().list();
        for (int i = 0; i < histList.size(); i++) {
            watched.add(histList.get(i).getMovieId());
        }

        HashSet<String> inWatch = new HashSet<String>(user.getWatchlist().list());

        ArrayList<Movie> candidates = new ArrayList<Movie>();
        ArrayList<Movie> all = lib.listAll();
        for (int i = 0; i < all.size(); i++) {
            Movie m = all.get(i);
            if (watched.contains(m.getId())) continue;
            if (inWatch.contains(m.getId())) continue;
            candidates.add(m);
        }

        sortByRatingThenYear(candidates);

        ArrayList<Movie> out = new ArrayList<Movie>();
        for (int i = 0; i < candidates.size() && i < topN; i++) {
            out.add(candidates.get(i));
        }
        return out;
    }

    private void sortByRatingThenYear(ArrayList<Movie> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                if (isBetter(list.get(j), list.get(best))) {
                    best = j;
                }
            }
            if (best != i) {
                Movie tmp = list.get(i);
                list.set(i, list.get(best));
                list.set(best, tmp);
            }
        }
    }

    private boolean isBetter(Movie a, Movie b) {
        if (a.getRating() != b.getRating()) {
            return a.getRating() > b.getRating();
        }
        return a.getYear() > b.getYear();
    }
}